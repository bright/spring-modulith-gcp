package pl.brightinventions.spring.datastore;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.spring.autoconfigure.datastore.DatastoreProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Datastore.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.datastore.enabled", matchIfMissing = true)
@AutoConfigureBefore(
    {
        com.google.cloud.spring.autoconfigure.datastore.DatastoreTransactionManagerAutoConfiguration.class
    }
)
public class DatastoreTransactionManagerAutoConfiguration {
  private DatastoreTransactionManagerAutoConfiguration() {
  }

  @AutoConfiguration
  static class DatastoreTransactionManagerConfiguration {
    private final DatastoreProvider datastore;
    private final TransactionManagerCustomizers transactionManagerCustomizers;

    DatastoreTransactionManagerConfiguration(DatastoreProvider datastore, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
      this.datastore = datastore;
      this.transactionManagerCustomizers = transactionManagerCustomizers.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public DatastoreTransactionManager datastoreTransactionManager() {
      DatastoreTransactionManager transactionManager = new DatastoreTransactionManager(this.datastore);
      if (this.transactionManagerCustomizers != null) {
        this.transactionManagerCustomizers.customize(transactionManager);
      }

      return transactionManager;
    }
  }
}
