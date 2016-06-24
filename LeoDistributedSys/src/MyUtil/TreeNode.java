package MyUtil;

import java.util.LinkedList;
import java.util.ListIterator;

public class TreeNode {
	public int nodeId;
	public TreeNode parent;
	public LinkedList<TreeNode> childern;
	
	public TreeNode(int nodeId, TreeNode par){
		this.nodeId = nodeId;
		parent = par;
		childern = new LinkedList<TreeNode>();
	}
	
	
	public String toString(){
		String res = "";
		res += "id: " + nodeId + "\n";
		if(parent!=null){
			res += "p: " + parent.nodeId + "\n";
		}else{
			res += "p: null \n";
		}
		
		ListIterator<TreeNode> it = childern.listIterator();
		if(childern.isEmpty()){
			res += "c: null\n";
			return res;
		}
		res += "c: ";
		while(it.hasNext()){			
			res += it.next().nodeId + " "; 
		}
		res += "\n";
		return res;
	}
	
	public static void display(TreeNode root){
		if(root==null) return;
		System.out.println(root.toString());
		for(int i = 0; i < root.childern.size(); i++){
			display(root.childern.get(i));
		}		
	}

}
