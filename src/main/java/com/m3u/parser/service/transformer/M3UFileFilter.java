package com.m3u.parser.service.transformer;

import com.m3u.parser.controller.model.M3UChanel;
import com.m3u.parser.controller.model.M3UDocument;
import com.m3u.parser.controller.model.M3UGroup;
import io.reactivex.functions.Function;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class M3UFileFilter implements Function<M3UDocument, M3UDocument> {

    private final M3UFileFilterBuilder filter;

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

    public static M3UFileFilter withParams(Set<String> categoryFilter) {
        return new M3UFileFilter(M3UFileFilterBuilder.builder().categoryFilter(categoryFilter).build());
    }

    @Override
    public M3UDocument apply(M3UDocument m3UDocument) {
        return this.filter(m3UDocument, this.filter);
    }

    @Builder
    public static class M3UFileFilterBuilder {
        @Builder.Default
        final Set<String> categoryFilter = new HashSet<>();
    }
}
