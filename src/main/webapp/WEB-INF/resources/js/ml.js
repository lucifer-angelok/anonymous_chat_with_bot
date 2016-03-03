var getCrowdSourcePagesCount = function() {
    $.ajax({
        url: "/mLink/count",
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
var drawPages = function() {
    getCrowdSourcePagesCount();
    $("#page-number").html(storage.crowdPage + 1 + "/" + storage.crowdPageCount);
};
var initCrowdSourcePage = function(page) {
    $.ajax({
        url: "/mLink/page/" + page,
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
                html += "<button onclick=\"editMl(" + value.id + ")\"";
                html += " class=\"btn btn-default\"";
                html += "id=\"but-" + value.id + "\"><span class=\"glyphicon glyphicon-edit\"></span></button>";
                html += "<button onclick=\"deleteMl(" + value.id + ")\"";
                html += " class=\"btn btn-default\"";
                html += "id=\"but-" + value.id + "\"><span class=\"glyphicon glyphicon-remove\"></span></button></td>";
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
        url: "/mLink/" + id,
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
var editMl = function(id) {
    $.ajax({
        url: "/mLink/" + id,
        type: "GET",
        dataType: "json",
        success: function(callback) {
            $.get("/resources/html_templates/ml_add_update.html", function(html) {
                $("#form-div").html(html);
                $("#submit-add").click(function(e) {
                    e.preventDefault();
                    saveMl();
                });
                $("#stimulus").val(callback.stimulus);
                $("#msg").val(callback.message);
                $("#ml-id").val(callback.id);
            });
        },
        error: function() {
            console.log("error edit");
        }
    });
};

var saveMl = function() {
    var data = getJSON($("#add-form"));
    if (!data.stimulus && !data.message) {
        $("#message").html(warningMessage("Нежелательно иметь пустые сообщения"));
        return;
    }
    $.ajax({
        url: "/mLink/",
        type: "POST",
        dataType: "json",
        data: data,
        success: function(callback) {
            var html = "<p class=\"bg-success panel-body text-success\">"
                + callback.stimulus + "-"
                + callback.message +" Связка добавлена!</p>";
            $("#form-div-msg").html(html);
            $("#form-div").html("");
            setTimeout(function() {
                $("#form-div-msg").html("");
            }, 1800);
            initCrowdSourcePage(storage.crowdPage);
        },
        error: function() {
            console.log("error edit");
        }
    });
    initCrowdSourcePage(storage.crowdPage);
};