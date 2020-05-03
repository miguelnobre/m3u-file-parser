package com.m3u.parser.service.filter;

import com.m3u.parser.controller.enums.EChannelQuality;
import com.m3u.parser.controller.model.M3UMetaSearch;
import io.reactivex.functions.Predicate;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class ImageQualityFilter implements Predicate<M3UMetaSearch> {

    private EChannelQuality minimumChannelQuality;

    public static ImageQualityFilter filterMinimumQuality(EChannelQuality minimumChannelQuality) {
        return new ImageQualityFilter(Objects.isNull(minimumChannelQuality)
                ? EChannelQuality.MOBILE : minimumChannelQuality);
    }

    @Override
    public boolean test(M3UMetaSearch m3UMetaSearch) throws Throwable {
        return m3UMetaSearch.getChannelQuality().isEqualOrGreaterThan(minimumChannelQuality);
    }
}
