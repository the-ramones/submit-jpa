package sp.system;

import java.util.HashMap;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;


/**
 * Dynamic Properties File Reader Task
 *
 * @author onlinetechvision.com
 * @since 26 May 2012
 * @version 1.0.0
 *
 */
public class DynamicPropertiesFileReaderTask implements BeanFactoryAware {

    @Override
    public void setBeanFactory(BeanFactory bf) throws BeansException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    private static Logger logger = LoggerFactory.getLogger(DynamicPropertiesFileReaderTask.class);
//    private Properties coreDynamicPropertiesBean;
//    private HashMap<String, String> dynamicPropertiesMap;
//    private BeanFactory beanFactory;
//
//    /**
//     * Starts reading the dynamic properties
//     *
//     */
//    public void start() {
//
//        setCoreDynamicPropertiesBean(createCoreDynamicPropertiesBeanInstance());
//
//        logger.info("**** Dynamic Properties File Reader Task is being started... ****");
//        readConfiguration();
//        logger.info("**** Dynamic Properties File Reader Task is stopped... ****");
//    }
//
//    /**
//     * Reads all the dynamic properties
//     *
//     */
//    private void readConfiguration() {
//        readMessageContent();
//        readMinimumVisitorCount();
//        readMaximumVisitorCount();
//    }
//
//    /**
//     * Reads Message_Content dynamic property
//     *
//     */
//    private void readMessageContent() {
//        String messageContent = getCoreDynamicPropertiesBean()
//                .getProperty(SystemConstants.DYNAMIC_PROPERTY_MESSAGE_CONTENT,
//                SystemConstants.DYNAMIC_PROPERTY_MESSAGE_CONTENT_DEFAULT_VALUE);
//
//        if (messageContent.equals("")) {
//            getDynamicPropertiesMap().put(SystemConstants.DYNAMIC_PROPERTY_MESSAGE_CONTENT,
//                    SystemConstants.DYNAMIC_PROPERTY_MESSAGE_CONTENT_DEFAULT_VALUE);
//
//            logger.error(SystemConstants.DYNAMIC_PROPERTY_MESSAGE_CONTENT
//                    + " value is not found so its default value is set. Default value : "
//                    + SystemConstants.DYNAMIC_PROPERTY_MESSAGE_CONTENT_DEFAULT_VALUE);
//
//        } else {
//            messageContent = messageContent.trim();
//            getDynamicPropertiesMap().put(SystemConstants.DYNAMIC_PROPERTY_MESSAGE_CONTENT, messageContent);
//            logger.info(SystemConstants.DYNAMIC_PROPERTY_MESSAGE_CONTENT + " : "
//                    + getDynamicPropertiesMap().get(SystemConstants.DYNAMIC_PROPERTY_MESSAGE_CONTENT));
//        }
//    }
//
//    /**
//     * Reads Minimum_Visitor_Count dynamic property
//     *
//     */
//    private void readMinimumVisitorCount() {
//        String minimumVisitorCount = getCoreDynamicPropertiesBean()
//                .getProperty(SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT,
//                SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT_DEFAULT_VALUE).trim();
//
//        try {
//            if (Integer.parseInt(minimumVisitorCount) > 0) {
//                getDynamicPropertiesMap().put(SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT,
//                        minimumVisitorCount);
//
//                logger.info(SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT + " : "
//                        + getDynamicPropertiesMap().get(SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT));
//            } else {
//                getDynamicPropertiesMap().put(SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT,
//                        SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT_DEFAULT_VALUE);
//
//                logger.error("Invalid " + SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT +
//           
//                " value encountered. Must be greater than 0. Its default value is set. 
//                                 Default value 
//         : "+ SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT_DEFAULT_VALUE);
//   }
//  } catch (NumberFormatException nfe) {
//            logger.error("Invalid " + SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT
//                    + " value encountered. Must be numeric!", nfe);
//
//            logger.warn(SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT
//                    + " default value is set. Default value : "
//                    + SystemConstants.DYNAMIC_PROPERTY_MINIMUM_VISITOR_COUNT_DEFAULT_VALUE);
//        }
//    }
//
//    /**
//     * Reads Maximum_Visitor_Count dynamic property
//     *
//     */
//    private void readMaximumVisitorCount() {
//        String maximumVisitorCount = getCoreDynamicPropertiesBean()
//                .getProperty(SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT,
//                SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT_DEFAULT_VALUE).trim();
//        try {
//            if (Integer.parseInt(maximumVisitorCount) > 0) {
//                getDynamicPropertiesMap().put(SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT, maximumVisitorCount);
//
//                logger.info(SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT + " : "
//                        + getDynamicPropertiesMap()
//                        .get(SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT));
//            } else {
//                getDynamicPropertiesMap().put(SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT,
//                        SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT_DEFAULT_VALUE);
//
//                logger.error("Invalid " + SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT
//                        + " value encountered. Must be greater than 0. Its default value is set. Default value : "
//                        + SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT_DEFAULT_VALUE);
//
//            }
//        } catch (NumberFormatException nfe) {
//            logger.error("Invalid " + SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT
//                    + " value encountered. Must be numeric!", nfe);
//
//            logger.warn(SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT
//                    + " default value is set. Default value : "
//                    + SystemConstants.DYNAMIC_PROPERTY_MAXIMUM_VISITOR_COUNT_DEFAULT_VALUE);
//        }
//    }
//
//    /**
//     * Gets CoreDynamicPropertiesBean
//     *
//     * @return Properties coreDynamicPropertiesBean
//     */
//    public Properties getCoreDynamicPropertiesBean() {
//        return coreDynamicPropertiesBean;
//    }
//
//    /**
//     * Sets CoreDynamicPropertiesBean
//     *
//     * @param Properties coreDynamicPropertiesBean
//     */
//    public void setCoreDynamicPropertiesBean(Properties coreDynamicPropertiesBean) {
//        this.coreDynamicPropertiesBean = coreDynamicPropertiesBean;
//    }
//
//    /**
//     * Gets DynamicPropertiesMap
//     *
//     * @return HashMap dynamicPropertiesMap
//     */
//    public HashMap<String, String> getDynamicPropertiesMap() {
//        return dynamicPropertiesMap;
//    }
//
//    /**
//     * Sets DynamicPropertiesMap
//     *
//     * @param HashMap dynamicPropertiesMap
//     */
//    public void setDynamicPropertiesMap(HashMap<String, String> dynamicPropertiesMap) {
//        this.dynamicPropertiesMap = dynamicPropertiesMap;
//    }
//
//    /**
//     * Gets a new instance of CoreDynamicPropertiesBean
//     *
//     * @return Properties CoreDynamicPropertiesBean
//     */
//    public Properties createCoreDynamicPropertiesBeanInstance() {
//        return (Properties) this.beanFactory.getBean(SystemConstants.BEAN_NAME_CORE_DYNAMIC_PROPERTIES_BEAN);
//    }
//
//    /**
//     * Sets BeanFactory
//     *
//     * @param BeanFactory beanFactory
//     */
//    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//        this.beanFactory = beanFactory;
//    }
}