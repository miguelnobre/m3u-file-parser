package com.m3u.parser.controller;

import com.m3u.parser.controller.model.M3UDocument;
import com.m3u.parser.service.M3UFileFilter;
import com.m3u.parser.service.M3UFileParser;
import com.m3u.parser.service.M3UFileWriter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("api/")
public class M3UFileController {

    private M3UFileParser m3UFileParser;
    private M3UFileFilter m3UFileFilter;
    private M3UFileWriter m3UFileWriter;

    @SneakyThrows
    @GetMapping("download")
    public void download(@RequestParam String fileUrl,
                         @RequestParam(required = false, defaultValue = "") Set<String> categoryFilter,
                         HttpServletResponse response) {

        M3UDocument originalDocument = m3UFileParser.parse(new URL(fileUrl));

        M3UDocument filteredDocument = m3UFileFilter.filter(
                originalDocument,
                M3UFileFilter.M3UFileFilterBuilder
                        .builder()
                        .categoryFilter(categoryFilter)
                        //new filters ...
                        .build());

        File file = m3UFileWriter.generateOutput(filteredDocument);

        response.setContentType("application/pdf");
        response.addHeader("Content-Disposition", "attachment; filename=etc.m3u");

        Files.copy(file.toPath(), response.getOutputStream());
        response.getOutputStream().flush();
    }
}
