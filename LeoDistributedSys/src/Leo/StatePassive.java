package Leo;

import MyUtil.LogWriter;

public class  StatePassive implements NodeState{	
	MAP map ;
	public StatePassive(MAP mp){
		this.map = mp;
	}
	public void handleActive(){
		//do nothing
	}
	
	public void handleReceiveApplication(){
		if(!SharedData.getSingleton().checkSummedMsgNum()){
			map.setState(new StateActive(map));
			LogWriter.getSingle().log("passive-->active");
			map.handleActive();
		}
	}
}
