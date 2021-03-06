$(document).ready(function() {
    $("#tabs").tabs();
    storage.crowdPage = 0;
    getCrowdSourcePagesCount();
    setInterval(getCrowdSourcePagesCount, 10000);
    initCrowdSourcePage(storage.crowdPage);
    setInterval(function() {
        initCrowdSourcePage(storage.crowdPage);
    }, 10000);
    $("#next").click(function(event) {
        event.preventDefault();
        storage.crowdPage = (storage.crowdPage < storage.crowdPageCount - 1)
            ? storage.crowdPage + 1
            : storage.crowdPage;
        initCrowdSourcePage(storage.crowdPage);
        getCrowdSourcePagesCount();
        drawPages();
    });
    $("#prev").click(function(event) {
        event.preventDefault();
        initCrowdSourcePage(storage.crowdPage == 0 ? 0 : --storage.crowdPage);
        getCrowdSourcePagesCount();
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

    $("#mail-submit").click(function(e) {
        e.preventDefault();
        var data = {
            message: $("#mail-m").val(),
            subject: $("#mail-s").val()
        };
        if (!data.message || !data.subject) {
            $("#mail-message").html(warningMessage("Пустые поля!"));
            setTimeout(function() {
                $("#mail-message").html("");
            }, 3000);
            return;
        }
        $("#mail-submit").prop("disabled", true);
        $.ajax({
            url: "/admin/mail",
            type: "POST",
            dataType: "json",
            data: data,
            success: function(callback) {
                $("#mail-submit").prop("disabled", false);
                $("#mail-message").html(successMessage("Отправлено " + callback.copies + " копий!"));
                setTimeout(function() {
                    $("#mail-message").html("");
                }, 3000);
            },
            error: function() {
                $("#mail-submit").prop("disabled", false);
                console.log("error");
                var html = errorMessage("Ошибка сервера!");
                $("#mail-message").html(html);
                setTimeout(function() {
                    $("#mail-message").html("");
                }, 3000);
            }
        });
    });

    //-------statistics
    $("#start-date").datepicker();
    $("#end-date").datepicker();
    $("#info-button").click(function(e) {
        e.preventDefault();
        var data = {
            startDate: $("#start-date").val(),
            endDate: $("#end-date").val()
        };
        $.ajax({
            url: "/admin/statistics",
            type: "POST",
            dataType: "json",
            data: data,
            success: function(callback) {
                var html = "<h2 class=\"text-info\">Пользователи</h2>";
                html += "<br><p class=\"text-area\">Пользователей всего: " + callback.usersCount;
                html += "<br>Зарегистрировано за промежуток: " + callback.registered;
                html += "<br>Активно за промежуток: " + callback.userActivity;
                html += "<br>Сообщений отправлено: " + callback.messagesCount;
                html += "</p><br>";
                html += "<h2 class=\"text-info\">Боты</h2>";
                html += "<br><p class=\"text-area\">Процент правильных ответов: " + callback.correctPercentage
                    + "% (Всего ответов: " + callback.chatCount + ")";
                html += "<br>Обмануто ботом: " + callback.fooledByBotPercentage
                    + "% (Всего ответов в чатах с ботом: " + callback.botChatsCount + ")";
                html += "<br>Объем базы CrowdSource: " + callback.sizeOfCrowdSource;
                html += "<br>Новых запией в CrowdSource: " + callback.newCrowdSourceRecords;
                html += "</p>";
                $("#statistics").html(html);
            },
            error: function() {
                var html = errorMessage("Ошибка сервера!");
                $("#statistics").html(html);
                setTimeout(function() {
                    $("#statistics").html("");
                }, 3000);
            }
        });
    });
    $("#bug-page-next").click(function(event) {
        event.preventDefault();
        storage.BTPage = (storage.BTPage < storage.BTPageCount - 1)
            ? storage.BTPage + 1
            : storage.BTPage;
        initBTPage(storage.BTPage);
        getBTPagesCount();
        drawBTPages();
    });
    $("#bug-page-prev").click(function(event) {
        event.preventDefault();
        initBTPage(storage.BTPage == 0 ? 0 : --storage.BTPage);
        getBTPagesCount();
        drawBTPages();
    });
    getBTPagesCount();
    initBTPage(storage.BTPage);
    setInterval(function() {
        initBTPage(storage.BTPage);
    }, 10000);
    setInterval(getBTPagesCount, 10000);
    setInterval(drawBTPages, 1000);
});