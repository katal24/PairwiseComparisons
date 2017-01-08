# przypisuje 0 jesli wartosc jest bliska 0
chopV <- function(value){
  if(value>0.000001){
    return(value)
  } else{
    return(0)
  }
}

# przypisuje 0 do kazdego elementu macierzy bliskiego 0
chopM <- function(matrix){
  matrix[matrix<0.000001] <- 0
  return(matrix)
}

#################### Eigenvalue Rank Methods ###################
#' @title The largest principal eigenvalue
#' @description Returns the largest principal eigenvalue of matrix
#' @param matrix - PC matrix
#' @return the largest principal eigenvalue of matrix
#' @export
principalEigenValue <- function(matrix){
  chopM(matrix);
  maxEigenVal <- max(Mod(eigen(apply(matrix, 2, as.numeric))$values))
  chopV(maxEigenVal)
}

#' @title The largest principal eigenvalue (symbolic version)
#' @description A symbolic version of function principalEigenValue
#' @param matrix - PC matrix
#' @return the largest principal eigenvalue of matrix
#' @export
principalEigenValueSym <- function(matrix){
  max(Mod(eigen(matrix)$values))
}

#' @title Eigenvector of matrix
#' @description Returns the eigenvector of matrix corresponding to itd principal eigenvalue
#' @param matrix - PC matrix
#' @return the eigenvector of matrix corresponding to itd principal eigenvaluex
#' @export
principalEigenVector <- function(matrix){
  chopM(matrix);
  maxEigenVector <- Mod(eigen(apply(matrix, 2, as.numeric))$vectors)[,1]
  chopM(maxEigenVector)
}

#' @title Eigenvector of matrix (symbolic version)
#' @description A symbolic version of function principalEigenVector
#' @param matrix - PC matrix
#' @return the eigenvector of matrix corresponding to itd principal eigenvaluex
#' @export
principalEigenVectorSym <- function(matrix){
  Mod(eigen(matrix)$vectors)[,1]
}

#' @title Value of the Saaty Inconsistency Index
#' @description Returns the value of the Saaty Inconsistency Index computed for the matrix
#' @param matrix - PC matrix
#' @return the value of the Saaty Inconsistency Index computed for the matrix
#' @export
saatyIdx <- function(matrix){
  chopM(matrix)
  matrix <- apply(matrix, 2, as.numeric)
  n <- nrow(matrix)
  alpha <- principalEigenValueSym(matrix)
  chopV((alpha - n)/(n-1))
}

#' @title Value of the Saaty Inconsistency Index (symbolic version)
#' @description A symbolic version of function saatyIdxSym
#' @param matrix - PC matrix
#' @return the value of the Saaty Inconsistency Index computed for the matrix
#' @export
saatyIdxSym <- function(matrix){
  n <- nrow(matrix)
  alpha <- principalEigenValueSym(matrix)
  return((alpha - n)/(n-1))
}

#' @title Rescaled principal eigen vector of matrix
#' @description Returns the principal eigen vector of matrix rescaled in a way that the sum of its entries is 1
#' @param matrix - PC matrix
#' @return the rescaled principal eigen vector of matrix
#' @export
eigenValueRank <- function(matrix){
  chopM(matrix)
  matrix <- apply(matrix, 2, as.numeric)
  eigenVector <- chopM(principalEigenVectorSym(matrix));
  chopM(eigenVector/sum(eigenVector))
}

#' @title Rescaled principal eigen vector of matrix (symbolic version)
#' @description   A symbolic version of function eigenValueRank
#' @param matrix - PC matrix
#' @return the principal eigen vector of matrix rescaled in a way that the sum of its entries is 1
#' @export
eigenValueRankSym <- function(matrix){
  eigenVector <- principalEigenVectorSym(matrix);
  eigenVector/sum(eigenVector)
}

#' @title AHP ranking using eigenvalue based method
#' @description
#' Computes milticriteria ranking using eigenvalue based method using criteria matrix and alternatives matrixes.
#' This is basic three levels AHP.
#' @param M - n x n criteria matrix
#' @param ... - matrixes with the comparisons of alternatives with respect to the given criteria
#' @return multicriteria ranking using eigenvalue based method
#' @export ----
ahp <- function(M, ...){
  counter <- 0
  eigenVectorAc <- eigenValueRankSym(M)
  vectors <- lapply(list(...), eigenValueRank)
  verctors2 <- lapply(vectors, function(m){
    counter <<- counter+1
    m*eigenVectorAc[counter]
  })
  chopM(rowSums(data.frame(verctors2)))
}

