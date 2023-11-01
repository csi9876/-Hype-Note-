package com.surf.editor.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(500, "서버에 문제가 생겼습니다."),
    ILLEGAL_ARGUMENT_EXCEPTION(400,"올바르지 않은 형식의 입력입니다."),
    METHOD_ARGUMENT_NOT_VALID_EXCEPTION(400,"올바르지 않은 형식입니다."),

    USER_NOT_FOUND(404,"해당 유저를 찾을 수 없습니다."),
    EDITOR_NOT_FOUND(404, "해당 게시글을 찾을 수 없습니다."),

    FAIL_CREATE_EDITOR(400,"게시글 생성에 실패 했습니다."),
    FAIL_WRITE_EDITOR(400,"게시글 작성에 실패 했습니다."),
    FAIL_DELETE_EDITOR(400,"게시글 삭제에 실패 했습니다."),
    FAIL_SEARCH_EDITOR(400,"게시글 검색에 실패 했습니다.");




    private int status;
    private String message;

    private static final Map<String, ErrorCode> messageMap = Collections.unmodifiableMap(
            Stream.of(values()).collect(
                    Collectors.toMap(ErrorCode::getMessage, Function.identity())));

    public static ErrorCode fromMessage(String message){
        return messageMap.get(message);
    }
}