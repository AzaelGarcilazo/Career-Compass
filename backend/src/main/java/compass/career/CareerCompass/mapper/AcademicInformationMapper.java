package compass.career.CareerCompass.mapper;

import compass.career.CareerCompass.dto.AcademicInformationRequest;
import compass.career.CareerCompass.dto.AcademicInformationResponse;
import compass.career.CareerCompass.model.AcademicInformation;
import compass.career.CareerCompass.model.User;

public final class AcademicInformationMapper {

    public static AcademicInformationResponse toResponse(AcademicInformation entity) {
        if (entity == null)
            return null;

        return AcademicInformationResponse.builder()
                .id(entity.getId())
                .institution(entity.getInstitution())
                .career(entity.getCareer())
                .average(entity.getAverage())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .inProgress(entity.getInProgress())
                .build();
    }

    public static AcademicInformation toEntity(AcademicInformationRequest dto, User user) {
        if (dto == null || user == null)
            return null;

        AcademicInformation entity = new AcademicInformation();
        entity.setUser(user);
        entity.setInstitution(dto.getInstitution());
        entity.setCareer(dto.getCareer());
        entity.setAverage(dto.getAverage());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setInProgress(dto.getInProgress());

        return entity;
    }

    public static void copyToEntity(AcademicInformationRequest dto, AcademicInformation entity) {
        if (dto == null || entity == null)
            return;

        entity.setInstitution(dto.getInstitution());
        entity.setCareer(dto.getCareer());
        entity.setAverage(dto.getAverage());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setInProgress(dto.getInProgress());
    }
}