#java
ahpFromVector <- function(M, matrices){
  counter <- 0
  eigenVectorAc <- eigenValueRankSym(M)
  vectors <- lapply(getListOfMatrices(matrices), eigenValueRank)
  verctors2 <- lapply(vectors, function(m){
    counter <<- counter+1
    m*eigenVectorAc[counter]
  })
  chopM(rowSums(data.frame(verctors2)))
}


################### Geometric mean rankings ###################
#' @title Rank list given as geometric means
#' @description Returns rank list given as geometric means of rows of the matrix
#' @param matrix - PC matrix
#' @return The rank list given as geometric means
#' @export
geometricRank <- function(matrix){
  apply(matrix, 1, function(row){
    prod(row)^(1/length(row))
  })
}

#' @title Rescaled rank list given as geometric means
#' @description Returns rank list given as geometric means of rows of the matrix rescaled in way
#' that the sum of its entries is 1
#' @param matrix - PC matrix
#' @return Rescaled rank list given as geometric means of rows of the matrix
#' @export
geometricRescaledRank <- function(matrix){
  geometricMeanVector <- apply(matrix, 1, function(row){
    prod(row)^(1/length(row))
  })
  geometricMeanVector/sum(geometricMeanVector)
}



################### HRE - Rankings withe the reference value ####################

#' @title Element of matrix
#' @description Returns [r,c] element of matrix
#' @param matrix - PC matrix
#' @param r - row
#' @param c - column
#' @return [r,c] element of matrix
#' @export
getMatrixEntry <- function(matrix, r, c){
  element <- matrix[r,c]
  element
}

#' @title Recreated matrix
#' @description Recreates recipropal matrix of the basis of upper-triagle of matrix
#' @param matrix - matrix
#' @return recreated PC matrix
#' @export
recreatePCMatrix <- function(matrix){
  for(r in 1:dim(matrix)[1]-1)
    for(c in (r+1):dim(matrix)[1])
      matrix[c,r] <- 1/matrix[r,c]
    diag(matrix) <- 1
    matrix
}

#' @title Delete rows
#' @description Delete rows from matrix
#' @param martix - PC matrix
#' @param listOfRows - indices of rows which need to be deleted
#' @return matrix without deleted rows
#' @export
deleteRows <- function(matrix, listOfRows){
  matrix <- matrix[-listOfRows,]
  matrix
}

#' @title Delete columns
#' @description Delete columns from matrix
#' @param martix - matrix
#' @param listOfColumns - indices of columns which need to be deleted
#' @return matrix without deleted columns
#' @export
deleteColumns <- function(matrix, listOfColumns){
  matrix <- matrix[,-listOfColumns]
  matrix
}

#' @title Delete rows and columns
#' @description Delete columns and rows from matrix
#' @param martix - matrix
#' @param listOfRowsAndColumns - indices of rows columns which need to be deleted
#' @return matrix without deleted rows and columns
#' @export ----
deleteRowsAndColumns <- function(matrix, listOfRowsAndColumns){
  matrix <- matrix[-listOfRowsAndColumns,-listOfRowsAndColumns]
  matrix
}

zerosIndices <- function(knownVector){
  which(knownVector == 0)
}

nonZerosIndices <- function(knownVector){
  which(knownVector != 0)
}

nonZerosValues <- function(knownVector){
  knownVector[knownVector != 0]
}

zerosValues <- function(knownVector){
  knownVector[knownVector == 0]
}


#' @title Set diagonal of matrix
#' @description Sets diagonal of matrix to specifed value
#' @param matrix - matrix
#' @param value - value which need to be set on the diagonal
#' @return Matrix with the specifed value on the diagonal
#' @export
setDiagonal <- function(matrix, value){
  diag(matrix) <- value
  matrix
}

#' @title Matrix for the HRE method
#' @description HRE matrix together with HRE constant term vector forms the linear equation system Au=b
#' @param matrix - PC matrix
#' @param knownVector - vector of known alternatives and others are marked by value 0
#' @return Matrix A for the HRE method
#' @export
HREmatrix <- function(matrix, knownVector){
  nonZerosIndices <- nonZerosIndices(knownVector)
  A <- (-1/(dim(matrix)[1]-1))*(deleteRowsAndColumns(matrix,nonZerosIndices))
  A <- setDiagonal(A,1)
  A
}

