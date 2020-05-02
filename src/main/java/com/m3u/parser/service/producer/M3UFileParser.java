package com.m3u.parser.service.producer;

import com.m3u.parser.controller.enums.EChannelQuality;
import com.m3u.parser.controller.model.*;
import com.m3u.parser.exception.FileFormatNotSupported;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Slf4j
@Service
@RequiredArgsConstructor
public class M3UFileParser {

    private static Pattern CHANNEL_NAME_PATTERN = Pattern.compile("#+\\s+(.*?)\\s+#+");
    private static String FILE_TYPE = "#EXTM3U";

    public static void main(String[] args) {
        String str = "## AFFF ##";
        Pattern pattern = Pattern.compile("#+\\s+(.*?)\\s+#+");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            System.out.println("|" + matcher.group(1) + "|");
        }
    }

    @SneakyThrows
    private M3UAttributes getLineAttributes(String line) {
        String idLabel = M3UAttributes.getAnnotation(M3UAttributes.M3UTag.class, "id").value();
        String nameLabel = M3UAttributes.getAnnotation(M3UAttributes.M3UTag.class, "name").value();
        String groupTitleLabel = M3UAttributes.getAnnotation(M3UAttributes.M3UTag.class, "groupTitle").value();

        String channelName = extractBetween(line, line.indexOf(nameLabel) + nameLabel.length() + 2, '"');

        M3UAttributes attributes = M3UAttributes.builder()
                .id(extractBetween(line, line.indexOf(idLabel) + idLabel.length() + 2, '"'))
                .name(channelName)
                .groupTitle(extractBetween(line, line.indexOf(groupTitleLabel) + groupTitleLabel.length() + 2, '"'))
                .channelQuality(derivateChannelQuality(channelName))
                .build();

        return attributes;
    }

    private String extractBetween(String line, int fromPosition, char toCharacter) {
        char[] content = line.toCharArray();
        StringBuilder builder = new StringBuilder();

        for (int i = fromPosition; i < content.length; i++) {
            if (content[i] == toCharacter) {
                return builder.toString();
            }

            builder.append(content[i]);
        }
        return "";
    }

    private M3UGroup createGroup(Map<String, M3UGroup> groupMap, String groupIdentifier) {
        M3UGroup group = M3UGroup.builder()
                .groupIdentifier(groupIdentifier)
                .chanelGroups(new ArrayList<>())
                .build();

        groupMap.put(groupIdentifier, group);
        return group;
    }

    private M3UChanelGroup createChannelsGroup(Map<String, M3UChanelGroup> chanelGroupMap, String groupIdentifier) {
        M3UChanelGroup group = M3UChanelGroup.builder()
                .groupIdentifier(groupIdentifier)
                .chanelList(new ArrayList<>())
                .build();

        return group;
    }

    private String getChannelNameSanitized(String channelName) {
        Matcher matcher = CHANNEL_NAME_PATTERN.matcher(channelName);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return channelName;
    }

    private EChannelQuality derivateChannelQuality(String channelName) {
        if (channelName.contains("(Baixa Internet)") || channelName.contains("Low Internet")) {
            return EChannelQuality.LOW_INTERNET;
        } else if (channelName.contains("( Mobile )")) {
            return EChannelQuality.MOBILE;
        } else if (channelName.contains(" HD ")) {
            return EChannelQuality.HD;
        } else if (channelName.contains(" FHD ")) {
            return EChannelQuality.FHD;
        } else {
            return EChannelQuality.NORMAL;
        }
    }

    private static boolean isM3UFile(String header) {
        return FILE_TYPE.equalsIgnoreCase(header);
    }

    public M3UDocument parse(URL fileLocation) {
        log.info("Reading file from {}", fileLocation.getPath());

        M3UDocument m3UDocument = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileLocation.openStream()))) {
            Map<String, M3UGroup> groupMap = new HashMap<>();
            Map<String, M3UChanelGroup> channelsGroupMap = new HashMap<>();

            String initialLine = reader.readLine();

            if (!isM3UFile(initialLine)) {
                throw new FileFormatNotSupported("File type not supported. Please provide a M3U file.");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                M3UAttributes attributes = getLineAttributes(line);

                M3UChanel channel = M3UChanel.builder()
                        .attributes(attributes)
                        .line(line)
                        .accessUrl(reader.readLine())   // Read next line
                        .build();

                M3UChanelGroup channelGroup = channelsGroupMap.get(channel.getAttributes().getId());
                if (channelGroup != null) {
                    channelGroup.getChanelList().add(channel);
                    continue;
                }

                M3UChanelGroup newChannelGroup = createChannelsGroup(channelsGroupMap, channel.getAttributes().getId());
                newChannelGroup.getChanelList().add(channel);

                if (isNotBlank(channel.getAttributes().getId())) {
                    channelsGroupMap.putIfAbsent(channel.getAttributes().getId(), newChannelGroup);
                }

                String groupKey = isNotBlank(channel.getAttributes().getGroupTitle())
                        ? channel.getAttributes().getGroupTitle()
                        : getChannelNameSanitized(attributes.getName());

                Optional.ofNullable(groupMap.get(groupKey))
                        .orElseGet(() -> createGroup(groupMap, groupKey))
                        .getChanelGroups()
                        .add(newChannelGroup);

                m3UDocument = M3UDocument.builder()
                        .initialLine(initialLine)
                        .groupList(List.copyOf(groupMap.values()))
                        .build();
            }
        } catch (Exception e) {
            log.error("Error trying to access remote file.", e);
        }

        return m3UDocument;
    }
}
