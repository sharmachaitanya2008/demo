package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONArray;

@Data
@AllArgsConstructor
public class FileData {
    int fileType;
    JSONArray fileContents;
}
