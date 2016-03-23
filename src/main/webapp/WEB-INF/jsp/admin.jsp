﻿<html>
<head>
	<meta charset="utf-8">
    <title>ABChat</title>
    <link rel="stylesheet" href="/resources/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/resources/jquery-ui/jquery-ui.css"/>
    <script src="/resources/jquery-ui/external/jquery/jquery.js"></script>
    <script src="/resources/jquery-ui/jquery-ui.js"></script>
    <script src="/resources/bootstrap/js/bootstrap.min.js"></script>
    <script src="/resources/js/utils.js"></script>
    <script src="/resources/js/ml.js"></script>
    <script src="/resources/js/bt.js"></script>
    <script src="/resources/js/strategy.js"></script>
    <script src="/resources/js/std_messages.js"></script>
    <script src="/resources/js/app_settings.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            window.storage = {};
            storage.crowdPage = 0;
            storage.BTPage = 0;
            storage.BTPageCount = 0;
            storage.crowdPageCount = 0;
            $.get("/resources/html_templates/admin_login.html", function(data) {
                $("#main-window").html(data);
            });
        });
    </script>
</head>
<body>
<div id="main-window" class="center-block text-center">

</div>
</body>
</html>