package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.SkillRequest;
import compass.career.CareerCompass.dto.SkillResponse;

import java.util.List;

public interface SkillService {
    SkillResponse create(Integer userId, SkillRequest request);
    SkillResponse updateProficiencyLevel(Integer userId, Integer id, Integer newLevel);
    List<SkillResponse> findByUserId(Integer userId);
}
