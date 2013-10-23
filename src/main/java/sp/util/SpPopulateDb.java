package sp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class. Populate Reports! database with random combinations of
 * existing records simply shuffling and pasting fields to create new ones.
 * Stand-alone. Doesn't need container
 *
 * @author Paul Kulitski
 */
public class SpPopulateDb {

    private static final Logger logger = LoggerFactory.getLogger(SpPopulateDb.class);

    public static void main(String[] args) throws SQLException {
        Map<String, String> metadata = findMetadata();
        int quatity = populateDb(metadata, INIT_GENERATE_QUANTITY);
        logger.info("Database {} succesfully populated with {} new records", metadata, quatity);
    }
    public static int INIT_GENERATE_QUANTITY = 500;
    public static int RECORDS_PER_TRANSACTION = 100;
    public static final String DB_PROPERTIES_FILENAME = "src/main/java/sp/util/report-populate.properties";
    public static final String DB_PROPERTIES_RESOURCE = "report-populate.properties";
    private static final int METADATA_PROPERTY_QUANTITY = 5;
    private static final String SELECT_ASTERISK = "SELECT * FROM :database";
    private static final String INSERT_STATEMENT =
            "INSERT INTO :database(startDate, endDate, performer, activity) VALUES(?,?,?,?)";
    public static String DATABASE_NAME = "reports";
    public static int INITIAL_QUANTITY = 50;
    private static final String[] METADATA_PROPERTY_NAMES = new String[]{
        "database.driverClassName",
        "database.url",
        "database.username",
        "database.password",
        "database.table"
    };

    /**
     * Finds properties file under the METADATA_PROPERTY_QUANTITY variable value
     * as a name
     */
    private static Map<String, String> findMetadata() {
        File file2 = new File(DB_PROPERTIES_FILENAME);
        logger.info(file2.getAbsolutePath());
        logger.info("properties isExists(): {}", file2.exists());
        InputStream dbPropertiesStream =
                ClassLoader.getSystemResourceAsStream(DB_PROPERTIES_RESOURCE);
        Properties dbProperties = new Properties();
        Map<String, String> metadata = new HashMap<String, String>(METADATA_PROPERTY_QUANTITY);
        try {
            dbProperties.load(dbPropertiesStream);
            for (String key : METADATA_PROPERTY_NAMES) {
                metadata.put(key, dbProperties.getProperty(key));
            }
        } catch (IOException ex) {
            logger.error("Cannot load properties file.", ex);
        } catch (Exception ex) {
            logger.error("Cannot load properties file.", ex);
            /*
             * Fallback to plain Java IO access
             */
            try {
                logger.info("fallback to direct access");
                File dbPropertiesFile = new File(DB_PROPERTIES_FILENAME);
                dbProperties.load(new FileInputStream(dbPropertiesFile));
                logger.info("properties has been loaded");
                for (String key : METADATA_PROPERTY_NAMES) {
                    metadata.put(key, dbProperties.getProperty(key));
                }
            } catch (IOException fileEx) {
                logger.warn("Cannot load properties directly from file", fileEx);
            }
        }
        return metadata;
    }

    /**
     * Fill in database with shuffled initial data as new records. A lot of
     * boiler plate. Switch to Spring JdbcUtil/Support
     *
     * @param metadata map of metadata
     * @return a number of rows generated
     */
    public static int populateDb(Map<String, String> metadata, int generateQuantity) throws SQLException {
        Set<String> performers = new HashSet<String>(INITIAL_QUANTITY);
        Set<String> activity = new HashSet<String>(INITIAL_QUANTITY);
        Set<Date> startDates = new HashSet<Date>(INITIAL_QUANTITY);
        Set<Date> endDates = new HashSet<Date>(INITIAL_QUANTITY);
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        PreparedStatement insert = null;
        int counter = 0;
        try {
            Class.forName(metadata.get(METADATA_PROPERTY_NAMES[0]));
            connection =
                    DriverManager.getConnection(
                    metadata.get(METADATA_PROPERTY_NAMES[1]),
                    metadata.get(METADATA_PROPERTY_NAMES[2]),
                    metadata.get(METADATA_PROPERTY_NAMES[3]));
            String selectAllSql = SELECT_ASTERISK
                    .replace(":database", metadata.get(METADATA_PROPERTY_NAMES[4]));
            statement = connection.createStatement(
                    ResultSet.FETCH_FORWARD | ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE);
            result = statement.executeQuery(selectAllSql);
            while (result.next()) {
                startDates.add(result.getDate("startDate"));
                endDates.add(result.getDate("endDate"));
                performers.add(result.getString("performer"));
                activity.add(result.getString("activity"));
            }
            counter = populate(connection, performers, activity, startDates, endDates);
        } catch (ClassNotFoundException ex) {
            logger.error("Cannot load database driver class.", ex);
        } catch (SQLException ex) {
            logger.error("Cannot populate database with records.", ex);
        } finally {
            try {
                logger.info("commiting..");
                connection.commit();
            } catch (SQLException commitEx) {
                logger.warn("cannot commit at the last action");
            }
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    logger.warn("Error has occured, when closing java.sql.ResultSet instance.", ex);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    logger.warn("Error has occured, when closing java.sql.Statement instance.", ex);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    logger.warn("Error has occured, when closing java.sql.Connection instance.", ex);
                }
            }
        }
        return counter;
    }

    private static int populate(Connection connection, Set... samples) throws SQLException {
        int counter = 0;
        Set<String> performers = samples[0];
        Set<String> activity = samples[1];
        Set<Date> startDates = samples[2];
        Set<Date> endDates = samples[3];
        PreparedStatement insert = connection.prepareStatement(
                INSERT_STATEMENT.replace(":database", DATABASE_NAME));
        Iterator<String> performersIterator = performers.iterator();
        connection.setAutoCommit(false);
        Savepoint savepoint = null;
        while (performersIterator.hasNext()) {
            String performerNext = performersIterator.next();
            Iterator<String> activityIterator = activity.iterator();
            while (activityIterator.hasNext()) {
                Iterator<Date> startDateIterator = startDates.iterator();
                String activityNext = activityIterator.next();
                while (startDateIterator.hasNext()) {
                    Iterator<Date> endDatesIterator = endDates.iterator();
                    Date startDateNext = startDateIterator.next();
                    while (endDatesIterator.hasNext()) {
                        try {
                            counter += 1;
                            if (counter > INIT_GENERATE_QUANTITY) {
                                connection.commit();
                                return counter;
                            }
                            logger.info("counter: {}", counter);
                            savepoint = connection.setSavepoint();
                            insert.setDate(1, (java.sql.Date) startDateNext);
                            insert.setDate(2, (java.sql.Date) endDatesIterator.next());
                            insert.setString(3, performerNext);
                            insert.setString(4, activityNext);
                            insert.execute();
                            if ((counter % RECORDS_PER_TRANSACTION) == 0) {
                                connection.commit();
                            }
                        } catch (SQLException ex) {
                            logger.warn("An error has occured while trying to insert a new record. Continuing to insert.", ex);
                            try {
                                connection.rollback(savepoint);
                            } catch (Exception e) {
                                try {
                                    connection.rollback();
                                } catch (SQLException rollEx) {
                                    logger.warn("cannot rollback after bad insert");
                                }
                            }
                        }
                    }
                }
            }
        }
        return counter;
    }
}
