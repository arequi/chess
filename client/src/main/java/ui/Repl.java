package ui;

import chess.ChessGame;
import ui.websocket.NotificationHandler;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler{
    public static State state;
    private final PreLoginUI preLogin;
    private final PostLoginUI postLogin;
    private GameplayUI gameplayUI;
    private final String serverUrl;
    public static ChessGame currentGame;
    public static String currentColor;

    public Repl(String serverUrl) throws ResponseException {

        this.serverUrl = serverUrl;
        preLogin = new PreLoginUI(serverUrl, this);
        postLogin = new PostLoginUI(serverUrl, this);
        gameplayUI = new GameplayUI(serverUrl, this);
    }

    // command line text
    public void run () throws Exception {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        state = State.LOGGED_OUT;
        while (!result.equals("quit")) {
            printPrompt(); // waiting for user input
            String line = scanner.nextLine(); // take in user input, store in line
            try {
                if (state == State.LOGGED_OUT) {
                    result = preLogin.eval(line);
                }
                else if (state == State.LOGGED_IN) {
                    result = postLogin.eval(line);
                }
                else {
                    result = gameplayUI.eval(line);
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result + "\n");
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + state + "]" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> sendError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame((LoadGameMessage) message);
        }
    }

    private void displayNotification (String notificationMessage) {
        System.out.println(notificationMessage);
    }

    private void sendError (String errorMessage) {
        System.out.println(errorMessage);
    }

    private void loadGame (LoadGameMessage message) {
        ChessGame game = message.getGame();
        currentGame = game;
        currentColor = message.getPlayerColor();
        PostLoginUI.displayBoard(message.getPlayerColor(), game.getBoard());
    }
}
