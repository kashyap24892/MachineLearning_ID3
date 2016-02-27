import java.util.ArrayList;
import java.util.HashMap;

public class Node {	
	private HashMap<Integer,ArrayList<String>> data;
	private Node left,right,parent;
	private String attribute;
	private int name, label;
	
	Node(){}

	Node(HashMap<Integer,ArrayList<String>> data, String attribute, int name){
		this.attribute = attribute;
		this.name = name;
		this.data = data;		
		this.left = null;
		this.right = null;
	}

	protected void setLeft(Node node){
		this.left = node;
	}	
	protected void setRight(Node node){
		this.right = node;
	}

	protected Node getLeft(){
		return this.left;
	}
	protected Node getRight(){
		return this.right;
	}
	protected HashMap<Integer,ArrayList<String>> getData(){
		return this.data;
	}
	protected int getName(){
		return this.name;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
}
