package com.sevenstars.roome.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Result {

    // HTTP Status Code
    SUCCESS(200, "API 요청이 성공했습니다."),
    FAIL(400, "불가능한 요청입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    ERROR(500, "에러가 발생했습니다."),

    // Common
    INVALID_TOKEN(1000, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(1001, "만료된 토큰입니다."),
    PUBLIC_KEY_NOT_FOUND(1002, "공개 키를 찾을 수 없습니다."),
    PROVIDER_NOT_FOUND(1003, "유효하지 않은 Provider ID 입니다."),
    PROVIDER_INVALID_RESPONSE(1004, "Provider의 응답이 유효하지 않습니다."),
    PUBLIC_KEY_UPDATE_FAIL(1005, "공개 키 업데이트에 실패했습니다."),
    REFRESH_TOKEN_NOT_FOUND(1006, "리프레시 토큰을 찾을 수 없습니다."),
    AUTHORIZATION_CODE_AND_ID_TOKEN_EMPTY(1007, "인가코드와 토큰이 모두 비어있습니다."),
    NULL(1008, "파라미터에 NULL이 포함되어있습니다."),

    // User
    USER_NOT_FOUND(2000, "사용자를 찾을 수 없습니다."),
    USER_AGE_NOT_OVER_FOURTEEN(2001, "만 14세 이상이 아닙니다."),
    USER_DOES_NOT_AGREE_TERMS_OF_SERVICE(2002, "서비스 이용약관에 동의하지 않았습니다."),
    USER_DOES_NOT_AGREE_PERSONAL_INFO(2003, "개인정보 이용 및 수집 동의에 동의하지 않았습니다."),
    USER_NICKNAME_NOT_ALLOWED(2004, "허용하지 않는 문자가 포함되어 있습니다."),
    USER_NICKNAME_ALREADY_USING(2005, "이미 사용중인 닉네임입니다."),
    USER_NICKNAME_EMPTY(2006, "빈 문자열입니다."),
    USER_NICKNAME_CAN_CONTAIN_KOREAN_ENGLISH_OR_NUMBERS(2007, "한글/영문/숫자만 입력 가능합니다."),
    USER_NICKNAME_LENGTH_EXCEEDED(2008, "닉네임은 8자를 초과할 수 없습니다."),
    USER_TERMS_AGREEMENT_NOT_FOUND(2009, "사용자 약관 동의를 찾을 수 없습니다."),

    // Profile
    PROFILE_NOT_FOUND(3000, "프로필을 찾을 수 없습니다.");

    private final int code;
    private final String message;
}
