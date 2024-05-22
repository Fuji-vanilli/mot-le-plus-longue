package org.dcdl.services;

import lombok.extern.slf4j.Slf4j;
import org.dcdl.models.Word;
import org.dcdl.repository.WordRepositoryImpl;
import org.dcdl.repository.WordRepository;
import org.dcdl.validator.WordValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.List;
import java.util.Objects;

@Slf4j
public class WordServiceImpl implements WordService{
    private final WordRepository wordRepository;
    private final WordValidator wordValidator;
    public WordServiceImpl(WordRepositoryImpl wordRepository, WordValidator validator) {
        this.wordRepository= wordRepository;
        this.wordValidator= validator;
    }
    public void addFromFile() throws IOException {
        Path path= Paths.get("src/main/resources/dico.txt");
        Files.lines(path)
                .forEach(line-> {
                    String s = removeAccent(line);
                    wordRepository.save(new Word(s));
                });
    }
    private String removeAccent(String word) {
        String normalize= Normalizer.normalize(word, Normalizer.Form.NFD);

        StringBuilder builder= new StringBuilder();
        for (char c: normalize.toCharArray()) {
            if (!Character.isLetter(c))
                continue;
            builder.append(c);
        }

        return builder.toString();
    }

    @Override
    public long totalWords() {
        return wordRepository.totalWords();
    }

    @Override
    public Word add(Word word) {
        final String name= word.getName();
        if (wordRepository.existByName(name)) {
            log.error("word already exist into the database!");
            return null;
        }

        if (!wordValidator.validate(word)) {
            log.error("word not valid to add into the database");
            return null;
        }

        return wordRepository.save(word);
    }
    @Override
    public Word get(long id) {
        if (Objects.isNull(wordRepository.findById(id))) {
            log.error("sorry, word with the id; {} doesn't exist!", id);
            return null;
        }

        return wordRepository.findById(id);
    }

    @Override
    public List<Word> all() {
        return wordRepository.findAll();
    }

    @Override
    public void delete(long id) {
        if (Objects.isNull(wordRepository.findById(id))) {
            log.error("sorry, word with the id; {} doesn't exist!", id);
            return;
        }

        wordRepository.deleteById(id);
    }
}
