package sp.util;

import java.io.Serializable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Enhanced {@link Pageable}. Mutable
 *
 * @author Paul Kulitski
 */
public class SpPageable implements Pageable, Serializable {

    private static final long serialVersionUID = 8280445938845391236L;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private Sort sort;

    public SpPageable() {
        pageNumber = 0;
        pageSize = 0;
    }

    public SpPageable(int pageNumber, int pageSize, long totalElements, Sort sort) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.sort = sort;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setPage(int page) {
        pageNumber = page;
    }

    public void setPageSize(int size) {
        pageSize = size;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getOffset() {
        return pageSize * pageNumber;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        pageNumber++;
        return this;
    }

    @Override
    public Pageable previousOrFirst() {
        if (pageNumber > 0) {
            pageNumber--;
        }
        return this;
    }

    @Override
    public Pageable first() {
        pageNumber = 0;
        return this;
    }

    @Override
    public boolean hasPrevious() {
        if (pageNumber > 0) {
            return true;
        }
        return false;
    }

    public boolean hasNext() {
        if (pageNumber <= totalElements / pageSize ) {
            return true;
        }
        return false;
    }

    public long getTotalPages() {
        return (totalElements /  pageSize) + 1;
    }
}