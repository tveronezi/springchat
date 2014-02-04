package springchat.test;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.*;
import springchat.service.ConnectionsService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ConnectionsServiceTest {

    @Autowired
    private ConnectionsService connectionsService;

    private static WebSocketMessage<?> receiverdMessage;

    @Test
    public void testConnections() throws IOException {
        WebSocketSession session = EasyMock.createNiceMock(WebSocketSession.class);
        connectionsService.addSession(session);

        session.sendMessage(EasyMock.anyObject(TextMessage.class));
        EasyMock.expectLastCall().andDelegateTo(new WebSocketSession() {
            @Override
            public String getId() {
                throw new RuntimeException();
            }

            @Override
            public URI getUri() {
                throw new RuntimeException();
            }

            @Override
            public HttpHeaders getHandshakeHeaders() {
                throw new RuntimeException();
            }

            @Override
            public Map<String, Object> getHandshakeAttributes() {
                throw new RuntimeException();
            }

            @Override
            public Principal getPrincipal() {
                throw new RuntimeException();
            }

            @Override
            public InetSocketAddress getLocalAddress() {
                throw new RuntimeException();
            }

            @Override
            public InetSocketAddress getRemoteAddress() {
                throw new RuntimeException();
            }

            @Override
            public String getAcceptedProtocol() {
                throw new RuntimeException();
            }

            @Override
            public List<WebSocketExtension> getExtensions() {
                throw new RuntimeException();
            }

            @Override
            public boolean isOpen() {
                throw new RuntimeException();
            }

            @Override
            public void sendMessage(WebSocketMessage<?> webSocketMessage) throws IOException {
                receiverdMessage = webSocketMessage;
            }

            @Override
            public void close() throws IOException {
                throw new RuntimeException();
            }

            @Override
            public void close(CloseStatus closeStatus) throws IOException {
                throw new RuntimeException();
            }
        });
        EasyMock.replay(session);
        connectionsService.sendToAll("test", "Hi there, session!");
        EasyMock.verify(session);

        connectionsService.removeSession(session);
        Assert.assertTrue(((TextMessage) receiverdMessage).getPayload().contains("Hi there, session!"));
    }
}
