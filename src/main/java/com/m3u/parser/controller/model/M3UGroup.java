package com.m3u.parser.controller.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class M3UGroup {

    private String groupIdentifier;
    @Builder.Default
    private List<M3UChanelGroup> chanelGroups = new ArrayList<>();

    @Override
    public String toString() {
        return "M3UGroup{" +
                "groupIdentifier='" + groupIdentifier + '\'' +
                ", chanelGroupsSize=" + chanelGroups.size() +
                '}';
    }
}
