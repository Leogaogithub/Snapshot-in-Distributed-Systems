package MyChannel;

import java.io.IOException;
import java.nio.ByteBuffer;

import MyUtil.ConfigExpert;
import MyUtil.UtilityTool;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;


public class InOutChannel {
	
	SctpChannel channel = null;
	int outId = 0;
	
	public InOutChannel(int id, SctpChannel ch){
		channel = ch;
		outId = id;
	}
	
	public void send(String message){
		send(channel, message);
	}
	
	public static void send(SctpChannel sctpChannel, String message){
		int MESSAGE_SIZE = ConfigExpert.getSingleton().MESSAGE_SIZE;
		try
		{
			ByteBuffer byteBuffer = ByteBuffer.allocate(MESSAGE_SIZE);	
			//Before sending messages add additional information about the message
			MessageInfo messageInfo = MessageInfo.createOutgoing(null,0);			
			//convert the string message into bytes and put it in the byte buffer
			byteBuffer.put(message.getBytes());
			//Reset a pointer to point to the start of buffer 
			byteBuffer.flip();
			sctpChannel.send(byteBuffer,messageInfo);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static String recieve(SctpChannel sctpChannel){
		String message = "";
		int MESSAGE_SIZE = ConfigExpert.getSingleton().MESSAGE_SIZE;
		try
		{
			ByteBuffer byteBuffer = ByteBuffer.allocate(MESSAGE_SIZE);
			MessageInfo messageInfo = sctpChannel.receive(byteBuffer,null,null);
			//byteBuffer.flip();
			//Just seeing what gets stored in messageInfo
			System.out.println(messageInfo);
			//Converting bytes to string. This looks nastier than in TCP
			//So better use a function call to write once and forget it :)
			message = UtilityTool.byteToString(byteBuffer);			
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		return message;
	}

}
