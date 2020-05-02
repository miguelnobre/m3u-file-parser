package com.m3u.parser.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class M3UDocument {
    @Builder.Default
    private String initialLine = "#EXTM3U";
    private List<M3UGroup> groupList;
}
