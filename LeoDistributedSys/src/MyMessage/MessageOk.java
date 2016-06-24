package MyMessage;

public class MessageOk extends Message {

	public MessageOk(){
		type = MessageParser.getSingleton().typeOk;
	}
	
	public String toString(){
		String res = type;
		return res;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
