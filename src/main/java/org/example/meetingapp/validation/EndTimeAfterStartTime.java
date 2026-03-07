package org.example.meetingapp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EndTimeAfterStartTimeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EndTimeAfterStartTime {
    String message() default "Sluttid måste vara efter starttid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}