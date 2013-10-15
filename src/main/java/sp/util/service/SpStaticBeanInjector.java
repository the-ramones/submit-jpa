package sp.util.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sp.repository.ReportRepository;
import sp.util.SpStatisticsGenerator;
import sp.util.SpStatsITextPdfBuilder;

/**
 * Service bean for injecting dependencies into static fields of singletons.
 *
 * @author Paul Kulitski
 */
@Component
public class SpStaticBeanInjector {

    /*
     * If you intend to express annotation-driven injection by name, do not 
     * primarily use @Autowired, even if is technically capable of referring to
     * a bean name through @Qualifier values. Instead, use the JSR-250 @Resource
     * annotation, which is semantically defined to identify a specific target 
     * component by its unique name, with the declared type being irrelevant 
     * for the matching process.
     * 
     * As a specific consequence of this semantic difference, beans that are themselves 
     * defined as a collection or map type cannot be injected through @Autowired, 
     * because type matching is not properly applicable to them. Use @Resource for 
     * such beans, referring to the specific collection or map bean by unique name.
     * 
     * @Autowired applies to fields, constructors, and multi-argument methods, allowing
     * for narrowing through qualifier annotations at the parameter level. By contrast,
     * @Resource is supported only for fields and bean property setter methods with a
     * single argument. As a consequence, stick with qualifiers if your injection target
     * is a constructor or a multi-argument method.
     * 
     * http://stackoverflow.com/questions/4093504/resource-vs-autowired
     */
    @Resource
    @Qualifier(value = "reportRepositoryImpl")
    private ReportRepository reportRepository;
    @Value("${font.cyberbit}")
    private String fontPath;

    @PostConstruct
    public void postConstruct() {
        /*
         * Injects ReportRepository singleton to SpStaticticsGenerator static 
         * field.
         */
        SpStatisticsGenerator.setReportRepository(reportRepository);
        /*
         * Injects font path to be utilized for iText TrueType BaseFont creation
         */
        SpStatsITextPdfBuilder.setFontPath(fontPath);
    }
}
