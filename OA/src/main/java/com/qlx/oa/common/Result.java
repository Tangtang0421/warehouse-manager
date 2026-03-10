package com.qlx.oa.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;//状态码
    private String msg;//提示信息
    private T data;//真正的数据，泛型<T>，里面可以装任何东西

    //成功
    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setMsg("success");
        return result;
    }

    //成功，并把查到的数据给前端
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    //失败，告诉前端哪里有问题
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
