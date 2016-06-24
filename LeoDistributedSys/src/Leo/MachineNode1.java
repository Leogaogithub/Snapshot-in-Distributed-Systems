package Leo;

import MyUtil.ConfigExpert;
import MyUtil.LogWriter;
import MyUtil.OutputWriter;

public class MachineNode1 {
	MAP mapLayer = null;
	int nodeId = 1;
	boolean active = false;	
	MAP map;
	static MachineNode1 single = new MachineNode1();
	
	public static MachineNode1 getSingleton(){
		return single;
	}
	public void setNodeId(int id){
		nodeId = id;
	}
	
	public int getNodeId(){
		return nodeId;
	}
	
	private MachineNode1(){
		
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
		int nodeId = 1;
		if(args.length > 0){
			nodeId = Integer.parseInt(args[0]);
		}
		if(args.length > 1){
			name = args[1];
		}				
		ConfigExpert.getSingleton().setNodeId(nodeId);
		MachineNode1.getSingleton().setNodeId(nodeId);
		ConfigExpert.getSingleton().loadFile(name);
		SharedData.getSingleton().init();
		

		
		String prefixName = name.substring(0, name.length()-4)+String.valueOf(nodeId);
		LogWriter.getSingle().open(prefixName+".log");
		LogWriter.getSingle().clear();		
		OutputWriter.getSingle().open(prefixName+".out");
		OutputWriter.getSingle().clear();
		OutputWriter.getSingle().reset();
		MachineNode1.getSingleton().inital();
	}

}
