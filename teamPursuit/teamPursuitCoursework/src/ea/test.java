package ea;

public class test {
    private Individual MultiPointCrossoverEck(Individual parent1, Individual parent2)
    {
        if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability)
            return parent1;

        Individual child1 = new Individual();

        int pCrossoverPoint1 = Parameters.rnd.nextInt(parent1.pacingStrategy.length);
        int pCrossoverPoint2 = Parameters.rnd.nextInt(parent1.pacingStrategy.length);

        int tCrossoverPoint1 = Parameters.rnd.nextInt(parent1.transitionStrategy.length);
        int tCrossoverPoint2 = Parameters.rnd.nextInt(parent1.transitionStrategy.length);

        if(tCrossoverPoint1 > tCrossoverPoint2)
        {
            int temp = pCrossoverPoint1;
            pCrossoverPoint1 = pCrossoverPoint2;
            pCrossoverPoint2 = temp;
        }

        if(pCrossoverPoint1 > pCrossoverPoint2)
        {
            int temp = pCrossoverPoint1;
            pCrossoverPoint1 = pCrossoverPoint2;
            pCrossoverPoint2 = temp;
        }

        for(int i = 0; i < pCrossoverPoint1; i++)
            child1.pacingStrategy[i] = parent1.pacingStrategy[i];

        for(int i = pCrossoverPoint1; i < pCrossoverPoint2; i++)
            child1.pacingStrategy[i] = parent2.pacingStrategy[i];

        for(int i = pCrossoverPoint2; i < parent1.pacingStrategy.length; i++)
            child1.pacingStrategy[i] = parent1.pacingStrategy[i];

        for(int i = 0; i < tCrossoverPoint1; i++)
            child1.transitionStrategy[i] = parent1.transitionStrategy[i];

        for(int i = tCrossoverPoint1; i < tCrossoverPoint2; i++)
            child1.transitionStrategy[i] = parent2.transitionStrategy[i];

        for(int i = tCrossoverPoint2; i < parent1.transitionStrategy.length; i++)
            child1.transitionStrategy[i] = parent1.transitionStrategy[i];

        return child1;
    }
}
