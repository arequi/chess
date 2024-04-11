package webSocketMessages.serverMessages;

public class LoadGame extends ServerMessage {
    public LoadGame(ServerMessageType type) {
        super(type);
        this.serverMessageType = ServerMessageType.LOAD_GAME;
    }
}
