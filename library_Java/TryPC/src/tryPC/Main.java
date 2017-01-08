package tryPC;

import pl.edu.agh.talaga.PairwiseComparisons;

/**
 * Created by dawid on 08.01.17.
 */
public class Main {
    public static void main(String[] args){
        PairwiseComparisons pc = new PairwiseComparisons("/home/dawid/Pulpit/R", "/home/dawid/Pulpit/Rscript", "/home/dawid/Pulpit/pairwiseComparisons.R");

        double[][] matrix = new double[][]{
                {  1,   3d/5d,  4d/7d,   5d/8d,    0.5  },
                {5d/3d,   1,    5d/7d,   5d/2d,  10d/3d },
                {7d/4d, 7d/5d,    1,     7d/2d,     4   },
                {8d/5d, 2d/5d,  2d/7d,     1,     4d/3d },
                {  2  , 3d/10d, 1d/4d,   3d/4d,     1   }
        };
        double[] triad = new double[]{0.8, 3, 0.12};

        double koczkodajIdx = pc.koczkodajIdx(matrix);
     //   double triadIdx = pc.koczkodajTriadIdx(triad);

//        System.out.println("koczkodajTriadIdx:");
//        System.out.println(triadIdx);
        System.out.println("\nkoczkodajMatrixIdx:");
        System.out.println(koczkodajIdx);
    }
}
