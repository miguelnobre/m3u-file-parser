package com.m3u.parser.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileFormatNotSupported extends Exception {

    public FileFormatNotSupported(String message) {
        super(message);
    }
}
