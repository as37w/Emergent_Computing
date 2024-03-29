package tspsolver;
/* Simple HillClimber
 * This program reads in a TSP file and attempts to find the minimum tour length
 * using a hill climbing algorithm
 * There are 2 move-operators built in: 
 *    swap - this just picks 2 cities at random and swaps 
 *           their positions in the tour
 *    twoOpt: this picks cities at random and then reverses the order of the
 *            existing tour between these two city positions.
 *
 *  To Run;
 *      java hillclimber [file] [iterations] [v]
 *
 *  The first parameter is the name of a TSP file
 *  The second parameter is the number of iterations to run the algorithm for
 *  The third parameter is optional. If given, a verbose report is written
 *  as the algorithm runs
 */ 



import java.io.*;
import java.text.*;
import java.util.Arrays;

public class hillclimber{
    int maxIterations;
    int solnLength=5;
    boolean isInitialised=false;
    double currentFitness;
    int numIter;
    int soln[];
    int newSoln[];
    int flipSize;
    int numCities;
    double xcoord[];
    double ycoord[];
    boolean verbose;
    int NEW=1;
    int OLD=2;
    boolean useSwap = true;

    public hillclimber(String myfile, int it, boolean v, boolean whichOp){
	// initialise parameters

	maxIterations=it;
	loadData(myfile);
	verbose=v;
	useSwap = whichOp;
	System.out.println("Solver is HILL CLIMBER");
    }

    
  public void runHC(){

	// neighbourSolution
	double fitness;
	// for formatting
	DecimalFormat df = new DecimalFormat ("0.00");

	// initialise with a random solution
	initialise();

	// run algorithm with parameters chosen 
	for (int i=0;i<maxIterations;i++){

	    // ******************************************************	 
	    // apply neighbourhood function to get a potential new solution
	    // you can replace this method with any of the methods
	    // provided, or code your own!
	    // ******************************************************* 
	    
		if (useSwap)
			swapTwoCities();
		else
			doubleBridge();

	    fitness=getTourLength(newSoln);
	    
	    // if the new one is better than the old one (shorter distance)
	    // then replace it
	    
	    if (fitness < currentFitness){
	    	replaceSolution();
	    	currentFitness=fitness;
		if (verbose){
		    System.out.println("iteration " + i + " found better solution: " + df.format(currentFitness));
		}
	    }
	}

	// print  the  final solution
	if (verbose){
	    System.out.println("\n\nFinal Solution:");
	    printTour(OLD);
	}
	
	System.out.println("Best tour length " +  df.format(currentFitness));
    } 
	     

    public void printTour(int which){
	for (int i=0;i<soln.length;i++){
	    if (which==OLD)
	    	System.out.print(soln[i] + "-");
	    else
	    	System.out.print(newSoln[i] + "-");
	}
	System.out.println("\n");
    }
    
    // this method initialises a solution with
    // a random permutation
    
    public void initialise(){
    
    	DecimalFormat df = new DecimalFormat("0.00");
    	// create an array to hold initial solution and new solutions
    	soln= new int[numCities];
    	newSoln= new int[numCities];

    	// and set it to a random permutation
    	permutation();
	
       	currentFitness=getTourLength(soln);	
       	System.out.println("Start tour length = "+df.format(currentFitness));
       	if (verbose) printTour(OLD);
    }

    // this method creates a random permutation  1..N
    public void permutation(){

   
      // insert integers 1..N
      for (int i = 0; i < numCities; i++)
         soln[i] = i+1;

      // shuffle
      for (int i = 0; i < numCities; i++) {
         int r = (int) (Math.random() * (i+1));     // int between 1 and N
         int swap = soln[r];
         soln[r] = soln[i];
         soln[i] = swap;
      }

      
    }

  
    /*
     * This is a simple neighbourhood function that picks two random points
     * and then swaps the cities
     */
    
    
    public void swapTwoCities(){
	// copy the current solution
	
    	for (int i=0;i<numCities;i++)
    		newSoln[i]=soln[i];


    	// mutate it
	
    	int place1, place2;
	
    	place1 = (int)(Math.random()*numCities);
    	place2 = (int)(Math.random()*numCities);
	    
	
    	int swap = soln[place1];
    	newSoln[place1] = soln[place2];
    	newSoln[place2] = swap;
   
    }

    // twoOpt
    // this is a well known heuristic for solving TSPS.
    // it randomly selects two edges in the tour, e.g. (a-b) and (c-d)
    // breaks them, and then reconnects them in a different way, e.g 
    // (a-c),(b-d) or (a-d)(b-c)
    // Essentially, this is performed by randomly picking two points
    // in the solution and the reversing the tour between them
    

    public void twoOpt(){
       

	// copy the current solution
	
	for (int i=0;i<numCities;i++)
	    newSoln[i]=soln[i];


	// pick two places
	int place1, place2;
	
	place1 = (int)(Math.random()*numCities);
	place2 = (int)(Math.random()*numCities);
	    
	// if place2<place1, swap them
	if (place2<place1){
	    int swap = place1;
	    place1=place2;
	    place2=swap;
	}
	
	// create a temporary array that holds all the values between place1 and place2
	int size = place2-place1+1;
	int[] temp = new int[size];
	for (int i=0;i<size;i++)
	    temp[i] = soln[place1+i];

	// now replace the cities in newSoln with those in temp but in reverse order
	int start=0;
	for (int i=place2;i>=place1;i--){
	    newSoln[i] = temp[start];
	    start++;
	}      

    }

