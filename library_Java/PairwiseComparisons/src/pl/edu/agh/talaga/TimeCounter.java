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

public class TimeCounter implements Runnable {
    int counter = 0;
    int MAX_TIME = 0;
    boolean openRfile;

    public TimeCounter(int maxTime){
        this.MAX_TIME = maxTime*1000;
        this.openRfile = false;
    }

    public TimeCounter(int maxTime, boolean openRfile){
        this.openRfile = openRfile;
        this.MAX_TIME = maxTime*1000;
    }

    @Override
    public void run(){
        counter = 0;

        try{
            while( !Thread.currentThread().isInterrupted()){
                if(counter > MAX_TIME){
                    if(this.openRfile){
                        throw new RcallException("Invalid path to pairwiseComparisons.R file.");
                    } else{
                        throw new RcallException("A problem occured while call function in R. Check your variables.");
                    }
                }
                counter += 100;
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e){
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}