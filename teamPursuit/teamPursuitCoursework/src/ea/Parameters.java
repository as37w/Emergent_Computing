package ea;

import java.util.Random;

public class Parameters {

	public static Random rnd = new Random(System.currentTimeMillis());

	/**
	 * used as a seed
	 * 
	 */	
	static final boolean [] DEFAULT_WOMENS_TRANSITION_STRATEGY = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
	public static final int [] DEFAULT_WOMENS_PACING_STRATEGY = {300, 300, 300, 300, 300, 300, 300, 350, 350, 300, 300, 350, 350, 350, 350, 300, 300, 350, 350, 350, 350, 300, 300};
	
	
	public static int popSize = 100;
	public static int tournamentSize = 6;
	
	public static int mutationRateMax = 5;//out of len
	public static double mutationProbability = 0.6;
	public static double crossoverProbability = 0.6;
	
	public static int maxIterations = 1000;
	
	
}
