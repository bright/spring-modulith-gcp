package pl.brightinventions.spring.datastore;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.datastore.v1.TransactionOptions;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Supplier;

public class DatastoreTransactionManager extends com.google.cloud.spring.data.datastore.core.DatastoreTransactionManager {
  private static final TransactionOptions READ_ONLY_OPTIONS =
      TransactionOptions.newBuilder()
          .setReadOnly(TransactionOptions.ReadOnly.newBuilder().build())
          .build();

  private final Supplier<Datastore> datastore;

  public DatastoreTransactionManager(final Supplier<Datastore> datastore) {
    super(datastore);
    this.datastore = datastore;
  }

  @Override
  protected Object doGetTransaction() throws TransactionException {
    Tx tx = (Tx) TransactionSynchronizationManager.getResource(datastore.get());
    if (tx != null && tx.getTransaction() != null && tx.getTransaction().isActive()) {
      return tx;
    }
    tx = new Tx(datastore.get());
    return tx;
  }

  @Override
  protected void doBegin(Object transactionObject, TransactionDefinition transactionDefinition)
      throws TransactionException {
    if (transactionDefinition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT
        && transactionDefinition.getIsolationLevel()
        != TransactionDefinition.ISOLATION_SERIALIZABLE) {
      throw new IllegalStateException(
          "DatastoreTransactionManager supports only isolation level "
              + "TransactionDefinition.ISOLATION_DEFAULT or ISOLATION_SERIALIZABLE");
    }

    Tx tx = (Tx) transactionObject;
    if (transactionDefinition.isReadOnly()) {
      tx.setTransaction(tx.getDatastore().newTransaction(READ_ONLY_OPTIONS));
    } else {
      tx.setTransaction(tx.getDatastore().newTransaction());
    }

    TransactionSynchronizationManager.bindResource(tx.getDatastore(), tx);
  }

  @Override
  protected void doCommit(DefaultTransactionStatus defaultTransactionStatus)
      throws TransactionException {
    Tx tx = (Tx) defaultTransactionStatus.getTransaction();
    try {
      if (tx.getTransaction().isActive()) {
        tx.getTransaction().commit();
      } else {
        this.logger.debug("Transaction was not committed because it is no longer active.");
      }
    } catch (DatastoreException ex) {
      throw new TransactionSystemException("Cloud Datastore transaction failed to commit.", ex);
    }
  }

  @Override
  protected void doRollback(DefaultTransactionStatus defaultTransactionStatus)
      throws TransactionException {
    Tx tx = (Tx) defaultTransactionStatus.getTransaction();
    try {
      if (tx.getTransaction().isActive()) {
        tx.getTransaction().rollback();
      } else {
        this.logger.debug("Transaction was not rolled back because it is no longer active.");
      }
    } catch (DatastoreException ex) {
      throw new TransactionSystemException("Cloud Datastore transaction failed to rollback.", ex);
    }
  }

  @Override
  protected boolean isExistingTransaction(Object transaction) {
    return ((Tx) transaction).getTransaction() != null;
  }

  @Override
  protected void doCleanupAfterCompletion(Object transaction) {
    Tx tx = (Tx) transaction;
    TransactionSynchronizationManager.unbindResource(tx.getDatastore());
  }

  /**
   * A class to contain the transaction context.
   */
//  public static class Tx implements SmartTransactionObject {
//    private Transaction transaction;
//    private Datastore datastore;
//
//    public Tx(Datastore datastore) {
//      this.datastore = datastore;
//    }
//
//    public void setTransaction(Transaction transaction) {
//      this.transaction = transaction;
//    }
//
//    public Datastore getDatastore() {
//      return datastore;
//    }
//  }
}
