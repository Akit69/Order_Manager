package com.info.entity;

import lombok.Data;

@Data
public class Result<T>{
    private Integer code;     // 状态码：200成功，其他失败
    private String message;   // 提示信息
    private T data;           // 返回数据

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result success() {
        return new Result(200, "操作成功", null);
    }

    public static <T> Result success(T data) {
        return new Result(200, "操作成功", data);
    }

    public static <T> Result success(String message, T data) {
        return new Result(200, message, data);
    }

    public static <T> Result error(String message) {
        return new Result(500, message, null);
    }

    public static <T> Result error(Integer code, String message) {
        return new Result(code, message, null);
    }


}
