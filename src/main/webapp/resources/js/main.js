/* Reports!
 * Custom UI callbacks
 */
function wider() {
    $('div#article').animate({"width": "98%"});
    $("html").removeClass("stop-scrolling");
}

function fadeIn() {
    $(".aside").fadeIn(400);
    $("html").removeClass("stop-scrolling");
}

/*
 * jQuery layout enhancements
 */
$(document).ready(function() {
    /*
     * Sticker animation
     */
    $('#hide_btn').click(function() {
        $("html").addClass("stop-scrolling");
        $(".aside").hide('slow');
        setTimeout(wider, 600);
        $(".sticky").css("visibility", "visible");
    });
    $(".sticky a").click(function() {
        $("html").addClass("stop-scrolling");   
        $(".sticky").animate({left: "+=2%"}, 1000, function() {
            $(this).css({visibility: "hidden", left: "94.5%"});
            if ($("body").width() < 768) {
                var aWidth = '98%';
            } else {
                var aWidth = '57%';
            }
            $('div#article').animate({"width": aWidth}, 600);
            setTimeout(fadeIn, 1000);
        });
    });
    /*
     * Social animation
     */
    $('#facebook').mouseenter(function() {
        $(this).addClass("facebook-active");
    }).mouseleave(function() {
        $(this).removeClass("facebook-active").addClass("facebook-dark");
    });
    $('#twitter').mouseenter(function() {
        $(this).addClass("twitter-active");
    }).mouseleave(function() {
        $(this).removeClass("twitter-active").addClass("twitter-dark");
    });
    $('#google').mouseenter(function() {
        $(this).addClass("google-active");
    }).mouseleave(function() {
        $(this).removeClass("google-active").addClass("google-dark");
    });
    /*
     * Check if a native date picker exists
     * 
     * if (!Modernizr.inputtypes['date']) {
     *    $('input[type=date]').datepicker();   
     * }
     */
    $('input[name="startDate"]').datepicker({dateFormat: "dd M yy"});
    $('input[name="endDate"]').datepicker({dateFormat: "dd M yy"});

    /*
     * Sticky side menu actions 
     */
    $("#language").click(function() {
        $("#popup-login").hide(50);
        setTimeout(function() {
            $("#popup-language").fadeToggle(150);
        }, 50);
        return false;
    });
    $("#login").click(function() {
        $("#popup-language").hide(50);
        setTimeout(function() {
            $("#popup-login").fadeToggle(150);
        }, 50);
        return false;
    });
    $("#popup-login,#popup-language").mouseleave(function(event) {
        $(this).fadeOut(150);
    });
});

/*
 * Onchange of Time Period Bounds inputs
 */
function resetPeriodSelect(element) {
    $('select[name="timePeriod"]').val("none");
}

/*
 * Form Date bound filling 
 */
/*
 * Number of month that quarters start with (Jan, Apr, Jul, Sep)
 */
var qtr_start = [0, 3, 6, 9];
/*
 * Fills in start date and end date inputs rendered above
 * with corresponding time period bounds
 */
function periodChange(select) {

    // gets the index of selected option
    var idx = select.selectedIndex;
    // gets the value of selected option
    var period = select.options[idx].value;
    var startDate;
    var endDate;
    // current time in user agent
    var timestamp = new Date();
    switch (period) {
        case "last-qtr":
            var quater_number = timestamp.getMonth() % 3;
            startDate = new Date(timestamp.getFullYear(), qtr_start[quater_number], 1);
            endDate = new Date(timestamp.getFullYear(), qtr_start[quater_number] + 3, 0);
            $("input[name='startDate']").datepicker('setDate', startDate);
            $("input[name='endDate']").datepicker('setDate', endDate);
            break;
        case "last-month":
            var month = timestamp.getMonth();
            startDate = new Date(timestamp.getFullYear(), month - 1, 1);
            endDate = new Date(timestamp.getFullYear(), month, 0);
            $("input[name='startDate']").datepicker('setDate', startDate);
            $("input[name='endDate']").datepicker('setDate', endDate);
            break;
        case "last-calendar-year":
            var year = timestamp.getFullYear();
            startDate = new Date(year - 1, 0, 1);
            endDate = new Date(year, 0, 0);
            $("input[name='startDate']").datepicker('setDate', startDate);
            $("input[name='endDate']").datepicker('setDate', endDate);
            break;
        case "current-year-to-date":
            startDate = new Date(timestamp.getFullYear(), 0, 1);
            $("input[name='startDate']").datepicker('setDate', startDate);
            $("input[name='endDate']").datepicker('setDate', timestamp);
            break;
        case "current-qtr-to-date":
            var quater_number = timestamp.getMonth() % 3;
            startDate = new Date(timestamp.getFullYear(), qtr_start[quater_number], 1);
            $("input[name='startDate']").datepicker('setDate', startDate);
            $("input[name='endDate']").datepicker('setDate', timestamp);
            break;
        case "current-month-to-date":
            startDate = new Date(timestamp.getFullYear(), timestamp.getMonth(), 1);
            $("input[name='startDate']").datepicker('setDate', startDate);
            $("input[name='endDate']").datepicker('setDate', timestamp);
            break;
    }

    /*
     * Ajax loader overlay
     */
    function upload(button) {
        var $d = $("<div id='ajax-overlay'></div>");
        $("body").append($d);
        $d.fadeIn();
        $d.click(function(e) {
            e.preventDefault();
            $(this).fadeOut();
            $(this).remove();
            return false;
        });
    }
}
