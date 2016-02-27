import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class DecisionTree {

	private static HashMap<Integer, ArrayList<String>> zero, one;
	private static File training_set, validation_set, test_set;	 
	private static double ID3_avgDepth = 0, random_avgDepth = 0;
	private static int nodesToPrune=0, nameID3 = 0, nameRandom = 0, doPrint=-1;

	public static void main(String[] args) {

		training_set = new File(args[1]);
		validation_set = new File(args[2]);
		test_set = new File(args[3]);
		nodesToPrune = Integer.parseInt(args[0]);
		doPrint = Integer.parseInt(args[4]);

		if(training_set.isFile()){
			trainID3(training_set);
			trainRandom(training_set);
		}
		else
			try {
				throw new FileNotFoundException("Could not find training set " + training_set);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		System.out.println("\t\t\t     Average Depth    Number of Nodes");
		System.out.println("Tree Constructed Using ID3:: " + (double)ID3_avgDepth/nameID3 + "  " + nameID3);
		System.out.println("Tree Constructed Using ID3:: " + (double)random_avgDepth/nameRandom + "  " + nameRandom);
	}

	//Create decision tree using ID3 algorithm
	private static void trainID3(File training_set) {
		try {
			ArrayList<String> decision_class= new ArrayList<>();
			HashMap<Integer,ArrayList<String>> curr_data = new HashMap<>();
			HashMap<Integer,ArrayList<String>> split_zero = new HashMap<>();
			HashMap<Integer,ArrayList<String>> split_one = new HashMap<>();
			BufferedReader br = new BufferedReader(new FileReader(training_set));
			Queue<Node> nodes = new LinkedList<>();			
			double max = 0, entropy = 0,info_gain = 0;
			int entry_no = 0,index = 0;
			String[] line = null;
			String lines = "";

			//Load training data into hashmap row by row
			while((lines = br.readLine())!=null){
				line = lines.split(",");
				curr_data.put(entry_no++, new ArrayList<>(Arrays.asList(line)));
			}	

			Node root = new Node(curr_data, null, 0);
			Tree decision_treeID3 = new Tree(root);
			Node curr_node = new Node();
			Node left, right;
			curr_node = root;
			nodes.add(root);			

			//Running the loop until there are no more splitting attribute is left
			while(!nodes.isEmpty() && curr_node.getData().get(0).size()>2){				

				max = 0; index = 0;
				curr_node = nodes.remove();
				curr_data = curr_node.getData();

				//Getting decision class values
				for (int j = 1; j < curr_data.get(0).size(); j++) {
					if(curr_data.get(j)!=null)
						decision_class.add(curr_data.get(j).get(curr_data.get(0).size()-1));
				}
				//Calculating entropy for each run
				entropy = calculateEntropy(decision_class);	

				//Inner loop to split the tree on the selected attribute based on max IG
				for (int i = 0; i < curr_data.get(0).size() - 1; i++) {
					info_gain = getInfoGain(curr_data, entropy, i);
					if(info_gain==0){						
						split_zero = null;
						split_one = null;
					}
					if(info_gain > max){						
						split_zero = zero;
						split_one = one;
						max = info_gain;
						index = i;
					}
				}
				//store the attribute used to split the node 
				curr_node.setAttribute(curr_data.get(0).get(index));

				if(split_zero!=null){					
					split_zero = removeAttribute(split_zero, index);					
					left = new Node(split_zero, null, ++nameID3);
					decision_treeID3.add(curr_node, left, "Left");
					//Add the node to queue only if it's pure for further classification
					if(!isPure(split_zero, index))
						nodes.add(left);
				}
				if(split_one!=null){					
					split_one = removeAttribute(split_one, index);						
					right = new Node(split_one, null, ++nameID3);
					decision_treeID3.add(curr_node, right, "Right");
					//Add the node to queue only if it's pure for further classification
					if(!isPure(split_one, index))
						nodes.add(right);
				}
			}
			br.close();
			
			//Print the tree only of command line parameter is set to 1
			if(doPrint==1)
				decision_treeID3.print(decision_treeID3.getRootNode());				

			//Print the accuracies of decision tree based on validation set and test set
			System.out.println("Accurarcy of decision tree generated using ID3 algorithm on validation set: " + getAccuracy(decision_treeID3, getMap(validation_set)));
			System.out.println("Accurarcy of decision tree generated using ID3 algorithm on test set: " +getAccuracy(decision_treeID3, getMap(test_set)));
			printDivider();

			ID3_avgDepth = decision_treeID3.getLeafNodesDepth();

			System.out.println("Pruning decision tree(Generated using ID3 algorithm): ");
			prune(decision_treeID3, nodesToPrune);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	private static void trainRandom(File training_set) {

		try {
			HashMap<Integer,ArrayList<String>> curr_data = new HashMap<>();
			HashMap<Integer,ArrayList<String>> split_zero = new HashMap<>();
			HashMap<Integer,ArrayList<String>> split_one = new HashMap<>();
			BufferedReader br = new BufferedReader(new FileReader(training_set));
			Queue<Node> nodes = new LinkedList<>();	
			int entry_no = 0;
			String[] line = null;
			String lines = "";

			//Load training data into hashmap row by row
			while((lines = br.readLine())!=null){
				line = lines.split(",");
				curr_data.put(entry_no++, new ArrayList<>(Arrays.asList(line)));
			}	

			Node root = new Node(curr_data, null, 0);
			Tree decision_treeRandom = new Tree(root);
			Node curr_node = new Node();
			Node left, right;
			curr_node = root;
			nodes.add(root);			

			//Running the loop until there are no more splitting attribute is left
			while(!nodes.isEmpty() && curr_node.getData().get(0).size()>2){
				curr_node = nodes.remove();
				curr_data = curr_node.getData();	

				//split the tree on the randomly selected attribute
				int upper = curr_data.get(0).size()-1, lower=0;				
				int attr_index = (int) (Math.random()* (upper - lower)) + lower;
				splitAttributes(curr_data, attr_index);
				split_zero = zero;
				split_one = one;

				
				curr_node.setAttribute(curr_data.get(0).get(attr_index));
				if(split_zero!=null && split_zero.size()!=0){					
					split_zero = removeAttribute(split_zero, attr_index);					
					left = new Node(split_zero, null, ++nameRandom);
					decision_treeRandom.add(curr_node, left, "Left");
					if(!isPure(split_zero, attr_index))
						nodes.add(left);
				}
				if(split_one!=null && split_one.size()!=0){					
					split_one = removeAttribute(split_one, attr_index);						
					right = new Node(split_one, null, ++nameRandom);
					decision_treeRandom.add(curr_node, right, "Right");
					if(!isPure(split_one, attr_index))
						nodes.add(right);
				}
			}
			br.close();

			System.out.println("Accurarcy of decision tree generated by random selection of attributes on validation set: " + getAccuracy(decision_treeRandom, getMap(validation_set)));
			System.out.println("Accurarcy of decision tree generated by random selection of attributes on test set: " +getAccuracy(decision_treeRandom, getMap(test_set)));
			printDivider();

			random_avgDepth = decision_treeRandom.getLeafNodesDepth();

			System.out.println("Pruning decision tree(Generated by random attribute selection): ");
			prune(decision_treeRandom, nodesToPrune);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void prune(Tree tree, int prune_no) throws IOException {
		int random_no=-1, upper=nameID3, lower=15, nodes_pruned = 0;
		while(nodes_pruned < prune_no){
			random_no=(int)(Math.random() * (upper - lower)) + lower;
			if(tree.deleteNode(tree.find(random_no)))
				nodes_pruned++;			
		}
		if(doPrint==1)
			tree.print(tree.getRootNode());

		System.out.println("Accurarcy of pruned tree("+prune_no+" nodes) on validation set: " + getAccuracy(tree, getMap(validation_set)));
		System.out.println("Accurarcy of pruned tree("+prune_no+" nodes) on test set: " +getAccuracy(tree, getMap(test_set)));
		printDivider();
	}

	private static HashMap<Integer,ArrayList<String>> getMap(File file) throws IOException {
		HashMap<Integer,ArrayList<String>> temp = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String lines;
		String[] line;
		int entry_no = 0;
		while((lines = br.readLine())!=null){
			line = lines.split(",");
			temp.put(entry_no++, new ArrayList<>(Arrays.asList(line)));
		}
		br.close();
		return temp;
	}

	private static void splitAttributes(HashMap<Integer, ArrayList<String>> curr_data, int attr_index) {
		one = new HashMap<>();zero = new HashMap<>();
		one.put(0, curr_data.get(0));
		zero.put(0, curr_data.get(0));
		int counter1 = 1, counter2 = 1;
		for (int i = 1; i < curr_data.size(); i++) {
			if(curr_data.get(i).get(attr_index).trim().equals("1"))
				one.put(counter1++, curr_data.get(i));
			else
				zero.put(counter2++, curr_data.get(i));
		}
	}

	private static HashMap<Integer, ArrayList<String>> removeAttribute(HashMap<Integer, ArrayList<String>> data, int index) {
		ArrayList<String> temp = new ArrayList<>();
		ArrayList<String> temp2 = new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			temp = data.get(i);
			temp2 = new ArrayList<String>(temp);
			temp2.remove(index);
			data.put(i, temp2);
		}
		return data;
	}	

	private static boolean isPure(HashMap<Integer, ArrayList<String>> list, int index) {
		for (int i = 1,j=2; i<list.size()-1 && j<list.size(); i++,j++) {
			String val1 = list.get(i).get(list.get(0).size() - 1);
			String val2 = list.get(j).get(list.get(0).size() - 1);		
			if(!val1.equals(val2))
				return false;
		}
		return true;
	}

	private static double getInfoGain(HashMap<Integer, ArrayList<String>> curr_data, double entropy, int column_index) {
		int ones=0, zeros=0, one_zero=0, one_one=0, zero_zero=0, zero_one=0, total=0, data_length = curr_data.get(0).size()-1;
		one = new HashMap<>();zero = new HashMap<>();
		int counter1 = 1, counter2 = 1;
		one.put(0, curr_data.get(0));
		zero.put(0, curr_data.get(0));
		for (int i = 1; i < curr_data.size(); i++) {
			//Add indices to the ArrayList
			ArrayList<String> curr_line = curr_data.get(i);
			if(curr_line.get(column_index).equals(("1"))){
				one.put(counter1++, curr_data.get(i));
				ones++;
				if(curr_line.get(data_length).equals("1"))  
					one_one++; 
				else 
					one_zero++;
			}
			else{
				zero.put(counter2++, curr_data.get(i));
				zeros++;
				if(curr_line.get(data_length).equals("1"))  
					zero_one++; 
				else 
					zero_zero++;
			}
			total++;

		}
		double T0,T1,I00 = 0, I01 = 0, I10 = 0, I11 = 0;

		if(zero_zero==0) I00 = 0;
		else I00 = (double)(-((double)zero_zero/(zero_one+zero_zero)*log2((double)zero_zero/(zero_one+zero_zero))));

		if(zero_one==0) I01 = 0;
		else I01 = -((double)zero_one/(zero_one+zero_zero)*log2((double)zero_one/(zero_one+zero_zero)));

		if(one_one==0) I11 = 0;
		else I11 = ((double)(-((double)one_one/(one_one+one_zero)*log2((double)one_one/(one_one+one_zero)))));

		if(one_zero==0) I10 = 0;
		else I10 = -((double)one_zero/(one_one+one_zero)*log2((double)one_zero/(one_one+one_zero)));

		T0 = I00 + I01;
		T1 = I11 + I10;
		return entropy - ((double)((double)ones/total)*T1+(((double)zeros/total)*T0));
	}

	private static double calculateEntropy(ArrayList<String> arrayList) {
		int ones=0,zeros=0,total=0;
		for (int i = 1; i < arrayList.size(); i++) {
			if(arrayList.get(i).trim().equals("1")) ones++;
			else zeros++;
			total++;
		}
		return (double)(-(((double)ones/total)*log2((double)ones/total)) -(((double)zeros/total)*log2((double)zeros/total)));
	}

	private static double log2(double x) {
		return Math.log(x)/Math.log(2.0d);
	}

	private static double getAccuracy(Tree tree, HashMap<Integer, ArrayList<String>> curr_data) {
		getLabel(tree);
		Node node = tree.getRootNode();
		ArrayList<String> curr_line = new ArrayList<>();
		int label = -1, total_instances = 0, correct_instances = 0;
		Node curr_node;
		String attr = null;		
		for (int j = 1; j < curr_data.size(); j++) {
			curr_node = node;
			attr = curr_node.getAttribute();
			curr_line = curr_data.get(j);
			for (int i = 0; i < curr_line.size(); i++) {				
				if(attr==null){
					label = curr_node.getLabel();
					if(curr_line.get(curr_line.size()-1).equals(""+label)){
						correct_instances++;
						break;
					}
				}
				if(curr_data.get(0).get(i).equals(attr)){
					if(curr_line.get(i).equals("1"))
						curr_node = curr_node.getRight();
					else
						curr_node = curr_node.getLeft();

					attr = curr_node.getAttribute();
					i=-1;
				}					
			}
			total_instances++;
		}
		return ((double)correct_instances*100/total_instances);
	}

	private static void getLabel(Tree decision_tree) {
		ArrayList<Node> leaf_nodes = new ArrayList<>();
		leaf_nodes = decision_tree.getLeafNodes();
		for (int i = 0; i < leaf_nodes.size(); i++) {
			int label = getMaxCount(leaf_nodes.get(i));
			leaf_nodes.get(i).setLabel(label);
		}
	}

	private static int getMaxCount(Node node) {
		HashMap<Integer, ArrayList<String>> data = node.getData();
		int zeros=0,ones=0;
		for (int i = 1; i < data.size(); i++) {
			if(data.get(i).get(data.get(0).size()-1).trim().equals("1"))
				ones++;
			else
				zeros++;
		}
		
		return ones>zeros ? 1 : 0;
	}

	private static void printDivider() {
		System.out.println("--------------------------------------------------------------------------------------------");
		System.out.println("--------------------------------------------------------------------------------------------");
		System.out.println("--------------------------------------------------------------------------------------------");		
	}
}
