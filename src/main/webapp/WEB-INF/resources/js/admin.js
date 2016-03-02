$(document).ready(function() {
    $("#tabs").tabs();
    storage.crowdPage = 0;

    var getCrowdSourcePagesCount = function() {
        $.ajax({
            url: "/admin/mls/count",
            type: "GET",
            dataType: "json",
            success: function(callback) {
                storage.crowdPageCount = Math.ceil(callback / 10.0);
            },
            error: function() {
                console.log("error count CSPage");
            }
        });
    };
    getCrowdSourcePagesCount();
    initCrowdSourcePage(storage.crowdPage);
    $("#next").click(function(event) {
        event.preventDefault();
        getCrowdSourcePagesCount();
        storage.crowdPage = (storage.crowdPage < storage.crowdPageCount - 1)
            ? storage.crowdPage + 1
            : storage.crowdPage;
        initCrowdSourcePage(storage.crowdPage);
    });
    $("#prev").click(function(event) {
        event.preventDefault();
        initCrowdSourcePage(storage.crowdPage == 0 ? 0 : --storage.crowdPage);
    });
});