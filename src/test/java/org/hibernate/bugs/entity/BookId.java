package org.hibernate.bugs.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;

public class BookId implements Serializable {
    public static final Random RANDOM = new Random();
    //    private String title;
//    private BookType type;
    private LocalDateTime createdDate;

//    @Override
//    public boolean equals(Object o) {
//        return false;
//    }
//
//    @Override
//    public int hashCode() {
//        return RANDOM.nextInt();
//    }
}
