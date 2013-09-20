function update_ajax_callback(e) {
    var url_update = "/sp/report/ajax/update";

    // before ajax call cause defaul action will be fired up
    e.preventDefault();
    alert("Watch out!");
    var serial_report = $("form[name='report-update']").serialize();
    serial_report = serial_report + "&id=" + encodeURI(id_val);
    $.ajax(url_update, {
        method: "POST",
        async: true,
        cache: false,
        data: serial_report,
        success: function(data) {
            $("#confirm").finish().html("Report has been successfully updated<br />in the database");
            $("#confirm").fadeIn(600)
                    .delay(3000)
                    .fadeOut(1200);
        },
        error: function() {
            $("#confirm").finish().html("Report cannot be updated<br />in the database");
            $("#confirm").fadeIn(600)
                    .delay(3000)
                    .fadeOut(1200);
        }
    });
    alert("Watch out!");
    $("#close-btn").click();
    return false;
    });
}
;