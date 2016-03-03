<html>
<head>
    <title>ABChat</title>
    <link rel="stylesheet" href="/resources/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/resources/jquery-ui/jquery-ui.css"/>
    <script src="/resources/jquery-ui/external/jquery/jquery.js"></script>
    <script src="/resources/jquery-ui/jquery-ui.js"></script>
    <script src="/resources/bootstrap/js/bootstrap.min.js"></script>
    <script src="/resources/js/utils.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            window.storage = {};
            $.get("/resources/html_templates/welcome_form.html", function(data) {
                $("#main-window").html(data);
            });
        });
    </script>
</head>
<body>
<div id="main-window" class="ui-widget">

</div>
</body>
</html>