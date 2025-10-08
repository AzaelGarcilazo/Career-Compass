package compass.career.CareerCompass.service;

import compass.career.CareerCompass.dto.CompleteProfileResponse;
import compass.career.CareerCompass.mapper.ProfileMapper;
import compass.career.CareerCompass.model.User;
import compass.career.CareerCompass.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CompleteProfileResponse getCompleteProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ProfileMapper.toCompleteProfileResponse(user);
    }
}
