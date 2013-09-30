package sp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sp.model.ajax.Statistics;

/**
 * Builder of PDF files. Renders statistics that represents by
 * {@link Statistics} instances. Have to be synchronized if shared between
 * multiple threads.
 *
 * @author Paul Kulitski
 */
public class SpStatsPdfBuilder {

    /*
     * Localized messages:
     *  keys: 'email.pdf.title', 'email.pdf.performers', 'email.pdf.activities', 
     *        'email.pdf.footer'
     */
    Map messages = new HashMap();
    static String user = "Paul Daniel";
    static Date date = new Date();
    static String performers = "dan van, man can, sam tam,dan van, man can, sam tam,d van, man can, sam tam,dan van, man can, sam tam,dan van, man can, sam tam,dan van, man can, sam tam,dan van, man can, sam tam,dan van, man can, sam tam,dan van, man can, sam tam,dan van, man can, sam tam,dan van, man can, sam tam,dan van, man can, sam tam";
    static String activities = "dance, drinking, face hilling, pit stopping, dance, drinking, face hilling, pit stopping, dance, drinking, face hilling, pit stopping, dance, drinking, face hilling, pit stopping, dance, drinking, face hilling, pit stopping, dance, drinking, face hilling, pit stopping, dance, drinking, face hilling, pit stopping, dance, drinking, face hilling, pit stopping, dance, drinking, face hilling, pit stopping, dance, drinking, face hilling, pit stopping";
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
    
    private static Logger logger = LoggerFactory.getLogger(SpStatsPdfBuilder.class);

    public void build() throws IOException, COSVisitorException {
        if ((messages != null) && (user != null) && (statistics != null)) {
            createAppendablePDF();
        }
    }

    private void createAppendablePDF() throws IOException, COSVisitorException {

        PDPageContentStream stream =
                appendStringLine(null, "Reports! checklist statistics",
                75, 0, helveticaBold, 16);

        appendStringLine(stream, user + ", " + getFormattedDate(date),
                -15, -30, helveticaBold, 14);

        appendStringLine(stream, "Amount of distinct performers: " + 12L,
                -120, -40, helvetica, 14);

        appendStringLine(stream, "Amount of distinct activities: " + 8L,
                0, -32, helvetica, 14);

        appendStringLine(stream, "Average activity duration: " + 15.4 + " day(s)",
                0, -32, helvetica, 14);

        // append list of performers
        appendStringLine(stream, "Performers: ", 0, -40, helveticaBold, 14);
        stream.moveTextPositionByAmount(20, 0);
        horPos += 20;

        float margin = getFontHeight(helvetica, 12);
        for (String performer : performers.split(", ")) {
            stream = appendStringLine(stream, performer, 0, -margin, helvetica, 12);
        }

        // append list of activities
        appendStringLine(stream, "Activities: ", -20, -40, helveticaBold, 14);
        stream.moveTextPositionByAmount(20, 0);
        horPos += 20;

        for (String activity : activities.split(", ")) {
            stream = appendStringLine(stream, activity, 0, -margin, helvetica, 12);
        }

        appendFooter(stream, "Reports! 2013, Paul Kulitski", 175, -500,
                helvetica, 12);
        stream.endText();
        stream.close();

        document.save("files/stats3.pdf");
        document.close();
    }

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
     * @param font font to be used for line renddering
     * @param fontSize font size
     * @return
     * @throws IOException
     */
    PDPageContentStream appendStringLine(PDPageContentStream stream,
            String line, float indent, float margin,
            PDFont font, float fontSize) throws IOException {
        PDPageContentStream contentStream = stream;
        System.out.println("VER.POS: " + vertPos);
        System.out.println("HOR.POS: " + horPos);
        System.out.println("LINE: " + line);
        /*
         * Firts page
         */
        if (contentStream == null) {
            System.out.println("NULL STREAM");
            PDPage newPage = new PDPage();
            document.addPage(newPage);
            contentStream = new PDPageContentStream(document, newPage);
            contentStream.beginText();
            vertPos = verticalDistance - bottomUpMargin + margin;
            horPos = leftRightMargin + indent;
            contentStream.moveTextPositionByAmount(horPos, vertPos);

            System.out.println("VER.POS: " + vertPos);
            System.out.println("HOR.POS: " + horPos);
        }
        //
        if ((vertPos + margin) > bottomUpMargin) {
            // CURRENT PAGE
            System.out.println("OLD STREAM");
            vertPos += margin;
            horPos += indent;
            contentStream.moveTextPositionByAmount(indent, margin);
            contentStream.setFont(font, fontSize);
            contentStream.drawString(line);
            stream = contentStream;
            return stream;
        } else {
            System.out.println("NEW STREAM");
            // NEXT PAGE
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

    private static String getFormattedDate(Date date) {
        DateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        if (date == null) {
            return format.format(new Date());
        } else {
            return format.format(date);
        }
    }
}
