package webSocketMessages.serverMessages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;
    private final String playerColor;
    public LoadGameMessage(ChessGame game, String playerColor) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.playerColor = playerColor;
    }

    public ChessGame getGame() {
        return this.game;
    }

    public String getPlayerColor () {
        return this.playerColor;
    }
}
