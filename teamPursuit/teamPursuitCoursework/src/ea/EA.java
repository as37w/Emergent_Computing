package ea;

/***
 * This is an example of an EA used to solve the problem
 *  A chromosome consists of two arrays - the pacing strategy and the transition strategy
 * This algorithm is only provided as an example of how to use the code and is very simple - it ONLY evolves the transition strategy and simply sticks with the default
 * pacing strategy
 * The default settings in the parameters file make the EA work like a hillclimber:
 * 	the population size is set to 1, and there is no crossover, just mutation
 * The pacing strategy array is never altered in this version- mutation and crossover are only
 * applied to the transition strategy array
 * It uses a simple (and not very helpful) fitness function - if a strategy results in an
 * incomplete race, the fitness is set to 1000, regardless of how much of the race is completed
 * If the race is completed, the fitness is equal to the time taken
 * The idea is to minimise the fitness value
 */


import java.util.ArrayList;
import java.util.Random;

import teamPursuit.TeamPursuit;
import teamPursuit.WomensTeamPursuit;

public class EA implements Runnable{
	
	// create a new team with the default settings
	public static TeamPursuit teamPursuit = new WomensTeamPursuit(); 
	
	private ArrayList<Individual> population = new ArrayList<Individual>();
	private int iteration = 0;
	
	public EA() {
		
	}

	
	public static void main(String[] args) {
		EA ea = new EA();
		ea.run();
	}

	public void run() {
		initialisePopulation();	
		System.out.println("finished init pop");
		iteration = 0;
		while(iteration < Parameters.maxIterations){
			iteration++;
			Individual parent1 = tournamentSelection();
			Individual parent2 = tournamentSelection();

		//	Individual child = multiPointCrossover(parent1, parent2);
			Individual child = crossover(parent1, parent2);
		//	Individual child = uniformCrossover(parent1, parent2);

			child = mutate(child);
			child.evaluate(teamPursuit);
			replace(child);
			printStats();
		}						
		Individual best = getBest(population);
		best.print();
		
	}

	private void printStats() {		
		System.out.println("" + iteration + " Best: " + getBest(population) + " Worst: " + getWorst(population));
	}


	private void replace(Individual child) {
		Individual worst = getWorst(population);
		if(child.getFitness() < worst.getFitness()){
			int idx = population.indexOf(worst);
			population.set(idx, child);
		}
	}


	private Individual mutate(Individual child) {
		if(Parameters.rnd.nextDouble() > Parameters.mutationProbability){
			return child;
		}
		
		// choose how many elements to alter
		int mutationRate = 1 + Parameters.rnd.nextInt(Parameters.mutationRateMax);
		
		// mutate the transition strategy

			//mutate the transition strategy by flipping boolean value
			for(int i = 0; i < mutationRate; i++){
				int index = Parameters.rnd.nextInt(child.transitionStrategy.length);
				child.transitionStrategy[index] = !child.transitionStrategy[index];
			}

			int index = Parameters.rnd.nextInt(child.pacingStrategy.length);

			for(int j =0; j < mutationRate; j++){
					int rand = 50 - Parameters.rnd.nextInt(100);
					if(child.pacingStrategy[index] + rand <= 1200 && child.pacingStrategy[index] + rand >= 200)
					{
						child.pacingStrategy[index] = child.pacingStrategy[index] + rand;
					}
			}

		return child;
	}


	private Individual crossover(Individual parent1, Individual parent2) {

		if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability){
			return parent1;
		}
		Individual child = new Individual();

		int crossoverPoint1 = Parameters.rnd.nextInt(parent1.transitionStrategy.length);
		int crossoverPoint2 = Parameters.rnd.nextInt(parent1.pacingStrategy.length);

		
		// just copy the pacing strategy from p1 - not evolving in this version
		for(int i = 0; i < crossoverPoint2; i++){
			child.pacingStrategy[i] = parent1.pacingStrategy[i];
		}

		for(int i = crossoverPoint2; i < parent2.pacingStrategy.length; i++){
			child.pacingStrategy[i] = parent2.pacingStrategy[i];
		}
		
		
		for(int i = 0; i < crossoverPoint1; i++){
			child.transitionStrategy[i] = parent1.transitionStrategy[i];
		}
		for(int i = crossoverPoint1; i < parent2.transitionStrategy.length; i++){
			child.transitionStrategy[i] = parent2.transitionStrategy[i];
		}


