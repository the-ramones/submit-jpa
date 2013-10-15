package sp.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import sp.model.ajax.Statistics;

/**
 * Builder of PDF files. Renders statistics that represents by
 * {@link Statistics} instances. Have to be synchronized if shared between
 * multiple threads.
 *
 * NOTE: unresolved issue with Cyrylic and Unicode symbols. PDFBoc
 * implementation will be dropped
 *
 * @author Paul Kulitski
 */
@Lazy
@Component
public class SpPdfBoxPdfBuilder implements SpPdfBuilder {

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
     * Constants
     *   100 - top margin
     *   100 - bottop margin
     *   50 - left margin
     *   50 - right margin
     */
    static final float verticalDistance = 800f;
    static final float horizontalDistance = 600f;
    static final float bottomUpMargin = 80f;
    static final float leftRightMargin = 50f;
    static final float bottomUpDistance = verticalDistance - 2 * bottomUpMargin;
    static final float leftRightDistance = horizontalDistance - 2 * leftRightMargin;
    /*
     * Fonts
     */
    static PDFont helveticaBold = PDType1Font.HELVETICA_BOLD;
    static PDFont helvetica = PDType1Font.HELVETICA;
    /*
     * PDF document
     */
    PDDocument document;

    /*
     * PDF document initialization
     * Note: TextToPdf - converts text file into PDF file
     */
    {
        try {
            document = new PDDocument();
        } catch (IOException ex) {
            logger.error("Cannot create a new PDF document", ex);
        }
    }
    /*
     * Current text position
     */
    float vertPos = 0;
    float horPos = 0;
    private static Logger logger = LoggerFactory.getLogger(SpPdfBoxPdfBuilder.class);
    private DateFormat dateFormat;
    

    /**
     * Build a PDF file with statistics to be used as a attachment to an e-mail.
     * {@link SpStatsPdfBuilder#setStatistics(sp.model.ajax.Statistics)} and
     * {@link SpStatsPdfBuilder#setUsername(java.lang.String)} have to be used
     * before invocation.
     *
     * @return s path to PDF file
     */
    @Override
    public String build() {
        if ((username != null) && (statistics != null)) {
            StringBuilder sb = new StringBuilder(64);
            sb.append(PDF_PATH_PREFIX).append(getUsername()).append('/');
            if (locale == null) {
                setLocale(Locale.ENGLISH);
            }
            dateFormat = SpDateFormatFactory.getDateFormat(locale);
            if (date == null) {
                date = new Date();
            }
            String userDirectory = sb.toString();
            checkOrCreatePath(userDirectory);
            String currentDate = dateFormat.format(date).replace(' ', '_');
            sb.append("stats_").append(currentDate).append(".pdf");
            setDate(date);
            String path = sb.toString();
            try {
                createAppendablePDF(path);
                return path;
            } catch (COSVisitorException cex) {
                logger.error("Cannot create PDF file. Check PDFBox library", cex);
            } catch (IOException ioex) {
                logger.error("Cannot create PDF file on the server", ioex);
            }
        }
        return null;
    }

