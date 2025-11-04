package compass.career.careercompass.service;

import compass.career.careercompass.dto.WorkExperienceRequest;
import compass.career.careercompass.dto.WorkExperienceResponse;

import java.util.List;

public interface WorkExperienceService {
    WorkExperienceResponse create(Integer userId, WorkExperienceRequest request);
    WorkExperienceResponse update(Integer userId, Integer id, WorkExperienceRequest request);
    List<WorkExperienceResponse> findByUserId(Integer userId, int page, int pageSize);
}
