		var ws
	    var path = window.location.pathname;
	    var webCtx = path.substring(0, path.indexOf('/', 1));
	    var username;
	    var to;//target


		window.onload=init;
		function init(){
			/*get me*/
			username = getPara("username"); 
			/*get target*/
			to = getPara("to"); 
			//chat websocket
			connect();
			document.getElementById('tittle').innerHTML = username+" to "+to;
			 
		}
			


	    
	    function connect() {
	    	var endPointURL = "ws://" + window.location.host + webCtx + "/ChatEndpoint/"+username+"/"+to;
	        ws = new WebSocket(endPointURL);


	        ws.onmessage = function(event) {
	            console.log(event.data);
	            var message = JSON.parse(event.data);
	            var html = '<div>'+message.from+ ': '+message.content+'</div>';

	            var msgArea = document.getElementById('msgArea');
	            if(message.content=='disconnected!'){
	            	html= '<div style="color:red;"><b>'+message.from+ ' disconnected!</b></div>';
	            }
	            msgArea.innerHTML += html;
	            msgArea.scrollTop = msgArea.scrollHeight;
	        };
	    }

	    function send() {
			
	        var msg = document.getElementById('msg');
	        var content = msg.value;
	        var html ='<div>me : '+content+'</div>';
						
			msg.value='';
	        var json = JSON.stringify({
	        	"to":to,
	            "content":content
	        });

	        ws.send(json);
	        var msgArea = document.getElementById('msgArea');
	        msgArea.innerHTML += html;
	        msgArea.scrollTop = msgArea.scrollHeight;
	    }

	    /*get parameter*/
	    function getPara(name) {
			  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
			  var regexS = "[\\?&]"+name+"=([^&#]*)";
			  var regex = new RegExp( regexS );
			  var results = regex.exec( window.location.href );
			  if( results == null )
			    return "";
			  else
			    return results[1];
		}