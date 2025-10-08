package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.CompleteProfileResponse;

public interface ProfileService {
    CompleteProfileResponse getCompleteProfile(Integer userId);
}
