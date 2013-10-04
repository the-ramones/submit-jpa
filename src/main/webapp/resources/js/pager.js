/* 
 * JS variant of pager. Useful with AJAX and without-reloading seach
 */

// attempt to templating
var PAGER_HTML = "#ajax_pager()";

function lazy_pager(pager, settings) {
    /* 
     * Defines pager appearance. If, for example equals 3,
     * then '<< 1 2 3 ... 'last' >>' will be drawn
     */
    // Max number of pages in pager
    var maxOnPager = parseInt(settings.maxOnPager);
    // Number of pages in pager
    var pages = pager.pageCount;
    // Current page
    var active = pager.page + 1;
    var $pager = $('div').addClass('pagination clearfix');
    $pager.append($('ul').addClass('clearfix'));
    var $prev = $('<li><a href="">«</a></li>');
    var $next = $('<li><a href="">»</a></li>');
    var $spacer = $('<li class="spacer">…</li>');
    var elem = '<li><a href=""></a></li>';
    if (pages > maxOnPager) {
        // draw 'spacer' and 'last available page' anchors

        /* Defines difference between active page and last page.
         * If $delta > $$maxOnPager, renders: << 2 3 4 5...100 >>
         * Otherwise: << 96 97 98 99 100 >>
         */
        var delta = pages - active;
        if (active === 1) {
            // append 'prev' anchor
            $pager.append($prev);
            $.prev.data('page', 'prev');
        }
        if (active > 1) {
            if (delta <= maxOnPager) {
                var lowerBound = pages - maxOnPager;
            } else {
                var lowerBound = active - 1;
            }
            var upperBound = loverBound + maxOnPager - 1;
        } else {
            var lowerBound = active;
            var upperBound = active + maxOnPager;
        }
        for (var i = lowerBound; i <= upperBound; i++) {
            var $anchor = $(elem);
            $pager.append($anchor);
            if (i === active) {
                $anchor.addClass('current');
            }
            $anchor.addClass('active');
            $anchor.data('page', i);
        }
        if (delta > maxOnPager) {
            $pager.append($spacer);
        }
        var last = pages;
        $last = $(elem);
        $pager.append($last)
        $last.addClass('active');
        if (last === active) {
            $last.addClass('current');
        }
        $last.data('page', last);
        if (active < pages) {
            $pager.append($next);
            $next.data('page', 'next');
        }
    } else {
        // draw all page's anchors without spacer
        if (active > 1) {
            $pager.append($prev);
            $prev.data('page', 'prev');
        }
        for (var i = 1; i <= pages; i++) {
            $anchor = $(elem);
            $pager.append($anchor);
            $anchor.addClass('active');
            if (i === active) {
                $anchor.addClass('current');
            }
            $anchor.data('page', i);
        }
        if (active < pages) {
            $pager.append($next);
            $next.data('page', 'next');
        }
    }
}
