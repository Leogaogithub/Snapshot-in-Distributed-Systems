package MyMessage;

public class MessageParser {
	public String typeOk = "ok";
	public String typeApp = "app";
	public String typeNodeId = "nodeId";
	public String stampEnd = "stampend";
	public String typeMarker = "marker";
	public String typeState = "state";
	public String typeFinish = "finish";
	
	static MessageParser single = new MessageParser();
	public static MessageParser getSingleton(){
		return single;
	}	
	private MessageParser(){
		
	}	
	
	public Message parser(String msg){
		if(msg.startsWith(typeOk)){
			return MessageFactory.getSingleton().getMessageOk();
		}else if(msg.startsWith(typeApp)){
			String content = msg.substring(typeApp.length(), msg.length());
			int endIndex = content.indexOf(stampEnd);
			String timeStamp = content.substring(0, endIndex);
			int contentStart = endIndex + stampEnd.length();
			content = content.substring(contentStart, content.length());
			return MessageFactory.getSingleton().getMessageApplication(timeStamp,content);			
		}else if(msg.startsWith(typeNodeId)){
			String content = msg.substring(typeNodeId.length(), msg.length());
			int id = Integer.parseInt(content);
			return MessageFactory.getSingleton().getMessageNodeID(id);			
		}else if(msg.startsWith(typeMarker)){
			return MessageFactory.getSingleton().getMessageMarker();			
		}else if(msg.startsWith(typeState)){
			String content = msg.substring(typeState.length(), msg.length());
			boolean isPassive = Boolean.parseBoolean(content);
			return MessageFactory.getSingleton().getMessageState(isPassive);				
		}else if(msg.startsWith(typeFinish)){
			return MessageFactory.getSingleton().getMessageFinish();
		}else{
			return null;
		}
	}
	
	public static void main(String[] args) {
		String smsg = "app1 2 3 4 5 6 stampend leol o l o";
		Message msg = MessageParser.getSingleton().parser(smsg);
		System.out.println(msg.toString());
	}

}
