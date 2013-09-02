/*
 * Custom UI callbacks
 */
function wider() {
    $('div#article').animate({"width": "90%"});
}
function fadeIn() {
    $(".aside").fadeIn(400);
}
/*
 * jQuery layout enhancements
 */
$(document).ready(function() {
    /*
     * Sticker animation
     */
    $('#hide_btn').click(function() {
        $(".aside").hide('slow');
        setTimeout(wider, 600);
        $(".sticky").css("visibility", "visible");
    });
    $(".sticky a").click(function() {
        $(".sticky").animate({left: "+=2%"}, 1000, function() {
            $(this).css({visibility: "hidden", left: "94.5%"});
            $('div#article').animate({"width": "57%"}, 600);
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

