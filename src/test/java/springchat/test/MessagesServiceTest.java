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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springchat.data.jpa.MessageEntity;
import springchat.service.BaseEAO;
import springchat.service.MessagesService;

import javax.persistence.Query;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MessagesServiceTest {

    @Autowired
    private MessagesService messages;

    @Autowired
    private BaseEAO eao;

    @Test
    public void testPersistence() {
        List<MessageEntity> msgs = messages.getMessages();
        final Long uid = messages.save(System.currentTimeMillis(), "botossauro", "Hi there!").getUid();
        Assert.assertNotNull(uid);
        Assert.assertEquals("botossauro", eao.withQuery("SELECT e FROM MessageEntity e WHERE e.uid = :puid",
                new BaseEAO.ExecutableQuery<MessageEntity>() {
                    @Override
                    public MessageEntity execute(Query query) {
                        query.setParameter("puid", uid);
                        return (MessageEntity) query.getSingleResult();
                    }
                }).getUser()
        );
        Assert.assertTrue(messages.getMessages().size() == msgs.size() + 1);
    }

}
