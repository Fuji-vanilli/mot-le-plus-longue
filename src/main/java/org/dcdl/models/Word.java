package org.dcdl.models;

import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Word {
    public static final int MIN_SIZE= 3;
    public static final int MAX_SIZE= 10;

    private long id;
    private String name;
    private int size;

    public Word(String name) {
        this.name= name;
    }
}
