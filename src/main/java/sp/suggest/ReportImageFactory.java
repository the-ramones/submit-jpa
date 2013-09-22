package sp.suggest;

import sp.model.Report;

/**
 * Factory for creating {@link ReportImage} instances
 *
 * @author Paul Kulitski
 * @Report
 */
public class ReportImageFactory {

    /**
     * Creates {@link ReportImage} for {@link Report} instance
     *
     * @param report report object
     * @return report image
     */
    public static synchronized ReportImage getReportImage(Report report) {
        ReportImage reportImage = new ReportImage();
        reportImage.setIdentifier(report.getId());
        StringBuilder sb = new StringBuilder();
        sb.append(report.getPerformer()).append(", ").append(report.getActivity());
        reportImage.setRepresentation(sb.toString());
        return reportImage;
    }
}
