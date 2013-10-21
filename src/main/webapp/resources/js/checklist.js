/* Reports!
 * Checklist JS library 
 */

/* Pop-up hint */
$(document).ready(function() {
    $(document.createElement("div"))
            .attr("id", "confirm")
            .appendTo($("body"))
            .hide();
});

/* Add to checklist handler */
function addToChecklist() {
    var indexes = [];
    $('tr.selected').each(function(index, value) {
        try {
            indexes.push(parseInt($(value).find('td:first-child').html()));
        } catch (error) {
            console.log("Error while parsing ID for a elemend beign selected");
        }
    });
    if (indexes.length > 0) {
        var indexesData = {"indexes": indexes};
        $.ajax(addToChecklistUrl,
                {method: "POST",
                    async: true,
                    cache: false,
                    data: indexesData,
                    complete: onWatchComplete,
                    dataType: 'text'});
    } else {
        $("#confirm").finish().html(SELECT_ONE_MSG)
                .fadeIn(600)
                .delay(3000)
                .fadeOut(1200);
    }
};

/* Callback for addToChecklist Ajax request */
function onWatchComplete(xhr, status) {
    if (status === "success") {
        $("#confirm").finish().html(ADDED_TO_CHECKLIST_MSG);
        $(".selected").removeClass("selected").addClass("selected-added");
    } else {
        $("#confirm").finish().html(NOTADDED_TO_CHECKLIST_MSG);
        console.log("Cannot fulfil request to the server: " + status);
    }
    $("#confirm").fadeIn(600)
            .delay(3000)
            .fadeOut(1200);
};



