package webSocketMessages.userCommands;

public class Leave extends UserGameCommand{
    public Leave(String authToken, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
    }
}
