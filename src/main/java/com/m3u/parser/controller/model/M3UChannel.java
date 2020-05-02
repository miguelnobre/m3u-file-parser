package com.m3u.parser.controller.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class M3UChannel {
    private M3UAttributes attributes;
    private String line;
    private String accessUrl;
}
