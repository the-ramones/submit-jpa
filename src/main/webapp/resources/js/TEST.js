function get_uri(tile) {
    var id_val = $(tile).find("dl > dd").filter(":first").text().trim();
    // jQuery ajax + utilize Promise object
    $.ajax("/sp/report/ajax/uri", {
        method: "GET",
        async: true,
        cache: false,
        data: "id=" + id_val,
    }).done(function(uri) {
        var html = '    <h2>Report URI:</h2> \
            <input type="text" value="" style="width: 320px;"> \
            <div class="modal-block"> \
            <div class="button-block-modal"> \
            <button id="close-btn" class="action delete">Close</button> \
            </div> \
            </div> \
            ';
        $(html).find("input[type='text']").val(uri);
        modal.open({ content: html});
        $("#close-btn").click(function() {
            $("#close").click();
        });
    })}; 