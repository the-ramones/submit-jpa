function wider() {
    $('div#article').animate({"width": "95%"});
}
function fadeIn() {
    $(".aside").fadeIn(400);
}
$(document).ready(function() {
    $('#hide_btn').click(function() {
        $(".aside").fadeOut(600);
        setTimeout(wider, 600);
        $(".sticky").css("visibility", "visible");
        //  $(".sticky").animate({visibility: "visible",left: "-=2%"}, 1000);
    });
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
    $(".sticky a").click(function() {
        $(".sticky").animate({left: "+=2%"}, 1000, function() {
            $(this).css({visibility: "hidden", left: "92%"});
            $('div#article').animate({"width": "57%"}, 600);
            setTimeout(fadeIn, 1000);
        });
    });

});
