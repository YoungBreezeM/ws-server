package com.yqf.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import netscape.javascript.JSObject;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yqf
 */
@Controller
@ServerEndpoint("/ws/{username}")
public class GroupChatController {
    /**
     * 保存 聊天在线链接 -> 聊天室成员 的映射关系
     */
    private static ConcurrentHashMap<String, Session> map = new ConcurrentHashMap<>();
    /**
     * 保存播放厅的管理
     */
    private static ConcurrentHashMap<String, Room> playRoom = new ConcurrentHashMap<>();
    List<String> rooms;

    /**
     * 收到消息调用的方法，群成员发送消息
     */
    @OnMessage
    public void onMessage(@PathParam("username") String username, String message) throws IOException {
        Result result = JSONObject.parseObject(message, Result.class);
        Strategy strategy = Strategy.valueOf(result.getTarget());
        strategy.run(playRoom,result.getData(),map);
    }

    /**
     * 建立连接调用的方法，返回在线人员
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        map.put(username, session);

        System.out.println("成员---- 在线：" + username + "  当前在线人数：" + map.size());

        List<String> onLineUsers = new ArrayList<>(map.keySet());
        rooms = new ArrayList<>(playRoom.keySet());
        for (String s : map.keySet()) {
            Session user = map.get(s);
            user.getBasicRemote().sendText(
                    JSON.toJSONString(
                            Result.success(Target.ONLINE_USERS, onLineUsers)
                    ));
            user.getBasicRemote().sendText(
                    JSON.toJSONString(
                            Result.success(Target.ROOMS, rooms)
                    ));
        }


    }

    /**
     * 关闭连接调用的方法，群成员退出
     */
    @OnClose
    public void onClose(@PathParam("username") String username) throws IOException {
        map.remove(username);
        playRoom.remove(username);
        rooms = new ArrayList<>(playRoom.keySet());

        List<String> onLineUsers = new ArrayList<>(map.keySet());
        for (String s : map.keySet()) {
            Session user = map.get(s);
            user.getBasicRemote().sendText(
                    JSON.toJSONString(
                            Result.success(Target.ONLINE_USERS, onLineUsers)
                    ));
            user.getBasicRemote().sendText(
                    JSON.toJSONString(
                            Result.success(Target.ROOMS, rooms)
                    ));
        }
        System.out.println("成员退出---- ：" + username + "  当前在线人数：" + map.size());
    }

    /**
     * 传输消息错误调用的方法
     */
    @OnError
    public void OnError(Throwable error) throws Throwable {
        System.out.println("消息传递出错");
        throw error;
    }
}