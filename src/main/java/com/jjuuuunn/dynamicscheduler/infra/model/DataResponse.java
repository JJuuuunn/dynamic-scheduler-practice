package com.jjuuuunn.dynamicscheduler.infra.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DataResponse<T>(T results, String nextPageKey, Integer totalCount) {

    public static <T> DataResponse<T> list(T data, Integer totalCount) {
        return new DataResponse<>(data, null, totalCount);
    }

    public static <T> DataResponse<T> list(T data, String nextPageKey) {
        return new DataResponse<>(data, nextPageKey, null);
    }

    public static <T> DataResponse<T> object(T data) {
        return new DataResponse<>(data, null, null);
    }


}
