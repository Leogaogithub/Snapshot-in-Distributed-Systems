package Leo;
import java.util.ArrayList;
import java.util.HashMap;

import MyUtil.ConfigExpert;
import MyUtil.LogWriter;
import MyUtil.UtilityTool;


public class SharedData {
	public int summedMsgNum = 0;
	public int localNodeId = 0;
	public boolean serverCnctAllClient = false;
	public boolean clientsCnctAllserver = false;
	public boolean allServerSentMeOk = false;
	public VectorClock vectorClock;
	public volatile boolean isFinished = false;	
	public synchronized void vCSendAction(){
		vectorClock.sendAction();
	}
	
	public synchronized void vCReceiveAction(String vec){
		vectorClock.receiveAction(vec);
	}
	public String getVectorClockStr(){
		return vectorClock.toString();
	}
	public String toString(){
		String res = "";
		res = "serverCnctAllClient-(" + serverCnctAllClient +") \n";
		res+= "clientsCnctAllserver-(" + clientsCnctAllserver +") \n";
		res+= "allServerSentMeOk-(" + allServerSentMeOk +") \n";
		res+= "summedMsgNum-(" + summedMsgNum +") \n";
		for(Integer id : neighberStateHash.keySet()){
			NeighberStateInfo nb = neighberStateHash.get(id);
			res += String.valueOf(id) +" "+ nb.toString();			
		}
		return res;
	}
	
	public synchronized void increaseSummedMsgNum(){
		summedMsgNum++;
		 LogWriter.getSingle().log(toString());
	}
	
	public synchronized boolean checkSummedMsgNum(){
		if(summedMsgNum >= ConfigExpert.getSingleton().maxNumber){
			return true;
		}
		return false;
	}
	public synchronized int getSummedMsgNum(){
		return summedMsgNum;
	}
	
	public class NeighberStateInfo{
		boolean isConnected;
		boolean isSendOk;
		boolean isServer;// s
		NeighberStateInfo(){
			isConnected = false;
			isSendOk = false;
			isServer = false;
		}
		
		public String toString(){
			String res = "isConnected-(" + isConnected +") ";
			res+= "isSendOk-(" + isSendOk +") ";
			res+= "isServer-(" + isServer +") \n";
			return res;
		}
	}
	
	public void init(){
		localNodeId = ConfigExpert.getSingleton().getLocalNodeId();
		int numProcess = ConfigExpert.getSingleton().numNodes;
		vectorClock = new VectorClock(numProcess,localNodeId);
		neighberStateHash = new HashMap<Integer, NeighberStateInfo>();
		ArrayList<Integer> neighList = ConfigExpert.getSingleton().getLocalNeighborList();
		for(Integer id : neighList){
			NeighberStateInfo nb = new NeighberStateInfo();				
			if(UtilityTool.preIsClient(localNodeId, id)){
				nb.isServer = true;
			}
			neighberStateHash.put(id, nb);
		}
	}
	
	public synchronized void updateIsConnected(int id){
		NeighberStateInfo nb = neighberStateHash.get(id);
		nb.isConnected = true;
		neighberStateHash.replace(id, nb);
		update();
		return;
	}
	
	public synchronized void updateIsSendOk(int id){
		NeighberStateInfo nb = neighberStateHash.get(id);
		nb.isSendOk = true;
		neighberStateHash.replace(id, nb);
		update();
		return;
	}
	
	public synchronized void update(){
		 serverCnctAllClient = true;
		 clientsCnctAllserver = true;
		 allServerSentMeOk = true;
		 
		 for(NeighberStateInfo nb : neighberStateHash.values()){
			 if((!nb.isConnected) && (nb.isServer)){
				 clientsCnctAllserver = false;
			 }
			 
			 if((!nb.isConnected) && (!nb.isServer)){
				 serverCnctAllClient = false;
			 }
			 
			 if(nb.isServer && (!nb.isSendOk)){
				 allServerSentMeOk = false;
			 }			 
		 }		
		 LogWriter.getSingle().log(toString());
	}
	
	
	private SharedData(){

	}
	
	public HashMap<Integer,NeighberStateInfo> neighberStateHash;
	
	static SharedData single = new SharedData();		
	public static SharedData getSingleton(){
		return single;
	}


}
