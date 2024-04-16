package webSocketMessages.serverMessages;

public class Error extends ServerMessage{
    private String errorMessage;
    public Error(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
    public String getErrorMessage () {
        return this.errorMessage;
    }

}
