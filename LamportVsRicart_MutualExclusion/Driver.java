import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public void readConfigFile(String fileToRead, int protocol) throws IOException {

		File file = new File(fileToRead);
		//ApplicationConstants.setConfigFileName(fileToRead.substring(0, fileToRead.lastIndexOf('.')));

		FileReader fr = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fr);

		String line = null;
		Integer lineCounter = 0;
		Integer totNumNodes = 0;
		Integer interRequestDelay = 0;
		Integer csExecutionTime = 0;
		Integer numberOfRequest = 0;

		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim().replaceAll("\\s+", ",");
			if (line.isEmpty() || line.charAt(0) == '#') {
				continue;
			}

			String[] lines = line.split(",");
			if (Character.isDigit(line.charAt(0))) {

				if (lineCounter == 0) {
					totNumNodes = Integer.parseInt(lines[0]);
					interRequestDelay = Integer.parseInt(lines[1]);
					csExecutionTime = Integer.parseInt(lines[2]);
					numberOfRequest = Integer.parseInt(lines[3]);
					
					//set the parameters to application constant
					ApplicationConstants.TOT_NODES=totNumNodes;
					ApplicationConstants.INTER_REQUEST_DELAY=interRequestDelay;
					ApplicationConstants.CSEXECUTION_TIME=csExecutionTime;
					ApplicationConstants.NUMBER_OF_REQUEST=numberOfRequest;
	
					
					lineCounter++;

				} else if (lineCounter <= totNumNodes) {

					Integer nodeId = Integer.parseInt(lines[0]);
					String hostName = lines[1];
					Integer portNum = Integer.parseInt(lines[2]);
					Node node = new Node(nodeId, hostName, portNum, protocol);
					nodeMap.put(nodeId, node);
					
					lineCounter++;

				} 
			}

		}
		bufferedReader.close();
	}

	
	/**
	 * Main method
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length < 3) {
			System.out.println("number of arguments less than 3");
			System.out.println("<Usage> Java <class-name> <node-id> <config-file-path> <protocol>");
			System.out.println("<protocol takes values either 0 or 1");
			System.out.println("0. Lamport");
			System.out.println("1. Ricarta & Agarwala");
		}

		int currentNodeId = Integer.parseInt(args[0]);
		String configFile = args[1];
		int protocol = Integer.parseInt(args[2]);

		Driver driver = new Driver();
		driver.readConfigFile(configFile, protocol);//read config file
		Node currentNode = nodeMap.get(currentNodeId);
		List<Node> neighbours = new ArrayList<>();
		Set<Integer> neighbourSet = new HashSet<>();
        for(Map.Entry<Integer, Node> map:nodeMap.entrySet()){
        	if(!map.getKey().equals(currentNodeId)){
        		neighbours.add(map.getValue());
        		neighbourSet.add(map.getKey());
        	}
        	
        }
		
		currentNode.setNeighbours(neighbours);
	    currentNode.initializeCommunication();
	

	}

}
