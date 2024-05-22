package org.dcdl.repository;

import org.dcdl.models.Word;

public interface WordRepository extends EntityRepository<Word, Long> {
    boolean existByName(String name);
    long totalWords();
}
