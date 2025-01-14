package app.v1.messagebroker.service.github;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GitMapperService {

    public static List<Map<String, Object>> filterFields(Map<String, Object> data) {
        List<Map<String, Object>> filteredList = new ArrayList<>();
        List<String> fieldsToKeep = List.of("filename", "changes", "patch");

        // Check if the map contains the "files" field
        if (data.containsKey("files") && data.get("files") instanceof List) {
            List<?> files = (List<?>) data.get("files");

            for (Object file : files) {
                if (file instanceof Map) {
                    Map<String, Object> fileMap = (Map<String, Object>) file;
                    Map<String, Object> filteredMap = new HashMap<>();

                    // Keep only the required fields
                    for (String field : fieldsToKeep) {
                        if (fileMap.containsKey(field)) {
                            filteredMap.put(field, fileMap.get(field));
                        }
                    }
                    filteredList.add(filteredMap);
                }
            }
        }
        return filteredList;
    }
}
