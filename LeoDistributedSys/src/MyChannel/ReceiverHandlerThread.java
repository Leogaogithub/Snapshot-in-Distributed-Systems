package MyChannel;
import Leo.MAP;
import Leo.SharedData;
import MyMessage.Message;
import MyMessage.MessageApplication;
import MyMessage.MessageOk;
import MyUtil.LogWriter;


public class ReceiverHandlerThread extends Thread {
	InOutChannel inOutChannel = null;
	Message msg;
	public ReceiverHandlerThread(InOutChannel channel, Message msg){
		inOutChannel=channel;
		this.msg = msg;
	}
	
	public void run() {
		handler(msg);
	}
	public void handler(Message msg){
		LogWriter.getSingle().log(msg.toString());	
		if(msg instanceof MessageOk){
			SharedData.getSingleton().updateIsSendOk(inOutChannel.outId);
			if(SharedData.getSingleton().clientsCnctAllserver 
			    && SharedData.getSingleton().serverCnctAllClient 
				&& SharedData.getSingleton().allServerSentMeOk){
				MAP.getSingleton().sendOktoAllClient();
			}
		}else if(msg instanceof MessageApplication){
			if(SharedData.getSingleton().clientsCnctAllserver 
				    && SharedData.getSingleton().serverCnctAllClient 
					&& SharedData.getSingleton().allServerSentMeOk){
				MAP.getSingleton().handleReceiveApplication((MessageApplication) msg);				
			}
		}else{
			
		}
	}
}
