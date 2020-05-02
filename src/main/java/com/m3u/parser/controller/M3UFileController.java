package com.m3u.parser.controller;

import com.m3u.parser.service.M3UDownloadFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.net.URL;
import java.nio.file.Files;
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
    public void download(@RequestParam String fileUrl,
                         @RequestParam(required = false, defaultValue = "") Set<String> categoryFilter,
                         HttpServletResponse response) {

        this.m3UDownloadFileService.downloadFile(new URL(fileUrl), categoryFilter)
                .subscribe(file -> {
                    response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etc.m3u");
                    Files.copy(file.toPath(), response.getOutputStream());
                    response.getOutputStream().flush();
                }).dispose();

    }
}
