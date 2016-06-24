package Leo;

import MyUtil.ConfigExpert;
import MyUtil.LogWriter;
import MyUtil.OutputWriter;

public class MachineNode2 {
	MAP mapLayer = null;
	int nodeId = 1;
	boolean active = false;	
	MAP map;
	static MachineNode2 single = new MachineNode2();
	
	public static MachineNode2 getSingleton(){
		return single;
	}
	public void setNodeId(int id){
		nodeId = id;
	}
	
	public int getNodeId(){
		return nodeId;
	}
	
	private MachineNode2(){
		
	}
	
	public void inital(){
		map = MAP.getSingleton();
		CLSnapshot.getSingleton().inital();
		map.go();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String name = "config.txt";
		int nodeId = 2;
		if(args.length > 0){
			nodeId = Integer.parseInt(args[0]);
		}
		if(args.length > 1){
			name = args[1];
		}		
		ConfigExpert.getSingleton().setNodeId(nodeId);
		MachineNode2.getSingleton().setNodeId(nodeId);
		ConfigExpert.getSingleton().loadFile(name);
		SharedData.getSingleton().init();
		

		
		String prefixName = name.substring(0, name.length()-4)+String.valueOf(nodeId);
		LogWriter.getSingle().open(prefixName+".log");
		LogWriter.getSingle().clear();		
		OutputWriter.getSingle().open(prefixName+".out");
		OutputWriter.getSingle().clear();
		OutputWriter.getSingle().reset();
		MachineNode2.getSingleton().inital();
	}

}