#' @title constant term vector for the HRE method
#' @description HRE constant term vector together with HRE matrix forms the linear equation system Au=b
#' @param matrix - PC matrix
#' @param knownVector - vector of known alternatives and others are marked by value 0
#' @return constant term vector b for the HRE method
#' @export
HREconstantTermVector <- function(matrix, knownVector){
  nonZerosIndices <- nonZerosIndices(knownVector)
  zerosIndices <- zerosIndices(knownVector)
  matrixWithoutRows <- deleteRows(matrix, nonZerosIndices)
  matrixWithoutRowsandColumns <- deleteColumns(matrixWithoutRows, zerosIndices)
  nonZerosValues <- nonZerosValues(knownVector)
  vectorB <- (1/(dim(matrix)[1]-1))* (matrixWithoutRowsandColumns %*% nonZerosValues)
  vectorB
}

#' @title Unknown HRE values
#' @description Counts values for the unknown alternatives
#' @param matrix - PC matrix
#' @param knownVector - vector of known alternatives and others are marked by value 0
#' @return values for the unknown alternatives
#' @export
HREpartialRank <- function(matrix, knownVector){
  solve(HREmatrix(matrix, knownVector), HREconstantTermVector(matrix, knownVector))
}

createFullRank <- function(knownVector, partialRank){
  zerosIndices <- zerosIndices(knownVector)
  for(i in zerosIndices){
    knownVector[i] <- head(partialRank,1)
    partialRank <- partialRank[-1]
  }
  knownVector
}

#' @title full rank HRE
#' @description Counts values for the unknown alternatives and add their to known alternatives
#' @param matrix - PC matrix
#' @param knownVector - vector of known alternatives and others are marked by value 0
#' @return values for the both: known and unknown alternatives
#' @export
HREfullRank <- function(matrix, knownVector){
  partialRank <- HREpartialRank(matrix, knownVector);
  fullVector <- createFullRank(knownVector, partialRank)
  fullVector
}


#' @title Rescaled HRE full rank
#' @description Counts the rescaled HRE full rank list. Rescaled in the entries sum up to 1
#' @param matrix - PC matrix
#' @param knownVector - vector of known alternatives and others are marked by value 0
#' @return the rescaled HRE full rank list
#' @export
HRErescaledRank <- function(matrix, knownVector){
  fullRank <- HREfullRank(matrix,knownVector);
  chopM(fullRank/sum(fullRank))
}



################### HRE Geometric - Rankings with the reference values ###################

numberOfUnknownValues <- function(knownVector){
  length(zerosValues(knownVector))
}

#' @title Matrix for the HRE geometric method
#' @description HRE matrix together with HRE constant term vector forms the linear equation system Au=b
#' @param matrix - PC matrix
#' @param knownVector - vector of known alternatives and others are marked by value 0
#' @return Matrix A for the HRE method
#' @export
HREgeomMatrix <- function(matrix, knownVector){
  dimOfMatrix <- dim(matrix)[1]
  numberOfUnknownValues <- numberOfUnknownValues(knownVector)
  A <- matrix(-1,numberOfUnknownValues,numberOfUnknownValues)
  diag(A) <- (dimOfMatrix-1)
  A
}

#' @title constant term vector for the HRE geometric method
#' @description HRE constant term vector together with HRE matrix forms the linear equation system Au=b
#' @param matrix - PC matrix
#' @param knownVector - vector of known alternatives and others are marked by value 0
#' @return constant term vector b for the HRE method
#' @export
HREgeomConstantTermVector <- function(matrix, knownVector){
  nonZerosIndices <- nonZerosIndices(knownVector)
  nonZerosValues <- nonZerosValues(knownVector)
  zerosIndices <- zerosIndices(knownVector)
  matrixWithoutRows <- deleteRows(matrix, nonZerosIndices)
  matrixWithoutRows
  log10(apply(matrixWithoutRows, 1, prod)*prod(nonZerosValues))
}

