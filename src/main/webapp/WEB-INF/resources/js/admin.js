$(document).ready(function() {
    $("#tabs").tabs();
    storage.crowdPage = 0;
    initCrowdSourcePage(storage.crowdPage);
    $("#next").click(function(event) {
        event.preventDefault();
        storage.crowdPage = (storage.crowdPage < storage.crowdPageCount - 1)
            ? storage.crowdPage + 1
            : storage.crowdPage;
        initCrowdSourcePage(storage.crowdPage);
        drawPages();
    });
    $("#prev").click(function(event) {
        event.preventDefault();
        initCrowdSourcePage(storage.crowdPage == 0 ? 0 : --storage.crowdPage);
        drawPages();
    });
    $("#add-button").click(function(event) {
        event.preventDefault();
        $.get("/resources/html_templates/ml_add_update.html", function(html) {
            $("#form-div").html(html);
            $("#submit-add").click(function(e) {
                e.preventDefault();
                saveMl();
            });
        });
    });
    setInterval(drawPages, 500);
});