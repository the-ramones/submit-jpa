package sp.util;

import java.util.List;

/**
 * Lightweight pager. Stores only IDs of a entities so it is safe to use it with
 * JPA-SQL queries (f.e. IN clause). Obsolete results just won't be included in
 * the result page. When use {@link SpLazyPager} additional control for
 * existence of entities needed.
 *
 * @author Paul Kulitski
 */
public class SpLightPager extends SpLazyPager {

    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public SpLightPager() {
        super();
    }

    public SpLightPager(int sourceCount) {
        super(sourceCount);
    }
}
