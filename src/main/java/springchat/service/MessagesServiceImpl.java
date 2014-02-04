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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springchat.data.jpa.MessageEntity;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Service
public class MessagesServiceImpl implements MessagesService {

    @Autowired
    private BaseEAO eao;

    @Autowired
    private JmsOperations jmsTemplate;

    @Override
    @Secured({"ROLE_CHATUSER"})
    public void postMessage(final String message) {
        jmsTemplate.send("java:comp/env/NewTextMessageChannel", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                Message notification = session.createMessage();
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                notification.setStringProperty("user", auth.getName());
                notification.setLongProperty("date", new Date().getTime());
                notification.setStringProperty("message", message);
                return notification;
            }
        });
    }

    @Override
    @Transactional
    public MessageEntity save(long date, String user, String message) {
        final MessageEntity bean = new MessageEntity();
        bean.setDate(new Date(date));
        bean.setUser(user);
        bean.setMessage(message);
        eao.execute(new BaseEAO.Executable<Void>() {
            @Override
            public Void execute(EntityManager em) {
                em.persist(bean);
                return null;
            }
        });
        return bean;
    }

    @Override
    public List<MessageEntity> getMessages() {
        return eao.withQuery("SELECT e FROM " + MessageEntity.class.getName() + " e",
                new BaseEAO.ExecutableQuery<List<MessageEntity>>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List<MessageEntity> execute(Query query) {
                        query.setMaxResults(MESSAGES_LIMIT);
                        return query.getResultList();
                    }
                });
    }

}
