package sp.config;

/**
 * Spring Java configuration of Bitronix transaction manager
 *
 * @author Paul Kulitski
 */
import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.util.Properties;
import java.io.IOException;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class BitronixJtaConfig {

    @Inject
    private Environment environment;
    protected static final Logger logger = LoggerFactory.getLogger(BitronixJtaConfig.class);
    /*
     * TODO: push to config file
     */
    private static final String ENTERPRISE_DS_UNIQUE_NAME = "jdbc/enterpriseDS";
    private static final String ENTERPRISEDS_DRIVER_PROPERTIES_FILE = "/enterpriseds-driver.properties";
    private static final String REGISTRY_DS_UNIQUE_NAME = "jdbc/registryDS";
    private static final String REGISTRYDS_DRIVER_PROPERTIES_FILE = "/registryds-driver.properties";
    private static final String ENTERPRISE_HIBERNATE_CONFIG_FILE = "/enterprise/model/enterprise/enterprise.cfg.xml";
    private static final String REGISTRY_HIBERNATE_CONFIG_FILE = "/enterprise/model/registry/registry.cfg.xml";
    private static final int MIN_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 32;
    private static final String TEST_QUERY_ENTERPRISE_DS = "SELECT 1 FROM reports";
    private static final String TEST_QUERY_REGISTRY_DS = "SELECT 1 FROM registers";
    private static final String JTA_JVM_UNIQUE_ID = "reports-spring-btm-node0";
    private static final String TX_LOG_1 = "tx-logs/tx-part1.btm";
    private static final String TX_LOG_2 = "tx-logs/tx-part2.btm";

    /**
     * Sole constructor
     */
    public BitronixJtaConfig() {
    }

    /**
     * Main Reports! datasource. Uses {@link MysqlXADataSource} capabilities
     *
     * @return XA datasource
     * @see PoolingDataSource
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    public PoolingDataSource enterpriseDataSource() {
        PoolingDataSource enterpriseDS = new PoolingDataSource();
        enterpriseDS.setClassName(
                com.mysql.jdbc.jdbc2.optional.MysqlXADataSource.class.getName());
        enterpriseDS.setUniqueName(ENTERPRISE_DS_UNIQUE_NAME);
        enterpriseDS.setMinPoolSize(MIN_POOL_SIZE);
        enterpriseDS.setMaxPoolSize(MAX_POOL_SIZE);
        enterpriseDS.setTestQuery(TEST_QUERY_ENTERPRISE_DS);
        //security?
        enterpriseDS.setAllowLocalTransactions(true);
        try {
            Properties props = new Properties();
            props.load(
                    Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(ENTERPRISEDS_DRIVER_PROPERTIES_FILE));
            enterpriseDS.setDriverProperties(props);
            enterpriseDS.init();
        } catch (IOException e) {
            logger.error("Cannot load properties file for a datasource driver initialization", e);
        }
        return enterpriseDS;
    }

    /**
     * Registry datasource.
     *
     * @return xa datasource
     * @see PoolingDataSource
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    public PoolingDataSource registryDataSource() {
        PoolingDataSource registryDS = new PoolingDataSource();
        registryDS.setClassName(
                com.mysql.jdbc.jdbc2.optional.MysqlXADataSource.class.getName());
        registryDS.setUniqueName(REGISTRY_DS_UNIQUE_NAME);
        registryDS.setMinPoolSize(MIN_POOL_SIZE);
        registryDS.setMaxPoolSize(MAX_POOL_SIZE);
        registryDS.setTestQuery(TEST_QUERY_REGISTRY_DS);
        // security?
        registryDS.setAllowLocalTransactions(true);
        try {
            Properties props = new Properties();
            props.load(
                    Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(REGISTRYDS_DRIVER_PROPERTIES_FILE));
            registryDS.setDriverProperties(props);
        } catch (IOException e) {
            logger.error("Cannot load properties file for a datasource driver initialization", e);
        }
        return registryDS;
    }

    /**
     * Bitronix configuration bean
     *
     * @return
     * @see bitronix.tm.Configuration
     */
    @Bean
    public bitronix.tm.Configuration btmConfig() {
        bitronix.tm.Configuration tmConfiguration =
                TransactionManagerServices.getConfiguration();
        tmConfiguration.setServerId(JTA_JVM_UNIQUE_ID);
        tmConfiguration.setLogPart1Filename(TX_LOG_1);
        tmConfiguration.setLogPart2Filename(TX_LOG_2);
        return tmConfiguration;
    }

    /**
     * Native Bitronix transaction manager bean
     *
     * @return
     * @see BitronixTransactionManager
     */
    @Bean(destroyMethod = "shutdown")
    @DependsOn("btmConfig")
    public BitronixTransactionManager bitronixTransactionManager() {
        BitronixTransactionManager btmTransactionManager =
                TransactionManagerServices.getTransactionManager();
        return btmTransactionManager;
    }

    /**
     * JTA transaction manager
     *
     * @return transaction manager
     * @see TransactionManager
     */
    @Bean
    @DependsOn("btmConfig")
    public TransactionManager transactionManagerNative() {
        return TransactionManagerServices.getTransactionManager();
    }

    /**
     * {@link UserTransaction} bean
     * 
     * @return 
     * @see UserTransaction
     */
    @Bean
    @DependsOn("btmConfig")
    public UserTransaction userTransaction() {
        return TransactionManagerServices.getTransactionManager();
    }

    /**
     * {@link PlatformTransactionManager} bean to be used in distributed
     * transaction management in Reports! Spring application
     * 
     * @return 
     * @see JtaTransactionManager
     */
    @Bean
    @DependsOn("bitronixTransactionManager")
    public JtaTransactionManager transactionManager() {
        JtaTransactionManager tm = new JtaTransactionManager();
        tm.setTransactionManager(bitronixTransactionManager());
        tm.setUserTransaction(bitronixTransactionManager());
        return tm;
    }
    
    /* Native Hibernate session factories
     * 
    @Bean
    @DependsOn("enterpriseDataSource")
    public AnnotationSessionFactoryBean enterpriseSessionFactory() {
        AnnotationSessionFactoryBean esf = new AnnotationSessionFactoryBean();
        esf.setDataSource(enterpriseDataSource());
        esf.setJtaTransactionManager(transactionManager());
        esf.setConfigLocation(new ClassPathResource(ENTERPRISE_HIBERNATE_CONFIG_FILE));
        return esf;
    }
    * 
    @Bean
    @DependsOn("registryDataSource")
    public AnnotationSessionFactoryBean registrySessionFactory() {
        AnnotationSessionFactoryBean rsf = new AnnotationSessionFactoryBean();
        rsf.setDataSource(registryDataSource());
        rsf.setJtaTransactionManager(transactionManager());
        rsf.setConfigLocation(new ClassPathResource(REGISTRY_HIBERNATE_CONFIG_FILE));
        return rsf;
    }
    */
}
