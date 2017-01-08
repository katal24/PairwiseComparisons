# PairwiseComparisons library

### The repository contains:
##### library_R:
* R package
##### library_Java directory:
* Java source code of the library,
* R file - responsible for computing
* TryPC - Example project in Java, which shows the use of the library
* .jar library

### In order to use PairwiseComparisons library in own project need to:
1. Install R package (http://cran.us.r-project.org/)
2. Download pairwiseComparisons.R file.
3. Add PairwiseComparisons.jar to project build path.
4. Import class _pl.edu.agh.talaga.PairwiseComparisons_.
5. Create new object PairwiseComparisons. You can give the path to pairwiseComparisons.R file as parameter in constructor or place it in the main project folder. 
6. Call methods.

### Constructor:
``PairwiseComparisons(String pathToR, String pathToRscript, [optional]String pathToSource, [optional]boolean keepOpenConnection)``
* _pathToR_ - set path to R, on Linux usually /usr/bin/R,
* _pathToRscript_ - set path to Rscript, on Linux usually /usr/bin/Rscript,
* _pathToSource_ - set path to pairwiseComparisons.R,
* _keepOpenConnection_ - set connection mode, default disposable mode.

### Additional information to help you use the library:
1. Library can work in continous mode (one permanent connection) or disposable mode (one connection for every method call).
Contionus mode works faster with greater number of function calls, but you be should close connection when it will no longer be used. You could remove PairwiseComparisons object or call ``close()`` method.

2. Default constructor uses continous mode. In order to change mode, you should call constructor with parameter false: ``new PairwiseComparisons(false)`` or ``new PairwiseComparisons(pathToRfile, false)`` .

3. Function ``setKeepOpenConnection(boolean)`` is also used for determining the mode of the library. It can be called many times during the runtime.

4. Functions _open_ and _close_ allow to open/close connection manually.
