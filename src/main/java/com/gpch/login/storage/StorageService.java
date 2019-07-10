package com.gpch.login.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file, String userEmailId);

    Stream<Path> loadAll(String userEmailId);

    Path load(String filename);

    Resource loadAsResource(String filename, String userEmailId);

    void deleteAll();

}
