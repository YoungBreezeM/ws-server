package com.yqf.ws;

import lombok.Data;

import javax.websocket.Session;

/**
 * @author yqf
 * @date 2021/2/1 下午9:23
 */
@Data
public class User {
    private String nickName;
    private String avatarUrl;
    private String joinRoom;

}
