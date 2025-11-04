package compass.career.careercompass.service;

import compass.career.careercompass.dto.CompleteProfileResponse;

public interface ProfileService {
    CompleteProfileResponse getCompleteProfile(Integer userId);
}
