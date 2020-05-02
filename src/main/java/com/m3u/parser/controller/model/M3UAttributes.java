package com.m3u.parser.controller.model;

import com.m3u.parser.controller.enums.EChannelQuality;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;

import java.lang.annotation.*;

@Value
@Builder
public class M3UAttributes {

    @M3UTag("tvg-id")
    private String id;

    @M3UTag("tvg-name")
    private String name;

    @M3UTag("group-title")
    private String groupTitle;

    private EChannelQuality channelQuality;

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface M3UTag {
        String value();
    }

    @SneakyThrows
    public static <T extends Annotation> T getAnnotation(Class<T> clazz, String field) {
        return M3UAttributes.class.getDeclaredField(field).getAnnotation(clazz);
    }
}
