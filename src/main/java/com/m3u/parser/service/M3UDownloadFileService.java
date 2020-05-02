package com.m3u.parser.service;

import com.m3u.parser.service.producer.M3UFileParser;
import com.m3u.parser.service.transformer.M3UFileFilter;
import com.m3u.parser.service.transformer.M3UFileWriter;
import io.reactivex.Maybe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class M3UDownloadFileService {

    private final M3UFileParser m3UFileParser;
    private final M3UFileWriter m3UFileWriter;

    public Maybe<File> downloadFile(String fileLocation, Set<String> categoryFilter) {
        return Maybe.fromSupplier(() -> m3UFileParser.parse(fileLocation))
                .map(M3UFileFilter.from(M3UFileFilter.M3UFileFilterBuilder
                        .builder()
                        .categoryFilter(categoryFilter)
                        //.anotherFilter()
                        .build())::filter)
                .map(m3UFileWriter::generateOutput);
    }
}
