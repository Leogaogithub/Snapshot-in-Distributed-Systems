package MyMessage;

public class MessageState extends Message {

	public boolean isPassive;	
	public MessageState(boolean passive){
		type = MessageParser.getSingleton().typeState;
		isPassive = passive;		
	}
	
	public String toString(){
		String res = type;
		res+=String.valueOf(isPassive);		
		return res;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
