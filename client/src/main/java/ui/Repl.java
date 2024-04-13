package ui;

import chess.ChessGame;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
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
    private final GameplayUI gameplayUI;
    private String serverUrl;

    public Repl(String serverUrl){
        this.serverUrl = serverUrl;
        preLogin = new PreLoginUI(serverUrl, this);
        postLogin = new PostLoginUI(serverUrl, this);
        gameplayUI = new GameplayUI(serverUrl, this);
    }


    // command line text
    public void run () throws Exception {
        Scanner scanner = new Scanner(System.in);
        var ws = new WebSocketFacade(serverUrl, this);
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
        // TODO: where does this go?
        while (true) ws.send(scanner.nextLine());
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + state + "]" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> sendError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }

    private void displayNotification (String notificationMessage) {

    }

    private void sendError (String errorMessage) {

    }

    private void loadGame (ChessGame game) {

    }
}
