package compass.career.careercompass.mapper;

import compass.career.careercompass.dto.AnswerOptionResponse;
import compass.career.careercompass.dto.QuestionResponse;
import compass.career.careercompass.dto.TestResponse;
import compass.career.careercompass.model.AnswerOption;
import compass.career.careercompass.model.Question;
import compass.career.careercompass.model.Test;

import java.util.stream.Collectors;

public final class TestMapper {

    public static TestResponse toResponse(Test entity) {
        if (entity == null)
            return null;

        return TestResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .testType(entity.getTestType().getName())
                .questionsToShow(entity.getQuestionsToShow())
                .questions(entity.getQuestions() != null ?
                        entity.getQuestions().stream()
                                .filter(Question::getActive)
                                .map(TestMapper::toQuestionResponse)
                                .collect(Collectors.toList()) : null)
                .build();
    }

    public static QuestionResponse toQuestionResponse(Question entity) {
        if (entity == null)
            return null;

        return QuestionResponse.builder()
                .id(entity.getId())
                .questionText(entity.getQuestionText())
                .orderNumber(entity.getOrderNumber())
                .options(entity.getAnswerOptions() != null ?
                        entity.getAnswerOptions().stream()
                                .map(TestMapper::toAnswerOptionResponse)
                                .collect(Collectors.toList()) : null)
                .build();
    }

    public static AnswerOptionResponse toAnswerOptionResponse(AnswerOption entity) {
        if (entity == null)
            return null;

        return AnswerOptionResponse.builder()
                .id(entity.getId())
                .optionText(entity.getOptionText())
                .build();
    }
}
