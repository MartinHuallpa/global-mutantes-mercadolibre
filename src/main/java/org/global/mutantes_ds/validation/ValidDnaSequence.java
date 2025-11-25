package org.global.mutantes_ds.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidDnaSequenceValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDnaSequence {

    String message() default "El ADN proporcionado no es válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
