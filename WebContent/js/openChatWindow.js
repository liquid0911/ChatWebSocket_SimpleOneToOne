var path = window.location.pathname;
var webCtx = path.substring(0, path.indexOf('/', 1));
function Connect() {
	var username = document.getElementById('username').value;
	var to = document.getElementById('to').value;
    
    var myWindow = window.open("/ChatWebSocket_SimpleOneToOne/chatRoom.html?username="+username+"&to="+to, "", "width=460,height=240");
			
}


