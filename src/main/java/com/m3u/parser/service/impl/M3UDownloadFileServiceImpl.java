package com.m3u.parser.service.impl;

import com.m3u.parser.service.M3UDownloadFileService;
import com.m3u.parser.service.producer.M3UFileParser;
import com.m3u.parser.service.transformer.M3UFileFilter;
import com.m3u.parser.service.transformer.M3UFileWriter;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class M3UDownloadFileServiceImpl implements M3UDownloadFileService {

    @Override
    public Single<File> downloadFile(URL fileLocation, Set<String> categoryFilter) {

        return Single.fromSupplier(M3UFileParser.fileParserFromUrl(fileLocation))
                .map(M3UFileFilter.withParams(categoryFilter)) //filter
                .map(new M3UFileWriter()); // map to file
    }
}
