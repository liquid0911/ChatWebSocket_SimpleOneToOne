package webscoket.chat;

import java.io.IOException;
import java.util.HashMap;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;


@ServerEndpoint("/ChatEndpoint/{username}/{to}")
public class ChatEndpoint {
	private static HashMap<Session, String> users = new HashMap<>();
	private static HashMap<String, Room> rooms = new HashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username, @PathParam("to") String to)
			throws IOException, EncodeException {
		System.out.println(username);
		users.put(session, username);
		/* 創建room */
		String roomId1 = username +"-"+ to;
		String roomId2 = to +"-"+username;
		/* 確認room是否存在 */
		Boolean roomIsExist1 = rooms.containsKey(roomId1.toString());
		Boolean roomIsExist2 = rooms.containsKey(roomId2.toString());

		if (roomIsExist1 == false & roomIsExist2 == false) {
			/* 不存在就建新的，並存入user的sessionId以待之後比對用 */
			Room room = new Room(roomId1);
			room.setMeber1(session.getId());
			rooms.put(roomId1, room);
		} else {
			/* 若room已存在,找到該room並存入sessionId */
			if (roomIsExist1) {
				/*判斷是誰退出聊天室又再次登入，若沒有則放入成員2*/
				if(rooms.get(roomId1).getMeber2()!=""){
					(rooms.get(roomId1)).setMeber1(session.getId());
				}else{
					(rooms.get(roomId1)).setMeber2(session.getId());
				}

			} else if (roomIsExist2) {
				/*判斷是誰退出聊天室又再次登入，若沒有則放入成員1*/
				if(rooms.get(roomId2).getMeber1()!=""){
					(rooms.get(roomId2)).setMeber2(session.getId());
				}else{
					(rooms.get(roomId2)).setMeber1(session.getId());
				}
			}
		}
	}

	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		
		try {
			JSONObject jsonObjectIn = new JSONObject(message);
			Message msg = new Message();
			System.out.println(jsonObjectIn.getString("to"));
			msg.setTo(jsonObjectIn.getString("to"));
			msg.setContent(jsonObjectIn.getString("content"));
			sendMessageToOneUser(msg,session);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnClose
	public void onClose(Session Session, CloseReason reason) {
		
		try{
			for(String roomId:rooms.keySet()){
				if((rooms.get(roomId).getMeber1()).equals(Session.getId())){
					rooms.get(roomId).setMeber1("");
					
					if(rooms.get(roomId).getMeber2()==""){
						rooms.remove(roomId);
					}else{
						Message msg = new Message();
						for (Session aSession : users.keySet()) {
							/* 取得收訊息者的session並發出訊息 */
							if ((aSession.getId()).equals(rooms.get(roomId).getMeber2())) {
								msg.setTo(users.get(aSession));
								msg.setContent("disconnected!");
							}
						}
						sendMessageToOneUser(msg,Session);
					}
				};
				if((rooms.get(roomId).getMeber2()).equals(Session.getId())){
					rooms.get(roomId).setMeber2("");
					if(rooms.get(roomId).getMeber1()==""){
						rooms.remove(roomId);
					}else{
						Message msg = new Message();
						for (Session aSession : users.keySet()) {
							/* 取得收訊息者的session並發出訊息 */
							if ((aSession.getId()).equals(rooms.get(roomId).getMeber1())) {
								msg.setTo(users.get(aSession));
								msg.setContent("disconnected!");
							}
						}
						sendMessageToOneUser(msg,Session);
					}
				}
			}
		}catch(Exception e){
			
		}
		users.remove(Session);
		System.out.println(Session.getId() + ": Disconnected: " + Integer.toString(reason.getCloseCode().getCode()));
	}

	@OnError
	public void onError(Session userSession, Throwable e) {
//		 e.printStackTrace();
	}

	private static void sendMessageToOneUser(Message message,Session session) throws IOException {
		/* 確認線上真的有這個user */
		if (users.containsValue(message.getTo())) {
			/* 組合可能的roomId */
			String username = users.get(session);
			String roomId1 = username +"-"+ message.getTo();
			String roomId2 = message.getTo() +"-"+ username;

			/* 先將訊息存入JSONObject */
			JSONObject msg = new JSONObject();
			try {
				msg.put("from", username);
				msg.put("content", message.getContent());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/* 確定該roomId是否存在,再比對該發訊息者是哪個room的成員 */
			if (rooms.containsKey(roomId1)) {
				/* 透過roomId取得該room實體 */
				Room room = rooms.get(roomId1);
				for (Session aSession : users.keySet()) {
					/* 取得收訊息者的session並發出訊息 */
					if ((aSession.getId()).equals(room.getMeber2())) {
						aSession.getAsyncRemote().sendText(msg.toString());
					}
				}

			} else if (rooms.containsKey(roomId2)) {
				Room room = rooms.get(roomId2);
				for (Session aSession : users.keySet()) {
					if ((aSession.getId()).equals(room.getMeber1())) {
						aSession.getAsyncRemote().sendText(msg.toString());
					}
				}
			}

		}

	}
}