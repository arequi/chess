package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.response.CreateGameResponse;
import model.response.JoinGameResponse;
import model.response.ListGamesResponse;
import model.response.LogoutResponse;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_BISHOP;
import static ui.State.*;

public class PostLoginUI {
    private final ServerFacade server;
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static int rowTracker;
    private static int columnTracker;
    private WebSocketFacade ws;
    private String serverUrl;
    private NotificationHandler notificationHandler;
    public static Integer currentGameID;

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
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        throw new ResponseException("Unable to log out");
    }

    public String createGame (String gameName) throws ResponseException {
        CreateGameResponse response = server.createGame(gameName);
        if (response.message() == null) {
            return "Successfully created game";
        }
        if (response.message().equals("Error: unauthorized")) {
            throw new ResponseException("unauthorized");
        }
        if (response.message().equals("Error: bad request")) {
            throw new ResponseException("Expected: <gameName>");
        }
        else {
            throw new ResponseException("Unknown error");
        }
    }

    public String listGames () throws ResponseException {
        ListGamesResponse response = server.listGames();
        if (response.message() == null) {
            return String.valueOf(response.games());
        }
        throw new ResponseException("unauthorized");
    }

    public String joinGame (String... params) throws Exception {
        int gameNum = Integer.parseInt(params[0]);
        String playerColorString = params[1];
        ChessGame.TeamColor playerColor;
        JoinGameResponse response = server.joinGame(gameNum, playerColorString);
        currentGameID = ServerFacade.gameIDs.get(gameNum);
        if (response.message() == null) {
            Repl.state = IN_GAME;
            if (playerColorString.equalsIgnoreCase("white")) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else {
                playerColor = ChessGame.TeamColor.BLACK;
            }
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            Integer gameID = ServerFacade.gameIDs.get(gameNum);
            ws.joinPlayer(PreLoginUI.authToken, gameID, playerColor);
            return "Successfully joined game.";
        }
        if (response.message().equals("Error: unauthorized")) {
            throw new ResponseException("unauthorized");
        }
        if (response.message().equals("Error: bad request")) {
            throw new ResponseException("Error: bad request");
        }
        if (response.message().equals("Error: already taken")) {
            throw new ResponseException("Error: already taken");
        } else {
            throw new ResponseException("Unknown error");
        }
    }

    public String joinObserver (String... params) throws ResponseException {
        int gameNum = Integer.parseInt(params[0]);
        JoinGameResponse response = server.observeGame(gameNum);
        if (response.message() == null) {
            Repl.state = IN_GAME;
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            Integer gameID = ServerFacade.gameIDs.get(gameNum);
            currentGameID = gameID;
            ws.joinObserver(PreLoginUI.authToken, gameID);
            return "Successfully observing game.";
        }
        if (response.message().equals("Error: unauthorized")) {
            throw new ResponseException("unauthorized");
        }
        if (response.message().equals("Error: bad request")) {
            throw new ResponseException("Error: bad request");
        }
        if (response.message().equals("Error: already taken")) {
            throw new ResponseException("Error: already taken");
        }
        else {
            throw new ResponseException("Unknown error");
        }
    }

    public static void displayBoard(String boardColor, ChessBoard currentBoard) {
        System.out.println();
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawHeaders(out);
        drawChessBoard(out, boardColor, currentBoard);
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
    private static void drawChessBoard(PrintStream out, String boardColor, ChessBoard currentBoard) {
        drawSquares(out, boardColor, currentBoard);
        setGrey(out);
        out.print(EMPTY.repeat(21) + " ");
        setBlack(out);
        out.println();
    }
    private static void drawSquares(PrintStream out, String boardColor, ChessBoard currentBoard) {
        String[] sideHeaders = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 " , " 6 " ," 7 "," 8 " };
        rowTracker = 0;
        boolean islightSquare = true;
        for (int i = 0; i < 8; i++) {
            setGrey(out);
            printHeaderText(out, sideHeaders[rowTracker]);
            rowTracker++;
            columnTracker = 0;
            for (int j = 0; j < 8; j++) {
                columnTracker++;
                ChessPosition position;
                // white on bottom (how real life chess game starts)
                if (boardColor.equalsIgnoreCase("white") || boardColor.equalsIgnoreCase("observer")) {
                    position = new ChessPosition(7-i+1, 7-j+1);
                }
                // black on bottom (how ChessBoard class resets normally)
                else {
                    position = new ChessPosition(i+1, j+1);
                }

                ChessPiece piece = currentBoard.getPiece(position);
                if (islightSquare) {
                    setLightGrey(out);
                }
                else {
                    setMagenta(out);
                }
                if (piece == null) {
                    printBlankSpace(out);
                }
                else {
                    printPlayer(out, piece);
                }
                if (columnTracker != 8) {
                    islightSquare = !islightSquare;
                }
                else {
                    setGrey(out);
                    out.print(" " + EMPTY + " ");
                    setBlack(out);
                    out.println();
                }
            }
        }
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
    private static void printPlayer(PrintStream out, ChessPiece piece) {
        String pieceColor = piece.getTeamColor().name();
        String printPiece;
        if (pieceColor.equalsIgnoreCase("white")) {
           out.print(SET_TEXT_COLOR_WHITE);
           printPiece = getWhitePieces(piece);
        }
        else {
            out.print(SET_TEXT_COLOR_BLACK);
            printPiece = getBlackPieces(piece);
        }
        out.print(" " + printPiece + " ");
    }
    private static void printBlankSpace(PrintStream out) {
        out.print(" " + EMPTY + " ");
    }


    private static String getWhitePieces(ChessPiece piece) {
        String printPiece = null;
        if (piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
            printPiece = WHITE_KING;
        }
        else if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN)) {
            printPiece = WHITE_QUEEN;
        }
        else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK)) {
            printPiece = WHITE_ROOK;
        }
        else if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT)) {
            printPiece = WHITE_KNIGHT;
        }
        else if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
            printPiece = WHITE_PAWN;
        }
        else if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP)) {
            printPiece = WHITE_BISHOP;
        }
        return printPiece;
    }

    private static String getBlackPieces(ChessPiece piece) {
        String printPiece = null;
        if (piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
            printPiece = BLACK_KING;
        }
        else if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN)) {
            printPiece = BLACK_QUEEN;
        }
        else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK)) {
            printPiece = BLACK_ROOK;
        }
        else if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT)) {
            printPiece = BLACK_KNIGHT;
        }
        else if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
            printPiece = BLACK_PAWN;
        }
        else if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP)) {
            printPiece = BLACK_BISHOP;
        }
        return printPiece;
    }

}


