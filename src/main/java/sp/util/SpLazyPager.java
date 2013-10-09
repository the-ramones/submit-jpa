package sp.util;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reports! lazy pager. Allows to split search results into a pages using
 * current source items count and page size value. Use with SQL 'limit' and
 * 'offset' constraints on ordered select query. Order is important. Useful for:
 *
 * SELECT * FROM registry ORDER BY registry.recordTime LIMIT n OFFSET m
 *
 * @author Paul Kulitski
 */
public class SpLazyPager {

    protected transient static final Logger logger = LoggerFactory.getLogger(SpLazyPager.class);
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_MAX_LINKED_PAGES = 10;
    private int sourceCount;
    private Date refreshDate;
    private int pageSize;
    private int page;
    private int maxLinkedPages;
    private int maxOnPager;
    private int pageCount;

    public SpLazyPager() {
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.maxLinkedPages = DEFAULT_MAX_LINKED_PAGES;
        this.sourceCount = 0;
        this.refreshDate = new Date();
        this.pageCount = 0;
    }

    public SpLazyPager(int sourceCount) {
        this.sourceCount = sourceCount;
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.maxLinkedPages = DEFAULT_MAX_LINKED_PAGES;
        this.refreshDate = new Date();
        this.pageCount = sourceCount / pageSize + 1;
    }

    public void setSourceCount(int sourceCount) {
        this.sourceCount = sourceCount;
        this.refreshDate = new Date();
        this.pageCount = getPageCount();
    }

    public int getSourceCount() {
        return sourceCount;
    }

    public Date getRefreshDate() {
        return refreshDate;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.pageCount = getPageCount();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPage(int page) {
        if ((page >= 0) && (page < getPageCount())) {
            this.page = page;
        }
    }

    public int getPage() {
        return page;
    }

    public void setMaxLinkedPages(int maxLinkedPages) {
        this.maxLinkedPages = maxLinkedPages;
    }

    public int getMaxLinkedPages() {
        return maxLinkedPages;
    }

    public int getPageCount() {
        return (sourceCount / pageSize) + 1;
    }

    public boolean isFirstPage() {
        if (page == 1) {
            return true;
        }
        return false;
    }

    public boolean isLastPage() {
        if (page == (sourceCount / pageSize + 1)) {
            return true;
        }
        return false;
    }

    public void previousPage() {
        if (page > 0) {
            page -= 1;
        }
    }

    public void nextPage() {
        if (page < (sourceCount / pageSize)) {
            page += 1;
        }
    }

    /*
     * Zero-based counting
     */
    public int getFirstElementOnPage() {
        return page * pageSize;
    }

    /*
     * Zero-based counting
     */
    public int getLastElementOnPage() {
        if (isLastPage()) {
            return (page * pageSize) + (sourceCount - page * pageSize) - 1;
        }
        return (page + 1) * pageSize;
    }

    public int getPageOffset() {
        return page * pageSize;
    }

    /**
     * Sets page on the
     * {@link org.springframework.beans.support.PagedListHolder}. Allows setting
     * up page as {@link java.lang.String}. Acceptable values: everything that
     * can be passed into {@link java.lang.Integer#valueOf(java.lang.String)}
     * plus string values 'next', 'prev', 'first', 'last'
     *
     * NOTE: 1-based counting for convenience using in View
     *
     * @param page page being set up
     */
    public boolean setPage(String page) {
        Integer p = null;
        try {
            p = Integer.valueOf(page);
            if ((p > 0) && (p <= getPageCount())) {
                setPage(p - 1);
                return true;
            }
        } catch (NumberFormatException ex) {
        }
        Pattern regexp = Pattern.compile("^next|prev|last|first$", Pattern.CASE_INSENSITIVE);
        if (p == null) {
            if (regexp.matcher(page).matches()) {
                page = page.toLowerCase();
                if (page.equals("next")) {
                    nextPage();
                } else if (page.equals("prev")) {
                    previousPage();
                } else if (page.equals("last")) {
                    setPage(getPageCount() - 1);
                } else if (page.equals("first")) {
                    setPage(0);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "SpLazyPager{" + "sourceCount=" + sourceCount + ", refreshDate=" + refreshDate + ", pageSize=" + pageSize + ", page=" + page + ", maxLinkedPages=" + maxLinkedPages + '}';
    }

    public int getMaxOnPager() {
        return maxOnPager;
    }

    public void setMaxOnPager(int maxOnPager) {
        this.maxOnPager = maxOnPager;
    }

    public void setRefreshDate(Date refreshDate) {
        this.refreshDate = refreshDate;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
