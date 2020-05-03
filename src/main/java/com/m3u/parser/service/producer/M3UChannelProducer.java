package com.m3u.parser.service.producer;

import com.m3u.parser.controller.enums.EChannelQuality;
import com.m3u.parser.controller.model.M3UAttributes;
import com.m3u.parser.controller.model.M3UChannel;
import com.m3u.parser.exception.FileFormatNotSupported;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

@RequiredArgsConstructor
@Slf4j
public class M3UChannelProducer implements Publisher<M3UChannel> {
    private static String FILE_TYPE = "#EXTM3U";
    private final String m3uFileLocation;

    private static boolean isM3UFile(String header) {
        return FILE_TYPE.equalsIgnoreCase(header);
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

    @SneakyThrows
    @Override
    public void subscribe(Subscriber<? super M3UChannel> subscriber) {

        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(this.m3uFileLocation).openStream()));
            Iterator<String> linesIterator = reader.lines().iterator();

            if (!isM3UFile(linesIterator.next())) {
                throw new FileFormatNotSupported("File type not supported. Please provide a M3U file.");
            }

            subscriber.onSubscribe(new Subscription() {

                @Override
                public void request(long n) {

                    while (linesIterator.hasNext() && --n > 0) {
                        String line = linesIterator.next();
                        M3UAttributes attributes = getLineAttributes(line);

                        M3UChannel channel = M3UChannel.builder()
                                .attributes(attributes)
                                .line(line)
                                .accessUrl(linesIterator.next())   // Read next line
                                .build();

                        subscriber.onNext(channel);
                        log.info("New channel submitted: {} ", channel);
                    }
                    if (n > 0) {
                        subscriber.onComplete();
                    }
                }

                @SneakyThrows
                @Override
                public void cancel() {
                    reader.close();
                }
            });
        } catch (Exception exception) {
            subscriber.onError(exception);
        }

    }

}
