package Leo;

import MyUtil.LogWriter;
import MyUtil.UtilityTool;

public class VectorClock {
	public int numProcess ;
	public int localNodeId;
	int c[] = null;
	public VectorClock(int numPro, int nodeId){
		numProcess = numPro;
		localNodeId = nodeId;
		c = new int[numPro];
		for(int i = 0; i < numProcess; i++){
			c[i] = 0;
		}
		c[localNodeId]++;
	}
	
	public void sendAction(){
		LogWriter.getSingle().log(this.toString());
		c[localNodeId]++;
		LogWriter.getSingle().log(this.toString());
	}
	
	public void tick(){
		c[localNodeId]++;
	}
	
	private void receiveAction(int[] tm){
		LogWriter.getSingle().log(this.toString());
		for(int i = 0; i < numProcess; i++){
			c[i] = Math.max(c[i], tm[i]);
		}
		c[localNodeId]++;
		LogWriter.getSingle().log(this.toString());
	}
	
	public void receiveAction(String timestamp){
		timestamp = UtilityTool.removeExtralBlank(timestamp);
		String tim[] = timestamp.split(" ");
		int[] tm = new int[numProcess];
		for(int i = 0; i < numProcess; i++){
			tm[i] = Integer.parseInt(tim[i]);
		}
		receiveAction(tm);
	}
	
	public String toString(){
		String res = "";
		for(int i = 0; i < numProcess; i++){
			res += c[i] + " ";
		}
		return res;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
