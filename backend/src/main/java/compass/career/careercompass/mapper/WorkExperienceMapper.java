package compass.career.careercompass.mapper;

import compass.career.careercompass.dto.WorkExperienceRequest;
import compass.career.careercompass.dto.WorkExperienceResponse;
import compass.career.careercompass.model.User;
import compass.career.careercompass.model.WorkExperience;

public final class WorkExperienceMapper {

    public static WorkExperienceResponse toResponse(WorkExperience entity) {
        if (entity == null)
            return null;

        return WorkExperienceResponse.builder()
                .id(entity.getId())
                .company(entity.getCompany())
                .position(entity.getPosition())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .currentJob(entity.getCurrentJob())
                .build();
    }

    public static WorkExperience toEntity(WorkExperienceRequest dto, User user) {
        if (dto == null || user == null)
            return null;

        WorkExperience entity = new WorkExperience();
        entity.setUser(user);
        entity.setCompany(dto.getCompany());
        entity.setPosition(dto.getPosition());
        entity.setDescription(dto.getDescription());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setCurrentJob(dto.getCurrentJob());

        return entity;
    }

    public static void copyToEntity(WorkExperienceRequest dto, WorkExperience entity) {
        if (dto == null || entity == null)
            return;

        entity.setCompany(dto.getCompany());
        entity.setPosition(dto.getPosition());
        entity.setDescription(dto.getDescription());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setCurrentJob(dto.getCurrentJob());
    }
}
