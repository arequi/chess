package webSocketMessages.userCommands;

public class MakeMove extends UserGameCommand{
    public MakeMove(String authToken) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
    }
}
