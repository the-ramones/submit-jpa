/* Reports!
 * Actions on search box interactions
 */
$(document).ready(function() {

    addSuggestToSearchInput();

    /*
     * On-click modal appearing
     */
    $(".table tbody tr").click(function(event) {
        if (!$(event.target).is(".button-block, .action")) {
            var buttons = $(".button-block");
            $(this).toggleClass("selected");
        }
    });

    $(".table tbody tr").dblclick(function(event) {
        var buttons = $(".button-block");
        var tr = $(this);
        buttons.css({
            top: $(this).position().top + $(this).height(),
            right: "20px"//$(this).position().left + $(this).width()
        });
        // storing ID of clicked report for use with Ajax CRUD operations
        var id_val = tr.find("td:first").text().trim();
        buttons.data("id", id_val);
        tr.addClass("active-row");
        // append buttons to the row
        $(this).append(buttons);
        buttons.removeClass("hidden");
    });

    /*
     * Button-group addition
     */
    $(".table tbody tr").mouseenter(function(event) {
        // TODO: 
    });

    $(".table tbody tr").mouseleave(function(avent) {
        var buttons = $(".button-block");
        buttons.addClass("hidden");
    });

    /*
     * Action on 'use-index' checkbox check
     */
    $("#use-index-checkbox").click(function(e) {
        if ($(this).is(":checked")) {
            var $pbar = $("#index-progress");
            $pbar.show();
            $('label[name="index-progress-label"]').show();
            progress($pbar);
        }
    });
});

function addSuggestToSearchInput() {

    /*
     * Hide suggest list and fill in the search input with the test from
     * the suggester was clicked on
     */
    $(".subnav").on("click", "a", function(e) {
        e.preventDefault();
        /*
         * Prevent unneccesary 'bounce' scrolling
         */
        $("html").addClass("stop-scrolling");

        $("input[name='search']").val($(this).text());
        $(".subnav li").css({
            visibility: 'hidden'});
        $("html").removeClass("stop-scrolling");
    });

    $("input[name='search']").keyup(function(event) {
        var key = event.which;
        var lis = $(".subnav li");
        var search = $("input[name='search']");
        if (lis.size() > 0) {
            if ([34, 39, 40].indexOf(key) !== -1) {
                var selected = lis.filter(".selected");
                if (selected.length) {
                    if (!selected.is(":last-child")) {
                        selected.removeClass("selected");
                        selected = selected.next();
                        selected.addClass("selected");
                    }
                } else {
                    selected = lis.first();
                    selected.addClass("selected");
                }
                search.val(selected.find("a").text());
            }
            else if ([33, 37, 38].indexOf(key) !== -1) {
                var selected = lis.filter(".selected");
                if (selected.length) {
                    if (!selected.is(":first-child")) {
                        selected.removeClass("selected");
                        selected = selected.prev();
                        selected.addClass("selected");
                    }
                } else {
                    selected = lis.last();
                    selected.addClass("selected");
                }
                search.val(selected.find("a").text());
            }
        }
    });   
}



