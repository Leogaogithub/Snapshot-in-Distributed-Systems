package MyChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;

import Leo.CLSnapshot;
import Leo.MAP;
import Leo.SharedData;
import MyMessage.Message;
import MyMessage.MessageApplication;
import MyMessage.MessageFinish;
import MyMessage.MessageMarker;
import MyMessage.MessageOk;
import MyMessage.MessageParser;
import MyMessage.MessageState;
import MyUtil.ConfigExpert;
import MyUtil.LogWriter;
import MyUtil.UtilityTool;
import com.sun.nio.sctp.MessageInfo;


public class SctpRecieverThread extends Thread {
	
	public int MESSAGE_SIZE = 0;	
	InOutChannel inOutChannel = null;
	//Buffer to hold messages in byte format		

	public SctpRecieverThread(InOutChannel channel){
		this.inOutChannel = channel;
		MESSAGE_SIZE = ConfigExpert.getSingleton().MESSAGE_SIZE;
	}
	
	private String getSuffix(){
		String res = " ";
		res += " from "+ String.valueOf(inOutChannel.outId);
		return res;
	}
	
	/**
	 * @param args
	 */
	public void run() {
		//Receive message in the channel (byte format) and store it in buf
		//Note: Actual message is in byre format stored in buf
		//MessageInfo has additional details of the message
		boolean runed = true;
		while(runed&& (!SharedData.getSingleton().isFinished)){
			MessageInfo messageInfo = null;
			ByteBuffer byteBuffer = ByteBuffer.allocate(MESSAGE_SIZE);
			String message;
			try {
				byteBuffer.clear();				
				messageInfo = inOutChannel.channel.receive(byteBuffer,null,null);				
			} 
			catch (AsynchronousCloseException e)
	        {
	            // this happens when another thread calls close().
				LogWriter.getSingle().log("MessagingService shutting down server thread.");
	            break;
	        }catch (IOException e) {
				LogWriter.getSingle().log("SctpRecieverThread problem---->");
				e.printStackTrace();
				runed = false;				
			}
			//Just seeing what gets stored in messageInfo			
			//Converting bytes to string. This looks nastier than in TCP
			//So better use a function call to write once and forget it :)
			if(inOutChannel.channel.isOpen()){
				message = UtilityTool.byteToString(byteBuffer);
				Message msg = MessageParser.getSingleton().parser(message);
				if(msg == null){
					System.out.println("loglog------------------\n"); continue;
				}
				handler(msg);
			}

			//Finally the actual message						
					
		}
	}	
	
	public void handler(Message msg){
		
		LogWriter.getSingle().log(msg.toString()+getSuffix());	
		if(msg instanceof MessageOk){
			SharedData.getSingleton().updateIsSendOk(inOutChannel.outId);
			if(SharedData.getSingleton().clientsCnctAllserver 
			    && SharedData.getSingleton().serverCnctAllClient 
				&& SharedData.getSingleton().allServerSentMeOk){
				MAP.getSingleton().sendOktoAllClient();
			}
		}else {
			if(SharedData.getSingleton().clientsCnctAllserver 
				    && SharedData.getSingleton().serverCnctAllClient 
					&& SharedData.getSingleton().allServerSentMeOk)
			{				
				if(msg instanceof MessageApplication){				
					CLSnapshot.getSingleton().handleReciveApplication((MessageApplication) msg, inOutChannel.outId);					
				}else if(msg instanceof MessageMarker){				
					CLSnapshot.getSingleton().handleReciveMarker((MessageMarker) msg, inOutChannel.outId);						
				}else if(msg instanceof MessageState){				
					CLSnapshot.getSingleton().handleReciveState((MessageState) msg, inOutChannel.outId);					
				}else if(msg instanceof MessageFinish){				
					CLSnapshot.getSingleton().handleReciveFinish((MessageFinish) msg);				
				}
				else{
					
				}
			}
			
		}
			
	}
}
