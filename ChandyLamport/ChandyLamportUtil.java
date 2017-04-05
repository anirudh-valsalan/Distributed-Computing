import java.util.ArrayList;

/**
 * @author anirudh kuttiyil valsalan 
 * 		   praveen erode murugesan
 * 		   rahul aravind
 * 
 * This class will store all collected global states.
 */
public class ChandyLamportUtil {
	public static ArrayList<GlobalState> globalStates;

	public static ArrayList<GlobalState> getGlobalStates() {
		return globalStates;
	}

	public static void addGlobalStates(GlobalState globalState) {
		if (globalStates == null)
			globalStates = new ArrayList<>();

		globalStates.add(globalState);
	}

}
