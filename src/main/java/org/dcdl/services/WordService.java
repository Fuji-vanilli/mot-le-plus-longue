package org.dcdl.services;

import org.dcdl.models.Word;

import java.util.List;

public interface WordService {
    long totalWords();
    Word add(Word word);
    Word get(long id);
    List<Word> all();
    void delete(long id);
}
