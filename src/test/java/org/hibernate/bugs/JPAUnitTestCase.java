package org.hibernate.bugs;

import org.hibernate.bugs.entity.Book;
import org.hibernate.bugs.entity.BookType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		if (entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}

	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		Book book2 = new Book("Gamlet", BookType.POETRY);
		Book book1 = new Book("War and Peace", BookType.FICTION);
        Book book3 = new Book("Garry Plotter", BookType.FICTION);

        Collection<Book> books = Arrays.asList(book1, book2, book3);

        books.forEach(entityManager::persist);

		entityManager.flush();
		entityManager.clear();


        System.out.println("\n =================== Load ===============\n");
		List<Book> loadedBooks = entityManager.createQuery("from Book", Book.class).getResultList();
		System.out.println("loaded: " + loadedBooks);
		assertThat(loadedBooks.size(), is(books.size()));
		assertTrue("book1", book1.isSameAsAnyOf(loadedBooks));
		assertTrue("book2", book2.isSameAsAnyOf(loadedBooks));
		assertTrue("book3", book3.isSameAsAnyOf(loadedBooks));


        entityManager.getTransaction().commit();
        entityManager.close();
	}


}