#' @title The base of unknown HRE values
#' @description Counts values for the unknown alternatives befor they are raised by the power 10
#' @param matrix - PC matrix
#' @param knownVector - vector of known alternatives and others are marked by value 0
#' @return vector of values for the unknown alternatives
#' @export
HREgeomIntermediateRank <- function(matrix, knownVector){
  solve(HREgeomMatrix(matrix, knownVector), HREgeomConstantTermVector(matrix, knownVector))
}

#' @title Unknown HRE values
#' @description Counts values for the unknown alternatives using HRE geometric method
#' @param matrix - PC matrix
#' @param knownVector - vector of known alternatives and others are marked by value 0
#' @return vector of values for the unknown alternatives
#' @export
HREgeomPartialRank <- function(matrix, knownVector){
  sapply(HREgeomIntermediateRank(matrix, knownVector), FUN=function(x) 10^x)
}

#' @title full rank HRE using geometric method
#' @description Counts values for the unknown alternatives and add their to known alternatives
#' @param matrix - PC matrix
#' @param knownVector - vector of known alternatives and others are marked by value 0
#' @return values for the both: known and unknown alternatives
#' @export
HREgeomFullRank <- function(matrix, knownVector){
  partialRank <- HREgeomPartialRank(matrix, knownVector);
  fullVector <- createFullRank(knownVector, partialRank)
  fullVector
}

#' @title Rescaled HRE full rank using geometric method
#' @description Counts the rescaled HRE full rank list. Rescaled in the entries sum up to 1
#' @param matrix - PC matrix
#' @param knownVector - vector of known alternatives and others are marked by value 0
#' @return the rescaled HRE full rank list
#' @export
HREgeomRescaledRank <- function(matrix, knownVector){
  fullRank <- HREgeomFullRank(matrix,knownVector);
  chopM(fullRank/sum(fullRank))
}


################### Koczkodaj inconsistency methods ###################

#' @title Koczkodaj triad innconsistency
#' @description Returns the Koczkodaj triad inconsistency
#' @param triad - vector of 3 elements
#' @return the Koczkodaj triad inconsistency
#' @export
koczkodajTriadIdx <- function(triad) {
  min(abs(1-(triad[3]/(triad[1]*triad[2]))), abs(1-(triad[1]*triad[2])/triad[3]))
}

getUniqueTuples <- function(n){
  combn(n,3)
}

makeATriad <- function(matrix, tuple){
  c(
    matrix[tuple[1],tuple[2]],
    matrix[tuple[2],tuple[3]],
    matrix[tuple[1],tuple[3]]
  )
}

uniqueTriadsTuplesAndIdx <- function(matrix){
  tuples <- getUniqueTuples(dim(matrix)[1])
  triads <- apply(tuples, 2, function(x) makeATriad(matrix=matrix,tuple=x))
  triadIdxs <- apply(triads, 2, koczkodajTriadIdx)
  t(rbind(tuples, triads, c(triadIdxs)))
}


#' @title The most inconsistent triad
#' @description Counts the worst triad in matrix according to Koczkodaj inconsistency criterion
#' @param matrix - PC Matrix
#' @return the worst triad in matrix according to Koczkodaj inconsistency criterion
#' @export
koczkodajTheWorstTriad <- function(matrix){
  sortMatrix <- sortKoczkodajMatrix(matrix)
  sortMatrix[1,]
}

#' @title The most inconsistent triads
#' @description Counts the worst triads in matrix according to Koczkodaj inconsistency criterion
#' @param matrix - PC Matrix
#' @param n - number of triads to return
#' @return n worst triads in matrix according to Koczkodaj inconsistency criterion
#' @export  ---
koczkodajTheWorstTriads <- function(matrix, n){
  sortMatrix <- sortKoczkodajMatrix(matrix)
  head(sortMatrix,n)
}

#' @title Koczkodaj inconsistency
#' @description Counts the value of Koczkodaj inconsistency for the matrix
#' @param matrix - PC matrix
#' @return Koczkodaj inconsistency
#' @export ----
koczkodajIdx <- function(matrix){
  sortMatrix <- sortKoczkodajMatrix(matrix)
  getTriadIdx(sortMatrix[1,])
}

getTriad <- function(triadRow){
  c(triadRow[4],triadRow[5],triadRow[6])
}

getTuple<- function(triadRow){
  c(triadRow[1],triadRow[2],triadRow[3])
}

getTriadIdx <- function(triadRow){
  triadRow[7]
}

