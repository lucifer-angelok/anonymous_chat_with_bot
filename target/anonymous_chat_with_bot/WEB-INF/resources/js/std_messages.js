var saveStdMessage = function() {
    var $strategies = $("#strategies");
    var id = $strategies.val();
    var data = getJSON($("#std-msg-form"));
    if (id == -1) {
        return;
    } else if (!data.message) {
        $("#std-msg-message").html(warningMessage("Сообщение не должно быть пустым!"));
        setTimeout(function() {
            $("#std-msg-message").html("");
        }, 3000);
        return;
    }
    $.ajax({
        url: "/bot/strategy/" + id + "/messages",
        type: "POST",
        data: data,
        dataType: "json",
        success: function(callback) {
            showMessages();
            $("#for-strategy-message").html(successMessage("Сообщение " + callback.message + " сохранено!"));
            clearMsg();
        },
        error: function() {
            console.log("error save strategy");
        }
    });
};

var addStdMessageForm = function() {
    $.get("/resources/html_templates/au_std_message.html", function(data) {
        $("#for-adding-std-message").html(data);
        $("#submit-std-msg").click(function(e) {
            e.preventDefault();
            saveStdMessage();
        });
        $("#cancel-std-msg").click(function(e) {
            e.preventDefault();
            $("#for-adding-std-message").html("");
        });
    });
};

var showMessages = function() {
    var $strategies = $("#strategies");
    var id = $strategies.val();
    if (id == -1) {
        return;
    }
    $strategies.prop("disabled", true);
    $("#strategy-messages-hide").prop("disabled", false);
    $("#strategy-messages").prop("disabled", true);

    $("#strategy-add").prop("disabled", true);
    $("#strategy-edit").prop("disabled", true);
    $("#strategy-remove").prop("disabled", true);
    $.ajax({
        url: "/bot/strategy/" + id + "/messages",
        type: "GET",
        dataType: "json",
        success: function(callback) {
            var html = "<table class=\"table table-hover\">";
            html += "<tr><th>Сообщение</th><th></th></tr>";
            $.each(callback, function(index, value) {
                html += "<tr><td>" + value.message + "</td>";
                html += "<td style=\"text-align: right; width: 35%\">";
                html += "<button onclick=\"editMessage(" + value.id + ")\"";
                html += " class=\"btn btn-default\"";
                html += "id=\"but-" + value.id + "\"><span class=\"glyphicon glyphicon-edit\"></span></button>";
                html += "<button onclick=\"deleteMessage(" + value.id + ")\"";
                html += " class=\"btn btn-default\"";
                html += "id=\"but-" + value.id + "\"><span class=\"glyphicon glyphicon-remove\"></span></button></td>";
                html += "</tr>";
            });
            html += "</table>";
            html += "<button id=\"add-std-msg\" class=\"btn btn-default\">" +
                "<span class=\"glyphicon glyphicon-plus\"></span></button>";
            html += "<div id=\"for-adding-std-message\"></div>";
            $("#for-strategy-form").html(html);
            $("#add-std-msg").click(function(e) {
                e.preventDefault();
                addStdMessageForm();
            });
        },
        error: function() {
            console.log("error save strategy");
        }
    });
};

var hideMessages = function() {
    $("#strategy-add").prop("disabled", false);
    $("#strategy-edit").prop("disabled", false);
    $("#strategy-remove").prop("disabled", false);
    $("#strategies").prop("disabled", false);
    $("#for-strategy-form").html("");
    $("#strategy-messages-hide").prop("disabled", true);
    $("#strategy-messages").prop("disabled", false);
};

var editMessage = function(id) {
    addStdMessageForm();
    var sId = $("#strategies").val();
    $.ajax({
        url: "/bot/strategy/" + sId + "/messages/" + id,
        type: "GET",
        dataType: "json",
        success: function(callback) {
            $("#std-message").val(callback.message);
            $("#sm-id").val(callback.id);
        },
        error: function() {
            console.log("error get for update std message");
        }
    });
};

var deleteMessage = function(id) {
    var sId = $("#strategies").val();
    $.ajax({
        url: "/bot/strategy/" + sId + "/messages/" + id,
        type: "DELETE",
        dataType: "json",
        success: function(callback) {
            if (callback) {
                $("#for-strategy-message").html(successMessage("Удаление прошло успешно!"));
                clearMsg();
                showMessages();
            }
        },
        error: function() {
            console.log("error del std message");
        }
    });
};