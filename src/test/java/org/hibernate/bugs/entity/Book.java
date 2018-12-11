package org.hibernate.bugs.entity;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.bugs.AlwaysUniqueLocalDateTimeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@TypeDefs({
        @TypeDef(name = "unique-local-date-time", typeClass = AlwaysUniqueLocalDateTimeType.class)
})
@Entity
//@IdClass(BookId.class)
public class Book implements Serializable {

    private String title;

    @Id
    @Type(type = "unique-local-date-time")
    @Column(columnDefinition = "datetime")
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    private BookType type;

    private Book() {
        //for ORM
    }

    public Book(String title, BookType type) {
        this.title = title;
        this.type = type;
        createdDate = LocalDateTime.parse("2042-01-01T12:42:42");
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", createdDate=" + createdDate +
                ", type=" + type +
                '}';
    }

    public boolean isSameAs(Book other) {
        if (other == null) return false;
        return Objects.equals(title, other.title) && type == other.type;
    }

    public boolean isSameAsAnyOf(Collection<Book> others) {
        for (Book other: others) {
            if (isSameAs(other)) {
                return true;
            }
        }
        return false;
    }
}
