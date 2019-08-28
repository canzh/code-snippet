// Standard web socket server: JSR-356
@ServerEndpoint( 
  value="/chat/{username}", 
  decoders = MessageDecoder.class, 
  encoders = MessageEncoder.class )
public class ChatEndpoint {
  
    private Session session;
    private static Set<ChatEndpoint> chatEndpoints 
      = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();
 
    @OnOpen
    public void onOpen(
      Session session, 
      @PathParam("username") String username) throws IOException {
  
        this.session = session;
        chatEndpoints.add(this);
        users.put(session.getId(), username);
 
        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected!");
        broadcast(message);
    }
 
    @OnMessage
    public void onMessage(Session session, Message message) 
      throws IOException {
  
        message.setFrom(users.get(session.getId()));
        broadcast(message);
    }
 
    @OnClose
    public void onClose(Session session) throws IOException {
  
        chatEndpoints.remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }
 
    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }
 
    private static void broadcast(Message message) 
      throws IOException, EncodeException {
  
        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                      sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

// SockJS server
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(myHandler(), "/myHandler");
	}

	@Bean
	public WebSocketHandler myHandler() {
		return new MyHandler();
	}
}

public class MyHandler extends TextWebSocketHandler {

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		// ...
	}
}

// STOMP server
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketServerConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/nitro/events");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/mywebsocket").setAllowedOrigins("*").withSockJS();
    }
}

@Controller
public class EventController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("events")
    @SendTo("/nitro/events")
    public EventEnvelope events(@Payload String message) {

        // another way to send message to client, other than @SendTo
        // this.simpMessagingTemplate.convertAndSend("/topic/events", message);

        return new EventChatroomPostEnvelope();
    }
}