import chess.ChessGame;
import ui.Repl;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        var port = 8080;
        String serverUrl = "http://localhost:" + port;
        if (args.length == 1) {
            serverUrl = args[0];
        }

        System.out.println("Started test HTTP server on " + port);
        System.out.println("\n ♕ Welcome to 240 chess. Type Help to get started. ♕");
        new Repl(serverUrl).run();
    }

}