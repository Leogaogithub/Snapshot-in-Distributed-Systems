package Leo;
import java.util.ArrayList;
import MyChannel.InOutChannelManager;
import MyChannel.SctpClientPart;
import MyChannel.SctpServerPart;
import MyMessage.Message;
import MyMessage.MessageApplication;
import MyMessage.MessageFactory;
import MyUtil.ConfigExpert;
import MyUtil.LogWriter;
import MyUtil.UtilityTool;


public class MAP {
	static MAP single = new MAP();
	NodeState state;
	
	//HashMap<Integer , SctpClientPart> clients = null;
	SctpServerPart server;
	
	public static MAP getSingleton(){
		return single;
	}
	

	int nodeId = 0; 
	private MAP(){		
		nodeId = ConfigExpert.getSingleton().localNodeId;
		state = new StatePassive(this);
	}
	public void setState(NodeState st){
		state = st;
	}
	
	public void handleReceiveApplication(MessageApplication msg){		
		SharedData.getSingleton().vCReceiveAction(msg.timeStamp);
		state.handleReceiveApplication();
	}
	
	public void handleActive(){
		state.handleActive();
	}
	
	public void setActive(){
		state = new StateActive(this);
	} 
	
	public void setPassive(){
		state = new StatePassive(this);
	}
	
	public boolean isPassive(){
		return state instanceof StatePassive;
	}
	
	private void connectAllChannel(){
		ArrayList<Integer> neighbors = ConfigExpert.getSingleton().getLocalNeighborList();
		NodeInfo serverInfo = ConfigExpert.getSingleton().getLocalNodeInfo();		
		server = new SctpServerPart(serverInfo);
		server.connectAllChannel();
		LogWriter.getSingle().write(String.valueOf(nodeId) + ": finished server.connectAllChannel() in MAP\n");
		for(int i = 0; i < neighbors.size(); i++){
			int id = neighbors.get(i);			
			if(UtilityTool.preIsClient(nodeId, id)){
				NodeInfo clientServerInfo = ConfigExpert.getSingleton().getNodeInfo(id);
				SctpClientPart client = new SctpClientPart(clientServerInfo);
				client.connectChanel();
				//clients.put(id, client);
			}
		}		
	}

	public void go(){		
		LogWriter.getSingle().log("go() in MAP");
		connectAllChannel();		
		LogWriter.getSingle().log("connectAllChannel() in MAP");
		//InOutChannelManager.getSingleton().startAllRecieverThread();
		while((!SharedData.getSingleton().clientsCnctAllserver) 
				|| (!SharedData.getSingleton().serverCnctAllClient) 
				|| (!SharedData.getSingleton().allServerSentMeOk)){
			sleep(100);
		}
		if(ConfigExpert.getSingleton().getLocalNumAsServer()
		==ConfigExpert.getSingleton().getLocalNeighorSize()){
			sendOktoAllClient();
			sleep(1000);
		}
		if(nodeId == ConfigExpert.getSingleton().globleRootNode){
			setActive();			
		}
		if(nodeId == ConfigExpert.getSingleton().globleRootNode){
			CLSnapshot.getSingleton().canBeRestart = true;
			new CLSnapshotThread().start();
		}
		handleActive();

		//sleep(1000);
	}
	
	public void sendOktoAllClient(){
		ArrayList<Integer> clientsAsServer = ConfigExpert.getSingleton().getClientsAsServer();
		for(Integer id: clientsAsServer){
			Message msg = MessageFactory.getSingleton().getMessageOk();
			InOutChannelManager.getSingleton().send(id, msg.toString());
		}
	}
	
	
	private void sleep(long waittime){
		try {
			Thread.sleep(waittime);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
	}

}
