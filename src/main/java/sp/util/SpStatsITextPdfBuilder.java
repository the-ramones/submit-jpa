package sp.util;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import sp.model.ajax.Statistics;
import static sp.util.SpPdfBuilder.*;

/**
 * Builder of PDF files. Renders statistics that represents by
 * {@link Statistics} instances. Have to be synchronized if shared between
 * multiple threads.
 *
 * @author Paul Kulitski
 */
@Lazy
@Component
public class SpStatsITextPdfBuilder implements SpPdfBuilder {

    protected static final Logger logger = LoggerFactory.getLogger(SpStatsITextPdfBuilder.class);
    /*
     * Will be statically injected
     */
    public static String fontPath;
    private MessageSource messageSource;
    private Statistics statistics;
    private String username;
    private Locale locale;
    private Date date;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    /*
     * Fonts
     */
    static BaseFont bf;
    static Font titleFont;
    static Font titleOrangeFont;
    static Font subFont;
    static Font subOrangeFont;
    static Font midFont;
    static Font midBoldFont;
    static Font orangeFont;
    static Font smallBoldFont;
    static Font smallFont;
    static int[] margins = new int[]{36, 36, 54, 36};
    static float default_indent = 36.0f;

    @Override
    public String build() {
        if (checkFieldsOrSetDefaults()) {
            try {
                Document document = new Document(PageSize.A4,
                        margins[0], margins[1], margins[2], margins[3]);
                String file = constructFilename();
                if (file != null) {
                    PdfWriter.getInstance(document, new FileOutputStream(file));
                    document.open();
                    createUnicodeFonts();
                    addMetaData(document);
                    addTitle(document);
                    addContent(document);
                    addFooter(document);
                    document.close();
                    return file;
                }
            } catch (DocumentException dex) {
                logger.error("Didn't fill in the document", dex);
            } catch (IOException ioex) {
                logger.error("Didn't create base font for Unicode", ioex);
            } catch (Exception e) {
                logger.error("Didn't able to create PDF file due to exception", e);
            }
        }
        return null;
    }

    private String constructFilename() {
        StringBuilder sb = new StringBuilder(64);
        sb.append(PDF_PATH_PREFIX).append(getUsername()).append('/');
        DateFormat dateFormat = SpDateFormatFactory.getDateFormat(locale);
        if (date == null) {
            date = new Date();
        }
        String userDirectory = sb.toString();
        if (!checkOrCreatePath(userDirectory)) {
            return null;
        }
        String currentDate = dateFormat.format(date).replace(' ', '_');
        String hash = SpHasher.getRandomHash();
        sb.append("stats_").append(currentDate).append(hash).append(".pdf");
        String path = sb.toString();
        return path;
    }

