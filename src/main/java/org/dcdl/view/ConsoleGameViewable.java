package org.dcdl.view;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dcdl.controller.GameController;
import org.dcdl.models.Word;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public class ConsoleGameViewable implements GameViewable{
    private final Scanner scanner= new Scanner(System.in);
    @Setter
    private GameController gameController;

    @Override
    public void promptDrawLetter() {
        System.out.println("Enter the letters draw!");
        String lettersDraw= "";
        Pattern pattern= Pattern.compile("[a-zA-Z]+");

        while (true) {
            lettersDraw= scanner.nextLine();
            Matcher matcher= pattern.matcher(lettersDraw);
            if (!matcher.matches()) {
                System.out.println("Your word is not valid...Please try again!");
            } else {
                break;
            }
        }

        gameController.addLetterForDraw(lettersDraw);
        gameController.startGame();
    }

    @Override
    public void promptRestartGame() {
        System.out.println("\nDo you want to play again!? Type Y/N");
        final String restartGame= scanner.nextLine();

        if (restartGame.equalsIgnoreCase("y")) {
            gameController.run();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void showMaxLengthResultGame(Map.Entry<Integer, List<String>> maxLengthResultGame) {
        if (maxLengthResultGame== null) {
            System.out.println("No result finding");
            return;
        }

        System.out.println(
                "Max result:\n"+
                maxLengthResultGame.getKey()+" => " +maxLengthResultGame.getValue().toString()
        );
    }

    @Override
    public void showAllResultGame(List<Word> words) {
        System.out.print("All results\n" );
        words.forEach(w-> System.out.print(w.getName()+" - "));
    }

}
