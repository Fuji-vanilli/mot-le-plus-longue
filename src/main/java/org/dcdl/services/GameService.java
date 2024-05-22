package org.dcdl.services;

import org.dcdl.models.Word;

import java.util.List;
import java.util.Map;

public interface GameService {
    List<Word> mostLongWordCheck(String draft);

    Map.Entry<Integer, List<String>> maxLengthWordCheck(String draft);
    List<String> allWordsName();
}
