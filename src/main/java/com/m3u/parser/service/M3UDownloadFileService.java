package com.m3u.parser.service;

import io.reactivex.Single;

import java.io.File;
import java.net.URL;
import java.util.Set;

public interface M3UDownloadFileService {

    Single<File> downloadFile(URL fileLocation, Set<String> categoryFilter);
}
