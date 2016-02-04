<html>
<head>
    <title>ABChat</title>
    <link rel="stylesheet" href="/resources/css/bootstrap.css"/>
    <link rel="stylesheet" href="/resources/jquery-ui/jquery-ui.css"/>
    <script src="/resources/js/jquery-2.1.4.min.js"></script>
    <script src="/resources/jquery-ui/jquery-ui.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#button-register").button();
            $("#button-register").click(function(e) {
                e.preventDefault();
                $.get("/resources/html_templates/register_form.html", function (data) {
                    $("#dialog").html(data);
                });
            });
            $("#button-login").button();
            $("#button-login").click(function(e) {
                e.preventDefault();
                $.get("/resources/html_templates/login_form.html", function (data) {
                    $("#dialog").html(data);
                });
            });
        });
    </script>
</head>
<body>
<div id="mainWindow" class="ui-widget">
    <div id="welcome" class="text-center">
        <img src="/resources/img/logo.jpg" alt="LOGO"><br/>
        <p class="panel-body">Попробуй угадать бота!</p>
        <button id="button-login">Войти</button>
        <button id="button-register">Регистрация</button>
    </div>
    <div id="dialog"></div>
</div>
</body>
</html>