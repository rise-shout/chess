package server;

import model.GameData;
import java.util.List;

public record GameListResult(List<GameData> games) {}