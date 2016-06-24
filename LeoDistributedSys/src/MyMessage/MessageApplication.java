package MyMessage;

public class MessageApplication extends Message {
	String content = "";
	public String timeStamp = "";
	public MessageApplication(String timeStamp, String con){
		type = MessageParser.getSingleton().typeApp;
		content = con;
		this.timeStamp = timeStamp;
	}
	
	public String toString(){
		String res = type;
		String end = MessageParser.getSingleton().stampEnd;
		res += timeStamp + end; 
		res += content;
		return res;
	}

}
