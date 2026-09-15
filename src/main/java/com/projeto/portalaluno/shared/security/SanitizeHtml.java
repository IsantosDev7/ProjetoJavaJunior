package com.projeto.portalaluno.shared.security;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SanitizeHtmlValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SanitizeHtml {
    String message() default "O texto contém tags HTML ou scripts não permitidos";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
