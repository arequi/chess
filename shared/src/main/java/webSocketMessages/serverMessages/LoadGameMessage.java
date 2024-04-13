package webSocketMessages.serverMessages;

import chess.ChessGame;
import model.GameData;

public class LoadGameMessage extends ServerMessage {
    ChessGame game;
    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = game;
    }

    public ChessGame getGame() {
        return this.game;
    }
}
