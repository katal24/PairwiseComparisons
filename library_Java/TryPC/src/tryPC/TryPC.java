package tryPC;

import pl.edu.agh.talaga.PairwiseComparisons;

import java.util.Arrays;

public class TryPC {
    public static void main(String[] args){
        PairwiseComparisons pairwiseComparisons = new PairwiseComparisons("/usr/bin/R", "/usr/bin/Rscript", "pairwiseComparisons.R", true);

        double[][] matrix = new double[][]{
                {  1,   3d/5d,  4d/7d,   5d/8d,    0.5  },
                {5d/3d,   1,    5d/7d,   5d/2d,  10d/3d },
                {7d/4d, 7d/5d,    1,     7d/2d,     4   },
                {8d/5d, 2d/5d,  2d/7d,     1,     4d/3d },
                {  2  , 3d/10d, 1d/4d,   3d/4d,     1   }
        };
        double[] mk = {0,0,5,2,0};
        int[] listToDelete = new int[]{1,3};
        double[] triad = new double[]{0.8, 3, 0.12};
        double[] list = new double[]{0, 2, 5, 0};
        double[] listToAij1 = new double[]{1, 2, 3};
        double[] listToAij2 = new double[]{4,5,6};


        System.out.println("\n principalEigenValue:\n" + pairwiseComparisons.principalEigenValue(matrix));
        System.out.println("\n principalEigenValueSym:\n" + pairwiseComparisons.principalEigenValueSym(matrix));
        System.out.println("\n principalEigenVector:\n" + Arrays.toString(pairwiseComparisons.principalEigenVector(matrix)));
        System.out.println("\n principalEigenVectorSym:\n" + Arrays.toString(pairwiseComparisons.principalEigenVectorSym(matrix)));

        System.out.println("\n saatyIdx:\n" + pairwiseComparisons.saatyIdx(matrix));
        System.out.println("\n saatyIdxSym:\n" + pairwiseComparisons.saatyIdxSym(matrix));

        System.out.println("\n eigenValueRank:\n" + Arrays.toString(pairwiseComparisons.eigenValueRank(matrix)));
        System.out.println("\n eigenValueRankSym:\n" + Arrays.toString(pairwiseComparisons.eigenValueRankSym(matrix)));
        System.out.println("\n ahp:\n" + Arrays.toString(pairwiseComparisons.ahp(matrix, matrix, matrix, matrix, matrix, matrix)));
        System.out.println("\n geometricRank:\n" + Arrays.toString(pairwiseComparisons.geometricRank(matrix)));
        System.out.println("\n geometricRescaledRank:\n" + Arrays.toString(pairwiseComparisons.geometricRescaledRank(matrix)));

        System.out.println("\n getMatrixEntry:\n" + pairwiseComparisons.getMatrixEntry(matrix, 1, 2));
        System.out.println("\n recreatePCMatrix:\n" + Arrays.deepToString(pairwiseComparisons.recreatePCMatrix(matrix)));
        System.out.println("\n deleteRows:\n" + Arrays.deepToString(pairwiseComparisons.deleteRows(matrix,listToDelete)));
        System.out.println("\n deleteColumns:\n" + Arrays.deepToString(pairwiseComparisons.deleteColumns(matrix,listToDelete)));
        System.out.println("\n deleteRowsAndColumns:\n" + Arrays.deepToString(pairwiseComparisons.deleteRowsAndColumns(matrix,listToDelete)));
        System.out.println("\n setDiagonal:\n" + Arrays.deepToString(pairwiseComparisons.setDiagonal(matrix,10)));

        System.out.println("\n HREmatrix:\n" + Arrays.deepToString(pairwiseComparisons.HREmatrix(matrix, mk)));
        System.out.println("\n HREconstantTermVector:\n" + Arrays.toString(pairwiseComparisons.HREconstantTermVector(matrix,mk)));
        System.out.println("\n HREpartialRank:\n" + Arrays.toString(pairwiseComparisons.HREpartialRank(matrix,mk)));
        System.out.println("\n HREfullRank:\n" + Arrays.toString(pairwiseComparisons.HREfullRank(matrix,mk)));
        System.out.println("\n HRErescaledRank:\n" + Arrays.toString(pairwiseComparisons.HRErescaledRank(matrix,mk)));
        System.out.println("\n HREgeomMatrix:\n" + Arrays.deepToString(pairwiseComparisons.HREgeomMatrix(matrix,mk)));
        System.out.println("\n HREgeomConstantTermVector:\n" + Arrays.toString(pairwiseComparisons.HREgeomConstantTermVector(matrix,mk)));
        System.out.println("\n HREgeomIntermediateRank:\n" + Arrays.toString(pairwiseComparisons.HREgeomIntermediateRank(matrix,mk)));
        System.out.println("\n HREgeomPartialRank:\n" + Arrays.toString(pairwiseComparisons.HREgeomPartialRank(matrix,mk)));
        System.out.println("\n HREgeomFullRank:\n" + Arrays.toString(pairwiseComparisons.HREgeomFullRank(matrix,mk)));
        System.out.println("\n HREgeomRescaledRank:\n" + Arrays.toString(pairwiseComparisons.HREgeomRescaledRank(matrix,mk)));

        System.out.println("\n koczkodajTriadIdx:\n" + pairwiseComparisons.koczkodajTriadIdx(triad));
        System.out.println("\n koczkodajTheWorstTriad:\n" + Arrays.toString(pairwiseComparisons.koczkodajTheWorstTriad(matrix)));
        System.out.println("\n koczkodajTheWorstTriads:\n" + Arrays.deepToString(pairwiseComparisons.koczkodajTheWorstTriads(matrix,3)));
        System.out.println("\n koczkodajIdx:\n" + pairwiseComparisons.koczkodajIdx(matrix));
        System.out.println("\n koczkodajConsistentTriad:\n" + Arrays.toString(pairwiseComparisons.koczkodajConsistentTriad(triad)));
        System.out.println("\n koczkodajImprovedMatrixStep:\n" + Arrays.deepToString(pairwiseComparisons.koczkodajImprovedMatrixStep(matrix)));

        System.out.println("\n harkerMatrixPlaceHolderCount:\n" + pairwiseComparisons.harkerMatrixPlaceHolderCount(matrix, 3));
        System.out.println("\n harkerMatrix:\n" + Arrays.deepToString(pairwiseComparisons.harkerMatrix(matrix)));
        System.out.println("\n errorMatrix:\n" + Arrays.deepToString(pairwiseComparisons.errorMatrix(matrix, pairwiseComparisons.eigenValueRank(matrix))));
        System.out.println("\n localDiscrepancyMatrix:\n" + Arrays.deepToString(pairwiseComparisons.localDiscrepancyMatrix(matrix, pairwiseComparisons.eigenValueRank(matrix))));
        System.out.println("\n globalDiscrepancy:\n" + pairwiseComparisons.globalDiscrepancy(matrix, pairwiseComparisons.eigenValueRank(matrix)));

        double[] mu = pairwiseComparisons.HREgeomRescaledRank(matrix, mk);
        System.out.println("\n cop1Check:\n" + pairwiseComparisons.cop1Check(matrix,mu));
        System.out.println("\n cop1ViolationList:\n" + Arrays.deepToString(pairwiseComparisons.cop1ViolationList(matrix,mu)));
        System.out.println("\n cop2Check:\n" + pairwiseComparisons.cop2Check(matrix, mu));
        System.out.println("\n cop2ViolationList:\n" + Arrays.deepToString(pairwiseComparisons.cop2ViolationList(matrix, mu)));

        pairwiseComparisons.open();
        pairwiseComparisons.close();
        pairwiseComparisons.open();

        System.out.println("\n kendallTauDistance:\n" + pairwiseComparisons.kendallTauDistance(new double[]{1,2,4,3}, new double[]{1,2,3,4}));
        System.out.println("\n normalizedKendallTauDistance:\n" + pairwiseComparisons.normalizedKendallTauDistance(new double[]{1,2,4,3}, new double[]{1,2,3,4}));

        pairwiseComparisons.setKeepOpenConnection(false);

        System.out.println("\n consistentMatrixFromRank:\n" + Arrays.deepToString(pairwiseComparisons.consistentMatrixFromRank(new double[]{1,2,3,4})));
        System.out.println("\n rankOrder:\n" + Arrays.toString(pairwiseComparisons.rankOrder(list)));

        System.out.println("\n AIJgeom:\n" + Arrays.deepToString(pairwiseComparisons.AIJgeom(matrix, matrix, matrix)));
        System.out.println("\n AIJgeom:\n" + Arrays.toString(pairwiseComparisons.AIJgeom(listToAij1, listToAij2)));
        System.out.println("\n AIJadd:\n" + Arrays.deepToString(pairwiseComparisons.AIJadd(matrix, matrix, matrix)));
        System.out.println("\n AIJadd:\n" + Arrays.toString(pairwiseComparisons.AIJadd(listToAij1, listToAij2)));
    }
}