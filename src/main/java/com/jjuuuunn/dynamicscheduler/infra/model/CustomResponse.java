package com.jjuuuunn.dynamicscheduler.infra.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

public record CustomResponse<T>(boolean success, int code, String message, T data) {

    public static <T> CustomResponse<T> ok(T data) {
        return new CustomResponse<>(true, HttpStatus.OK.value(), null, data);
    }

    public static <T> CustomResponse<T> ok(T data, Integer count) {
        return CustomResponse.ok((T) DataResponse.list(data, count));
    }

//    public static <T> CustomResponse<T> ok(T data, Integer count, Integer onCount, Integer offCont) {
//        return CustomResponse.ok((T) DataResponseWithOnOffCount.list(data, count,onCount,offCont));
//    }

    public static <T> CustomResponse<T>  ok(CommonMessageEnum commonMessage) {
        return new CustomResponse<>(true, HttpStatus.OK.value(), commonMessage.getDesc(), null);
    }


    public static <T> CustomResponse<T> ext(CustomResponse<T> response, T data) {
        return new CustomResponse<>(response.success, response.code, response.message, data);
    }

    public static <T> CustomResponse<T> msg(CommonMessageEnum commonMessage) {
        boolean success = commonMessage.getCode() == HttpStatus.OK;
        return new CustomResponse<>(success, commonMessage.getCode().value(), commonMessage.getDesc(), null);
    }

    public static <T> CustomResponse<T> fail(int CustomHttpStatusCode, String message) {
        return new CustomResponse<>(false, CustomHttpStatusCode, message, null);
    }

    public static <T> CustomResponse<T> fail(Exception e) {
        return new CustomResponse<>(false, HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
    }

    public static <T> CustomResponse<T> fail(HttpStatus httpStatus, Exception e) {
        return new CustomResponse<>(false, httpStatus.value(), e.getMessage(), null);
    }

    public static <T> CustomResponse<T> fail(CommonMessageEnum commonMessageEnum, String message) {
        return new CustomResponse<>(false, commonMessageEnum.getCode().value(), message, null);
    }

    public static <T> CustomResponse<T> fail(HttpStatus httpStatus, Exception e, String errorMessage) {
        return new CustomResponse<>(false, httpStatus.value(), errorMessage, null);
    }

    public static <T> CustomResponse<T> fail(CommonMessageEnum commonMessage, Exception e) {
        int code = commonMessage.getCode().value();

        String message = e instanceof BindException
                ? handleBindException((BindException) e)
                : e instanceof ConstraintViolationException
                ? handleConstraintViolationException((ConstraintViolationException) e)
                : commonMessage.getDesc();

        message += e.getMessage();
//        e.printStackTrace();

        return new CustomResponse<>(false, code, message, null);
    }

    private static String handleBindException(BindException bindException) {
        FieldError fieldError = bindException.getFieldError();
        String field = fieldError.getField();
        Object rejectedValueObj = fieldError.getRejectedValue();
        String rejectedValue = (rejectedValueObj != null) ? rejectedValueObj.toString() : "null";
        String code = fieldError.getCode();
        String keyValueString = String.format("[%s:%s]", field, rejectedValue);

        CommonMessageEnum commonMessage = getCommonMessageForCode(code);

        return keyValueString + commonMessage.getDesc();
    }

    private static String handleConstraintViolationException(ConstraintViolationException constraintException) {
        ConstraintViolation<?> constraintViolation = constraintException.getConstraintViolations().stream().findFirst().get();
        String propertyPath = constraintViolation.getPropertyPath().toString();
        String invalidValue = constraintViolation.getInvalidValue().toString();
        String keyValueString = String.format("[%s:%s]", propertyPath, invalidValue);
        return keyValueString + constraintViolation.getMessage();
    }

//    //데이터 베이스의 무결성을 위반했을 때 발생,
//    //익셉션 어드바이스에서 체크하지 말고, 엔티티 저장 전 데이터 체크를 통해 미리 예방할 것,
//    private static String handleViolationException(DataIntegrityViolationException dataIntegrityViolationException) {
//        String detailMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
//        return String.format("[Data integrity violation: %s]",detailMessage);
//    }
//
    private static CommonMessageEnum getCommonMessageForCode(String code) {
        return switch (code) {
            case "Size" -> CommonMessageEnum.FAIL_VALID_SIZE;
            case "Email" -> CommonMessageEnum.FAIL_VALID_EMAIL;
            case "NotBlank" -> CommonMessageEnum.FAIL_VALID_NOT_BLANK;
            case "NotNull" -> CommonMessageEnum.FAIL_VALID_NOT_NULL;
            default -> CommonMessageEnum.FAIL_VALID;
        };
    }
}
