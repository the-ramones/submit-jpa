package sp.system;

/**
 * System constants holder
 *
 * @author Paul Kulitski
 */
public class SystemConstants {

    /*
     * Front-end keys
     */
    public static final String PAGE_SIZE = "pagination.threshold";
    public static final String PROMPT_LIMIT = "pagination.maxonpager";
    public static final String MAX_ON_PAGER = "prompt.limit.default";
    /*
     * Front-end default values
     */
    public static final String PAGE_SIZE_DEFAULT_VALUE = "10";
    public static final String PROMPT_LIMIT_DEFAULT_VALUE = "8";
    public static final String MAX_ON_PAGER_DAFAULT_VALUE = "4";
    /*
     * Back-end keys
     * 
     */
    public static final String SOLR_HOST = "solr.host";
    public static final String SOLR_HOST_DEFAULT_VALUE = "http://127.0.0.1:8983/solr";
    public static final String SOLR_CORE = "solr.core";
    public static final String SOLR_CORE_DEFAULT_VALUE = "reports";
    public static final String SOLR_TIMEOUT = "solr.timeout";
    public static final String SOLR_TIMEOUT_DEFAULT_VALUE = "16000";
    public static final String SOLR_MAXCONNECTIONS = "solr.maxConnections";
    public static final String SOLR_MAXCONNECTIONS_DEFAULT_VALUE = "128";
    public static final String DATABASE_URL = "database.url";
    public static final String DATABASE_URL_DEFAULT_VALUE = "jdbc:mysql://localhost:3306/enterprise?autoReconnect=true&characterEncoding=utf8";
    public static final String DATABASE_SCHEME = "database.schema";
    public static final String DATABASE_SCHEME_DAFAULT_VALUE = "enterprise";
}
