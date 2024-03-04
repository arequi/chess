package request;

public record JoinGameRequest(String playercolor, int gameID, Boolean success, String message) {
}
