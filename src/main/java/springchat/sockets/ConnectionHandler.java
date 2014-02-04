package springchat.sockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import springchat.service.ConnectionsService;

public class ConnectionHandler extends TextWebSocketHandler {

    @Autowired
    private ConnectionsService service;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        service.addSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        service.removeSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        service.removeSession(session);
    }
}
