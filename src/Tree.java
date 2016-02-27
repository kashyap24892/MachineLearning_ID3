import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Tree {
	private Node root;

	Tree(){}
	Tree(Node node){ root = node; }

	public void setAttribute(Node node, String attr){ node.setAttribute(attr); }

	public Node getRootNode() {return root;}

	public Node traverseLeft(){	return this.root.getLeft(); }

	public Node traverseRight(){ return this.root.getRight(); }

	public void add(Node parent, Node node, String direction) {		
		Node temp = find(parent);
		if(direction=="Left")
			temp.setLeft(node);
		else
			temp.setRight(node);

		node.setParent(parent);
	}

	private Node find(Node node) {
		return find(node.getName());
	}

	public Node find(int random_no) {
		Queue<Node> q = new LinkedList<>();
		Node temp = null;
		q.add(root);
		while(!q.isEmpty()){
			temp = q.remove();
			if(temp.getName()==random_no)
				break;
			if(temp.getLeft()!=null)
				q.add(temp.getLeft());
			if(temp.getRight()!=null)
				q.add(temp.getRight());
		}
		return temp;
	}

	public ArrayList<Node> getLeafNodes() {
		ArrayList<Node> leaf_nodes = new ArrayList<>();
		Queue<Node> q = new LinkedList<>();
		q.add(root);
		while(!q.isEmpty()){
			Node temp = q.remove();
			if(temp.getLeft()!=null)
				q.add(temp.getLeft());
			if(temp.getRight()!=null)
				q.add(temp.getRight());
			if(temp.getLeft()==null && temp.getRight()==null)
				leaf_nodes.add(temp);
		}
		return leaf_nodes;
	}

	public boolean deleteNode(Node nodeToPrune) {
		Node temp = find(nodeToPrune);
		if(temp!=null){
			if(temp.getLeft()==null && temp.getRight()==null)
				return false;
			else{
				nodeToPrune.setAttribute(null);
				nodeToPrune.setLeft(null);
				nodeToPrune.setRight(null);
				return true;
			}
		}
		return false;
	}
	public void print(Node node){
		print(node.getLeft(),0, "");
		print(node.getRight(),1, "");

	}
	public void print(Node n,int dir,String space){
		if(n == null)
			return;
		else{
			if(n.getLeft() == null && n.getRight() == null)
				System.out.println(space+""+n.getParent().getAttribute() + " = " + dir +" : "+n.getLabel());
			else
				System.out.println(space+""+n.getParent().getAttribute() + " = " + dir);

			space += "| ";
			print(n.getLeft(),0,space);
			print(n.getRight(),1,space);
		}
	}
	public double getLeafNodesDepth() {
		double depth = 0;
		Queue<Node> q = new LinkedList<>();
		q.add(root);
		while(!q.isEmpty()){
			Node temp = q.remove();
			if(temp.getLeft()!=null)
				q.add(temp.getLeft());
			if(temp.getRight()!=null)
				q.add(temp.getRight());
			if(temp.getLeft()==null && temp.getRight()==null)
				depth += getDepth(temp);
		}
		return depth;
		
	}
	private int getDepth(Node temp) {
		int depth = 0;
		while(temp.getName()!=0){
			temp = temp.getParent();
			depth++;
		}
		return depth;
	}
}