    private boolean checkOrCreatePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            return true;
        }
        return true;
    }

    /**
     * Create a linear PDF file. Uses simple PDF page positioning and splitting
     * content flow into pages.
     *
     * @param path path to store PDF file
     * @throws IOException
     * @throws COSVisitorException
     */
    private void createAppendablePDF(String path) throws IOException, COSVisitorException {

        PDPageContentStream stream =
                appendStringLine(null, messageSource.getMessage(PDF_TITLE_KEY, null, locale),
                75, 0, helveticaBold, 16);

        appendStringLine(stream, username + ", " + dateFormat.format(date),
                -15, -30, helveticaBold, 14);

        appendStringLine(stream, messageSource.getMessage(PDF_AMOUNTPERFORMERS_KEY, null, locale)
                + ": " + statistics.getCountPerformers(),
                -120, -40, helvetica, 14);

        appendStringLine(stream, messageSource.getMessage(PDF_AMOUTACTIVITIES_KEY, null, locale)
                + ": " + statistics.getCountActivities(),
                0, -32, helvetica, 14);

        appendStringLine(stream, messageSource.getMessage(PDF_AVG_KEY, null, locale)
                + ": " + statistics.getAverageRange()
                + messageSource.getMessage(PDF_DAYS_KEY, null, locale),
                0, -32, helvetica, 14);

        appendStringLine(stream, messageSource.getMessage(PDF_PERFORMER_KEY, null, locale)
                + ':', 0, -40, helveticaBold, 14);

        stream.moveTextPositionByAmount(20, 0);
        horPos += 20;

        float margin = getFontHeight(helvetica, 12);
        for (String performer : statistics.getPerformers().split(", ")) {
            stream = appendStringLine(stream, performer, 0, -margin, helvetica, 12);
        }

        appendStringLine(stream, messageSource.getMessage(PDF_ACTIVITIES_KEY, null, locale),
                -20, -40, helveticaBold, 14);
        stream.moveTextPositionByAmount(20, 0);
        horPos += 20;

        for (String activity : statistics.getActivities().split(", ")) {
            stream = appendStringLine(stream, activity, 0, -margin, helvetica, 12);
        }

        appendFooter(stream, messageSource.getMessage(PDF_FOOTER_KEY, null, locale),
                175, -500, helvetica, 12);
        stream.endText();
        stream.close();

        document.save(path);
        document.close();
    }

    /**
     * Appends a footer to the end of the last document's page.
     *
     * @param stream pdf page stream
     * @param line line to be drawn
     * @param indent horizontal indent
     * @param margin vertical margin
     * @param font font to be used
     * @param fontSize font size
     * @return page content stream object (allows chain calls)
     * @throws IOException
     */
    PDPageContentStream appendFooter(PDPageContentStream stream,
            String line, float indent, float margin,
            PDFont font, float fontSize) throws IOException {
        if (stream != null) {
            float width = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
            float pWidth = width * line.length();
            stream.moveTextPositionByAmount(leftRightMargin - horPos, leftRightMargin - vertPos);
            stream.setFont(font, fontSize);
            stream.drawString(line);
        }
        return stream;
    }

    /**
     * Appends a new line to the document performing paging if needed.
     *
     * @param stream page content stream
     * @param line string to be added
     * @param indent horizontal indent
     * @param margin vertical margin between lines
     * @param font font to be used for line rendering
     * @param fontSize font size
     * @return
     * @throws IOException
     */
    PDPageContentStream appendStringLine(PDPageContentStream stream,
            String line, float indent, float margin,
            PDFont font, float fontSize) throws IOException {
        PDPageContentStream contentStream = stream;
        /*
         * Firts page
         */
        if (contentStream == null) {
            PDPage newPage = new PDPage();
            document.addPage(newPage);
            contentStream = new PDPageContentStream(document, newPage);
            contentStream.beginText();
            vertPos = verticalDistance - bottomUpMargin + margin;
            horPos = leftRightMargin + indent;
            contentStream.moveTextPositionByAmount(horPos, vertPos);
        }
        if ((vertPos + margin) > bottomUpMargin) {
            vertPos += margin;
            horPos += indent;
            contentStream.moveTextPositionByAmount(indent, margin);
            contentStream.setFont(font, fontSize);
            contentStream.drawString(line);
            stream = contentStream;
            return stream;
        } else {
            stream.endText();
            stream.close();

            PDPage newPage = new PDPage();
            document.addPage(newPage);
            PDPageContentStream newStream = new PDPageContentStream(document, newPage);

            newStream.beginText();
            vertPos = verticalDistance - bottomUpMargin;
            horPos = horPos + indent;

            newStream.moveTextPositionByAmount(horPos + indent, vertPos);

            newStream.setFont(font, fontSize);

            newStream.drawString(line);

            stream = newStream;
            return stream;
        }
    }

    /**
     * Prints string line with PDF line feed.
     *
     * @param contentStream page content stream
     * @param font font to be used
     * @param lines strings to be drawn
     * @param x horizontal indent
     * @param y vertical margin
     * @throws IOException
     */
    private static void printMultipleLines(PDPageContentStream contentStream,
            PDFont font, List<String> lines, float x, float y) throws IOException {
        if (lines.isEmpty()) {
            return;
        }
        final int numberOfLines = lines.size();
        final float fontHeight = getFontHeight(font, 12);

        contentStream.beginText();
        contentStream.appendRawCommands(fontHeight + " TL\n");
        contentStream.moveTextPositionByAmount(x, y);
        contentStream.drawString(lines.get(0));
        for (int i = 1; i < numberOfLines; i++) {
            contentStream.appendRawCommands(escapeString(lines.get(i), "UTF-8"));
            contentStream.appendRawCommands(" \'\n");
        }
        contentStream.endText();
    }

    private static String escapeString(String text, String encoding) throws IOException {
        ByteArrayOutputStream buffer;
        try {
            COSString string = new COSString(text);
            buffer = new ByteArrayOutputStream();
            string.writePDF(buffer);
            return new String(buffer.toByteArray(), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Cannot escape string:" + e);
        }
    }

    private static float getFontHeight(PDFont font, float size) {
        float fontHeight =
                font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * size;
        return fontHeight;
    }
}
