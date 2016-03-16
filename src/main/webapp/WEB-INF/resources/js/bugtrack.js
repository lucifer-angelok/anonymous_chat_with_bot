$(document).ready(function() {
    $("#bug-track-dialog").dialog({
        title: "Сообщение об ошибке",
        modal: true,
        resizable: false,
        draggable: false,
        close: function() {
            $(this).dialog("destroy").remove();
        },
        buttons: [
            {
                text: "Ок",
                click: function() {
                    var data = {
                        login: storage.userLogin,
                        description: $("#bug-msg").val()
                    };
                    if (!data.description) {
                        var html = warningMessage("Заполните все поля!");
                        $("#message").html(html);
                    } else {
                        $.ajax({                                                //check user
                            url: "/bugTrack",
                            type: "POST",
                            dataType: "json",
                            data: data,
                            success: function(callback) {
                                sendSuccess();
                                $("#bug-track-dialog").dialog("close");
                            },
                            error: function() {
                                console.log("error bug sending");
                                var html = errorMessage("Ошибка сервера!");
                                $("#bug-res-message").html(html);
                            }
                        });
                    }
                }
            }
        ]
    });
    var sendSuccess = function() {
        var okDialog = "";
        okDialog += "<div id=\"ok-dialog\">";
        okDialog += "Спасибо за ваше участие!";
        okDialog += "</div>";
        $("#main-window").append(okDialog);
        $("#ok-dialog").dialog({
            draggable: false,
            resizable: false,
            modal: true,
            close: function() {
                $(this).dialog("destroy").remove();
            },
            buttons: [
                {
                    text: "Ok",
                    click: function() {
                        $("#ok-dialog").dialog("close");
                    }
                }
            ]
        });
    };
});