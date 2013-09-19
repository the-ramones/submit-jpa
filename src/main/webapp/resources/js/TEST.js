
//TODO: l10n
$(document).ready(function() {
    $(document.createElement("div"))
            .attr("id", "confirm")
            .appendTo($("body"))
            .hide();
});

function add_to_watchlist() {
    var indexes = [];
    $('tr.selected').each(function(index, value) {
        try {
            indexes.push(parseInt($(value).find('td:first-child').html()));
        } catch (error) {
            console.log("Error while parsing ID for a elemend beign selected");
        }
    });
    if (indexes.length > 0) {
        var url = "#springUrl('/report/ajax/watch')"; 
                var indexesData = {"indexes": indexes};
        $.ajax(url, {
            method: "POST",
            async: true,
            cache: false,
            data: indexesData,
            complete: onWatchComplete,
            dataType: 'text'
        }
        );
    } else {
        $("#confirm").finish().html("Select at least one element. <br />Just click on it")
                .fadeIn(600)
                .delay(3000)
                .fadeOut(1200);
    }
}
;

function onWatchComplete(xhr, status) {
    if (status === "success") {
        $("#confirm").finish().html("Selected elements has been <br />added to your checklist");
        $(".selected").removeClass("selected").addClass("selected-added");
        //alert(status);
    } else {
        $("#confirm").finish().html("Cannot add selected elements.<br />Please, try later");
        console.log("Cannot fulfil request to the server: " + status);
    }
    $("#confirm").fadeIn(600)
            .delay(3000)
            .fadeOut(1200);
}


function modal_add() {
    var html = '#modal_add( )';
    modal.open({content: html});
    $("#close-btn").click(function() {
        $("#close").click();
    });
}
;

function modal_update() {
    var html = '#modal_update( )';
    modal.open({content: html});
    $("#close-btn").click(function() {
        $("#close").click();
    });
}
;

function modal_delete(delete_button) {
    // modal form HTML template
    var html = '#modal_delete( )';
    // template processing
    var url = "#springUrl('/report/ajax')";
    var id_val = $(delete_button).parent(".button-block").data("id").trim();
    $.get(url,
            {id: id_val},
    function(report) {
        alert($(html).html());
        html = $(html).find("#id").text(report.id).end()
                .find("#startDate").text(report.startDate).end()
                .find("#endDate").text(report.startDate).end()
                .find("#performer").text(report.performer).end()
                .find("#activity").text(report.activity).end();
        html = html.get(0).outerHTML;
        alert(html);
        modal.open({content: html});
    },
            'json');

    $("#close-btn").click(function() {
        $("#close").click();
    });
    $("#confirm-btn").click(function() {
        var url = "#springUrl('/report/ajax/remove')";
        $.post(url,
                {id: id_val},
        function(data) {
            if (data === 'success') {
                $("#confirm").finish().html("Selected element has been <br />removed from the database");
                $("#close").click();
            } else {
                $("#confirm").finish().html("Cannot remove selected element<br />cause it doesn't exist.<br /> Please, refresh the page");
                $("#close").click();
            }
        },
                'text'
                );
    });
}
;

function modal_action() {
    var html = '#modal_action( )';
    modal.open({content: html});
    $("#close-btn").click(function() {
        $("#close").click();
    });
}
;
