package com.jjuuuunn.dynamicscheduler.infra.model;

import org.springframework.http.HttpStatus;

public enum CommonMessageEnum {

    REQUEST(HttpStatus.OK, "요청 성공", "요청이 성공적으로 처리되었습니다.")
    , REQUEST_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "요청실패", "요청이 실패 하였습니다.")
    , MODIFY(HttpStatus.OK, "수정", "수정이 완료되었습니다.")
    , SAVE(HttpStatus.OK, "등록", "등록이 완료되었습니다.")
    , DELETE(HttpStatus.OK, "삭제", "삭제가 완료되었습니다.")
    , AVAILABLE_USER_ID(HttpStatus.OK, "사용 가능한 아이디", "사용 가능한 아이디입니다.")
    , FAIL_VALID(HttpStatus.UNPROCESSABLE_ENTITY, "유효성검사 실패", "데이터가 적합하지 않습니다.")
    , FAIL_VALID_SIZE(HttpStatus.UNPROCESSABLE_ENTITY, "유효성검사 실패", "해당 데이터의 사이즈를 확인해주세요.")
    , FAIL_VALID_NOT_NULL(HttpStatus.UNPROCESSABLE_ENTITY, "유효성검사 실패", "해당 데이터는 Null 일수 없습니다.")
    , FAIL_VALID_NOT_BLANK(HttpStatus.UNPROCESSABLE_ENTITY, "유효성검사 실패", "해당 데이터는 공백 일수 없습니다.")
    , FAIL_VALID_EMAIL(HttpStatus.UNPROCESSABLE_ENTITY, "유효성검사 실패", "Email 형식이 아닙니다.")
    , FAIL_DUPLICATE(HttpStatus.CONFLICT, "데이터 중복", "해당 데이터가 이미 등록 되어있습니다.")
    , TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰 만료", "인증 유효기간이 만료 되었습니다.")
    , TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, "토큰 미존재", "인증 토큰이 존재하지 않습니다.")
    , TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰 미유효", "유효한 토큰이 아닙니다.")
    , TOKEN_INVALID_LOGGED_IN_ELSE_WHERE(HttpStatus.UNAUTHORIZED,"다른 기기 로그인", "다른 기기에 로그딘 되어 있습니다.")
    , FAIL_AUTH(HttpStatus.UNAUTHORIZED, "인증 실패", "인증이 유효하지 않습니다.")
    , FAIL_DENY(HttpStatus.FORBIDDEN, "권한 없음", "권한이 존재하지 않습니다..")
    , FAIL_LOGIN(HttpStatus.UNAUTHORIZED, "로그인 실패",
            "로그인 실패. \n " +
            "삭제 또는 등록 되지 않은 아이디거나, \n " +
            "아이디 또는 비밀번호를 잘못 입력하셨습니다."
                                                    )
    , FAIL_NO_RESULT(HttpStatus.NOT_FOUND, "데이터미존재", "해당 데이터가 존재하지 않습니다.")
    ;


    private HttpStatus code;
    private String title;
    private String desc;


    CommonMessageEnum(HttpStatus code, String title, String desc) {
        this.code = code;
        this.title = title;
        this.desc = desc;
    }

    public HttpStatus getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDesc() {
        return this.desc;
    }
}