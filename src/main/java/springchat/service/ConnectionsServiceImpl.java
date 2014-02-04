/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package springchat.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Service
public class ConnectionsServiceImpl implements ConnectionsService {
    private static Logger log = LoggerFactory.getLogger(ConnectionsServiceImpl.class);
    private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<WebSocketSession>());
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    @Override
    public void sendToAll(String typeName, Object data) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", typeName);
        map.put("data", data);
        final String messageJson = gson.toJson(map);
        for (WebSocketSession it : sessions) {
            try {
                it.sendMessage(new TextMessage(messageJson));
            } catch (Exception e) {
                log.error("Session unreachable", e);
            }
        }
    }

}
