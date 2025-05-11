package com.example.demo.interfaces;

import com.example.demo.model.FileData;
import com.example.demo.model.FileInfo;

import java.util.List;
import java.util.Map;

public interface DataProcessingService {
    List<FileData> processFiles(List<FileInfo> files);
//    Map<Integer, List<Map<String, Object>>> processFiles(List<FileInfo> files);
}
