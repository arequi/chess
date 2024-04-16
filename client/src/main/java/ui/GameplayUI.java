package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.response.JoinGameResponse;
import model.response.LoginResponse;
import model.response.RegisterResponse;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import webSocketMessages.serverMessages.Notification;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.PostLoginUI.currentGameID;
import static ui.PostLoginUI.displayBoard;
import static ui.State.IN_GAME;
import static ui.State.LOGGED_IN;

public class GameplayUI {
    private final ServerFacade server;
    String serverUrl;
    NotificationHandler notificationHandler;
    WebSocketFacade ws = new WebSocketFacade(serverUrl, notificationHandler);
    public GameplayUI(String serverUrl, NotificationHandler notificationHandler) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }



    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "highlight" -> highlight(params);
                case "redraw" -> redraw(params);
                case "move" -> move(params);
                case "resign" -> resign();
                case "leave" -> leave();
                default -> displayHelp();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String displayHelp () {
        String helpString =
                " highlight" + SET_TEXT_COLOR_MAGENTA + " - legal moves"
                        + SET_TEXT_COLOR_BLUE + "\n redraw <WHITE|BLACK|OBSERVER>" + SET_TEXT_COLOR_MAGENTA + " - chessboard"
                        + SET_TEXT_COLOR_BLUE + "\n move <START_POSITION> <END_POSITION> <PROMOTION_PIECE|NONE>" +
                        SET_TEXT_COLOR_MAGENTA + " - a chess piece"
                        + SET_TEXT_COLOR_BLUE + "\n resign" + SET_TEXT_COLOR_MAGENTA + " - from game"
                        + SET_TEXT_COLOR_BLUE + "\n leave" +  SET_TEXT_COLOR_MAGENTA + " - game"
                        + SET_TEXT_COLOR_BLUE + "\n help" + SET_TEXT_COLOR_MAGENTA + " - with possible commands";

        return helpString;
    }

    public String highlight (String... params) throws ResponseException {
        return "Successfully Highlighted moves";
    }

    public String redraw (String... params) throws ResponseException {

//        PostLoginUI.displayBoard(color, currentBoard);
        return "Current board displayed";
    }

    public String move (String... params) throws ResponseException {
        String startString = params[0];
        String endString = params[1];
        int startCol = getCol(startString);
        int startRow = getRow(startString);
        int endCol = getCol(endString);
        int endRow = getRow(endString);
        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        ChessPosition endPosition = new ChessPosition(endRow, endCol);
        ChessPiece.PieceType promotionPiece;
        if (params[2].equalsIgnoreCase("none")) {
            promotionPiece = null;
        }
        else {
            promotionPiece = getPromotionPiece(params[2]);
        }
        ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
        ws.makeMove(PreLoginUI.authToken, currentGameID, move);
        return "Successfully made a move";
    }

    public String resign () throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n" + RESET_TEXT_COLOR + "Are you sure you want to resign?");
        String line = scanner.nextLine();
        if (line.contains("y")) {
            Repl.state = State.LOGGED_IN;
            ws.resign(PreLoginUI.authToken, currentGameID);
            return "Successfully resigned";
        }
        return "Didn't actually want to resign";
    }

    public String leave () throws ResponseException {
        Repl.state = LOGGED_IN;
        ws.leave(PreLoginUI.authToken, currentGameID);
        return "Successfully Left Game";
    }

    private int getCol (String positionString) {
        if (positionString.contains("h")) {
            return 1;
        }
        if (positionString.contains("g")) {
            return 2;
        }
        if (positionString.contains("f")) {
            return 3;
        }
        if (positionString.contains("e")) {
            return 4;
        }
        if (positionString.contains("d")) {
            return 5;
        }
        if (positionString.contains("c")) {
            return 6;
        }
        if (positionString.contains("b")) {
            return 7;
        }
        if (positionString.contains("a")) {
            return 8;
        }
        return 0;
    }
    private int getRow (String positionString) {
        if (positionString.contains("1")) {
            return 1;
        }
        if (positionString.contains("2")) {
            return 2;
        }
        if (positionString.contains("3")) {
            return 3;
        }
        if (positionString.contains("4")) {
            return 4;
        }
        if (positionString.contains("5")) {
            return 5;
        }
        if (positionString.contains("6")) {
            return 6;
        }
        if (positionString.contains("7")) {
            return 7;
        }
        if (positionString.contains("8")) {
            return 8;
        }
        return 0;
    }

    private ChessPiece.PieceType getPromotionPiece (String promotionString) {
        if (promotionString.contains("knight")) {
            return ChessPiece.PieceType.KNIGHT;
        }
        if (promotionString.contains("bishop")) {
            return ChessPiece.PieceType.BISHOP;
        }
        if (promotionString.contains("rook")) {
            return ChessPiece.PieceType.ROOK;
        }
        if (promotionString.contains("queen")) {
            return ChessPiece.PieceType.QUEEN;
        }
        return null;
    }


}
