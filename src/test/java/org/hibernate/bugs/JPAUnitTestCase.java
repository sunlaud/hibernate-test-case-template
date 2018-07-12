package org.hibernate.bugs;

import org.hibernate.bugs.entity.Author;
import org.hibernate.bugs.entity.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

        Author author = new Author("John");
        Book book = new Book("War and Peace", author);

        entityManager.persist(author);
        entityManager.persist(book);

		entityManager.getTransaction().commit();
		entityManager.close();


        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();


        System.out.println("\n =================== Load ===============\n");
        Book loadedBook = entityManager.find(Book.class, 1L);

        System.out.println("\n =================== Playing ===============\n");
        System.out.println("author: " + loadedBook.getAuthor().getId());

        entityManager.getTransaction().commit();
        entityManager.close();
	}
}
