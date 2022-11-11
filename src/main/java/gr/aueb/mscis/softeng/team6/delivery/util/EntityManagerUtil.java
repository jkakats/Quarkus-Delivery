package gr.aueb.mscis.softeng.team6.delivery.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.RollbackException;
import java.util.function.Consumer;

/**
 * Entity manager utility class.
 *
 * @since 0.1.0
 */
public class EntityManagerUtil {
  private static EntityManagerFactory factory;

  private static final ThreadLocal<EntityManager> manager = new ThreadLocal<>();

  /** The name of the persistence unit. */
  public static final String UNIT_NAME = "delivery";

  private EntityManagerUtil() {}

  private static EntityManagerFactory getFactory() {
    if (factory == null) {
      factory = Persistence.createEntityManagerFactory(UNIT_NAME);
    }
    return factory;
  }

  /**
   * Get or create an entity manager.
   *
   * @return the current entity manager instance.
   */
  public static EntityManager getManager() {
    var em = manager.get();
    if (em == null || !em.isOpen()) {
      em = getFactory().createEntityManager();
      manager.set(em);
    }
    return em;
  }

  /**
   * Run a transaction using the entity manager.
   *
   * @param transaction the transaction method or lambda.
   */
  public static void runTransaction(Consumer<EntityManager> transaction) {
    var em = getManager();
    var tx = em.getTransaction();
    try {
      tx.begin();
      transaction.accept(em);
      tx.commit();
    } catch (RollbackException ex) {
      tx.rollback();
    } finally {
      em.close();
    }
  }

  /** Close the entity manager factory. */
  @SuppressWarnings("unused")
  public static void closeFactory() {
    if (factory != null && factory.isOpen()) {
      factory.close();
    }
  }
}