    private boolean checkOrCreatePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            return true;
        }
        return true;
    }

    private boolean checkFieldsOrSetDefaults() {
        boolean result = true;
        if (statistics == null) {
            result = false;
        }
        if (locale == null) {
            locale = Locale.US;
        }
        if (username == null) {
            result = false;
        }
        if (date == null) {
            date = new Date();
        }
        if (messageSource == null) {
            result = false;
        }
        return result;
    }

    private void addMetaData(Document document) {
        /*
         * TODO: move to file
         */
        document.addTitle("Reports! Statistics iText PDF");
        document.addSubject("User statistics");
        document.addKeywords("reports, statistics, pdf");
        document.addAuthor("Paul Kulitski");
        document.addCreator("Reports! application-generated");
    }

    private void addTitle(Document document) {
        // write a title
        try {
            Paragraph preface = new Paragraph();
            addEmptyLine(preface, 1);
            Paragraph title = new Paragraph(messageSource.getMessage(PDF_TITLE_KEY, null, locale), titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            preface.add(title);
            addEmptyLine(preface, 1);

            // write username and date sub-title
            Paragraph subTitle = new Paragraph(username + ", " + SpDateFormatFactory.getDateFormat(locale).format(date), subOrangeFont);
            subTitle.setAlignment(Paragraph.ALIGN_CENTER);
            preface.add(subTitle);
            addEmptyLine(preface, 3);
            document.add(preface);
        } catch (DocumentException dex) {
            logger.error("Weren't able to write title to the document", dex);
        }
    }

    private void addContent(Document document) {
        try {
            Paragraph content = new Paragraph();
            // write inline statistics
            Paragraph stats = new Paragraph();
            stats.add(new Chunk(messageSource.getMessage(PDF_AMOUNTPERFORMERS_KEY, null, locale)
                    + ": " + statistics.getCountPerformers(), midFont));
            stats.add(Chunk.NEWLINE);
            stats.add(new Chunk(messageSource.getMessage(PDF_AMOUTACTIVITIES_KEY, null, locale)
                    + ": " + statistics.getCountActivities(), midFont));
            stats.add(Chunk.NEWLINE);
            stats.add(new Chunk(messageSource.getMessage(PDF_AVG_KEY, null, locale)
                    + ": " + statistics.getAverageRange() + " "
                    + messageSource.getMessage(PDF_DAYS_KEY, null, locale), midFont));
            stats.add(Chunk.NEWLINE);
            content.add(stats);
            addEmptyLine(content, 1);

            content.add(new Paragraph(messageSource.getMessage(PDF_PERFORMER_KEY, null, locale),
                    midBoldFont));

            Paragraph performers = new Paragraph(default_indent / 2);
            performers.setFont(midFont);
            performers.setIndentationLeft(default_indent);
            // write performers
            for (String performer : statistics.getPerformers().split(", ")) {
                performers.add(new Chunk(performer, midFont));
                performers.add(Chunk.NEWLINE);
            }
            addEmptyLine(performers, 1);
            content.add(performers);

            content.add(new Paragraph(messageSource.getMessage(PDF_ACTIVITIES_KEY, null, locale),
                    midBoldFont));

            Paragraph activities = new Paragraph(default_indent / 2);
            activities.setFont(midFont);
            activities.setIndentationLeft(default_indent);
            // write performers
            for (String activity : statistics.getActivities().split(", ")) {
                activities.add(new Chunk(activity, midFont));
                activities.add(Chunk.NEWLINE);
            }
            addEmptyLine(activities, 1);
            content.add(activities);

            document.add(content);

        } catch (DocumentException dex) {
            logger.error("Don't able to write title of the document", dex);
        }

    }

    private void createTable(Section subCatPart)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);

        PdfPCell c = new PdfPCell(new Phrase("Table Header 1"));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c);

        c = new PdfPCell(new Phrase("Table Header 2"));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c);

        c = new PdfPCell(new Phrase("Table Header 3"));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c);
        table.setHeaderRows(1);

        table.addCell("1.0");
        table.addCell("1.1");
        table.addCell("1.2");
        table.addCell("2.1");
        table.addCell("2.2");
        table.addCell("2.3");

        subCatPart.add(table);
    }

    private void createList(Section subCatPart) {
        List list = new List(true, false, 10);
        list.add(new ListItem("First point element"));
        list.add(new ListItem("Second point element"));
        list.add(new ListItem("Third point element"));
        subCatPart.add(list);
    }

    private void addEmptyLine(Paragraph paragraph, int count) {
        for (int i = 0; i < count; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private static void createUnicodeFonts() {
        /*
         * Base font for Unicode. Cyrilic fonts are rendered just right
         */
        try {
            /*
             * TODO: think of that way
             * FontFactory.register(fontPath, "Bitstream Cyberbit");
             */
            if (bf == null) {
                bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                titleFont = new Font(bf, 22, Font.BOLD);
                titleOrangeFont = new Font(bf, 22, Font.BOLD, new BaseColor(204, 103, 26));
                subFont = new Font(bf, 18, Font.BOLD);
                subOrangeFont = new Font(bf, 18, Font.BOLD, new BaseColor(204, 103, 26));
                midFont = new Font(bf, 14, Font.NORMAL);
                midBoldFont = new Font(bf, 14, Font.BOLD);
                orangeFont = new Font(bf, 12, Font.NORMAL, new BaseColor(204, 103, 26));
                smallBoldFont = new Font(bf, 12, Font.BOLD);
                smallFont = new Font(bf, 12, Font.NORMAL);
            }
        } catch (DocumentException dex) {
            logger.error("Problem with font loading", dex);
        } catch (IOException e) {
            logger.error("Weren't able to load Unicode font", e);
        }
    }

    private void addFooter(Document document) {
        try {
            Paragraph footer = new Paragraph(messageSource.getMessage(PDF_FOOTER_KEY, null, locale), orangeFont);
            footer.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(footer);
        } catch (DocumentException dex) {
            logger.error("Cannot write a footer to the document", dex);
        }
    }

    public static String getFontPath() {
        return fontPath;
    }

    public static void setFontPath(String fontPath) {
        SpStatsITextPdfBuilder.fontPath = fontPath;
    }
}
