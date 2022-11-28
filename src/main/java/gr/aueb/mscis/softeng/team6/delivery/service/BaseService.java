package gr.aueb.mscis.softeng.team6.delivery.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;

/**
 * Base service class.
 *
 * @since 0.1.0
 */
abstract class BaseService {
  protected final EntityManager em;

  protected BaseService(EntityManager em) {
    this.em = em;
  }

  /** Persist the given object and handle exceptions. */
  protected <T> T persistObject(T object) {
    var tx = em.getTransaction();
    try {
      tx.begin();
      em.persist(object);
      tx.commit();
      return object;
    } catch (ConstraintViolationException ex) {
      for (var cv : ex.getConstraintViolations()) {
        System.err.println(cv.getPropertyPath() + " " + cv.getMessage());
      }
      tx.rollback();
    } catch (PersistenceException ex) {
      // NOTE: it's not always caught above ¯\_(ツ)_/¯
      if (ex.getCause() instanceof ConstraintViolationException cause) {
        for (var cv : cause.getConstraintViolations()) {
          System.err.println(cv.getPropertyPath() + " " + cv.getMessage());
        }
      } else {
        System.err.println(ex.getCause().getMessage());
      }
      tx.rollback();
    }
    return null;
  }
}