sortKoczkodajMatrix <- function(M){
  matrixFullKoczkodaj <- uniqueTriadsTuplesAndIdx(M)
  matrixFullKoczkodaj[order(matrixFullKoczkodaj[,7],decreasing = TRUE),]
}

#' @title Consistent triad
#' @description Counts the closest consistenr triad
#' @param triad - vector of three values
#' @return the closest consistend triad
#' @export
koczkodajConsistentTriad <- function(triad){
  q <- triad[1]
  r <- triad[2]
  s <- triad[3]
  c(q^(2/3)*r^(-1/3)*s^(1/3),
    q^(-1/3)*r^(2/3)*s^(1/3),
    q^(1/3)*r^(1/3)*s^(2/3))
}

#' @title More consistent matrix
#' @description Counts improved (more consistent) matrix in which the most inconsistent triad is replaced by tje closest consistent one
#' @param matric - PC matrix
#' @return more consistent PC matrix
#' @export
koczkodajImprovedMatrixStep <- function(matrix){
  worstTriad <- koczkodajTheWorstTriad(matrix)
  improvedTriad <- koczkodajConsistentTriad(getTriad(worstTriad))
  mik = getTuple(worstTriad)[-3]
  mkj = getTuple(worstTriad)[-1]
  mij = getTuple(worstTriad)[-2]

  mikval = improvedTriad[1]
  mkjval = improvedTriad[2]
  mijval = improvedTriad[3]

  matrix[t(mij)] <- mijval
  matrix[mij[2],mij[1]] <- 1/mijval
  matrix[t(mkj)] <- mkjval
  matrix[mkj[2],mkj[1]] <- 1/mkjval
  matrix[t(mik)] <- mikval
  matrix[mik[2],mik[1]] <- 1/mikval
  matrix
}



################### Aggregation of Individual Judgments (AIJ) ###################
#' @title Aggregation matrix/vector (aritmetic means)
#' @description Computes aggregation matrix/vector whose elements are aritmethic means
#' of elements from input matrices/vectors (whose need to be the same dimension)
#' @param ... - matrices orvectors the same dimension
#' @return aggregation matrix/vector
#' @export
AIJadd <- function(...){
  matrixes <- list(...)
  mean <- Reduce('+', matrixes)/length(matrixes)
  mean
}

#' @title Aggregation matrix/vector (geometric means)
#' @description Computes aggregation matrix/vector whose elements are aritmethic means
#' of elements from input matrices/vectors (whose need to be the same dimension)
#' @param ... - matrices or vectors the same dimension
#' @return aggregation matrix/vector
#' @export
AIJgeom <- function(...){
  matrixes <- list(...)
  meanGeom <- Reduce('*', matrixes)^(1/length(matrixes))
  meanGeom
}

#java
AIJgeomFromVector<- function(matrixes){
  matrixes1 <- getListOfMatrices(matrixes)
  meanGeom <- Reduce('*', matrixes1)^(1/length(matrixes1))
  meanGeom
}
 #java
AIJaddFromVector<- function(matrixes){
  matrixes1 <- getListOfMatrices(matrixes)
  mean <- Reduce('+', matrixes1)/length(matrixes1)
  mean
}
#java
AIJvectorsAddFromVector<- function(vectors, len){
  vectors <- getListOfVectors(len, vectors)
  mean <- Reduce('+', vectors)/length(vectors)
  mean
}
#java
AIJvectorsGeomFromVector<- function(vectors, len){
  vectors <- getListOfVectors(len, vectors)
  meanGeom <- Reduce('*', vectors)^(1/length(vectors))
  meanGeom
}

getListOfMatrices <- function(matrix){
  matrices <- list()
  dim <- dim(matrix)[2]
  dim1 <- dim(matrix)[1]
  count <- 1;
  for(i in 1:dim1){
    if(i %% dim == 0 ){
      matrices[count] <- list(matrix[(i-dim+1):i,])
      count <- count+1;
      }
  }
  matrices
}

getListOfVectors <- function(len, vs){
  vectors <- list()
  dim <- length(vs)

  count <- 1;
  for(i in 1:dim){
    if(i %% len == 0 ){
      vectors[count] <- list(vs[(i-len+1):i])
      count <- count+1;
    }
  }
  vectors
}
################### Incomplete pairwise compairsons matrices ###################

