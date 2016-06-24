package MyChannel;
import java.io.IOException;
import java.util.HashMap;


public class InOutChannelManager {
	static InOutChannelManager single = new InOutChannelManager();	
	HashMap<Integer, InOutChannel> channels = null;
	public static InOutChannelManager getSingleton(){
		return single;
	}
	
	int outNodeId = 0;	
	private InOutChannelManager(){
		channels = new HashMap<Integer, InOutChannel>();
	}
	
	public void addOutChannel(InOutChannel  ch){
		channels.put(ch.outId, ch);
	}
	
	private InOutChannel getOutChannel(int outId){
		return channels.get(outId);
	}
	
	public void send(int dstId, String message){
		InOutChannel ch = getOutChannel(dstId);
		ch.send(message);
	}
	
	public void sendToNeighbors(String message){
		for(InOutChannel ch : channels.values()){
			ch.send(message);
		}
	}
	public void startAllRecieverThread(){
		for(InOutChannel ch : channels.values()){
			new SctpRecieverThread(ch).start();
		}
	}
	
	public void closeAll(){
		for(InOutChannel ch : channels.values()){
			try {
				ch.channel.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
	}
}
