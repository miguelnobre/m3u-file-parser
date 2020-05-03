package com.m3u.parser.service;

import com.m3u.parser.controller.enums.EChannelQuality;
import com.m3u.parser.controller.model.M3UChannel;
import com.m3u.parser.controller.model.M3UMetaSearch;
import com.m3u.parser.service.filter.CategoryFilter;
import com.m3u.parser.service.filter.ImageQualityFilter;
import com.m3u.parser.service.producer.M3UChannelProducer;
import com.m3u.parser.service.producer.M3UFileParser;
import com.m3u.parser.service.transformer.M3UFileFilter;
import com.m3u.parser.service.transformer.M3UFileWriter;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Set;

@Service
@Slf4j
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

    public Flowable<M3UChannel> downloadFile2(String fileLocation, @NotNull Set<String> categoryFilter, EChannelQuality minimumChannelQuality) {

        return Flowable.<M3UChannel>fromPublisher(new M3UChannelProducer(fileLocation))
                .map(M3UMetaSearch::fromChannel)
                .filter(CategoryFilter.filterCategory(categoryFilter))
                .filter(ImageQualityFilter.filterMinimumQuality(minimumChannelQuality))
                .map(M3UMetaSearch::getM3UChannel);
    }
}
