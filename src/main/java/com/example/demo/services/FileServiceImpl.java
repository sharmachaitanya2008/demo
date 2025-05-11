package com.example.demo.services;


import com.example.demo.interfaces.FileService;
import com.example.demo.model.FileInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileServiceImpl implements FileService {
    static String FILE_PATTERN = "(\\d{4}-\\d{2}-\\d{2})-1800-000(\\d+)\\.json";
    @Override
    public List<FileInfo> getRelevantFiles(String directoryPath, Integer daysToSubtract) {
        LocalDate today = LocalDate.now();
        LocalDate cutoffDate = today.minusDays(daysToSubtract);
        List<FileInfo> files = new ArrayList<>();

        File dir = new File(directoryPath);
        File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json"));

        if (jsonFiles != null) {
            Pattern pattern = Pattern.compile(FILE_PATTERN);
            for (File file : jsonFiles) {
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.find()) {
                    LocalDate fileDate = LocalDate.parse(matcher.group(1));
                    Integer fileType = Integer.parseInt(matcher.group(2));
                    if (!fileDate.isBefore(cutoffDate)) {
                        files.add(new FileInfo(fileDate, fileType, file.toPath()));
                    }
                }
            }
        }
        return files;
    }
    public void writeToCsv(Map<Integer, JSONArray> map) throws IOException {

        for(var x:map.entrySet()) {
            Integer key = x.getKey();
            JSONArray value = x.getValue();
            JsonNode jsonTree = new ObjectMapper().readTree(String.valueOf(value));
            CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();

            JsonNode firstObject = jsonTree.elements().next();
            firstObject.fieldNames().forEachRemaining(csvSchemaBuilder::addColumn);
            CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();

            CsvMapper csvMapper = new CsvMapper();
            csvMapper.writerFor(JsonNode.class)
                    .with(csvSchema)
                    .writeValue(new File(String.format("src/main/resources/%s.csv", key)), jsonTree);
        }
    }
}
