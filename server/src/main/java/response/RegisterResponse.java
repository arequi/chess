package response;

public record RegisterResponse(String username, String authToken, Boolean success, String message) {

}

