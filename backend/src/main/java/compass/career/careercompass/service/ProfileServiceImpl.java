package compass.career.careercompass.service;

import compass.career.careercompass.dto.CompleteProfileResponse;
import compass.career.careercompass.mapper.ProfileMapper;
import compass.career.careercompass.model.User;
import compass.career.careercompass.repository.UserRepository;
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