#' @title Number of zeros elements in row
#' @description Counts number of zeros elements in given row in matrix
#' @param matrix - matrix
#' @param row - number of row which will be checked
#' @return number of zeros elements in row
#' @export
harkerMatrixPlaceHolderCount <- function(matrix, row){
  length(which(matrix[row,] != 0))   # which - zwraca elementy spelniajace watrunek
}

#' @title Harher matrix
#' @description Computes harker matrix A for a given incomplete PC matrix.
#' Then matrix A is ready to use in eigenvalue based method.
#' Empty and incorrect values are replaced by 0 and diagonal values are replaced by 1.
#' @param matrix - matrix
#' @return matrix which is ready to use in eigenvalue based method
#' @export
harkerMatrix <- function(matrix){
  matrix[matrix < 0] <- 0
  diag(matrix) <- 1
  matrix
}


#################### Ranking discrepancy ###################

#' @title Ranking discrepancy
#' @description Computes discrepancy for each element of matrix
#' and create matrix containing entries in form e_ij = m_ji*(mju_i/mju_j).
#' When the M matrix is consistent every r_ij equals 1.
#' @param M - PC matrix
#' @param mju - ranking of matrix
#' @return error matrix E = [e_ij]
#' @export
errorMatrix <- function(matrix, mju){
  E <- matrix
  for(i in 1:dim(matrix)[1])
    for(j in 1:dim(matrix)[2])
      E[i,j] = matrix[j,i]*mju[i]/mju[j]
    # E = outer(1:nrow(mm), 1:ncol(mm), FUN=function(r,c) {print(r); print(c); mm[r,][c]})
    E
}

#' @title Local discrepancy
#' @description Compute matrix with entries d_ij = max{e_ij-1, 1/e_ij-1}
#' @param matrix - matrix
#' @param mju - ranking of matrix
#' @return matrix with locals discrepancy
#' @export
localDiscrepancyMatrix <- function(matrix, mju){
  errorMatrix <- errorMatrix(matrix,mju)
  for(i in 1:dim(errorMatrix)[1])
    for(j in 1:dim(errorMatrix)[2])
      errorMatrix[i,j] = max( errorMatrix[i,j]-1, 1/errorMatrix[i,j] -1)
    chopM(errorMatrix)
}

#' @title Global discrepancy
#' @description Finds maximal entry of local discrepancy matrix
#' @param matrix - matrix
#' @param mju - ranking of matrix
#' @return maximal value of local discrepancy
#' @export
globalDiscrepancy <- function(matrix, mju){
  max(localDiscrepancyMatrix(matrix, mju))
}



########## Condition of order preservation ##########
# tej jedej nie robie - bo to lista
#' @title List of COP1
#' @description counts the list of indices that violate the first Condition of Order Preservation
#' postulate (formulated by Bana e Costa and Vansnick)
#' @param matrix - PC matrix
#' @param resultList - ranking of matrix
#' @return the matrix of indices that violate the first Condition of Order Preservation (every row includes one pair)
#' @export
cop1ViolationList <- function(matrix, resultList){
  errorList <- matrix(c(0,0),1)
  for(i in 1:dim(matrix)[1]){
    for(j in 1:dim(matrix)[2]){
      element <- matrix[i,j]
      if(i!=j && (element > 1 && resultList[i]<resultList[j]) || (element < 1 && resultList[i]>resultList[j])){
        errorList <- rbind(errorList, c(i,j))
      }
    }
  }
  errorList[-1,]
}

# tego jednego nie mam - bo to boolean
#' @title check COP1
#' @description checks if the first Condition of Order Preservation  is fulfilled
#' @param matrix - PC matrix
#' @param resultList - ranking of matrix
#' @return true if the first Condition of Order Preservation  is fulfilled, else false
#' @export
cop1Check <- function(matrix, resultList){
  length(cop1ViolationList(matrix, resultList)) == 0
}

# ## JAVA
#' @title check COP1Details
#' @description checks if the first Condition of Order Preservation  is fulfilled
#' @param matrix - PC matrix
#' @param resultList - ranking of matrix
#' @return true if the first Condition of Order Preservation  is fulfilled, else false
#' @export
cop1CheckDetails <- function(matrix, resultList){
  if(length(cop1ViolationList(matrix, resultList)) == 0){
    return(1)
  } else {
    return(0);
  }
}

