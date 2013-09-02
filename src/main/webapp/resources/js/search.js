/*
 * Actions on search box interactions
 */
$(document).ready(function() {
    $(".subnav").on("click", "a", function() {
        $("input[name='search']").val($(this).text());
        $(".subnav li").css({
            visibility: 'hidden'});
    });
    $("input[name='search']").keydown(function(event) {
        var key = event.which;
        console.log(key);
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
    $("input[name='search']").keypress(function(event) {
        if ($(".subnav li").size() === 1) {
            for (var i = 0; i < 3; i++) {
                $(".subnav li").clone().appendTo($(".subnav"));
            }
        }
        $(".subnav li").css({
            visibility: 'visible',
            top: '120%',
            opacity: "1",
            transition: "all"
        });
    });
    /*
     * On-click modal apзearing
     */
    $(".table tbody tr").click(function(event) {
        if (!$(event.target).is(".button-block, .action")) {
            var buttons = $(".button-block");
            $(this).toggleClass("selected");
        }
        // TODO: add modal dialog
    });
    $(".table tbody tr").dblclick(function(event) {
        var buttons = $(".button-block");
        var tr = $(this);
        buttons.css({
            top: $(this).position().top + $(this).height(),
            right: "20px"//$(this).position().left + $(this).width()
        });
        $(this).append(buttons);
        buttons.removeClass("hidden");
    });
    /*
     * Button-group addition
     */
    $(".table tbody tr").mouseenter(function(event) {
        // TODO 
    });
    $(".table tbody tr").mouseleave(function(avent) {
        var buttons = $(".button-block");
        buttons.addClass("hidden");
    });
});

