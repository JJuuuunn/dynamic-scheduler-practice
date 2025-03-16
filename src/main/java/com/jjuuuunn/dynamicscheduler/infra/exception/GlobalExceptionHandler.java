package com.jjuuuunn.dynamicscheduler.infra.exception;

import com.jjuuuunn.dynamicscheduler.infra.model.CustomResponse;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.jjuuuunn.dynamicscheduler.infra.model.CommonMessageEnum.*;
import static com.jjuuuunn.dynamicscheduler.infra.model.CustomResponse.fail;
import static org.springframework.http.HttpStatus.BAD_REQUEST;



@Slf4j(topic = "ERROR.ADVICE")
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public CustomResponse<String> customException(Exception e) {return fail(e);}

    //JPA 영속화 전 미리 체크할 것
    @ExceptionHandler(DataIntegrityViolationException.class)
    public CustomResponse<String> dataIntegrityViolationException(DataIntegrityViolationException e) {return fail(FAIL_VALID,e);}

    @ExceptionHandler(DuplicateKeyException.class)
    public CustomResponse<String> duplicateKeyException(DuplicateKeyException e) {return fail(FAIL_DUPLICATE,e);}

    @ExceptionHandler(NoResultException.class)
    public CustomResponse<String> noResultException(NoResultException e) {return fail(FAIL_NO_RESULT, e); }

    @ExceptionHandler(IllegalArgumentException.class)
    public CustomResponse<String> illegalArgumentException(IllegalArgumentException e) {return fail(FAIL_VALID_NOT_NULL, e); }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CustomResponse<String> MethodArgumentNotValidException(MethodArgumentNotValidException  e) {
        StringBuilder errorMessage = new StringBuilder();
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getField()).append(" 는").append(fieldError.getDefaultMessage()).append(" \n ");
        }

        return fail(BAD_REQUEST, e, errorMessage.toString());
    }
}
