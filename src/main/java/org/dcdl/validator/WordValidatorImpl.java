package org.dcdl.validator;
import org.dcdl.models.Word;


public class WordValidatorImpl implements WordValidator {
    public boolean validate(Word word) {
        return word.getName().length() >= Word.MIN_SIZE && word.getName().length() <= Word.MAX_SIZE;
    }
}
