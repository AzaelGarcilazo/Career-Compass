package compass.career.careercompass.mapper;

import compass.career.careercompass.dto.SkillRequest;
import compass.career.careercompass.dto.SkillResponse;
import compass.career.careercompass.model.Skill;
import compass.career.careercompass.model.User;

public final class SkillMapper {

    public static SkillResponse toResponse(Skill entity) {
        if (entity == null)
            return null;

        return SkillResponse.builder()
                .id(entity.getId())
                .skillName(entity.getSkillName())
                .proficiencyLevel(entity.getProficiencyLevel())
                .build();
    }

    public static Skill toEntity(SkillRequest dto, User user) {
        if (dto == null || user == null)
            return null;

        Skill entity = new Skill();
        entity.setUser(user);
        entity.setSkillName(dto.getSkillName());
        entity.setProficiencyLevel(dto.getProficiencyLevel());

        return entity;
    }

    public static void copyToEntity(SkillRequest dto, Skill entity) {
        if (dto == null || entity == null)
            return;

        entity.setSkillName(dto.getSkillName());
        entity.setProficiencyLevel(dto.getProficiencyLevel());
    }
}
