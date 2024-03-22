package model;

import java.util.ArrayList;

public record GameData(Integer gameID, String whiteUsername, String blackUsername, String gameName, chess.ChessGame game, ArrayList<UserData> observers) {

}
