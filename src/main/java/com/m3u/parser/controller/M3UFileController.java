package com.m3u.parser.controller;

import com.m3u.parser.controller.model.M3UChanel;
import com.m3u.parser.controller.model.M3UDocument;
import com.m3u.parser.controller.model.M3UGroup;
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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("api/")
public class M3UFileController {

    private M3UFileParser m3UFileParser;
    private M3UFileWriter m3UFileWriter;

    @SneakyThrows
    @GetMapping("download")
    public void download(@RequestParam String fileUrl,
                         @RequestParam(required = false, defaultValue = "") Set<String> categoryFilter,
                         HttpServletResponse response) {
        M3UDocument document = m3UFileParser.parse(new URL(fileUrl));

        // Filtering Groups
        List<M3UGroup> filteredGroups = document.getGroupList()
                .stream()
                .filter(group -> group.isInGroupTitle(categoryFilter))
                .collect(Collectors.toList());

        // Fetch Best Quality Channel
        filteredGroups.forEach(group -> {
            group.getChanelGroups().forEach(m3UChanelGroup -> {
                M3UChanel bestQualityChannel = m3UChanelGroup.getChanelList()
                        .stream()
                        .max(Comparator.comparing((M3UChanel c) -> c.getAttributes().getChannelQuality().getLevel()))
                        .get();
                m3UChanelGroup.setChanelList(List.of(bestQualityChannel));
            });
        });

        File file = m3UFileWriter.generateOutput(new M3UDocument(document.getInitialLine(), document.getGroupList()));

        response.setContentType("application/pdf");
        response.addHeader("Content-Disposition", "attachment; filename=etc.m3u");

        Files.copy(file.toPath(), response.getOutputStream());
        response.getOutputStream().flush();
    }
}
