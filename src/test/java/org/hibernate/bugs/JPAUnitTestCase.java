package org.hibernate.bugs;

import org.hibernate.bugs.entity.Author;
import org.hibernate.bugs.entity.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JPAUnitTestCase {
    private static final int DB_LOCK_TIMEOUT = 10000;

    private final ExecutorService someOtherThread = Executors.newCachedThreadPool();
    private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() throws InterruptedException {
        someOtherThread.shutdown();
        someOtherThread.awaitTermination(1, TimeUnit.MINUTES);
		entityManagerFactory.close();
	}

	@Test
	public void timeoutWaitingForLockWhenReading() throws Exception {
        createBookAndAuthor();

        someOtherThread.execute(doInTransaction(em -> {
            updateAuthor(1, em);
            Thread.sleep(DB_LOCK_TIMEOUT + 1000);
        }));

        someOtherThread.execute(doInTransaction(em -> {
            Thread.sleep(500);
            read(Author.class, 1, em);
        }));
	}

    @Test
    public void timeoutWaitingForLockWhenWriting() throws Exception {
        createBookAndAuthor();
        createBookAndAuthor();

        someOtherThread.execute(doInTransaction(em -> {
            updateAuthor(1, em);
            Thread.sleep(DB_LOCK_TIMEOUT + 1000);
        }));

        someOtherThread.execute(doInTransaction(em -> {
            updateAuthor(2, em);
            Thread.sleep(DB_LOCK_TIMEOUT + 1000);
        }));
    }

    @Test
    public void dealock() throws Exception {
        createBookAndAuthor();

        someOtherThread.execute(doInTransaction(em -> {
            updateAuthor(1, em);
            Thread.sleep(500);
            updateBook(1, em);
        }));

        someOtherThread.execute(doInTransaction(em -> {
            updateBook(1, em);
            Thread.sleep(500);
            updateAuthor(1, em);
        }));
    }

    @Test
    public void dealock2() throws Exception {
        createBookAndAuthor();

        someOtherThread.execute(doInTransaction(em -> {
            updateAuthor(1, em);
            Thread.sleep(500);
            read(Book.class, 1, em);
        }));

        someOtherThread.execute(doInTransaction(em -> {
            updateBook(1, em);
            Thread.sleep(500);
            read(Author.class, 1, em);
        }));
    }


    private <T> void read(Class<T> clazz, long id, EntityManager em) {
        log("================ searching for " + clazz.getSimpleName() + " with id=" + id + "...");
        T found = em.find(clazz, id);
        log("================ found: " + found);
    }

    private void updateAuthor(long id, EntityManager em) {
        log("================ updating author  with id=" + id + "...");
        Query query = em.createQuery("update Author a set name = :name where a.id = :id");
        query.setParameter("name", "Updated Name " + new Random().nextInt(1000));
        query.setParameter("id", id);
        int updated = query.executeUpdate();
        em.flush();
        log("================ updated " + updated + " author(s)");
    }

    private void updateBook(long id, EntityManager em) {
        log("================ updating book with id=" + id + "...");
        Query query = em.createQuery("update Book b set title = :name where b.id = :id");
        query.setParameter("name", "Updated Title " + new Random().nextInt(1000));
        query.setParameter("id", id);
        int updated = query.executeUpdate();
        em.flush();
        log("================ updated " + updated + " book(s)");
    }


    private void createBookAndAuthor() {
	    doInTransaction(entityManager -> {
            Author author = new Author("John");
            Book book = new Book("Title", author);

            entityManager.persist(author);
            entityManager.persist(book);
            log("================ created: author=" + author + ", book=" + book);
        }).run();
    }

    private Runnable doInTransaction(InTransaction executable) {
	    return () -> {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            try {
                entityManager.getTransaction().begin();
                executable.run(entityManager);
                entityManager.getTransaction().commit();
                log("=========== transaction committed");
            } catch (Exception ex) {
                log("=========== error");
                ex.printStackTrace();
                entityManager.getTransaction().rollback();
                throw new RuntimeException(ex);
            } finally {
                entityManager.close();
            }
        };
    }

    private synchronized void log(String s) {
        System.out.println("============" + Thread.currentThread().getName() + "=====" + s);
    }


    private interface InTransaction {
        void run(EntityManager em) throws Exception;
    }
}
