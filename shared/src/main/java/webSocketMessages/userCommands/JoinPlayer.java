package webSocketMessages.userCommands;


import chess.ChessGame;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.JOIN_PLAYER;

public class JoinPlayer extends UserGameCommand{
    Integer gameID;
    ChessGame.TeamColor playerColor;
    public JoinPlayer(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.commandType = JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public Integer getGameID () {
        return this.gameID;
    }
    public ChessGame.TeamColor getPlayerColor() {
        return this.playerColor;
    }
}
