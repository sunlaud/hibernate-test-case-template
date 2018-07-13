package org.hibernate.bugs;

import org.hibernate.bugs.entity.Author;
import org.hibernate.bugs.entity.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.function.Consumer;

public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	@Test
	public void hhh123Test() throws Exception {
        Author author = new Author("John");
        Book book = new Book("War and Peace", author);

        doInTransaction(entityManager -> {
            entityManager.persist(author);
            entityManager.persist(book);
//            entityManager.createQuery("update Author a set a.version = 5").executeUpdate();
        });

        author.setVersion(5L);

        System.out.println("\n =================== Done init ===============\n");

        doInTransaction(entityManager -> {
            // Book loadedBook = entityManager.find(Book.class, 1L);
            Book loadedBook = entityManager.merge(book);
            loadedBook.setTitle("merged");
        });
	}


    private void doInTransaction(Consumer<EntityManager> callable) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        callable.accept(entityManager);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
