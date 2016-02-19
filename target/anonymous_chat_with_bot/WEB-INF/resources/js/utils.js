function getJSON($form) {                                                       //serialize form to json
    var jsonData = {};
    var formData = $form.serializeArray();
    $.each(formData, function () {
        if (jsonData[this.name]) {
            if (!jsonData[this.name].push) {
                jsonData[this.name] = [jsonData[this.name]];
            }
            jsonData[this.name].push(this.value || '');
        } else {
            jsonData[this.name] = this.value || '';
        }

    });
    return jsonData;
}
var warningMessage = function (message) {                                       //create warning message with jquery-ui
    var html = "";
    html += "<div class=\"ui-state-highlight ui-corner-all\" style=\"margin-top: 20px; padding: 0 .7em;\">"
    html += "<p><span class=\"ui-icon ui-icon-info\" style=\"float: left; margin-right: .3em;\"></span>"
    html += message + "</p>"
    html += "</div>";
    return html;
};
var errorMessage = function (message) {                                         //error message with jquery-ui
    var html = "";
    html += "<div class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em;\">";
    html += "<p><span class=\"ui-icon ui-icon-alert\" style=\"float: left; margin-right: .3em;\"></span>";
    html += message + "</p>";
    html += "</div>";
    return html;
};