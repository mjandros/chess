package model.results;

import model.GameData;

import java.util.Collection;

public record ListGamesResult(Collection<GameData> games) {
}
