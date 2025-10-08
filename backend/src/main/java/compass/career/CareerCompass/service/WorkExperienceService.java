package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.WorkExperienceRequest;
import compass.career.CareerCompass.dto.WorkExperienceResponse;

import java.util.List;

public interface WorkExperienceService {
    WorkExperienceResponse create(Integer userId, WorkExperienceRequest request);
    WorkExperienceResponse update(Integer userId, Integer id, WorkExperienceRequest request);
    List<WorkExperienceResponse> findByUserId(Integer userId);
}
