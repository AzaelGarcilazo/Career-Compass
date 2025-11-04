package compass.career.careercompass.service;

import compass.career.careercompass.dto.AcademicInformationRequest;
import compass.career.careercompass.dto.AcademicInformationResponse;

import java.util.List;

public interface AcademicInformationService {
    AcademicInformationResponse create(Integer userId, AcademicInformationRequest request);
    AcademicInformationResponse update(Integer userId, Integer id, AcademicInformationRequest request);
    List<AcademicInformationResponse> findByUserId(Integer userId, int page, int pageSize);
}
