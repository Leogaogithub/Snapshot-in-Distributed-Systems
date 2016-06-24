package MyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class SpanningTreeGenerator {
	ArrayList<Integer>[] neighbors;
	HashMap<Integer, TreeNode> treeNodes;
	int localId = 0;
	
	public SpanningTreeGenerator(int localId, ArrayList<Integer>[] nbs){
		 neighbors = nbs;
		 this.localId = localId;
		 treeNodes = new HashMap<Integer, TreeNode>();
	}
	
	public TreeNode getCurTreeNode(){		
		return treeNodes.get(localId);
	}
	
	public void generate(){			
		TreeNode root = new TreeNode(0,null);
		treeNodes.put(0, root);
		TreeNode parent = root;
		
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.addLast(0);
		while(!queue.isEmpty()){
			int curId = queue.pollFirst();
			parent = treeNodes.get(curId);
			for(int j = 0; j < neighbors[curId].size(); j++){
				int nbId = neighbors[curId].get(j);
				if(treeNodes.containsKey(nbId))  continue;
				TreeNode child = new TreeNode(nbId, parent);
				treeNodes.put(nbId, child);
				parent.childern.add(child);
				queue.addLast(nbId);				
			}			
		}		
	}
	

	public static void main(String[] args) {
		String name = "config.txt";
		ConfigExpert.getSingleton().loadFile(name);
		int localId = 0;
		ArrayList<Integer>[] nbs = ConfigExpert.getSingleton().neighbors;
		
		SpanningTreeGenerator sp = new SpanningTreeGenerator(localId, nbs);
		sp.generate();
		TreeNode cur = sp.getCurTreeNode();
		cur.display(cur);		

	}

}
