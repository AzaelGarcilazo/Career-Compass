package compass.career.CareerCompass.service;

import java.util.Map;

public interface AzureCognitiveService {
    Map<String, Object> analyzePersonality(Map<String, Object> responses);
    Map<String, Object> analyzeText(String text);
}
