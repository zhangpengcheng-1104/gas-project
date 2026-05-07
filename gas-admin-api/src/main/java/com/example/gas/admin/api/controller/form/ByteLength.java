package com.example.gas.api.controller.form;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ByteLengthValidator.class)
@Documented
public @interface ByteLength {
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    String message() default "字节长度不合法";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
