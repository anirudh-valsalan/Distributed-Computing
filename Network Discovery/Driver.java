import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Driver {
	/**
	 * @author anirudh Kuttiyil valsalan
	 * NETID:axk153230
	 */

	public static Map<Integer, Node> nodeMap = null;
	public static Map<Integer, List<Integer>> neighMap = null;
	

	public Driver() {
		nodeMap = new HashMap<>();
		neighMap = new HashMap<>();
	}

	public void readConfigFile(String fileToRead) throws IOException {

		File file = new File(fileToRead);
		FileReader fr = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fr);
		String line = null;
		Integer lineCounter = 0;
		Integer totNumNodes = 0;

		while ((line = bufferedReader.readLine()) != null) {
			 line=line.trim().replaceAll("\\s+", ",");
            if(line.isEmpty()||line.charAt(0)=='#'){
            	continue;
            }
           
        	String[] lines = line.split(",");
			if (Character.isDigit(line.charAt(0))) {

				if (lineCounter == 0) {
					totNumNodes = Integer.parseInt(lines[0]);
					lineCounter++;

				} else if (lineCounter <= totNumNodes) {

					Integer nodeId = Integer.parseInt(lines[0]);
					String hostName = lines[1];
					Integer portNum = Integer.parseInt(lines[2]);
					Node node = new Node(nodeId, hostName, portNum);
					nodeMap.put(nodeId, node);
					lineCounter++;

				} else {

					String[] neighArray = line.split(",");
					Integer mapIndex = Integer.parseInt(neighArray[0]);
					List<Integer> neighlist = new ArrayList<>();
					int count = 1;
					while (count < neighArray.length) {
						if (!neighArray[count].contains("#")) {
							neighlist.add(Integer.parseInt(neighArray[count]));
						} else {
							break;
						}
						count++;

					}

					neighMap.put(mapIndex, neighlist);

				}
			}

		}

	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length != 2) {
			System.out.println("number of arguments less than 2");
		}
		
		int currentNodeId = Integer.parseInt(args[0]);
		String configFile = args[1];

		Driver imp = new Driver();
		imp.readConfigFile(configFile);
		Node currentNode = nodeMap.get(currentNodeId);
		List<Integer> nodeIdList = neighMap.get(currentNodeId);
		List<Node> neighbours = new ArrayList<>();
		Set<Integer> neighbourSet = new HashSet<>();

		for (Integer nodeId : nodeIdList) {
			Node node = nodeMap.get(nodeId);
			//	System.out.println("neig " + node.getHost());
			neighbours.add(node);
			neighbourSet.add(node.getNodeId());
		}

		currentNode.setNeighbours(neighbours);
		currentNode.setNeighboursNodeIdSet(neighbourSet);
		currentNode.initializeCommunication();
		//System.out.println(neighMap.toString());

	}

}
