package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.SkillRequest;
import compass.career.CareerCompass.dto.SkillResponse;
import compass.career.CareerCompass.dto.UpdateSkillRequest;

import java.util.List;

public interface SkillService {
    SkillResponse create(Integer userId, SkillRequest request);
    SkillResponse update(Integer userId, Integer id, UpdateSkillRequest request);
    List<SkillResponse> findByUserId(Integer userId);
}