#tego jesdengo nie - bo to lista
#' @title List of COP2
#' @description counts the list of indices that violate the second Condition of Order Preservation
#' postulate (formulated by Bana e Costa and Vansnick)
#' @param matrix - PC matrix
#' @param resultList - ranking of matrix
#' @return the matrix of indices that violate the second Condition of Order Preservation (every row includes one pair)
#' @export
cop2ViolationList <- function(matrix, resultList){
  errorList <- matrix(c(0,0,0,0),1)
  rows <- dim(matrix)[1]
  columns <- dim(matrix)[2]
  for(i in 1:rows)
    for(j in 1:columns)
      for(k in 1:rows)
        for(l in 1:columns){
          if(i!=j && k!=l){
            a <- matrix[i,j]
            b <- matrix[k,l]
            if((a>b && resultList[i]/resultList[j] < resultList[k]/resultList[l]) || (a<b && resultList[i]/resultList[j] > resultList[k]/resultList[l])){
              errorList <- rbind(errorList, c(i,j,k,l))
            }
          }
        }
  errorList
}


#' @title check COP2
#' @description checks if the second Condition of Order Preservation  is fulfilled
#' @param matrix - PC matrix
#' @param resultList - ranking of matrix
#' @return true if the second Condition of Order Preservation is fulfilled, else false
#' @export
cop2Check <- function(matrix, resultList){
  length(cop2ViolationList(matrix, resultList)) == 0
}

# JAVA
#' @title check COP2
#' @description checks if the second Condition of Order Preservation  is fulfilled
#' @param matrix - PC matrix
#' @param resultList - ranking of matrix
#' @return true if the second Condition of Order Preservation is fulfilled, else false
#' @export
cop2CheckDetails <- function(matrix, resultList){
  if(length(cop2ViolationList(matrix, resultList)) == 0){
    return(1)
  } else {
    return(0);
  }
}




#################### Comparing ranks ####################
kendallTauDistanceForPair <- function(list1, list2, element1, element2){
  posInList1Elem1 <- match(element1, list1)
  posInList1Elem2 <- match(element2, list1)
  posInList2Elem1 <- match(element1, list2)
  posInList2Elem2 <- match(element2, list2)
  if(sign(posInList1Elem1 - posInList1Elem2) == sign(posInList2Elem1 - posInList2Elem2)){
    return(0)
  } else{
    return(1)
  }
}

kendallTauDistanceForPairAsVector <- function(list1, list2, c){
  kendallTauDistanceForPair(list1, list2, c[1], c[2])
}

#' @title Kendall Tau distance for two vectors
#' @description Computes Kendall (bubble sort) distance between two rank vectors
#' @param list1 - first rank to compare
#' @param list2 - second rank to compare
#' @return number of swap which need to be make to order of elements will be identical
#' @export
kendallTauDistance <- function(list1, list2){
  sum(apply(combn(list1,2),2,kendallTauDistanceForPairAsVector,list1=list1, list2=list2))
}

#' @title Normalized Kendall Tau distance for two vectors
#' @description Computes Kendall (bubble sort) distance between two rank vectors and divited it by numbers of all possible pairs
#' @param list1 - first rank to compare
#' @param list2 - second rank to compare
#' @return proportion of Kendall Tau distance to numbers of all possible pairs
#' @export
normalizedKendallTauDistance <- function(list1,list2){
  n <- length(list1)
  kendallTauDistance <- kendallTauDistance(list1,list2)
  kendallTauDistance/(n*(n-1)/2)
}



########## Auxiliary functions ##########

#' @title Consistent matrix from rank
#' @description Creates consistent PC matrix from the rank vector V so that m_ij = v[i]/v[j]
#' @param rankList - ranking of matrix
#' @return  - full PC matrix
#' @export
consistentMatrixFromRank <- function(rankList){
  length <- length(rankList)
  m <- matrix(, nrow=length, ncol=length)
  for(i in 1:length)
    for(j in 1:length)
      m[i,j] <- rankList[i]/rankList[j]
  m
}


#' @title Sort rank
#' @description Sorts elements of rank
#' @param rankList - ranking of matrix
#' @return ranking - sorted rank
#' @export
rankOrder <- function(rankList){
  sort(rankList, decreasing=TRUE)
}
