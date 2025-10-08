package compass.career.CareerCompass.mapper;

import compass.career.CareerCompass.dto.CompleteProfileResponse;
import compass.career.CareerCompass.model.User;

import java.util.stream.Collectors;

public final class ProfileMapper {

    public static CompleteProfileResponse toCompleteProfileResponse(User user) {
        if (user == null)
            return null;

        return CompleteProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .country(user.getCountry())
                .city(user.getCity())
                .role(user.getRole().getName())
                .academicInformation(user.getAcademicInformations() != null ?
                        user.getAcademicInformations().stream()
                                .map(AcademicInformationMapper::toResponse)
                                .collect(Collectors.toList()) : null)
                .workExperience(user.getWorkExperiences() != null ?
                        user.getWorkExperiences().stream()
                                .map(WorkExperienceMapper::toResponse)
                                .collect(Collectors.toList()) : null)
                .skills(user.getSkills() != null ?
                        user.getSkills().stream()
                                .map(SkillMapper::toResponse)
                                .collect(Collectors.toList()) : null)
                .build();
    }
}
