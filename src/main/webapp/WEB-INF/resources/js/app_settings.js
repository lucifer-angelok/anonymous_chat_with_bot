var loadSettings = function() {
    $.ajax({
        url: "/admin/config",
        type: "GET",
        dataType: "json",
        success: function(callback) {
            $.get("/resources/html_templates/settings_form.html", function(data) {
                $("#app-settings").html(data);
                $("#bot-prob").val(callback.botProbability * 100);
                $("#points-win").val(callback.pointsWin);
                $("#points-loose").val(callback.pointsLoose);
                var $retries = $("#retries");
                if (callback.addBotAfter) {
                    $("#bot-after").prop("checked", true);
                    $retries.prop("disabled", false);
                }
                $retries.val(callback.retries);
                $("#settings-save").click(function(e) {
                    e.preventDefault();
                    saveSettings();
                });
            });
        },
        error: function() {
            console.log("error get settings");
        }
    });
};

var saveSettings = function() {
    var data = getJSON($("#settings-form"));
    data.addBotAfter = $("#bot-after").prop("checked");
    $.ajax({
        url: "/admin/config",
        type: "POST",
        data: data,
        dataType: "json",
        success: function() {
            $("#settings-message").html(successMessage("Настройки изменены!"));
            $("#settings-form").html("");
            setTimeout(function() {
                $("#settings-message").html("");
            }, 3000);
        },
        error: function() {
            console.log("error save settings");
        }
    });
};