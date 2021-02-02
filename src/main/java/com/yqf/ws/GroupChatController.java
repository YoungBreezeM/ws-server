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
@ServerEndpoint("/groupChat/{username}")
public class GroupChatController {
    /** 保存 聊天在线链接 -> 聊天室成员 的映射关系*/
    private static ConcurrentHashMap<String, Session> map = new ConcurrentHashMap<>();
    /** 保存聊天记录*/
    private static List<String> chatRecord = new ArrayList<>();
 
    /**收到消息调用的方法，群成员发送消息*/
    @OnMessage
    public void onMessage(@PathParam("username") String username, String message) {
        System.out.println(message);
        Session sender = map.get(username);
        Message res = JSONObject.parseObject(message, Message.class);
        Session receiver = map.get(res.getReceiver().getNickName());
        // 分别向聊天室的成员发送消息
        try {
            sender.getBasicRemote().sendText(message);
            receiver.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**建立连接调用的方法，返回在线人员*/
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException{
        map.put(username,session);
        List<String> onLineUsers = new ArrayList<>(map.keySet());
        session.getBasicRemote().sendText(JSON.toJSONString(onLineUsers));

        System.out.println("成员加入---- 聊天：" + username + "  当前聊天人数：" + map.size());
    }
 
    /**关闭连接调用的方法，群成员退出*/
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        map.remove(username);
        System.out.println("成员退出---- 聊天室号：" + username + "  当前聊天室人数：" + map.size());
    }
 
    /**传输消息错误调用的方法*/
    @OnError
    public void OnError(Throwable error) {
        System.out.println("消息传递出错");
    }
}