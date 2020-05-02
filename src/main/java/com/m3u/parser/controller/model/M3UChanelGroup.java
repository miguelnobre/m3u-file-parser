package com.m3u.parser.controller.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class M3UChanelGroup {

    private String groupIdentifier;
    private List<M3UChanel> chanelList;

    @Override
    public String toString() {
        return "M3UGroup{" +
                "groupIdentifier='" + groupIdentifier + '\'' +
                ", chanelListSize=" + chanelList.size() +
                '}';
    }
}
