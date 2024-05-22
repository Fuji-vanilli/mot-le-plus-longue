package org.dcdl.view;

import org.dcdl.controller.GameController;
import org.dcdl.models.Word;

import java.util.List;
import java.util.Map;

public interface GameViewable {
    void setGameController(GameController gameController);
    void promptDrawLetter();
    void promptRestartGame();
    void showMaxLengthResultGame(Map.Entry<Integer, List<String>> maxLengthResultGame);
    void showAllResultGame(List<Word> words);
}
