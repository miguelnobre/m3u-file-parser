package com.m3u.parser.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EChannelQuality {
    MOBILE(0),
    LOW_INTERNET(1),
    NORMAL(2),
    HD(3),
    FHD(4);

    @Getter
    private int level;

    public boolean isEqualOrGreaterThan(EChannelQuality compareTo) {
        return getLevel() >= compareTo.getLevel();
    }
}