    // replace the old soution with the new solution
    public void replaceSolution(){
	for (int j=0;j<numCities;j++)
	    soln[j] = newSoln[j];
    }


    public double getTourLength(int []someSolution){
	double fitness=0;

	double x1,x2,y1,y2;
	int k;
	// calculate the total length of the tour

	for (int j=0;j<someSolution.length;j++){
	    int id1=someSolution[j];
	    if (j==someSolution.length-1) 
		k=0;
	    else
		k=j+1;
	    int id2 = someSolution[k];
	    
	    x1=xcoord[id1];
	    y1=ycoord[id1];
	    x2=xcoord[id2];
	    y2=ycoord[id2];
	    

	    double dist = Math.sqrt( (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	    fitness+=dist;
	}
	   
	return(fitness);
    }	

    public void doubleBridge(){


    	for (int i=0;i<numCities;i++)
    		newSoln[i]=soln[i];

	int pos1,pos2,pos3;
	pos1 = 1 + (int) (Math.random()*newSoln.length/4);
	pos2 = pos1 + 1 + (int) (Math.random()*newSoln.length/4);
	pos3 = pos2 + 1 + (int) (Math.random()*newSoln.length/4);

	System.out.println(pos1);
	System.out.println(pos2);
	System.out.println(pos3);

	int size = numCities -pos3+1;
	int[] s1 = new int[size];
	for (int i=0; i<size; i++ )
		s1[i] = soln[i];

	int size1 = pos2-pos1+1;
	int[] s2 = new int[size1];
	for (int i=0;i<size1;i++)
		s2[i] = soln[pos1+i];

	int size2 = pos3-pos2+1;
	int[] s3 = new int[size2];
	for (int i=0;i<2;i++)
		s3[i] = soln[pos2+i];

	int size3 = numCities - pos3+1;
	int[] s4 = new int[size3];
	for(int i=0;i<2;i++){
		s4[i] = soln[pos3+i];
	}

	int start=0;

	for (int i=0;i<=pos1;i++){
		newSoln[i] = s1[start];
		start++;
	}

	int start1=0;
	for (int i=pos1;i<=pos2;i++){
		newSoln[i] = s4[start1];
		start1++;
	}

	int start2=0;
	for (int i=pos2;i<=pos3;i++){
		newSoln[i] = s3[start2];
		start2++;
	}

	for(int i =0; i < newSoln.length; i++){
		System.out.println(newSoln[i]);
	}


	int start3=0;
	for (int i=pos3;i<=numCities;i++){
		newSoln[i] = s2[start3];
		start3++;
	}
    }


    // read  in the TSP data

  public void loadData(String myFile){
      System.out.println("file is "+myFile);
      try{
	    
	  StreamTokenizer st = new StreamTokenizer(new FileReader(myFile));
	  boolean foundDim=false;
	  st.wordChars(33,126);
	  while (!foundDim){
	    // read until we find the DIMENSION: 
	    int token = st.nextToken();
	   
	    if (token == StreamTokenizer.TT_WORD){
		String theword  = (String)st.sval;
		if (theword.equals("DIMENSION:"))
		    foundDim=true;
		else 
		    st.nextToken();
	    } else 
		st.nextToken();
	    if (token == StreamTokenizer.TT_EOF)
		break;
	
	  }	

	  // next thing should be a number then (numCities)
	  int token = st.nextToken();
	  if (st.ttype != StreamTokenizer.TT_NUMBER){
		// error!
	      System.out.println ("error ? token is not a number");
	      System.exit(0);
	    } else {
		numCities = (int)st.nval;
		System.out.println("Num cities is "+numCities);
		xcoord = new double[numCities+1];
	        ycoord = new double[numCities+1];
	    }
	  
	    // now skip until we find the NODE_COORD
	  boolean foundCoords=false;
	  
	  while (!foundCoords){
	      token = st.nextToken();
	      if (token == StreamTokenizer.TT_NUMBER){
		  System.out.println ("Error reading file " + st.nval);
		  System.exit(0);
	      }
      
	      if (st.ttype== StreamTokenizer.TT_WORD){
		  String theword = (String)st.sval;
		  if (theword.equals("NODE_COORD_SECTION"))
		      foundCoords=true;
		  else
		      st.nextToken();
	      } else
		st.nextToken();
	      
	  }
	
	    int i;
	    // loop over data then and read coords
	    for (i=0;i<numCities;i++){
		st.nextToken();
		int id = (int)st.nval;
		st.nextToken();
		double x = (double)st.nval;	
		st.nextToken();
		double y = (double)st.nval;
		xcoord[id] =x;
		ycoord[id]=y;
		
	    }

	    	    
	}
	catch (Exception e){
	    System.out.println("Error reading problem file " + myFile);
	}
    }

}



