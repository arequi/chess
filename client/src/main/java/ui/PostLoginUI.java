package ui;

import chess.ChessGame;
import model.response.CreateGameResponse;
import model.response.JoinGameResponse;
import model.response.ListGamesResponse;
import model.response.LogoutResponse;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_BISHOP;
import static ui.State.*;

public class PostLoginUI {
    private final ServerFacade server;
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static int numberTracker;
    private WebSocketFacade ws;
    private String serverUrl;
    private NotificationHandler notificationHandler;

    public PostLoginUI(String serverUrl, NotificationHandler notificationHandler) {
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
                case "create" -> createGame(Arrays.toString(params));
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> joinObserver(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> displayHelp();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String displayHelp () {
        String helpString =
                SET_TEXT_COLOR_BLUE + " create <NAME>" + SET_TEXT_COLOR_MAGENTA + " - a game"
                        + SET_TEXT_COLOR_BLUE + "\n list" + SET_TEXT_COLOR_MAGENTA + " - games"
                        + SET_TEXT_COLOR_BLUE + "\n join <ID> [WHITE|BLACK|<empty>]" + SET_TEXT_COLOR_MAGENTA + " - a game"
                        + SET_TEXT_COLOR_BLUE + "\n observe <ID>" + SET_TEXT_COLOR_MAGENTA + " - a game"
                        + SET_TEXT_COLOR_BLUE + "\n logout" + SET_TEXT_COLOR_MAGENTA + " - when you are done"
                        + SET_TEXT_COLOR_BLUE + "\n quit" +  SET_TEXT_COLOR_MAGENTA + " - playing chess"
                        + SET_TEXT_COLOR_BLUE + "\n help" + SET_TEXT_COLOR_MAGENTA + " - with possible commands";

        return helpString;

    }

    public String logout () throws ResponseException {
        LogoutResponse response = server.logout();
        if (response.message() == null) {
            return "Successfully logged out.";
        }
        throw new ResponseException(401, "Unable to log out");
    }

    public String createGame (String gameName) throws ResponseException {
        CreateGameResponse response = server.createGame(gameName);
        if (response.message() == null) {
            return "Successfully created game";
        }
        if (response.message().equals("Error: unauthorized")) {
            throw new ResponseException(401, "unauthorized");
        }
        if (response.message().equals("Error: bad request")) {
            throw new ResponseException(400, "Expected: <gameName>");
        }
        else {
            throw new ResponseException(500, "Unknown error");
        }
    }

    public String listGames () throws ResponseException {
        ListGamesResponse response = server.listGames();
        if (response.message() == null) {
            return String.valueOf(response.games());
        }
        throw new ResponseException(401, "unauthorized");
    }

    public String joinGame (String... params) throws ResponseException {
        int gameNum = Integer.parseInt(params[0]);
        String playerColorString = params[1];
        ChessGame.TeamColor playerColor;
        JoinGameResponse response = server.joinGame(gameNum, playerColorString);
        if (response.message() == null) {
            Repl.state = IN_GAME;
            if (playerColorString.equalsIgnoreCase("white")) {
                playerColor = ChessGame.TeamColor.WHITE;
                displayBoard("white");
            }
            else {
                playerColor = ChessGame.TeamColor.BLACK;
                displayBoard("black");
            }
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.joinPlayer(PreLoginUI.authToken, gameNum, playerColor);
            return "Successfully joined game.";
        }
        if (response.message().equals("Error: unauthorized")) {
            throw new ResponseException(401, "unauthorized");
        }
        if (response.message().equals("Error: bad request")) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (response.message().equals("Error: already taken")) {
            throw new ResponseException(403, "Error: already taken");
        }
        else {
            throw new ResponseException(500, "Unknown error");
        }
    }

    public String joinObserver (String... params) throws ResponseException {
        int gameNum = Integer.parseInt(params[0]);
        JoinGameResponse response = server.observeGame(gameNum);
        if (response.message() == null) {
            Repl.state = IN_GAME;
            displayBoard("white");
            return "Successfully observing game.";
        }
        if (response.message().equals("Error: unauthorized")) {
            throw new ResponseException(401, "unauthorized");
        }
        if (response.message().equals("Error: bad request")) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (response.message().equals("Error: already taken")) {
            throw new ResponseException(403, "Error: already taken");
        }
        else {
            throw new ResponseException(500, "Unknown error");
        }
    }

    public static void displayBoard(String boardColor) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawHeaders(out);
        drawChessBoard(out, boardColor);
        setBlack(out);
        out.println(EMPTY.repeat(10));
        out.print(SET_BG_COLOR_ORIGINAL);
        out.print(SET_TEXT_COLOR_WHITE);
    }
    private static void drawHeaders(PrintStream out) {
        setGrey(out);
        String[] topHeaders = {EMPTY + "h", EMPTY + " g", EMPTY + " f", EMPTY + " e",EMPTY + " d",EMPTY + " c",EMPTY + " b",EMPTY + " a "};
        out.print(" " + EMPTY);
         for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            printHeaderText(out, topHeaders[boardCol]);
        }
        setGrey(out);
        out.print(" " + EMPTY + " ");
        setBlack(out);
        out.println();
    }
    private static void printHeaderText(PrintStream out, String text) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(text);
    }
    private static void drawChessBoard(PrintStream out, String boardColor) {
        numberTracker = 0;
//        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES/2; ++boardRow) {
            drawRowOfSquares(out, boardColor);
//        }
        setGrey(out);
        out.print(EMPTY.repeat(21) + " ");
        setBlack(out);
        out.println();
    }
    private static void drawRowOfSquares(PrintStream out, String boardColor) {
        String[] sideHeaders = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 " , " 6 " ," 7 "," 8 " };
        for (int squareRow = 0; squareRow < BOARD_SIZE_IN_SQUARES/2; ++squareRow) {
            setGrey(out);
            printHeaderText(out, sideHeaders[numberTracker]);
            numberTracker++;
            if (boardColor.equals("white")) {
                if (numberTracker == 1) {
                    drawOddRow(out, "black", false);
                }
                else if (numberTracker == 7) {
                    drawOddRow(out, "white", true);
                }
                else {
                    drawOddRow(out, null, false);
                }
            }
            else if (boardColor.equals("black")) {
                if (numberTracker == 1) {
                    drawOddRow(out, "white", false);
                }
                else if (numberTracker == 7) {
                    drawOddRow(out, "black", true);
                }
                else {
                    drawOddRow(out, null, false);
                }
            }
            setGrey(out);
            printHeaderText(out, sideHeaders[numberTracker]);
            numberTracker++;
            if (boardColor.equals("white")) {
                if (numberTracker == 2) {
                    drawEvenRow(out, "black", true);
                }
                else if (numberTracker == 8) {
                    drawEvenRow(out, "white", false);
                }
                else {
                    drawEvenRow(out, null, false);
                }
            }
            else if (boardColor.equals("black")) {
                if (numberTracker == 2) {
                    drawEvenRow(out, "white", true);
                }
                else if (numberTracker == 8) {
                    drawEvenRow(out, "black", false);
                }
                else {
                    drawEvenRow(out, null, false);
                }
            }
        }
    }
    private static void drawOddRow (PrintStream out, String pieceColor, boolean isPawn) {
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES/2; ++boardCol) {
            if (pieceColor == null) {
                setMagenta(out);
                out.print(" " + EMPTY + " ");
                setLightGrey(out);
                out.print(" " + EMPTY + " ");
            }
            else {
                setMagenta(out);
                printPlayer(out, pieceColor, isPawn, (boardCol*2));
                setLightGrey(out);
                printPlayer(out, pieceColor, isPawn, (boardCol*2)+1);
            }
        }
        setGrey(out);
        out.print(" " + EMPTY + " ");
        setBlack(out);
        out.println();
    }
    private static void drawEvenRow(PrintStream out, String pieceColor, boolean isPawn) {
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES/2; ++boardCol) {
            if (pieceColor == null) {
                setLightGrey(out);
                out.print(" " + EMPTY + " ");
                setMagenta(out);
                out.print(" " + EMPTY + " ");
            }
            else {
                setLightGrey(out);
                printPlayer(out, pieceColor, isPawn, (boardCol*2));
                setMagenta(out);
                printPlayer(out, pieceColor, isPawn, (boardCol*2)+1);
            }
        }
        setGrey(out);
        out.print(" " + EMPTY + " ");
        setBlack(out);
        out.println();
    }
    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
    private static void setMagenta(PrintStream out) {
        out.print(SET_BG_COLOR_MAGENTA);
        out.print(SET_TEXT_COLOR_MAGENTA);
    }
    private static void setLightGrey(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }
    private static void setGrey(PrintStream out) {
        out.print(SET_TEXT_COLOR_DARK_GREY);
        out.print(SET_BG_COLOR_DARK_GREY);
    }
    private static void printPlayer(PrintStream out, String pieceColor, boolean isPawn, int column) {
        String[] whitePieces = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
        String[] blackPieces = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        out.print(SET_TEXT_COLOR_BLACK);
        if (pieceColor.equals("white")) {
            out.print(SET_TEXT_COLOR_WHITE);
            if (isPawn) {
                out.print(" " + WHITE_PAWN + " ");
            }
            else {
                out.print(" " + whitePieces[column] + " ");
            }
        }
        else {
            out.print(SET_TEXT_COLOR_BLACK);
            if (isPawn) {
                out.print(" " + BLACK_PAWN + " ");
            }
            else {
                out.print(" " + blackPieces[column] + " ");
            }
        }
    }

}


