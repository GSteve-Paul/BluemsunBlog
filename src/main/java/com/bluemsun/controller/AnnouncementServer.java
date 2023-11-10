package com.bluemsun.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
@Scope("prototype")
@ServerEndpoint("/websocket/announce")
public class AnnouncementServer
{
    //clients
    private static CopyOnWriteArraySet<AnnouncementServer> webSocketSet = new CopyOnWriteArraySet<>();

    //session of client
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        log.info("session " + session.getId() + " connected" );
        try {
            sendMessage("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        log.info("one connection is closed");
    }

    @OnMessage
    public void onMessage(String message,Session session) {
        if("heart".equals(message)) {
            try {
                sendMessage("ok");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session,Throwable error) {
        log.error("error occurs");
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public void announce(String message) {
        for(AnnouncementServer item: webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
