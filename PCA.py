from math import sqrt
from numpy.linalg import eig

def PCA(data : list, dimensions : int) -> tuple:
    # Returns the data set given to the number of dimensions desired
    # by employing Principal Component Analysis.

    # Standardize data
    standardizedData = standardize(data)

    # Calculate covariance matrix
    covMatrix = covarianceMatrix(standardizedData)

    # Calculate eigenvalues and eigenvectors and sort in descending order
    eigenvalues, eigenvectors = eig(covMatrix)
    sortKey(eigenvalues, eigenvectors)

    # Create feature matrix and calculate final data set
    featureVector, percentages = createFeatureMatrix(eigenvalues, eigenvectors, dimensions)
    finalData = createFinalDataSet(featureVector, standardizedData)

    return finalData, percentages


def standardize(data : list) -> list:
    # Standardizes the matrix as z-scores respective to their 
    # columns (dimensions).

    rowNum = len(data)
    colNum = len(data[0])
    res = [[0 for i in range(colNum)] for j in range(rowNum)]

    for j in range(colNum):
        mean = findMean(data, j)
        std = findStdDev(data, j, mean)
        for i in range(rowNum):
            res[i][j] = (data[i][j] - mean)/std

    return res

def findMean(matrix : list, columnIdx : int) -> float:
    # Calculates the mean of a column in a matrix.

    total = 0
    for i in range(len(matrix)):
        total += matrix[i][columnIdx]
    
    return total/len(matrix)

def findStdDev(matrix : list, columnIdx : int, mean : float) -> float:
    # Calculates the standard deviation of a column in a matrix.

    total = 0
    for i in range(len(matrix)):
        total += ((matrix[i][columnIdx] - mean))**2
    sd = sqrt(total/len(matrix))

    return sd
    
def covarianceMatrix(Z : list) -> list:
    # Calculates the covariance of a matrix data set as Z^T x Z.

    Zt = tranpose(Z)
    return matrixMultiplication(Zt, Z)

def tranpose(matrix : list) -> list:
    # Returns the transpose of a matrix.

    rowNum = len(matrix)
    colNum = len(matrix[0])
    res = [[0 for i in range(rowNum)] for j in range(colNum)]

    for i in range(rowNum):
        for j in range(colNum):
            res[j][i] = matrix[i][j]

    return res

def matrixMultiplication(m1 : list, m2 : list) -> list:
    # Multiplies two matrices together assuming it is valid.
    #Cij = sum from k = 1 to k = n(column of m1), Aik * Bkj

    row = len(m1)
    col = len(m2[0])
    res = [[0 for i in range(col)] for j in range(row)]

    for i in range(row):
        for j in range(col):
            c = 0
            for k in range(len(m2)):
                c += m1[i][k]*m2[k][j]
            res[i][j] = c

    return res

def sortKey(arr : list, mat: list) -> None:
    # Sorts eigenvalues in descending order and reorders the columns
    # of eigenvectors respectively
    # Based on bubblesort

    n = len(arr)
  
    # Traverse through all array elements
    for i in range(n-1):

        # Last i elements are already in place
        for j in range(0, n-i-1):
            # Traverse the array from 0 to n-i-1
            # Swap if the element found is greater
            # Than the next element
            if arr[j] < arr[j + 1] :
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                for t in range(len(mat)):
                    mat[t][j], mat[t][j + 1] = mat[t][j + 1], mat[t][j]


def createFeatureMatrix(eigenvalues : list, eigenvectors : list, dimensions : int) -> tuple:
    # Combines selected eigenvectors.

    fm = [0]*len(eigenvectors)
    total = sum(eigenvalues)
    percentages = [0]*dimensions

    for i in range(len(eigenvectors)):
        rowVector = eigenvectors[i]
        fm[i] = rowVector[0:dimensions]

    for i in range(dimensions):
        percentages[i] = eigenvalues[i]/total * 100

    return fm, percentages

def createFinalDataSet(featureVector : list, dataSet : list) -> list:
    # Calculates final data set.

    return matrixMultiplication(dataSet, featureVector)