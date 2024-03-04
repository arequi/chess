package request;

public record CreateGameRequest(String gameName, Boolean success, String message) {
}
