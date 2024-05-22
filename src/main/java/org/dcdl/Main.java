package org.dcdl;

import org.dcdl.controller.GameController;
import org.dcdl.repository.WordRepositoryImpl;
import org.dcdl.services.GameServiceImpl;
import org.dcdl.services.WordServiceImpl;
import org.dcdl.utils.ConfigLoader;
import org.dcdl.validator.WordValidatorImpl;
import org.dcdl.view.ConsoleGameViewable;

import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        final Properties properties= ConfigLoader.getProperties();
        WordRepositoryImpl wordRepository;
        WordValidatorImpl wordValidator;

        try {
            Class<?> repositoryClassImpl = Class.forName(properties.getProperty("repository-class-name"));
            Class<?> validatorClassImpl = Class.forName(properties.getProperty("validator-class-name"));

            wordRepository= (WordRepositoryImpl) repositoryClassImpl.getDeclaredConstructor().newInstance();
            wordValidator= (WordValidatorImpl) validatorClassImpl.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        WordServiceImpl wordService= new WordServiceImpl(wordRepository, wordValidator);
        GameServiceImpl gameService= new GameServiceImpl(wordService);

        GameController gameController= new GameController(new ConsoleGameViewable(), gameService);
        gameController.run();

    }
}