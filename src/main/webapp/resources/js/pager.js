/* Reports!
 * JS variant of pager. Useful with AJAX and without-reloading seach
 */

// attempt to templating
var PAGER_HTML = "#ajax_pager()";

/*
 * Create a pager and returns as a jQuery object (TODO: revert to native DOM object?)
 * 
 * @param {type} pager serialized lightweight pager object (MUST HAVE: maxOnPager, pageCount, page)
 * @param {type} settings settings to be applied to the pager (currently not used)
 * @returns {Object} jQuery object that holds pager 
 */
function lazy_pager(pager, settings) {
    /* 
     * Defines pager appearance. If, for example equals 3,
     * then '<< 1 2 3 ... 'last' >>' will be drawn
     */
    // Max number of pages in pager
    var maxOnPager = pager.maxOnPager;
    // Number of pages in pager
    var pages = pager.pageCount;
    // Current page
    var active = pager.page + 1;
    //var $pager = $('<div>').addClass('pagination clearfix');
    var $pager = $('<ul>');
    $pager.addClass('clearfix');
    var $prev = $('<li><a>«</a></li>');
    var $next = $('<li><a>»</a></li>');
    var $spacer = $('<li class="spacer">…</li>');
    var elem = '<li><a></a></li>';
    if (pages > maxOnPager) {
        // draw 'spacer' and 'last available page' anchors

        /* Defines difference between active page and last page.
         * If $delta > $$maxOnPager, renders: << 2 3 4 5...100 >>
         * Otherwise: << 96 97 98 99 100 >>
         */
        var delta = pages - active;
        if (active !== 1) {
            // append 'prev' anchor
            $pager.append($prev);
            $prev.find('a').data('page', 'prev');
        }
        if (active > 1) {
            if (delta <= maxOnPager) {
                var lowerBound = pages - maxOnPager;
            } else {
                var lowerBound = active - 1;
            }
            var upperBound = lowerBound + maxOnPager - 1;
        } else {
            var lowerBound = active;
            var upperBound = active + maxOnPager;
        }
        for (var i = lowerBound; i <= upperBound; i++) {
            var $anchor = $(elem);
            $pager.append($anchor);
            if (i === active) {
                $anchor.find('a').addClass('current');
            }
            $anchor.find('a').addClass('active');
            $anchor.find('a').data('page', i);
            $anchor.find('a').text(i);
        }
        if (delta > maxOnPager) {
            $pager.append($spacer);
        }
        var last = pages;
        $last = $(elem);
        $pager.append($last);
        $last.find('a').addClass('active');
        if (last === active) {
            $last.find('a').addClass('current');
        }
        $last.find('a').data('page', last);
        $last.find('a').text(last);
        if (active < pages) {
            $pager.append($next);
            $next.find('a').data('page', 'next');
        }
    } else {
        // draw all page's anchors without spacer
        if (active > 1) {
            $pager.append($prev);
            $prev.find('a').data('page', 'prev');
        }
        for (var i = 1; i <= pages; i++) {
            $anchor = $(elem);
            $pager.append($anchor);
            $anchor.find('a').addClass('active');
            if (i === active) {
                $anchor.find('a').addClass('current');
            }
            $anchor.find('a').data('page', i);
            $anchor.find('a').text(i);
        }
        if (active < pages) {
            $pager.append($next);
            $next.find('a').data('page', 'next');
        }
    }
    var $wrapper = $('<div class="pagination clearfix"></div>');
    $wrapper.html($pager);
    return $wrapper;
}
