package com.yqf.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yqf
 * @date 2021/2/18 上午11:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private String target;
    private Object data;
    private Integer code;

    public static Result success(String target,Object data){
        return new Result(target,data,200);
    }

    public static Result fail(String target,Object data){
        return new Result(target,data,500);
    }
}
