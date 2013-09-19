function modal_update() {
    // getting a ajax backing object
    var url = "#springUrl('/report/ajax')";
    var html = '#modal_update( )';
    html = $(html);
    var id_val = $(delete_button).parent(".button-block").data("id").trim();
            $.get(url,
            {id: id_val},
    function(report) {
        html = $(html).find("#id").text(report.id).end()
                .find("#startDate").text((new Date(report.startDate)).toString()).end()
                .find("#endDate").text((new Date(report.endDate)).toString()).end()
                .find("#performer").text(report.performer).end()
                .find("#activity").text(report.activity).end();
        html.find("span").text(report.id);
        html = html.get(0).outerHTML;
        modal.open({content: html});
        $("#close-btn").click(function() {
            $("#close").click();
        });
    },
            'json'
            );
    var url_update = "#springUrl('/report/ajax/update')";
    var serial_report = $("form[name='report-update']").serialize();
    $("update-btn").click(function() {
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
            $("close-btn").click();
        },
                error: function() {
            $("#confirm").finish().html("Report cannot be updated<br />in the database");
            $("#confirm").fadeIn(600)
                    .delay(3000)
                    .fadeOut(1200);
            $("close-btn").click();
        }        
    });
    return false;
    });
};