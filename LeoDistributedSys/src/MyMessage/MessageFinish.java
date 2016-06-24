package MyMessage;

public class MessageFinish extends Message {

	public MessageFinish(){
		type = MessageParser.getSingleton().typeFinish;
	}
	
	public String toString(){
		String res = type;
		return res;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
