package response;

public record LoginResponse(String username, String authToken, Boolean success, String message) {
}
