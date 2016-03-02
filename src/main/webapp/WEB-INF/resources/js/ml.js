var initCrowdSourcePage = function(page) {
    $.ajax({
        url: "/admin/mls/" + page,
        type: "GET",
        dataType: "json",
        success: function(callback) {
            var html = "<table style=\"width: 90%; word-break: break-all;\" class=\"table table-hover center-block\">";
            html += "<th>Стимул</th><th>Ответ</th><th></th>";
            $.each(callback, function(index, value) {
                html += "<tr><td name=\"stimulus\" id=\"" + value.id + "\">";
                html += value.stimulus + "</td>";
                html += "<td name=\"message\" id=\"" + value.id + "\">";
                html += value.message + "</td>";
                html += "<td style=\"text-align: right; width: 35%\">";
                html += "<button onclick=\"deleteMl(" + value.id + ")\" class=\"btn btn-default\" id=\"but-" + value.id + "\">Удалить</button></td>";
                html += "</tr>";
            });
            html += "</table>";
            $("#ml-list").html(html);
        },
        error: function() {
            console.log("error init CSPage");
        }
    });
};
var deleteMl = function(id) {
    $.ajax({
        url: "/admin/mls/delete/" + id,
        type: "DELETE",
        dataType: "json",
        success: function(callback) {
            initCrowdSourcePage(storage.crowdPage);
        },
        error: function() {
            console.log("error deleting ml");
        }
    });
};