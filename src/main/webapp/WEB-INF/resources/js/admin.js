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
        mlAddForm();
    });
    setInterval(drawPages, 1000);

    //--------------------------------------------strategies
    loadStrategies();
    $("#strategy-add").click(function(e) {
        e.preventDefault();
        $.get("/resources/html_templates/strategy_form.html", function(html) {
            $("#for-strategy-form").html(html);
            $("#strategy-save").click(function(e) {
                e.preventDefault();
                saveStrategy();
            });
            $("#strategy-cancel").click(function(e) {
                e.preventDefault();
                $("#for-strategy-form").html("");
            });
        });
    });
    $("#strategy-edit").click(function(e) {
        e.preventDefault();
        $.get("/resources/html_templates/strategy_form.html", function(html) {
            $("#for-strategy-form").html(html);
            updateStrategy();
            $("#strategy-save").click(function(e) {
                e.preventDefault();
                saveStrategy();
            });
            $("#strategy-cancel").click(function(e) {
                e.preventDefault();
                $("#for-strategy-form").html("");
            });
        });
    });
    $("#strategy-remove").click(function(e) {
        e.preventDefault();
        deleteStrategy();
    });

    //------------std messages

    $("#strategy-messages").click(function(e) {
        e.preventDefault();
        showMessages();
    });

    $("#strategy-messages-hide").click(function(e) {
        e.preventDefault();
        hideMessages();
    });

    //---------------config

    $("#load-settings-button").click(function(e) {
        e.preventDefault();
        loadSettings();
    });

});