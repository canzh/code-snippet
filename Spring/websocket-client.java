// standard ws client: JSR-356
@ClientEndpoint
public class WsClient {
    Logger logger = LoggerFactory.getLogger("WsClient");

    private TokenManager tokenManager;
    private Session userSession;
    private ObjectMapper objectMapper = new ObjectMapper();

    public WsClient(URI wsUrl, TokenManager tokenManager) {
        this.tokenManager = tokenManager;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(this, wsUrl);
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        logger.info("OnOpen");

        this.userSession = session;

        NitroCommandPayload payload = new NitroCommandPayload();
        payload.setStsToken(tokenManager.getToken());

        NitroCommandEnvelope envelope = new NitroCommandEnvelope();
        envelope.setCommand("connect");
        envelope.setReqId(UUID.randomUUID().toString());
        envelope.setPayload(payload);

        try {
            session.getAsyncRemote().sendText(objectMapper.writeValueAsString(envelope));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        logger.info("OnClose");
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("OnMessage");
        logger.info(message);
    }

    @Scheduled(fixedRate = 4 * 60 * 1000, initialDelay = 4 * 60 * 1000)
    public void refreshAuth() {
        if (this.userSession != null) {
            logger.info("refresh auto token");

            NitroCommandPayload payload = new NitroCommandPayload();
            payload.setStsToken(tokenManager.getToken());

            NitroCommandEnvelope envelope = new NitroCommandEnvelope();
            envelope.setCommand("authenticate");
            envelope.setReqId(UUID.randomUUID().toString());
            envelope.setPayload(payload);

            this.userSession.getAsyncRemote().sendObject(envelope);
        }
    }
}

// SockJS client
public class SockJSClient {

    // Spring SockJs sample
    public void init() {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        // JsonFactory jsonFactory = new MappingJsonFactory(new ObjectMapper());

        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setHttpHeaderNames("X-Auth-Token");
        sockJsClient.doHandshake(new MyWebSocketHandler(), "ws://example.com:8080/sockjs");
    }
}


// STOMP client
public class StompClient {
    private String wsUrl;

    @Autowired
    private WebSocketStompClient stompClient;

    public StompClient(String wsUrl) {
        this.wsUrl = wsUrl;

        // STOMP client sample, another way other than Autowired
        // WebSocketClient client = new StandardWebSocketClient();
        // WebSocketStompClient stompClient = new WebSocketStompClient(client);
        // stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        // StompSessionHandler sessionHandler = new NitroStompSessionHandler();
        // StompSession stompSession = stompClient.connect(wsUrl, sessionHandler).get();

        // stompSession.send("topic/greetings", "Hello new user");

        // stompSession.subscribe("topic/greetings", sessionHandler);
    }
}

public class NitroStompSessionHandler extends StompSessionHandlerAdapter {
    private Logger logger = LogManager.getLogger(NitroStompSessionHandler.class);

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("New session established : " + session.getSessionId());
        session.subscribe("/topic/messages", this);
        logger.info("Subscribed to /topic/messages");
        session.send("/app/chat", null);
        logger.info("Message sent to websocket server");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Got an exception", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return EventEnvelope.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        EventEnvelope msg = (EventEnvelope) payload;
//        logger.info("Received : " + msg.getText() + " from : " + msg.getFrom());
    }
}