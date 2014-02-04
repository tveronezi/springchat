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

package springchat.jms;

import org.springframework.beans.factory.annotation.Autowired;
import springchat.UnmanagedException;
import springchat.data.dto.MessageDto;
import springchat.data.jpa.MessageEntity;
import springchat.service.ConnectionsService;
import springchat.service.MessagesService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class NewTextMessage implements MessageListener {

    @Autowired
    private ConnectionsService connections;

    @Autowired
    private MessagesService messagesService;

    @Override
    public void onMessage(Message jms) {
        MessageDto dto = new MessageDto();
        try {
            dto.setContent(jms.getStringProperty("message"));
            dto.setFrom(jms.getStringProperty("user"));
            dto.setTimestamp(jms.getLongProperty("date"));
        } catch (JMSException e) {
            throw new UnmanagedException(e);
        }
        MessageEntity bean = messagesService.save(dto.getTimestamp(), dto.getFrom(), dto.getContent());
        dto.setId(bean.getUid());
        connections.sendToAll("text", dto);
    }

}
