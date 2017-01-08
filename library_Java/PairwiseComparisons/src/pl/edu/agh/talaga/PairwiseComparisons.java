//Java library to calculate pairwise comparisons methods
//        Dawid Talaga (C) 2016
//        e-mail: talagadawid@gmail.com
//
//This program is free software: you can redistribute it and/or modify
//        it under the terms of the GNU General Public License as published by
//        the Free Software Foundation, either version 3 of the License, or
//        (at your option) any later version.
//
//        This program is distributed in the hope that it will be useful,
//        but WITHOUT ANY WARRANTY; without even the implied warranty of
//        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//        GNU General Public License for more details.
//
//        You should have received a copy of the GNU General Public License
//        along with this program.  If not, see <http://www.gnu.org/licenses/>.

package pl.edu.agh.talaga;

import com.github.rcaller.scriptengine.RCallerScriptEngine;

import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PairwiseComparisons {

    private ScriptEngineManager manager;
    private RCallerScriptEngine engine;
    private boolean keepOpenConnection;
    private boolean engineIsOpen;
    private String pathToSource;
    private Thread t;
    private double[][] matrixResult;
    private double[] vector;
    double result;
    private String R;
    private String Rscript;

    public PairwiseComparisons(String R, String Rscript, String pathToSource){
        makeEngine();
        this.keepOpenConnection = false;
        this.engineIsOpen = false;
        this.R = R;
        this.Rscript = Rscript;
        this.pathToSource = pathToSource;
    }

    public PairwiseComparisons(String R, String Rscript){
        makeEngine();
        this.keepOpenConnection = false;
        this.engineIsOpen = false;
        this.R = R;
        this.Rscript = Rscript;
        this.pathToSource = "pairwiseComparisons.R";
    }

    public PairwiseComparisons(String R, String Rscript, String pathToSource, boolean keepOpenConnection){
        makeEngine();
        this.keepOpenConnection = keepOpenConnection;
        this.engineIsOpen = false;
        this.R = R;
        this.Rscript = Rscript;
        this.pathToSource = pathToSource;
        if(keepOpenConnection){
            makeCaller();
        }
    }

    public PairwiseComparisons(String R, String Rscript, boolean keepOpenConnection){
        makeEngine();
        this.keepOpenConnection = keepOpenConnection;
        this.engineIsOpen = false;
        this.R = R;
        this.Rscript = Rscript;
        this.pathToSource = "pairwiseComparisons.R";
        if(keepOpenConnection){
            makeCaller();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(engineIsOpen){
            engine.close();
        }
    }


    void makeEngine() {
        manager = new ScriptEngineManager();
    }

    private void makeCaller(){
        engine = new RCallerScriptEngine(this.R, this.Rscript);
        Thread openFileThread = new Thread(new TimeCounter(2, true));
        openFileThread.start();
        try {
            engine.eval("source(\""+this.pathToSource+"\")");
        } catch (Exception e) {
            throw new RcallException(e.toString());
        } finally {
            openFileThread.interrupt();
        }

        engineIsOpen = true;
    }

    public void close(){
        try {


            engine.close();
        } catch(Exception e){
            throw new RcallException("Rconnection in closed.");
        }
            engineIsOpen = false;
    }

    public void open(){
        if(!engineIsOpen) {
            makeCaller();
        }
    }

    private void openConnection(){
        if(!keepOpenConnection && !engineIsOpen){
            makeCaller();
            engineIsOpen = true;
        }
        t = new Thread(new TimeCounter(5));
        t.start();
    }

    private void closeIfOpen(){
        if(engineIsOpen){
            close();
        }
        if(t != null){
            t.interrupt();
        }
    }

    private void closeConnection(){
        if(!keepOpenConnection && engineIsOpen){
            engine.close();
            engineIsOpen = false;
        }
        t.interrupt();
    }

    public void setKeepOpenConnection(boolean keepOpenConnection){
        if(!this.keepOpenConnection && keepOpenConnection && !engineIsOpen){
            makeCaller();
        }
        this.keepOpenConnection = keepOpenConnection;
    }

    public boolean getKeepOpenConnection(){
        return keepOpenConnection;
    }

    // // VALIDATION

   private void validateDimOfMatrix(double[][] matrix) throws PcMatrixException {
        int dimension1 = matrix.length;

        for(int i=0; i<dimension1; i++){
            int dimension2 = matrix[i].length;
             if(dimension1 != dimension2){
                 closeIfOpen();
                 throw new PcMatrixException("Dimension of matrix is incorrect.");
            }
        }

    }

    private void validateMatrix(double[][] matrix) throws PcMatrixException {
        validateDimOfMatrix(matrix);
        int dimension1 = matrix.length;
        int dimension2 = matrix[0].length;

        for(int i=0; i<dimension1; i++){
            for(int j=0; j<dimension2; j++){
                if(i==j && matrix[i][j]!=1){
                    closeIfOpen();
                    throw new PcMatrixException("One or more values on diagonal is incorrect.");
                }
                if(matrix[i][j] <=0 ){
                    closeIfOpen();
                    throw new PcMatrixException("One or more values in matrix is not positive.");
                }
            }
        }
    }

    private void validateMatrixAndVector(double[][] matrix, double[] vector) throws PcMatrixException {
        validateMatrix(matrix);
        validateDoubleVector(vector);
        int matrixDimension = matrix.length;
        int vectorDimension = vector.length;

        if(matrixDimension != vectorDimension){
            closeIfOpen();
            throw new PcMatrixException("Dimension of matrix or vector is incorrect.");
        }
    }

    private void validateIntVector(int[] vector) throws PcValueException {
        for(int i=0; i<vector.length; i++){
           if(vector[i]<0){
               closeIfOpen();
               throw new PcValueException("One or more values in vector is not positive.");
            }
        }
    }

    private boolean isDuplicate(int[] vector){
        Set<Integer> set = new HashSet<>();
        for(int i=0; i<vector.length; i++){
                set.add(vector[i]);
        }
        if(set.size() != vector.length){
            return true;
        }
        return false;
    }

    private void validateIntVectorToRowDelete(double[][] matrix, int[] vector) throws PcValueException {
        if(isDuplicate(vector)){
            closeIfOpen();
            throw new PcValueException("Duplicate in vector.");
        }
        for(int i=0; i<matrix.length; i++){
            if(vector.length > matrix[i].length){
                closeIfOpen();
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
        for(int i=0; i<vector.length; i++){
            if(vector[i] > matrix.length){
                closeIfOpen();
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
    }

    private void validateIntVectorToColumnDelete(double[][] matrix, int[] vector) throws PcValueException {
        if(isDuplicate(vector)){
            throw new PcValueException("Duplicate in vector.");
        }
        for(int i=0; i<matrix.length; i++){
            if(vector.length > matrix[i].length){
                closeIfOpen();
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
        for(int i=0; i<vector.length; i++){
            if(vector[i] > matrix[0].length){
                closeIfOpen();
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
    }

    private void validateIntVectorToRowAndColumnDelete(double[][] matrix, int[] vector) throws PcValueException {
        if(isDuplicate(vector)){
            closeIfOpen();
            throw new PcValueException("Duplicate in vector.");
        }
        for(int i=0; i<matrix.length; i++){
            if(vector.length > matrix[i].length){
                closeIfOpen();
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
        for(int i=0; i<vector.length; i++){
            if(vector[i] > matrix.length || vector[i] > matrix[0].length){
                closeIfOpen();
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
    }

    private void validateTwoDoubleVectorToKendall(double[] vectorA, double[] vectorB) throws PcValueException {
        if(vectorA.length != vectorB.length){
            closeIfOpen();
            throw new PcValueException("Vectors must be the same size");
        }
        for(int i=0; i<vectorA.length; i++){
            if(!isValueInVector(vectorB, vectorA[i]) || !isValueInVector(vectorA, vectorB[i]) ){
                closeIfOpen();
                throw new PcValueException("Vectors must have the same values");
            }
        }
    }

    private void validateDoubleVector(double[] vector) throws PcValueException {
        for(int i=0; i<vector.length; i++){
            if(vector[i]<0){
                closeIfOpen();
                throw new PcValueException("One or more values in vector is not positive.");
            }
        }
    }

    private boolean isValueInVector(double[] vector, double value){
        for(int i=0; i<vector.length; i++){
            if(vector[i] == value){
                return true;
            }
        }
        return false;
    }

    private void validateTriad(double[] triad) throws PcValueException {
        if(triad.length != 3){
            closeIfOpen();
            throw new PcValueException("Number of triad elements is not equal 3.");
        }
    }

    private void validateInt(int value) throws PcValueException {
            if(value<0){
                closeIfOpen();
                throw new PcValueException("Value is not positive.");
            }
    }

    private void validateGetFromMatrix(double[][] matrix, int row, int column) throws PcMatrixException {
        if(row > matrix.length || column > matrix[0].length){
            closeIfOpen();
            throw new PcMatrixException("Matrix index out of bounds");
        }
    }

    private void validateCheckSize(int count){
        if(count != 1){
            closeIfOpen();
            throw new PcValueException("Vectors must have the same length.");
        }
    }


    //// PAIRWISE COMPARISONS METHODS

    public double principalEigenValue(double[][] matrix){
        openConnection();
        validateMatrix(matrix);
        try {
            engine.put("m", matrix);
            engine.eval("res <- principalEigenValue(m)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return result;
    }


    public double principalEigenValueSym(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- principalEigenValueSym(m)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return result;
    }


    public double[] principalEigenVector(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- principalEigenVector(m)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] principalEigenVectorSym(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- principalEigenVectorSym(m)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double saatyIdx(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- saatyIdx(m)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return result;
    }


    public double saatyIdxSym(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- saatyIdxSym(m)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return result;
    }


    public double[] eigenValueRank(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- eigenValueRank(m)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] eigenValueRankSym(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- eigenValueRankSym(m)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] ahp(double[][] mainMatrix, double[][]... matrices){
        validateMatrix(mainMatrix);
        validateDimOfMatrix(mainMatrix);
        Set<Integer> countLenth = new HashSet<>();
        countLenth.add(mainMatrix.length);
        for(double[][] matrix : matrices){
            validateMatrix(matrix);
            validateDimOfMatrix(matrix);
            countLenth.add(matrix.length);
        }
        validateCheckSize(countLenth.size());
        countLenth.clear();
        countLenth.add(mainMatrix.length);
        countLenth.add(matrices.length);
        validateCheckSize(countLenth.size());

        double[][] matrix = rbindMatrices(matrices);
        openConnection();

        try {

            engine.put("M", mainMatrix);
            engine.put("matrices", matrix);
            engine.eval("res <- ahpFromVector(M,matrices)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    private double[] ahp( double[][] matrices){
        double[][] mainMatrix = Arrays.copyOfRange(matrices, 0, matrices[0].length);
        double[][] matrix = Arrays.copyOfRange(matrices, matrices[0].length, matrices.length);
        System.out.println(Arrays.deepToString(mainMatrix));
        System.out.println(Arrays.deepToString(matrix));

        validateMatrix(mainMatrix);
        validateDimOfMatrix(mainMatrix);
        Set<Integer> countLenth = new HashSet<>();
        countLenth.add(mainMatrix.length);
        countLenth.add(matrix[0].length);
        validateCheckSize(countLenth.size());

        openConnection();

        try {
            engine.put("M", mainMatrix);
            engine.put("matrices", matrix);
            engine.eval("res <- ahpFromVector(M,matrices)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }




    public double[] geometricRank(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- geometricRank(m)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] geometricRescaledRank(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- geometricRescaledRank(m)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double getMatrixEntry(double[][] matrix, int row, int column){
        validateInt(row);
        validateInt(column);
        validateGetFromMatrix(matrix, row, column);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("r", row);
            engine.put("c", column);
            engine.eval("res <- getMatrixEntry(m, r, c)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return result;
    }


    public double[][] recreatePCMatrix(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- t(recreatePCMatrix(m))");
            matrixResult = (double[][]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return matrixResult;
    }


    public double[][] deleteRows(double[][] matrix, int[] listOfRows){
        validateMatrix(matrix);
        validateIntVector(listOfRows);
        validateIntVectorToRowDelete(matrix, listOfRows);

        openConnection();

        double[] temp = null;
        try {
            engine.put("m", matrix);
            engine.put("l", listOfRows);
            engine.eval("res <- deleteRows(m,l)");
            engine.eval("res1 <- c(deleteRows(m,l))");
            temp = (double[]) engine.get("res1");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        double[][] m = new double[temp.length/matrix[0].length][matrix[0].length];

        ArrayList<Double> l = new ArrayList<Double>();
        for(int i=0; i< temp.length; i++){
                l.add(temp[i]);
        }

        for(int i=0; i<m[0].length; i++){
            for(int j=0; j<m.length; j++){
                m[j][i] = l.get(0);
                l.remove(0);
            }
        }

        closeConnection();
        return m;
    }


    public double[][] deleteColumns(double[][] matrix, int[] listOfColumns){
        validateMatrix(matrix);
        validateIntVector(listOfColumns);
        validateIntVectorToColumnDelete(matrix, listOfColumns);

        openConnection();


        double[] temp = null;
        try {
            engine.put("m", matrix);
            engine.put("l", listOfColumns);
            engine.eval("res <- deleteColumns(m,l)");
            engine.eval("res1 <- c(deleteColumns(m,l))");
            temp = (double[]) engine.get("res1");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        double[][] m = new double[matrix.length][temp.length/matrix.length];


        ArrayList<Double> l = new ArrayList<Double>();
        for(int i=0; i< temp.length; i++){
            l.add(temp[i]);
        }

        for(int i=0; i<m[0].length; i++){
            for(int j=0; j<m.length; j++){
                m[j][i] = l.get(0);
                l.remove(0);
            }
        }

        closeConnection();
        return m;
    }


    public double[][] deleteRowsAndColumns(double[][] matrix, int[] listOfRowsColumns){
        validateMatrix(matrix);
        validateIntVector(listOfRowsColumns);
        validateIntVectorToRowAndColumnDelete(matrix, listOfRowsColumns);
        openConnection();
        double[] temp = null;
        try {
            engine.put("m", matrix);
            engine.put("l", listOfRowsColumns);
            engine.eval("res1 <- c(deleteRowsAndColumns(m,l))");
            temp = (double[]) engine.get("res1");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        double[][] m = new double[matrix.length - listOfRowsColumns.length][matrix[0].length - listOfRowsColumns.length];


        ArrayList<Double> l = new ArrayList<Double>();
        for(int i=0; i< temp.length; i++){
            l.add(temp[i]);
        }

        for(int i=0; i<m[0].length; i++){
            for(int j=0; j<m.length; j++){
                m[j][i] = l.get(0);
                l.remove(0);
            }
        }
        closeConnection();
        return m;
    }


    public double[][] setDiagonal(double[][] matrix, double valueToSet){
        validateMatrix(matrix);
        openConnection();

        try {
            engine.put("m", matrix);
            engine.put("v", valueToSet);
            engine.eval("res <- t(setDiagonal(m,v))");
            matrixResult = (double[][]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return matrixResult;
    }


    public double[][] HREmatrix(double[][] matrix, double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();

        try {
            engine.put("m", matrix);
            engine.put("v", knowVector);
            engine.eval("res <- t(HREmatrix(m,v))");
            matrixResult = (double[][]) engine.get("res");
        }
     catch (Exception e) {
        closeIfOpen();
        throw new RcallException("A problem occured while call function in R. Check your variables.");
    }

        closeConnection();

        return matrixResult;
    }


    public double[] HREconstantTermVector(double[][] matrix, double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("v", knowVector);
            engine.eval("res <- HREconstantTermVector(m,v)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] HREpartialRank(double[][] matrix, double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("v", knowVector);
            engine.eval("res <- HREpartialRank(m,v)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }


        closeConnection();
        return vector;
    }


    public double[] HREfullRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("v", knowVector);
            engine.eval("res <- HREfullRank(m,v)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] HRErescaledRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("v", knowVector);
            engine.eval("res <- HRErescaledRank(m,v)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[][] HREgeomMatrix(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("v", knowVector);
            engine.eval("res <- t(HREgeomMatrix(m,v))");
            matrixResult = (double[][]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return matrixResult;
    }


    public double[] HREgeomConstantTermVector(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("v", knowVector);
            engine.eval("res <- HREgeomConstantTermVector(m,v)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] HREgeomIntermediateRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("v", knowVector);
            engine.eval("res <- HREgeomIntermediateRank(m,v)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] HREgeomPartialRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("v", knowVector);
            engine.eval("res <- HREgeomPartialRank(m,v)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] HREgeomFullRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("v", knowVector);
            engine.eval("res <- HREgeomFullRank(m,v)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] HREgeomRescaledRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("v", knowVector);
            engine.eval("res <- HREgeomRescaledRank(m,v)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double koczkodajTriadIdx(double[] triad){
        validateTriad(triad);
        openConnection();
        try {
            engine.put("v", triad);
            engine.eval("res <- koczkodajTriadIdx(v)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return result;
    }


    public double[] koczkodajTheWorstTriad(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- koczkodajTheWorstTriad(m)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[][] koczkodajTheWorstTriads(double[][] matrix,  int numberOfTriads){
        validateMatrix(matrix);
        validateInt(numberOfTriads);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("v", numberOfTriads);
            engine.eval("res <- koczkodajTheWorstTriads(m,v)");
            matrixResult = (double[][]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return matrixResult;
    }


    public double koczkodajIdx(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- koczkodajIdx(m)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return result;
    }


    public double[] koczkodajConsistentTriad(double[] triad){
        validateTriad(triad);
        openConnection();
        try {
            engine.put("v", triad);
            engine.eval("res <- koczkodajConsistentTriad(v)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[][] koczkodajImprovedMatrixStep(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- t(koczkodajImprovedMatrixStep(m))");
            matrixResult = (double[][]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return matrixResult;
    }


    public double[][] AIJadd(double[][]... matrices){
        Set<Integer> countLenth = new HashSet<>();
        for(double[][] matrix : matrices){
            validateMatrix(matrix);
            validateDimOfMatrix(matrix);
            countLenth.add(matrix.length);
        }
        validateCheckSize(countLenth.size());

        double[][] bigMatrix = rbindMatrices(matrices);
        openConnection();
        try {
            engine.put("m", bigMatrix);
            engine.eval("res <- t(AIJaddFromVector(m))");
            matrixResult = ((double[][]) engine.get("res"));
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return matrixResult;
    }


    public double[][] AIJgeom(double[][]... matrices){
        Set<Integer> countLenth = new HashSet<>();
        for(double[][] matrix : matrices){
            validateMatrix(matrix);
            validateDimOfMatrix(matrix);
            countLenth.add(matrix.length);
        }
        validateCheckSize(countLenth.size());

        double[][] bigMatrix = rbindMatrices(matrices);
        openConnection();
        try {
            engine.put("m", bigMatrix);
            engine.eval("res <- t(AIJgeomFromVector(m))");
            matrixResult = ((double[][]) engine.get("res"));
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return matrixResult;
    }


    public double[][] rbindMatrices(double[][]... matrices){
        double[][] matrix = new double[matrices.length * matrices[0].length][];
        int counter = 0;
        for(double[][] m : matrices){
            for(int row=0; row<m.length; row++){
                matrix[counter] = m[row];
                counter++;
            }
        }
        return matrix;
    }


    public double[] AIJadd(double[]... vectors){
        Set<Integer> countLenth = new HashSet<>();
        for(double[] vector : vectors){
            validateDoubleVector(vector);
            countLenth.add(vector.length);
        }
        validateCheckSize(countLenth.size());

        double[] bigVector = rbindVectors(vectors);
        openConnection();
        try {
            engine.put("m", bigVector);
            engine.put("len", vectors[0].length);
            engine.eval("res <- AIJvectorsAddFromVector(m, len)");
            vector = ((double[]) engine.get("res"));
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] AIJgeom(double[]... vectors){
        Set<Integer> countLenth = new HashSet<>();
        for(double[] vector : vectors){
            validateDoubleVector(vector);
            countLenth.add(vector.length);
        }
        validateCheckSize(countLenth.size());


        double[] bigVector = rbindVectors(vectors);
        openConnection();

        try {
            engine.put("m", bigVector);
            engine.put("len", vectors[0].length);
            engine.eval("res <- AIJvectorsGeomFromVector(m, len)");
            vector = ((double[]) engine.get("res"));
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }


    public double[] rbindVectors(double[]... vectors){
        double[] vector = new double[vectors.length * vectors[0].length];
        int counter = 0;
        for(double[] v : vectors){
            for(int el=0; el<v.length; el++){
                vector[counter] = v[el];
                counter++;
            }
        }
        return vector;
    }

    public double harkerMatrixPlaceHolderCount(double[][] matrix, int row){
        validateMatrix(matrix);
        validateInt(row);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("r", row);
            engine.eval("res <- t(harkerMatrixPlaceHolderCount(m,r))");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return result;
    }


    public double[][] harkerMatrix(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.eval("res <- t(harkerMatrix(m))");
            matrixResult = (double[][]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return matrixResult;
    }


    public double[][] errorMatrix(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("r", rankingOfMatrix);
            engine.eval("res <- t(errorMatrix(m,r))");
            matrixResult = (double[][]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return matrixResult;
    }


    public double[][] localDiscrepancyMatrix(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("r", rankingOfMatrix);
            engine.eval("res <- t(localDiscrepancyMatrix(m,r))");
            matrixResult = (double[][]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return matrixResult;
    }


    public double globalDiscrepancy(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("r", rankingOfMatrix);
            engine.eval("res <- globalDiscrepancy(m,r)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return result;
    }


    private int[][] castToIntMatrix(double[][] doubleMatrix){
        int[][] intMatrix = new int[doubleMatrix.length][doubleMatrix[0].length];
        for(int i=0; i< doubleMatrix.length; i++){
            for(int j=0; j< doubleMatrix[0].length; j++) {
                intMatrix[i][j] = (int) doubleMatrix[i][j];
            }
        }
        return intMatrix;
    }


    public int[][] cop1ViolationList(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        boolean temp = cop1Check(matrix, rankingOfMatrix);
        if(temp){
            return new int[][]{};
        }
        openConnection();
        double[] temp1 = null;
        try {
            engine.put("m", matrix);
            engine.put("r", rankingOfMatrix);
            engine.eval("res1 <- c(cop1ViolationList(m,r))");
            temp1 = (double[]) engine.get("res1");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        double[][] m = new double[temp1.length/2][2];

        ArrayList<Double> l = new ArrayList<Double>();
        for(int i=0; i< temp1.length; i++){
            l.add(temp1[i]);
        }

        for(int i=0; i<m[0].length; i++){
            for(int j=0; j<m.length; j++){
                m[j][i] = l.get(0);
                l.remove(0);
            }
        }

        int[][] intMatrix = castToIntMatrix(m);
        closeConnection();
        return intMatrix;
    }


    public boolean cop1Check(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("r", rankingOfMatrix);
            engine.eval("res <- cop1CheckDetails(m,r)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        boolean resBool = result == 0.0 ? false : true;
        closeConnection();
        return resBool;
    }


    public int[][] cop2ViolationList(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        boolean temp = cop2Check(matrix, rankingOfMatrix);
        if(temp){
            return new int[][]{};
        }
        openConnection();
        double[] temp1 = null;
        try {
            engine.put("m", matrix);
            engine.put("r", rankingOfMatrix);
            engine.eval("res1 <- c(cop2ViolationList(m,r))");
            temp1 = (double[]) engine.get("res1");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        double[][] m = new double[temp1.length/4][4];

        ArrayList<Double> l = new ArrayList<Double>();
        for(int i=0; i< temp1.length; i++){
            l.add(temp1[i]);
        }

        for(int i=0; i<m[0].length; i++){
            for(int j=0; j<m.length; j++){
                m[j][i] = l.get(0);
                l.remove(0);
            }
        }

        int[][] intMatrix = castToIntMatrix(m);
        closeConnection();
        return Arrays.copyOfRange(intMatrix, 1, intMatrix.length);
    }


    public boolean cop2Check(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        openConnection();
        try {
            engine.put("m", matrix);
            engine.put("r", rankingOfMatrix);
            engine.eval("res <- cop2CheckDetails(m,r)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        boolean resBool = result == 0.0 ? false : true;
        closeConnection();
        return resBool;
    }


    public double kendallTauDistance(double[] vectorA, double[] vectorB){
        validateTwoDoubleVectorToKendall(vectorA, vectorB);
        openConnection();
        try {
            engine.put("v1", vectorA);
            engine.put("v2", vectorB);
            engine.eval("res <- kendallTauDistance(v1,v2)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return result;
    }


    public double normalizedKendallTauDistance(double[] vectorA, double[] vectorB){
        validateTwoDoubleVectorToKendall(vectorA, vectorB);
        openConnection();
        try {
            engine.put("v1", vectorA);
            engine.put("v2", vectorB);
            engine.eval("res <- normalizedKendallTauDistance(v1,v2)");
            result = ((double[]) engine.get("res"))[0];
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return result;
    }


    public double[][] consistentMatrixFromRank(double[] rankingOfMatrix){
        validateDoubleVector(rankingOfMatrix);
        openConnection();
        try {
            engine.put("r", rankingOfMatrix);
            engine.eval("res <- t(consistentMatrixFromRank(r))");
            matrixResult = (double[][]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return matrixResult;
    }


    public double[] rankOrder(double[] rankingOfMatrix){
        openConnection();
        try {
            engine.put("r", rankingOfMatrix);
            engine.eval("res <- rankOrder(r)");
            vector = (double[]) engine.get("res");
        } catch (Exception e) {
            closeIfOpen();
            throw new RcallException("A problem occured while call function in R. Check your variables.");
        }

        closeConnection();
        return vector;
    }
}