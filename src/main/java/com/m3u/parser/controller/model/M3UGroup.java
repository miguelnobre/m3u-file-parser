package com.m3u.parser.controller.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class M3UGroup {

    private String groupIdentifier;
    @Builder.Default
    private List<M3UChanelGroup> chanelGroups = new ArrayList<>();

    public boolean isPortuguese() {
        return groupIdentifier.equals("Portugal");
    }

    public boolean isInGroupTitle(Set<String> groupTitle) {
        return groupTitle == null || groupTitle.isEmpty() || groupTitle.contains(groupIdentifier);
    }

    @Override
    public String toString() {
        return "M3UGroup{" +
                "groupIdentifier='" + groupIdentifier + '\'' +
                ", chanelGroupsSize=" + chanelGroups.size() +
                '}';
    }
}
