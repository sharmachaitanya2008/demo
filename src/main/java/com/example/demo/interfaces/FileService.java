package com.example.demo.interfaces;

import com.example.demo.model.FileInfo;

import java.util.List;

public interface FileService {
    List<FileInfo> getRelevantFiles(String directoryPath, Integer daysToSubtract);
}
