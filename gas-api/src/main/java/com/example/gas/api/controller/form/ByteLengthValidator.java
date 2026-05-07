package com.example.gas.api.controller.form;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.charset.StandardCharsets;

public class ByteLengthValidator implements ConstraintValidator<ByteLength, String> {
    private int min;
    private int max;

    @Override
    public void initialize(ByteLength annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int byteLen = value.getBytes(StandardCharsets.UTF_8).length;
        return byteLen >= min && byteLen <= max;
    }
}
