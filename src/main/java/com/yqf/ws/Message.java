package com.yqf.ws;

import lombok.Data;

/**
 * @author yqf
 * @date 2021/2/1 下午9:25
 */
@Data
public class Message {
    private String content;
    private String type;
    private User sender;
    private User receiver;
    private String time;
}
