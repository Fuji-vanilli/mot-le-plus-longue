package org.dcdl.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dcdl.models.Word;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Getter
@Slf4j
public class GameServiceImpl implements GameService{
    private final WordService wordService;
    private List<Word> words;

    public GameServiceImpl(WordServiceImpl wordService) {
        this.wordService= wordService;
        initWords();
    }
    @Override
    public List<Word> mostLongWordCheck(String draft) {
        return words.stream()
                .filter(w-> w.getSize()< draft.length())
                .filter(w-> checkMatcher(draft, w.getName()))
                .filter(w-> w.getSize()> 5)
                .toList();
    }
    @Override
    public Map.Entry<Integer, List<String>> maxLengthWordCheck(String draft) {
        return mostLongWordCheck(draft).stream()
                .collect(Collectors.groupingBy(
                        Word::getSize,
                        Collectors.mapping(Word::getName, Collectors.toList())
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByKey())
                .orElse(null);
    }

    public boolean checkMatcher(String draft, String word) {
        Map<Character, Long> letterCount = draft.chars()
                .mapToObj(i -> (char) i)
                .collect(Collectors.groupingBy(
                        identity(),
                        Collectors.counting()
                ));

        for (char c: word.toCharArray()) {
            if (!letterCount.containsKey(c) || letterCount.get(c)== 0) {
                return false;
            }
            letterCount.put(c, letterCount.get(c)- 1);
        }

        return true;
    }
    private String removeAccent(String word) {
        String normalize= Normalizer.normalize(word, Normalizer.Form.NFD);

        StringBuilder builder= new StringBuilder();
        for (char c: word.toCharArray()) {
            if (!Character.isLetter(c))
                continue;
            builder.append(c);
        }

        return builder.toString();
    }
    @Override
    public List<String> allWordsName() {
        return wordService.all().stream()
                .map(Word::getName)
                .toList();
    }

    private void initWords() {
        if (Objects.isNull(words)) {
            words= wordService.all();
        }
    }

}
