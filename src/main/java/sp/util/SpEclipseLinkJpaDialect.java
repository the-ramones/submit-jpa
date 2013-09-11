package sp.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.eclipse.persistence.sessions.Session;
//import org.eclipse.persistence.sessions.Session;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

/**
 * JPA Dialect enhancement for supporting custom isolation levels
 *
 * @author web-resource
 */
public class SpEclipseLinkJpaDialect extends EclipseLinkJpaDialect {

    private static final Logger logger = Logger.getLogger(SpEclipseLinkJpaDialect.class.getName());

    /**
     * This method is overridden to set custom isolation levels on the
     * connection
     *
     * @param entityManager
     * @param definition
     * @return
     * @throws PersistenceException
     * @throws SQLException
     * @throws TransactionException
     */
    @SuppressWarnings("deprecation")
    @Override
    public Object beginTransaction(final EntityManager entityManager, final TransactionDefinition definition) throws PersistenceException, SQLException, TransactionException {
        boolean infoEnabled = false;
        boolean debugEnabled = false;
        Session session = (Session) entityManager.getDelegate();
        if (definition.getTimeout() != TransactionDefinition.TIMEOUT_DEFAULT) {
            // getSession(entityManager).acquireUnitOfWork().setTimeout(definition.getTimeout());
        }
        Connection connection;// = session.connection();

        //    Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(connection, definition);

        entityManager.getTransaction().begin();

        Object transactionDataFromHibernateJpaTemplate = prepareTransaction(entityManager, definition.isReadOnly(), definition.getName());

        //     return new IsolationSupportSessionTransactionData(transactionDataFromHibernateJpaTemplate, previousIsolationLevel, connection);
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.orm.jpa.vendor.HibernateJpaDialect#cleanupTransaction(java.lang.Object)
     */
    @Override
    public void cleanupTransaction(Object transactionData) {
        super.cleanupTransaction(((IsolationSupportSessionTransactionData) transactionData).getSessionTransactionDataFromHibernateTemplate());
        ((IsolationSupportSessionTransactionData) transactionData).resetIsolationLevel();
    }

    private static class IsolationSupportSessionTransactionData {

        private final Object sessionTransactionDataFromHibernateJpaTemplate;
        private final Integer previousIsolationLevel;
        private final Connection connection;

        public IsolationSupportSessionTransactionData(Object sessionTransactionDataFromHibernateJpaTemplate, Integer previousIsolationLevel, Connection connection) {
            this.sessionTransactionDataFromHibernateJpaTemplate = sessionTransactionDataFromHibernateJpaTemplate;
            this.previousIsolationLevel = previousIsolationLevel;
            this.connection = connection;
        }

        public void resetIsolationLevel() {
            if (this.previousIsolationLevel != null) {
                DataSourceUtils.resetConnectionAfterTransaction(connection, previousIsolationLevel);
            }
        }

        public Object getSessionTransactionDataFromHibernateTemplate() {
            return this.sessionTransactionDataFromHibernateJpaTemplate;
        }
    }
}
