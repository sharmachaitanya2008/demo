package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class FileInfo {
    private LocalDate fileDate;
    private Integer fileType;
    private Path filePath;
}
