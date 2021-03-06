var getBTPagesCount = function() {
    $.ajax({
        url: "/bugTrack/page/count",
        type: "GET",
        dataType: "json",
        success: function(callback) {
            storage.BTPageCount = callback;
        },
        error: function() {
            console.log("error count BTPage");
        }
    });
};
var drawBTPages = function() {
    $("#bug-page-number").html(storage.BTPage + 1 + "/" + storage.BTPageCount);
};
var initBTPage = function(page) {
    $.ajax({
        url: "/bugTrack/page/" + page,
        type: "GET",
        dataType: "json",
        success: function(callback) {
            var html = "<table style=\"width: 90%; word-break: break-all;\" class=\"table table-hover center-block\">";
            html += "<th style=\"width: 20%\">Пользователь</th><th style=\"width: 70%\">Сообщение</th><th style=\"width: 10%\"></th>";
            $.each(callback, function(index, value) {
                html += "<tr><td>";
                html += value.login + "</td>";
                html += "<td>";
                html += value.description + "</td>";
                html += "<td style=\"text-align: right; width: 35%\">";
                html += "<button onclick=\"deleteBT(" + value.id + ")\"";
                html += " class=\"btn btn-default\"";
                html += "id=\"but-" + value.id + "\"><span class=\"glyphicon glyphicon-remove\"></span></button></td>";
                html += "</tr>";
            });
            html += "</table>";
            $("#bug-track-list").html(html);
        },
        error: function() {
            console.log("error init BTPage");
        }
    });
};
var deleteBT = function(id) {
    $.ajax({
        url: "/bugTrack/" + id,
        type: "DELETE",
        dataType: "json",
        success: function(callback) {
            initBTPage(storage.BTPage);
        },
        error: function() {
            console.log("error deleting bt");
        }
    });
};