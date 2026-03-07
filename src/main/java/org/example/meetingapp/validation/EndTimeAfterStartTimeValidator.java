package org.example.meetingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.meetingapp.dto.MeetingCreateDto;

public class EndTimeAfterStartTimeValidator
        implements ConstraintValidator<EndTimeAfterStartTime, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof MeetingCreateDto dto) {
            if (dto.getStartTime() == null || dto.getEndTime() == null) {
                return true; // Hanteras av @NotNull
            }
            return dto.getEndTime().isAfter(dto.getStartTime());
        }
        if (obj instanceof org.example.meetingapp.dto.MeetingUpdateDto dto) {
            if (dto.getStartTime() == null || dto.getEndTime() == null) {
                return true;
            }
            return dto.getEndTime().isAfter(dto.getStartTime());
        }
        return true;
    }
}