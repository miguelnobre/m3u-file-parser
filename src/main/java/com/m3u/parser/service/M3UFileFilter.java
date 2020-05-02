package com.m3u.parser.service;

import com.m3u.parser.controller.model.M3UChanel;
import com.m3u.parser.controller.model.M3UDocument;
import com.m3u.parser.controller.model.M3UGroup;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class M3UFileFilter {

    public M3UDocument filter(M3UDocument originalDocument, M3UFileFilterBuilder filter) {
        M3UDocument filteredDocument = M3UDocument.builder()
                .initialLine(originalDocument.getInitialLine())
                .build();

        // Filtering Groups
        List<M3UGroup> filteredGroups = originalDocument.getGroupList()
                .stream()
                .filter(group -> filter.categoryFilter == null || filter.categoryFilter.isEmpty() || filter.categoryFilter.contains(group.getGroupIdentifier()))
                .collect(Collectors.toList());

        filteredDocument.setGroupList(filteredGroups);

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

        return filteredDocument;
    }

    @Builder
    public static class M3UFileFilterBuilder {
        @Builder.Default
        final Set<String> categoryFilter = new HashSet<>();
    }
}
