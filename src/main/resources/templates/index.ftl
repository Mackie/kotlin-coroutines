<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>EB Chat</title>
</head>
<body>
<div>Chat Fenster</div>
<div>
    <form id="form_message">
        <input type="text" name="message" id="message" />
        <input type="button" value="Send!" onclick="send()" />
    </form>
</div>
<div id="inbox" class="inbox" ></div>
</body>
<script>
    var user = "Mustermann"

    var connection = new WebSocket("ws://localhost:8080/socket");

    connection.onmessage = function (e) {
        var msg = JSON.parse(e.data);
        var div = document.getElementById('inbox');
        div.innerHTML += msg
    };

    function send() {
        var msg = document.getElementById("message").value;
        connection.send('{"text": "' + msg + '", "user": "' + user + '"}');
    }

    function format(msg) {
        return '<div class="message-line">' + msg.sender + '>>' + msg.text + '</div>'
    }
</script>
</html>