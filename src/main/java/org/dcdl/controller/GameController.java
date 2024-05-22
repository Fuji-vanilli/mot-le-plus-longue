package org.dcdl.controller;

import org.dcdl.models.GameState;
import org.dcdl.models.Word;
import org.dcdl.services.GameService;
import org.dcdl.view.ConsoleGameViewable;

import java.util.List;
import java.util.Map;

public class GameController {
    private final ConsoleGameViewable gameViewable;
    private final GameService gameService;
    private GameState gameState;
    private String currentDrawLetters;

    public GameController(ConsoleGameViewable gameViewable, GameService gameService) {
        this.gameViewable= gameViewable;
        this.gameService= gameService;
        this.gameState= GameState.DRAW_LETTERS;
        gameViewable.setGameController(this);
    }
    public void run() {
        switch (this.gameState) {
            case RESTART_GAME, DRAW_LETTERS -> gameViewable.promptDrawLetter();
            case RESULT_REVEALED -> gameViewable.promptRestartGame();
        }
    }
    public void startGame() {
        if (gameState== GameState.START_CALCULATE) {
            List<Word> resultGame = gameService.mostLongWordCheck(currentDrawLetters);
            Map.Entry<Integer, List<String>> maxResultGame = gameService.maxLengthWordCheck(currentDrawLetters);

            gameState= GameState.RESULT_REVEALED;
            displayResultGame(maxResultGame, resultGame);
        }
    }
    public void addLetterForDraw(String draw) {
        currentDrawLetters = draw;
        gameState= GameState.START_CALCULATE;
    }
    public void displayResultGame(Map.Entry<Integer, List<String>> maxResultGame, List<Word> resultGame) {
        if (gameState== GameState.RESULT_REVEALED) {
            gameViewable.showMaxLengthResultGame(maxResultGame);
            gameViewable.showAllResultGame(resultGame);
        }
        gameState= GameState.RESTART_GAME;
        gameViewable.promptRestartGame();
    }
    public void rebuildGame() {
        if (gameState== GameState.RESTART_GAME) {
            gameViewable.promptRestartGame();
        }
    }

}
