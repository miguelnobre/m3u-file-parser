package com.m3u.parser.service.transformer;

import com.m3u.parser.controller.model.M3UChanel;
import com.m3u.parser.controller.model.M3UDocument;
import com.m3u.parser.controller.model.M3UGroup;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@RequiredArgsConstructor
public class M3UFileFilter {

    private static M3UFileFilter DEFAULT_FILTER = null;

    private final M3UFileFilterAttributes filterAttributes;

    public static M3UFileFilter getDefaultFilter() {
        if (DEFAULT_FILTER == null) {
            DEFAULT_FILTER = new M3UFileFilter(new M3UFileFilterAttributes());
        }
        return DEFAULT_FILTER;
    }

    public static M3UFileFilter from(M3UFileFilterAttributes filterAttributes) {
        return new M3UFileFilter(filterAttributes);
    }

    public M3UDocument filter(M3UDocument originalDocument) {
        M3UDocument filteredDocument = M3UDocument.builder()
                .initialLine(originalDocument.getInitialLine())
                .build();

        // Filtering Groups
        List<M3UGroup> filteredGroups = originalDocument.getGroupList()
                .stream()
                .filter(group -> filterAttributes.categoryFilter == null || filterAttributes.categoryFilter.isEmpty() || filterAttributes.categoryFilter.contains(group.getGroupIdentifier()))
                .collect(Collectors.toList());

        filteredDocument.setGroupList(filteredGroups);

        // Fetch Best Quality Channel
        if (filterAttributes.onlyBestQualityChannelFilter) {
            filteredGroups.forEach(group -> {
                group.getChanelGroups().forEach(m3UChanelGroup -> {
                    M3UChanel bestQualityChannel = m3UChanelGroup.getChanelList()
                            .stream()
                            .max(Comparator.comparing((M3UChanel c) -> c.getAttributes().getChannelQuality().getLevel()))
                            .get();
                    m3UChanelGroup.setChanelList(asList(bestQualityChannel));
                });
            });
        }
        return filteredDocument;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class M3UFileFilterAttributes {
        @Builder.Default
        final Set<String> categoryFilter = new HashSet<>();
        boolean onlyBestQualityChannelFilter = false;
    }
}
