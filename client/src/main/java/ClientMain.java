import ui.Repl;

import java.io.IOException;
import java.net.URISyntaxException;

public class ClientMain {
    public static void main(String[] args) throws URISyntaxException, IOException {
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