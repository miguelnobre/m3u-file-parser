package com.m3u.parser.service.transformer;

import com.m3u.parser.controller.model.M3UChanelGroup;
import com.m3u.parser.controller.model.M3UChannel;
import com.m3u.parser.controller.model.M3UDocument;
import com.m3u.parser.controller.model.M3UGroup;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

@Service
public class M3UFileWriter {

    @SneakyThrows
    public File generateOutput(M3UDocument document) {

        File temp = File.createTempFile("generated", null);
        temp.deleteOnExit();

        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));

        writer.write(document.getInitialLine() + "\n");

        for (M3UGroup group : document.getGroupList()) {
            for (M3UChanelGroup channelGroup : group.getChanelGroups()) {
                for (M3UChannel subChannel : channelGroup.getChanelList()) {
                    writer.write(subChannel.getLine() + "\n");
                    writer.write(subChannel.getAccessUrl() + "\n");
                }
            }
        }
        writer.close();

        return temp;
    }
}
