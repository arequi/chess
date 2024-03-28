package ui;

public class PostLoginUI {
    private final String serverUrl;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;

    public PostLoginUI(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String displayHelp () {
        return """
                - list
                - adopt <pet id>
                - rescue <name> <CAT|DOG|FROG|FISH>
                - adoptAll
                - signOut
                - quit
                """;
    }

    public void logout () {
        // set state to logged out

    }

    public void createGame () {

    }

    public void listGames () {

    }

    public void joinGame () {

    }

    public void joinObserver () {

    }
}
