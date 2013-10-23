/* Reports!
 * Custom modal window implementaion using jQuery
 */
var modal = (function() {
    var
            method = {},
            $overlay,
            $modal,
            $content,
            $close;

    // Center the modal in the viewport
    method.center = function() {
        var top, left;

        top = Math.max($(window).height() - $modal.outerHeight(), 0) / 2;
        left = Math.max($(window).width() - $modal.outerWidth(), 0) / 2;

        $modal.css({
            top: top + $(window).scrollTop(),
            left: left + $(window).scrollLeft()
        });
    };

    // Open the modal
    method.open = function(settings) {
        $content.empty().append(settings.content);

        $modal.css({
            width: settings.width || 'auto',
            height: settings.height || 'auto'
        });

        // What to do with vertical resize, when window height is large?
        method.center();

        /* use event namespacing */
        $(window).bind('resize.modal', method.center);

        $modal.show();
        $overlay.show();
        
        /*
         * Stop resizing of modal width
         */
        $modal.css({            
            width: $modal.css("width")
        });
    };

    // Close the modal
    method.close = function() {
        $modal.hide();
        $overlay.hide();
        $content.empty();
        $(window).unbind('resize.modal');
    };

    $overlay = $('<div id="overlay"></div>');
    $modal = $('<div id="modal"></div>');
    $content = $('<div id="content" class="clearfix"></div>');
    $close = $('<a id="close" href="#">close</a>');

    $modal.hide();
    $overlay.hide();
    $modal.append($content, $close);

    $(document).ready(function() {
        $('body').append($overlay, $modal);
    });

    $close.click(function(e) {
        e.preventDefault();
        method.close();
    });

    return method;
}());


