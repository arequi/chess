package response;

import model.GameData;

import java.util.ArrayList;

public record ListGamesResponse(ArrayList<GameData> games, Boolean success, String message) {
}
