package sp.util;

import java.nio.charset.Charset;
import java.util.Arrays;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 *
 * @author the-ramones
 */
public class EncodingPostProcessor implements BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String name)
            throws BeansException {
        if (bean instanceof AnnotationMethodHandlerAdapter) {
            HttpMessageConverter<?>[] convs = ((AnnotationMethodHandlerAdapter) bean).getMessageConverters();
            for (HttpMessageConverter<?> conv : convs) {
                if (conv instanceof StringHttpMessageConverter) {
//                    ((StringHttpMessageConverter) conv).setSupportedMediaTypes(
////                            Arrays.asList(new MediaType("text", "html",
//                            Charset.forName("UTF-8"))));
                }
            }
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String name)
            throws BeansException {
        return bean;
    }
    
//    <bean class = "EncodingPostProcessor " /> 
}
