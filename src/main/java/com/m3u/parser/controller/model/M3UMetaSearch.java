package com.m3u.parser.controller.model;

import com.m3u.parser.controller.enums.EChannelQuality;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class M3UMetaSearch {
    private static Pattern CHANNEL_NAME_PATTERN = Pattern.compile("#+\\s+(.*?)\\s+#+");

    private M3UChannel m3UChannel;
    private String m3uChannelGroup;
    private String groupId;

    public static M3UMetaSearch fromChannel(M3UChannel m3UChannel) {

        String groupKey = isNotBlank(m3UChannel.getAttributes().getGroupTitle())
                ? m3UChannel.getAttributes().getGroupTitle()
                : getChannelNameSanitized(m3UChannel.getAttributes().getName());

        String groupChannelKey = isNotBlank(m3UChannel.getAttributes().getId())
                ? m3UChannel.getAttributes().getId()
                : groupKey;

        return M3UMetaSearch.builder().groupId(groupKey).m3uChannelGroup(groupChannelKey).m3UChannel(m3UChannel).build();
    }

    private static String getChannelNameSanitized(String channelName) {
        Matcher matcher = CHANNEL_NAME_PATTERN.matcher(channelName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return channelName;
    }

    public EChannelQuality getChannelQuality() {
        return this.m3UChannel.getAttributes().getChannelQuality();
    }
}
