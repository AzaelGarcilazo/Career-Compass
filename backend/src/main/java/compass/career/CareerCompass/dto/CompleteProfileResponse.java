package compass.career.CareerCompass.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class CompleteProfileResponse {
    Integer id;
    String name;
    String email;
    LocalDate birthDate;
    String country;
    String city;
    String role;
    List<AcademicInformationResponse> academicInformation;
    List<WorkExperienceResponse> workExperience;
    List<SkillResponse> skills;
}
