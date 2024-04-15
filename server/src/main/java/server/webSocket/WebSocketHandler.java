package server.webSocket;

import chess.*;
import com.google.gson.Gson;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.MakeMove;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.Collection;


@WebSocket
public class WebSocketHandler {


    private final ConnectionManager connections = new ConnectionManager();
    public static Collection<ChessGame> createdGames;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        Connection conn = connections.getConnection(command.getAuthString(), session);
        if (conn == null) {
            if (new SQLAuthDAO().getAuth(command.getAuthString()) != null) {
                connections.add(command.getAuthString(), session);
            }
        }
        conn = connections.getConnection(command.getAuthString(), session);
        if (conn != null) {
            switch (command.getCommandType()) {
                // LOAD_GAME to root client and NOTIFICATION to everyone else
                case JOIN_PLAYER -> joinPlayer(message, conn);
                case JOIN_OBSERVER -> joinObserver(message, conn);
                // server verifies move validity
                // game boards updated & game updated in database
                // LOAD_GAME to everyone
                // NOTIFICATION to everyone except root client
                case MAKE_MOVE -> makeMove(message, conn);
                // update game to remove root client & update game in database
                // NOTIFICATION to everyone else that root client left
                case LEAVE -> leaveGame(command.getAuthString());
                // game over in server & update game in database
                // NOTIFICATION to everyone
                case RESIGN -> resignGame(command.getAuthString());
            }
        }
        else {
            connections.sendError("error: user not found.", session);
        }
     }

    public void joinPlayer(String message, Connection conn) throws Exception {
        JoinPlayer joinPlayer = new Gson().fromJson(message, JoinPlayer.class);
        String color = String.valueOf(joinPlayer.getPlayerColor());
        Integer gameID = joinPlayer.getGameID();
        GameData gameData = new SQLGameDAO().getGame(gameID);
        ChessGame game = gameData.game();
//        createdGames.add(game);
        ChessBoard board;
        if (game.getBoard() == null) {
            board = new ChessBoard();
            board.resetBoard();
            game.setBoard(board);
            game.setTeamTurn(ChessGame.TeamColor.WHITE);
        }
        var serverMessage = new LoadGame(game);
        String authToken = conn.authToken;
        connections.broadcast(authToken, serverMessage);
        AuthData authData = new SQLAuthDAO().getAuth(authToken);
        String username = authData.username();
        var serverNotification = new Notification(username + " has joined the game as " + color);
        connections.broadcast(authToken, serverNotification);
    }

    private void joinObserver(String message, Connection conn) throws IOException, DataAccessException {
        JoinObserver joinObserver = new Gson().fromJson(message, JoinObserver.class);
        Integer gameID = joinObserver.getGameID();
        GameData gameData = new SQLGameDAO().getGame(gameID);
        ChessGame game = gameData.game();
        ChessBoard board;
        if (game.getBoard() == null) {
            board = new ChessBoard();
            board.resetBoard();
            game.setBoard(board);
            game.setTeamTurn(ChessGame.TeamColor.WHITE);
        }
        var serverMessage = new LoadGame(game);
        String authToken = conn.authToken;
        connections.broadcast(authToken, serverMessage);
        AuthData authData = new SQLAuthDAO().getAuth(authToken);
        String username = authData.username();
        var serverNotification = new Notification(username + " is observing the game");
        connections.broadcast(authToken, serverNotification);
    }

    public void makeMove(String message, Connection conn) throws IOException, DataAccessException, InvalidMoveException {
        MakeMove makeMove = new Gson().fromJson(message, MakeMove.class);
        Integer gameID = makeMove.getGameID();
        ChessGame game = new SQLGameDAO().getGame(gameID).game();
        ChessMove move = makeMove.getMove();
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        if (game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)) {
            // mirror move
            startPosition = new ChessPosition(8-startPosition.getRow()+1, 8-startPosition.getColumn()+1);
        }
        String startString = getPositionString(startPosition.getRow(), startPosition.getColumn());
        String endString = getPositionString(endPosition.getRow(), endPosition.getColumn());
        String authToken = conn.authToken;
        AuthData authData = new SQLAuthDAO().getAuth(authToken);
        String username = authData.username();
        game.makeMove(move);
        var serverMessage = new LoadGame(game);
        connections.broadcastMakeMoveLoadGame(serverMessage);
        var serverNotification = new Notification(username + "moved from " + startString + " to " + endString);
        connections.broadcast(authToken, serverNotification);
    }

    public void leaveGame(String authToken)throws IOException {
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(authToken, serverMessage);
    }

    public void resignGame(String userName)throws IOException {
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast("", serverMessage);
    }

    private String getPositionString (int row, int col) {
        String letter = letter(col);
        String positionString;
        return letter + row;
    }

    private String letter (int col) {
        if (col == 8) {
            return "a";
        }
        if (col == 7) {
            return "b";
        }
        if (col == 6) {
            return "c";
        }
        if (col == 5) {
            return "d";
        }
        if (col == 4) {
            return "e";
        }
        if (col == 3) {
            return "f";
        }
        if (col == 2) {
            return "g";
        }
        if (col == 1) {
            return "h";
        }
        return null;
    }
}