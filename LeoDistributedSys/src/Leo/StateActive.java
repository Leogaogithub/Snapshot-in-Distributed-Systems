package Leo;
import java.util.ArrayList;
import java.util.Random;

import MyChannel.InOutChannelManager;
import MyMessage.Message;
import MyMessage.MessageFactory;
import MyUtil.ConfigExpert;
import MyUtil.LogWriter;


public class StateActive implements NodeState {	 
	MAP map ;
	public StateActive(MAP mp){
		this.map = mp;
	}
	public void handleActive(){		
		processSend();
	}
	
	public void handleReceiveApplication(){
		//do nothing
	}
	
	private int getUniformRandomNodeId(){
		Random randomno = new Random();
		int size = ConfigExpert.getSingleton().getLocalNeighorSize();
		int i = randomno.nextInt(size);
		ArrayList<Integer> nb  = ConfigExpert.getSingleton().getLocalNeighborList();
		int selectedNodeId = nb.get(i);
		return selectedNodeId;
	}
	
	private int getUniformRandom(int min, int max){
		int result = 0;
		Random randomno = new Random();
		int size = max-min+1;
		int rd = randomno.nextInt(size);
		result = rd+min;		
		return result;
	}
	
	private void sendApplication(int dstId){
		SharedData.getSingleton().increaseSummedMsgNum();
		String content =  "leo message";
		SharedData.getSingleton().vCSendAction();
		String timeStamp = SharedData.getSingleton().getVectorClockStr();
		Message msg = MessageFactory.getSingleton().getMessageApplication(timeStamp,content, map.nodeId, dstId);
		InOutChannelManager.getSingleton().send(dstId, msg.toString());
	}
	
	private void processSend(){
		synchronized(map){
			int min =  ConfigExpert.getSingleton().minPerActive;
			int max = ConfigExpert.getSingleton().maxPerActive;
			int perAchive = getUniformRandom(min, max);
			int i = 0;
			while(i<=perAchive && !SharedData.getSingleton().checkSummedMsgNum()){
				i++;				
				int dstId = getUniformRandomNodeId();
				sendApplication(dstId);
				try {
					Thread.sleep(ConfigExpert.getSingleton().minSendDelay);
				} catch (InterruptedException e) {				
					e.printStackTrace();
				}			
			}			
			map.setState(new StatePassive(map));	
			LogWriter.getSingle().log("active-->passive");
		}
	}
}
