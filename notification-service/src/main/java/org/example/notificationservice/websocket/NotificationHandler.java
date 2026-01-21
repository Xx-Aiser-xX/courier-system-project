package org.example.notificationservice.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(NotificationHandler.class);

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = getUserIdFromSession(session);

        if (userId != null) {
            sessions.put(userId, session);
            log.info("пользователь {} подключился, сессия: {}", userId, session.getId());
        }
        else {
            log.warn("подключение без userId, сессия: {}", session.getId());
            try {
                session.close(CloseStatus.BAD_DATA);
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.remove(userId);
            log.info("пользователь {} отключился", userId);
        }
    }

    public void sendToUser(String userId, String message) {
        WebSocketSession session = sessions.get(userId);

        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                log.info("сообщение отправлено пользователю {}", userId);
            } catch (IOException e) {
                log.error("ошибка отправки сообщения пользователю {}", userId, e);
            }
        }
        else {
            log.warn("пользователь {} не в сети, сообщение пропущено", userId);
        }
    }

    private String getUserIdFromSession(WebSocketSession session) {
        try {
            URI uri = session.getUri();
            if (uri == null || uri.getQuery() == null)
                return null;

            String query = uri.getQuery();
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1 && "userId".equals(pair[0]))
                    return pair[1];
            }
        } catch (Exception e) {
            log.error("ошибка парсинга URL", e);
        }
        return null;
    }
}