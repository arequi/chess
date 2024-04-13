package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand{
    public LeaveCommand(String authToken) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
    }
}
