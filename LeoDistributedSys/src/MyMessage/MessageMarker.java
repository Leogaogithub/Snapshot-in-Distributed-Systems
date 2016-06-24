package MyMessage;

public class MessageMarker extends Message {

	public MessageMarker(){
		type = MessageParser.getSingleton().typeMarker;
	}
	
	public String toString(){
		String res = type;
		return res;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
