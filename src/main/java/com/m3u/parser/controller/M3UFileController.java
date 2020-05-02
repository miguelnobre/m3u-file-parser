package com.m3u.parser.controller;

import com.m3u.parser.service.M3UDownloadFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/")
@Api(tags = {"M3U file Download API"})
public class M3UFileController {

    private final M3UDownloadFileService m3UDownloadFileService;

    @SneakyThrows
    @GetMapping("download")
    @ApiOperation(value = "Download M3U file", notes = "Filter and download m3u file")
    //using raw type because swagger doc doesn't support InputStreamResource
    public ResponseEntity<StreamingResponseBody> download(@RequestParam String fileUrl,
                                                          @RequestParam(required = false, defaultValue = "") Set<String> categoryFilter,
                                                          final HttpServletResponse response) {

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etc.m3u");

        StreamingResponseBody streamingResponseBody = outputStream -> {

            m3UDownloadFileService.downloadFile2(fileUrl, categoryFilter)
                    .doOnSubscribe(subscription -> outputStream.write("#EXTM3U".getBytes()))
                    .blockingSubscribe(m3UChannel -> {
                        outputStream.write(System.lineSeparator().getBytes());
                        outputStream.write(m3UChannel.getLine().getBytes());
                        outputStream.write(System.lineSeparator().getBytes());
                        outputStream.write(m3UChannel.getAccessUrl().getBytes());
                        outputStream.flush();
                    });

        };


        return new ResponseEntity(streamingResponseBody, HttpStatus.OK);

    }
}
