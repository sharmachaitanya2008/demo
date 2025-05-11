package com.example.demo.services;

import com.example.demo.interfaces.DataProcessingService;
import com.example.demo.model.FileData;
import com.example.demo.model.FileInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DataProcessingServiceImpl implements DataProcessingService {
    final String dateField = "OutboundDate";

    @Override
    @SneakyThrows
    public List<FileData> processFiles(List<FileInfo> files)
    {
        List<FileData> data = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (FileInfo file : files) {
            JSONArray output = new JSONArray();
            JSONArray jsonArray = getData(file.getFilePath().toString());
            int length = jsonArray.length();
            for(int i = 0;i<length;i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.optString(dateField,"").equalsIgnoreCase(today.toString()))
                {
                    output.put(jsonObject);
                }
            }
            FileData fileData = new FileData(file.getFileType(),output);
            data.add(fileData);
        }
        return data;
    }
    public Map<Integer,JSONArray> collateData(List<FileData> fileData)
    {
        Map<Integer,JSONArray> collatedData = new HashMap<>();
        for(FileData file:fileData)
        {
            JSONArray existingData = collatedData.getOrDefault(file.getFileType(), new JSONArray());
            JSONArray newData = file.getFileContents();

            JSONArray mergedArray = new JSONArray();
            for(int i = 0; i < existingData.length(); i++){
                mergedArray.put(existingData.get(i));
            }
            for(int i = 0; i < newData.length(); i++){
                mergedArray.put(newData.get(i));
            }
            collatedData.put(file.getFileType(),mergedArray);
        }
        return collatedData;
    }
    JSONArray getData(String filePath) throws IOException {

        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return new JSONArray(repairJson(content));
    }
    String repairJson(String content) {
        // Remove any leading/trailing spaces
        content = content.trim();
        // Add commas between } { (where missing)
        String repaired = content.replaceAll("\\}\\s*\\{", "},\n{");
        // Wrap it inside [ ]
        repaired = "[\n" + repaired + "\n]";
        return repaired;
    }

}
