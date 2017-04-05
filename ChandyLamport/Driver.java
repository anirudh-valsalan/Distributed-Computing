import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * @author anirudh kuttiyil valsalan
 * 		   praveen erode murugesan
 * 		   rahul aravind
 * 
 * Class which will read configuration file and build the spanning tree.
 */
public class Driver {
	

	public static Map<Integer, Node> nodeMap = null;
	public static Map<Integer, List<Integer>> neighMap = null;

	public Driver() {
		nodeMap = new HashMap<>();//store node Id and node information
		neighMap = new HashMap<>();//store node Id and neighbor list information.
	}

	/**
	 * Method to read the config file
	 * @param fileToRead
	 * @throws IOException
	 */
	public void readConfigFile(String fileToRead) throws IOException {

		File file = new File(fileToRead);
		ApplicationConstants.setConfigFileName(fileToRead.substring(0, fileToRead.lastIndexOf('.')));

		FileReader fr = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fr);

		String line = null;
		Integer lineCounter = 0;
		Integer totNumNodes = 0;
		Integer minPerActive = 0;
		Integer maxPerActive = 0;
		Integer minSendDelay = 0;
		Integer snapShotDelay = 0;
		Integer maxMessages = 0;
		Integer mapIndex = 0;

		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim().replaceAll("\\s+", ",");
			if (line.isEmpty() || line.charAt(0) == '#') {
				continue;
			}

			String[] lines = line.split(",");
			if (Character.isDigit(line.charAt(0))) {

				if (lineCounter == 0) {
					totNumNodes = Integer.parseInt(lines[0]);
					minPerActive = Integer.parseInt(lines[1]);
					maxPerActive = Integer.parseInt(lines[2]);
					minSendDelay = Integer.parseInt(lines[3]);
					snapShotDelay = Integer.parseInt(lines[4]);
					maxMessages = Integer.parseInt(lines[5]);
					//set the parameters to application constant
					ApplicationConstants.setTotNumNodes(totNumNodes);
					ApplicationConstants.setMinPerActive(minPerActive);
					ApplicationConstants.setMaxPerActive(maxPerActive);
					ApplicationConstants.setMinSendDelay(minSendDelay);
					ApplicationConstants.setSnapShotDelay(snapShotDelay);
					ApplicationConstants.setMaxMessages(maxMessages);
					
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
					List<Integer> neighlist = new ArrayList<>();
					int count = 0;
					while (count < neighArray.length) {
						if (!neighArray[count].contains("#")) {
							neighlist.add(Integer.parseInt(neighArray[count]));
						} else {
							break;
						}
						count++;

					}

					neighMap.put(mapIndex++, neighlist);

				}
			}

		}
		bufferedReader.close();
		buildSpanningTree(nodeMap, neighMap);//spanning tree is constructed here
	}

	/**
	 * build spanning tree based on node map and neighbor map
	 * spanning tree is build for converge cast 
	 * @param nodeMap
	 * @param neighMap
	 */
	private static void buildSpanningTree(Map<Integer, Node> nodeMap, Map<Integer, List<Integer>> neighMap) {
		if (nodeMap != null && nodeMap.size() != 0) {
			boolean[] visited = new boolean[ApplicationConstants.Tot_Num_Nodes];
			Arrays.fill(visited, false);
			Queue<Integer> queue = new LinkedList<Integer>();
			queue.add(0);
			visited[0] = true;
			while (!queue.isEmpty()) {
				int nodeId = queue.remove();
				Node parent = nodeMap.get(nodeId);
				List<Integer> neighbours = neighMap.get(nodeId);
				if (neighbours != null && neighbours.size() > 0) {
					for (Integer neighbourId : neighbours) {
						if (!visited[neighbourId]) {
							Node neighbour = nodeMap.get(neighbourId);
							neighbour.setParentNode(parent);
							visited[neighbourId] = true;
							queue.add(neighbourId);
						}
					}
				}
			}

		}
	}
	/**
	 * Main method
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length < 2) {
			System.out.println("number of arguments less than 2");
		}

		int currentNodeId = Integer.parseInt(args[0]);
		String configFile = args[1];

		Driver driver = new Driver();
		driver.readConfigFile(configFile);//read config file
		Node currentNode = nodeMap.get(currentNodeId);
		List<Integer> nodeIdList = neighMap.get(currentNodeId);
		List<Node> neighbours = new ArrayList<>();
		Set<Integer> neighbourSet = new HashSet<>();

		for (Integer nodeId : nodeIdList) {
			Node node = nodeMap.get(nodeId);
			neighbours.add(node);
			neighbourSet.add(node.getNodeId());
		}
		currentNode.setNeighbours(neighbours);
		currentNode.resetMarkerReceivedMap();
	    currentNode.initializeCommunication();

	}

}
