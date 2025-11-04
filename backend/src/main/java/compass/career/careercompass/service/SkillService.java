package compass.career.careercompass.service;

import compass.career.careercompass.dto.SkillRequest;
import compass.career.careercompass.dto.SkillResponse;
import compass.career.careercompass.dto.UpdateSkillRequest;

import java.util.List;

public interface SkillService {
    SkillResponse create(Integer userId, SkillRequest request);
    SkillResponse update(Integer userId, Integer id, UpdateSkillRequest request);
    List<SkillResponse> findByUserId(Integer userId, int page, int pageSize);
}