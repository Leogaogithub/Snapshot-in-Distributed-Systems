package Leo;


import MyUtil.ConfigExpert;
import MyUtil.LogWriter;


public class CLSnapshotThread extends Thread {

	int snapshotDelay;
	
	public CLSnapshotThread(){
		snapshotDelay = ConfigExpert.getSingleton().snapshotDelay;
	}
	
	public void run() {		
		boolean runed = true;
		while(runed && (!SharedData.getSingleton().isFinished)){
			if(CLSnapshot.getSingleton().canBeRestart){
				LogWriter.getSingle().log("CLSnapshotThread   restart ......");
				CLSnapshot.getSingleton().canBeRestart = false;
				CLSnapshot.getSingleton().changeColor();
				try {
					Thread.sleep(snapshotDelay/2);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(snapshotDelay/2);
			} catch (InterruptedException e) {					
				e.printStackTrace();
			}
		
		}
	}

}