		return child;
	}

	public Individual uniformCrossover(Individual parent1, Individual parent2){
		if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability){
			return parent1;
		}


		Individual child = new Individual();

		for(int i =0; i < parent1.pacingStrategy.length; i++){
			if(Parameters.rnd.nextInt(2)  == 0){
				child.pacingStrategy[i] = parent1.pacingStrategy[i];
			}else{
				child.pacingStrategy[i] = parent2.pacingStrategy[i];
			}
		}

		for(int i =0; i< parent1.transitionStrategy.length; i++){
			if(Parameters.rnd.nextInt(2) == 0){
				child.transitionStrategy[i] = parent1.transitionStrategy[i];
			}else{
				child.transitionStrategy[i] = parent2.transitionStrategy[i];
			}

		}
		return child;
	}

	public Individual multiPointCrossover(Individual parent1, Individual parent2){
		if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability){
			return parent1;
		}

		Individual child = new Individual();

		int tcrossoverPoint1 = Parameters.rnd.nextInt(parent1.transitionStrategy.length);
		int tcrossoverPoint2 = Parameters.rnd.nextInt(parent1.transitionStrategy.length);

		int pcrossoverPoint3 = Parameters.rnd.nextInt(parent1.pacingStrategy.length);
		int pcrossoverPoint4 = Parameters.rnd.nextInt(parent1.pacingStrategy.length);




		if(tcrossoverPoint1 > tcrossoverPoint2){
			int tmp = tcrossoverPoint1;
			tcrossoverPoint1 = tcrossoverPoint2;
			tcrossoverPoint2 = tmp;
		}

		if(pcrossoverPoint3 > pcrossoverPoint4){
			int tmp = pcrossoverPoint3;
			pcrossoverPoint3 = pcrossoverPoint4;
			pcrossoverPoint4 = tmp;
		}


		for(int i =0; i < tcrossoverPoint1; i++){
			child.transitionStrategy[i] = parent1.transitionStrategy[i];
		}

		for(int i = tcrossoverPoint1; i < tcrossoverPoint2; i++){
			child.transitionStrategy[i] = parent2.transitionStrategy[i];
		}

		for(int i = tcrossoverPoint2; i < parent1.transitionStrategy.length; i++){
			child.transitionStrategy[i] = parent1.transitionStrategy[i];
		}


		for(int i =0; i < pcrossoverPoint3; i++){
			child.pacingStrategy[i] = parent1.pacingStrategy[i];
		}

		for(int i = pcrossoverPoint3; i < pcrossoverPoint4; i++){
			child.pacingStrategy[i] = parent2.pacingStrategy[i];
		}

		for(int i = pcrossoverPoint4; i < parent1.pacingStrategy.length; i++){
			child.pacingStrategy[i] = parent1.pacingStrategy[i];
		}

		return child;

	}


	/**
	 * Returns a COPY of the individual selected using tournament selection
	 * @return
	 */
	private Individual tournamentSelection() {
		ArrayList<Individual> candidates = new ArrayList<Individual>();
		for(int i = 0; i < Parameters.tournamentSize; i++){
			candidates.add(population.get(Parameters.rnd.nextInt(population.size())));
		}
		return getBest(candidates).copy();
	}


	private Individual getBest(ArrayList<Individual> aPopulation) {
		double bestFitness = Double.MAX_VALUE;
		Individual best = null;
		for(Individual individual : aPopulation){
			if(individual.getFitness() < bestFitness || best == null){
				best = individual;
				bestFitness = best.getFitness();
			}
		}
		return best;
	}

	private Individual getWorst(ArrayList<Individual> aPopulation) {
		double worstFitness = 0;
		Individual worst = null;
		for(Individual individual : population){
			if(individual.getFitness() > worstFitness || worst == null){
				worst = individual;
				worstFitness = worst.getFitness();
			}
		}
		return worst;
	}
	
	private void printPopulation() {
		for(Individual individual : population){
			System.out.println(individual);
		}
	}

	private void initialisePopulation() {
		while(population.size() < Parameters.popSize){
			Individual individual = new Individual();
			individual.initialise();			
			individual.evaluate(teamPursuit);
			population.add(individual);
							
		}		
	}	
}

/*
USING TWO POINT CROSSOVER AND MUTATING BOTH

1000 Best: 213.85200000000833 Worst: 215.98600000000727
719,300,689,300,300,300,300,350,291,590,300,350,642,1021,350,300,418,350,350,350,350,300,300,
true,false,true,false,false,false,true,true,false,true,false,false,true,false,false,true,true,true,true,false,false,true,
213.85200000000833


 */