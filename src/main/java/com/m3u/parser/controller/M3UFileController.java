package com.m3u.parser.controller;

import com.m3u.parser.service.M3UDownloadFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.HttpHeaders;
import java.io.FileInputStream;
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
    public ResponseEntity download(@RequestParam String fileUrl,
                                   @RequestParam(required = false, defaultValue = "") Set<String> categoryFilter) {

        return this.m3UDownloadFileService.downloadFile(fileUrl, categoryFilter)
                .map(file -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etc.m3u")
                        .contentLength(file.length())
                        .body(new InputStreamResource(new FileInputStream(file)))
                ).blockingGet(ResponseEntity.notFound().build());

    }
}
