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

package springchat.test;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.WebSocketSession;
import springchat.data.dto.MessageDto;
import springchat.data.jpa.MessageEntity;
import springchat.jms.NewTextMessage;
import springchat.service.ConnectionsService;
import springchat.service.MessagesService;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class NewTextMessageTest {

    @Autowired
    private NewTextMessage newTextMessage;

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private ConnectionsService connections;

    @Test
    public void testOnMessage() throws JMSException {
        final long uid = 10l;
        final String content = "Hi there!";
        final String user = "boto";
        final long timestamp = System.currentTimeMillis();

        Message jmsMessage = EasyMock.createNiceMock(Message.class);
        EasyMock.expect(jmsMessage.getStringProperty("message")).andReturn(content).once();
        EasyMock.expect(jmsMessage.getStringProperty("user")).andReturn(user).once();
        EasyMock.expect(jmsMessage.getLongProperty("date")).andReturn(timestamp).once();

        MessageEntity entityMessage = new MessageEntity();
        entityMessage.setUid(uid);
        entityMessage.setDate(new Date(timestamp));
        entityMessage.setMessage(content);
        entityMessage.setUser(user);

        EasyMock.expect(messagesService.save(timestamp, user, content)).andReturn(entityMessage).once();

        connections.sendToAll(EasyMock.anyObject(String.class), EasyMock.anyObject(MessageDto.class));
        EasyMock.expectLastCall().andDelegateTo(new ConnectionsService() {
            @Override
            public void addSession(WebSocketSession session) {
                Assert.fail();
            }

            @Override
            public void removeSession(WebSocketSession session) {
                Assert.fail();
            }

            @Override
            public void sendToAll(String typeName, Object data) {
                Assert.assertEquals(MessageDto.class, data.getClass());
                Assert.assertEquals(uid, MessageDto.class.cast(data).getId().longValue());
            }
        });

        EasyMock.replay(messagesService, connections, jmsMessage);
        newTextMessage.onMessage(jmsMessage);
        EasyMock.verify(messagesService, connections, jmsMessage);
    }

}
