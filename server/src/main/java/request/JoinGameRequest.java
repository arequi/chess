package request;

public record JoinGameRequest(String playerColor, int gameID, Boolean success, String message) {
}
