package ute.edu.validation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Lỗi nghiệp vụ gắn với từng trường của form. */
public class FormValidationException extends IllegalArgumentException {
    private final Map<String, String> fieldErrors;

    public FormValidationException(Map<String, String> fieldErrors) {
        super(fieldErrors.values().stream().findFirst().orElse("Thông tin chưa hợp lệ."));
        this.fieldErrors = Collections.unmodifiableMap(new LinkedHashMap<>(fieldErrors));
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
