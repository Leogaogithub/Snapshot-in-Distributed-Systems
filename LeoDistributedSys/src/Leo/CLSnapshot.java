package Leo;

import java.util.ArrayList;
import java.util.LinkedList;
import MyChannel.InOutChannelManager;
import MyMessage.Message;
import MyMessage.MessageApplication;
import MyMessage.MessageFactory;
import MyMessage.MessageFinish;
import MyMessage.MessageMarker;
import MyMessage.MessageState;
import MyUtil.ConfigExpert;
import MyUtil.LogWriter;
import MyUtil.OutputWriter;
import MyUtil.TreeNode;

public class CLSnapshot {
	static final int blue = 0, red = 1;
	int curColor = blue;
	boolean closed[];
	boolean isRecChanEmptyFromChild[];
	boolean isRecAllChanEmptyFromChild;
	boolean isAllChannelEmpty = true;
	boolean isAllClosed;
	boolean isAllChildernPassive;
	int  numReciveState;
	MAP app;
	ArrayList<Integer> neighbers = null;
	LinkedList<Message> chans[] = null;
	int numNodes = 0;
	TreeNode curTreeNode = null;
	volatile boolean canBeRestart;	
	
	public String toString(){
		String res = "";
		res = "curColor-(" + curColor +") \n";		
		res+= "isAllChildernPassive-(" + isAllChildernPassive +") \n";
		res+= "isAllChannelEmpty-(" + isAllChannelEmpty +") \n";
		res+= "isAllClosed-(" + isAllClosed +") \n";
		res+= "numReciveState-(" + numReciveState +") \n";
		for(boolean cl : closed){			
			res += String.valueOf(cl) +" ";			
		}
		res +="\n isRecChanEmptyFromChild: \n";		
		for(boolean ir : isRecChanEmptyFromChild){			
			res += String.valueOf(ir) +" ";			
		}
		res +="\n";
		return res;
	}
	
	static CLSnapshot single = new CLSnapshot();		
	public static CLSnapshot getSingleton(){
		return single;
	}
	
	public void inital(){
		numNodes = ConfigExpert.getSingleton().numNodes;
		neighbers = ConfigExpert.getSingleton().getLocalNeighborList();
		closed = new boolean[numNodes];
		isRecChanEmptyFromChild = new boolean[numNodes];
		curTreeNode = ConfigExpert.getSingleton().curTreeNode;
		chans = new LinkedList[numNodes];
		refresh();
	}
	
	public void refresh(){
		numReciveState = 0;
		canBeRestart = false;
		isAllChildernPassive = true;
		curColor = blue;
		isAllChannelEmpty = true;
		isRecAllChanEmptyFromChild = true;
		isAllClosed = true;
		for(int i = 0; i < numNodes; i++){
			closed[i] = true;
			isRecChanEmptyFromChild[i] = true;
			chans[i] = new LinkedList<Message>();
		}
		
		LinkedList<TreeNode> children = curTreeNode.childern;
		for(TreeNode child: children){
			isRecChanEmptyFromChild[child.nodeId] = false;
			isRecAllChanEmptyFromChild = false;
		}
		for(Integer id :neighbers){
			closed[id] = false;
			isAllClosed = false;
		}		
	}
	
	void update(){
		synchronized(this){
			isAllClosed = true;
			isRecAllChanEmptyFromChild = true;
			for(Integer id :neighbers){
				if(closed[id] == false){
					isAllClosed = false;
					break;
				}			
			}
			
			LinkedList<TreeNode> children = curTreeNode.childern;
			for(TreeNode child: children){
				if(isRecChanEmptyFromChild[child.nodeId] == false){
					isRecAllChanEmptyFromChild = false;
					break;
				}				
			}			
		}
		
		LogWriter.getSingle().log(this.toString());
	}
	
	public void recordLocalSnapshot(){
		String str = SharedData.getSingleton().getVectorClockStr();
		LogWriter.getSingle().log(str);
		OutputWriter.getSingle().log(str);			
	}	
	
	public void changeColor(){
		synchronized(this){
			if(curColor == blue){
				curColor = red;
				recordLocalSnapshot();
				sendMarkerToNeighbors();
			}
		}
	}
	
	public void sendMarkerToNeighbors(){
		String message = MessageFactory.getSingleton().getMessageMarker().toString();
		InOutChannelManager.getSingleton().sendToNeighbors(message);
		LogWriter.getSingle().log("sendMarkerToNeighbors---");
	}
	
	public void handleReciveApplication(MessageApplication msg, int fromId){
		if(curColor==red && closed[fromId] == false){
			chans[fromId].add(msg);
			isAllChannelEmpty = false;
		}
		MAP.getSingleton().handleReceiveApplication(msg);
	}
	
	
	public void handleReciveMarker(MessageMarker msg, int fromId){
		synchronized(this){
			LogWriter.getSingle().log("handleReciveMarker-------------");
			changeColor();//
			closed[fromId] = true;
			update();
			if(isDone()){
				for(LinkedList<Message> ch: chans){
					if(!ch.isEmpty()){
						isAllChannelEmpty = false;
						break;
					}
				}
				sendStateMessage();
			}
		}		
	}	
	
	public void handleReciveState(MessageState msg, int fromId){
		synchronized(this){		
			isRecChanEmptyFromChild[fromId] = true;
			isAllChildernPassive = isAllChildernPassive && msg.isPassive;
			update();
			sendStateMessage();	
			numReciveState++;	
			
			if(canFinish()){ // root
				processFinish();
			}
			
		}
	}			
	
	public boolean canFinish(){
		if((curTreeNode.nodeId == ConfigExpert.getSingleton().globleRootNode)
				&& isDone()
				&& getCurPassive()
				&& (numReciveState==curTreeNode.childern.size())
				&& isAllChannelEmpty)
		{
			return true;
		}
		return false;
	}
	
	public void sendFinishToChildren(){
		LinkedList<TreeNode> children = curTreeNode.childern;
		Message msg = MessageFactory.getSingleton().getMessageFinish();
		for(TreeNode child: children){
			InOutChannelManager.getSingleton().send(child.nodeId, msg.toString());
		}
		LogWriter.getSingle().log("sendFinishToChildren.....");
	}
	
	public void handleReciveFinish(MessageFinish msg){
		synchronized(this){	
			processFinish();
		}		
	}
	
	private void processFinish(){
		sendFinishToChildren();
		SharedData.getSingleton().isFinished = true;			
		InOutChannelManager.getSingleton().closeAll();
	}
	
	boolean isDone(){
		if(curColor == blue) return false;
		for(int i = 0; i < numNodes; i++){
			if(closed[i] == false) return false;
		}
		return true;
	}
	
	boolean getCurPassive(){
		boolean state = isAllChildernPassive && MAP.getSingleton().isPassive();
		return state;
	}
	
	void sendStateMessage(){
		if(isDone() && isRecAllChanEmptyFromChild){		
			LogWriter.getSingle().log("sendStateMessage---");
			if(curTreeNode.parent != null){
				int  pid = curTreeNode.parent.nodeId;
				boolean state = getCurPassive();
				MessageState Msg = (MessageState)MessageFactory.getSingleton().getMessageState(state);
				InOutChannelManager.getSingleton().send(pid, Msg.toString());				
			}
			refresh();
			canBeRestart = true;
		}
	}	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
