package com.yqf.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.tools.ant.taskdefs.Tar;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yqf
 */

public enum Strategy {

    //创建播放厅
    CreateRoom(Target.CREATE_ROOM) {
        @Override
        void run(ConcurrentHashMap<String, Room> rooms,Object data,ConcurrentHashMap<String, Session> map) throws IOException {
            User user = JSONObject.parseObject(data.toString(), User.class);
            System.out.println(user.getNickName()+"------>创建房间");
            ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
            users.put(user.getNickName(),user);
            Room room = new Room();
            room.setUsers(users);
            room.setName(user.getNickName());
            rooms.put(user.getNickName(),room);


            for (String s : map.keySet()) {
                map.get(s).getBasicRemote()
                        .sendText(JSON.toJSONString(
                                Result.success(Target.ROOMS,new ArrayList<>(rooms.keySet()))
                        ));
            }
        }
    },
    JoinRoom(Target.JOIN_ROOM){
        @Override
        void run(ConcurrentHashMap<String, Room> rooms, Object data, ConcurrentHashMap<String, Session> map) throws IOException {
            User user = JSONObject.parseObject(data.toString(), User.class);
            Room room = rooms.get(user.getJoinRoom());
            ConcurrentMap<String, User> users = room.getUsers();
            if(!user.getJoinRoom().equals(user.getNickName())){
                System.out.println(user.getNickName()+"----->加入房间");
                users.put(user.getNickName(),user);

            }
            //给新进用户视频
            Session newJoinUser = map.get(user.getNickName());
            Video video = room.getVideo();
            newJoinUser.getBasicRemote()
                    .sendText(JSONObject.toJSONString(
                            Result.success(Target.SET_VIDEO,video)
                    ));
            for (String s : users.keySet()) {
                Session session = map.get(s);
                if(session!=null){
                    session
                            .getBasicRemote()
                            .sendText(JSON.toJSONString(
                                    Result.success(Target.USES,users)
                            ));

                }
            }
        }
    },
    ExitRoom(Target.EXIT_ROOM){
        @Override
        void run(ConcurrentHashMap<String, Room> rooms, Object data, ConcurrentHashMap<String, Session> map) throws IOException {
            User user = JSONObject.parseObject(data.toString(), User.class);
            ConcurrentMap<String, User> users = rooms.get(user.getJoinRoom()).getUsers();
            if(!user.getNickName().equals(user.getJoinRoom())){
                users.remove(user.getNickName());
            }
            for (String s : users.keySet()) {
                Session session = map.get(s);
                if(session!=null){
                    session
                            .getBasicRemote()
                            .sendText(JSON.toJSONString(
                                    Result.success(Target.USES,users)
                            ));
                }

            }
            System.out.println(user.getNickName()+"退出房间");
        }
    },
    Rooms(Target.ROOMS){
        @Override
        void run(ConcurrentHashMap<String, Room> rooms, Object data, ConcurrentHashMap<String, Session> map) throws IOException {
            for (String s : map.keySet()) {
                Session session = map.get(s);
                if(session!=null){
                    session.getBasicRemote()
                            .sendText(JSON.toJSONString(
                                    Result.success(Target.ROOMS,new ArrayList<>(rooms.keySet()))
                            ));
                }
            }
        }
    },
    SetVideo(Target.SET_VIDEO){
        @Override
        void run(ConcurrentHashMap<String, Room> rooms, Object data, ConcurrentHashMap<String, Session> map) throws IOException {
            Room room = JSONObject.parseObject(data.toString(), Room.class);
            Room rs = rooms.get(room.getName());
            rs.setVideo(room.getVideo());
            ConcurrentMap<String, User> users = rs.getUsers();
            for (String s : users.keySet()) {
                Session session = map.get(s);
                if(session!=null){
                    session.getBasicRemote()
                            .sendText(JSONObject.toJSONString(
                                    Result.success(Target.SET_VIDEO,rs.getVideo())
                            ));
                }
            }

            System.out.println(room);
        }
    }
    ;

    abstract void run(ConcurrentHashMap<String, Room> rooms, Object data, ConcurrentHashMap<String, Session> map) throws IOException;

    public String action;

    Strategy(String statusCode) {
        this.action = statusCode;
    }

}