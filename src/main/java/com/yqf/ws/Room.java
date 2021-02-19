package com.yqf.ws;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yqf
 * @date 2021/2/18 上午11:51
 */
@Data
public class Room {
    private String name;
    private Video video;
    private ConcurrentMap<String,User> users;
}
