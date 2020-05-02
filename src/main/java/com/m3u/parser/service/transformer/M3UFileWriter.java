package com.m3u.parser.service.transformer;

import com.m3u.parser.controller.model.M3UChanel;
import com.m3u.parser.controller.model.M3UChanelGroup;
import com.m3u.parser.controller.model.M3UDocument;
import com.m3u.parser.controller.model.M3UGroup;
import io.reactivex.functions.Function;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class M3UFileWriter implements Function<M3UDocument, File> {

    @SneakyThrows
    public File generateOutput(M3UDocument document) {

        File temp = File.createTempFile("generated", null);
        temp.deleteOnExit();

        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));

        writer.write(document.getInitialLine() + "\n");

        for (M3UGroup group : document.getGroupList()) {
            for (M3UChanelGroup channelGroup : group.getChanelGroups()) {
                for (M3UChanel subChannel : channelGroup.getChanelList()) {
                    writer.write(subChannel.getLine() + "\n");
                    writer.write(subChannel.getAccessUrl() + "\n");
                }
            }
        }
        writer.close();

        return temp;
    }

    @Override
    public File apply(M3UDocument m3UDocument) throws Throwable {
        return this.generateOutput(m3UDocument);
    }

}
