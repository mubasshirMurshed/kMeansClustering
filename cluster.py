from math import *
import random
from typing import List, Tuple
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
from PCA import PCA

random.seed(1)
WIDTH = 100
THETA_DENSITY = 20
PHI_DENSITY = 10
CLUSTER_DENSITY = 100

class ClusterGroup:
    
    def __init__(self, dimensions: int, length: int) -> None:
        self.clusterArray = []                          # Array of clusters
        self.dimensions = dimensions
        self.length = length
    
    def generate(self) -> None:
        # Wipe data
        self.clusterArray = []

        # Instantiate each cluster and append to array
        for i in range(0, self.length):
            radius = float((input(f"Radius for Cluster {i + 1}? ")))
            if self.dimensions == 2:
                cluster = Cluster2D(radius, CLUSTER_DENSITY, self.dimensions, i)
            elif self.dimensions == 3:
                cluster = Cluster3D(radius, CLUSTER_DENSITY, self.dimensions, i)
            else:
                cluster = Cluster(radius, CLUSTER_DENSITY, self.dimensions, i)
            
            # Generate data
            cluster.generate()
            self.clusterArray.append(cluster)
        return

    def print(self) -> None:
        for cluster in self.clusterArray:
            cluster.print()

    def readFromFile(self, filename: str) -> None:
        pass

    def writeToFile(self, filename: str) -> None:
        pass

    def draw(self) -> None:
        fig = plt.figure()
        
        if self.dimensions == 2:
            ax = fig.add_subplot(111)
        else:
            ax = fig.add_subplot(111, projection='3d')

            ax.set_zlabel('Z Label')
        
        ax.set_xlabel('X Label')
        ax.set_ylabel('Y Label')

        colours = ["b", "g", "r", "c", "m", "y", "k", "w"]
        
        previous = 0 # Keep track of where cluster begins for general dim cases
        for cluster in self.clusterArray:
            if self.dimensions == 2 or self.dimensions == 3:
                cluster.addToScatterplot(ax, colours)
            else:
                data, percentages = PCA(self.getAllData(), 3)
                cluster.addToScatterplot(ax, colours, data[previous: previous + cluster.length]) # Only provide the cluster's data itself
                previous += cluster.length # Update where the previous stop was

        if type(self) != KMeansClusterGroup and ALLOW_ENCLOSE:
            self.enclose(ax)
        return

    def enclose(self, ax) -> None:
        #enclosedAllow = input("Enclose? (y/n): ")
        for cluster in self.clusterArray:
            cluster.enclose(ax)
        return

    def getAllData(self) -> list:
        data = []
        for cluster in self.clusterArray:
            data += cluster.points

        return data


class KMeansClusterGroup(ClusterGroup):

    def __init__(self, dimensions: int, length: int) -> None:
        super().__init__(dimensions, length)

    def KMeansClustering(self, clusterGroup: ClusterGroup) -> None:
        # Get all points in data
        data = []
        for cluster in clusterGroup.clusterArray:
            data += cluster.points
        
        # Create n random points for n clusters
        clusterCentres = []
        for _ in range(self.length):
            coordinate = []
            for _ in range(self.dimensions):
                coordinate += [random.uniform(0, WIDTH)]
            clusterCentres += [tuple(coordinate)]

        # Initialise data
        previousAssignedCluster = [-1]*len(data)
        assignedCluster = [0]*len(data)
        counter = 0

        # Assign all data points to a particular centre based on closest distance
        for i in range(len(data)):
                assignedCluster[i] = self.findClosest(data[i], clusterCentres)

        while (previousAssignedCluster != assignedCluster or counter <= 100):
            
            # Keep record of current assigned points
            for i in range(len(data)):
                previousAssignedCluster[i] = assignedCluster[i]

            # Calculate new cluster centres
            for i in range(len(clusterCentres)):
                totals = [0]*self.dimensions
                count = 0
                for j in range(len(data)):
                    # Find sum of each dimension for cluster i
                    if assignedCluster[j] == i:
                        count += 1
                        coordinate = data[j]
                        for t in range(self.dimensions):
                            totals[t] += coordinate[t]
                    
                # Calculate average for cluster i
                if count != 0:
                    for j in range(len(totals)):
                        totals[j] = totals[j]/count
                    
                # Replace centre
                clusterCentres[i] = tuple(totals)

            # Assign all data points to a particular centre based on closest distance
            for i in range(len(data)):
                assignedCluster[i] = self.findClosest(data[i], clusterCentres)
            
            # Update counter
            counter += 1

        self.updateClusterArray(data, assignedCluster, clusterCentres)
        return

    def findClosest(self, coordinate: Tuple, centres: list) -> int:
        minimum = self.calculateDistanceSq(coordinate, centres[0])  #Keeps track of minimum square distance
        minIndex = 0
        for i in range(1, len(centres)):
            value = self.calculateDistanceSq(coordinate, centres[i])
            if value < minimum:
                minimum = value
                minIndex = i
        return minIndex

    def calculateDistanceSq(self, c1: Tuple, c2: Tuple) -> float:
        total = 0
        for i in range(self.dimensions):
            total += (c1[i] - c2[i])**2
        return total

    def updateClusterArray(self, data: list, assign: list, centres: list) -> None:
        for i in range(self.length):
            # Create clusters iteratively
            if self.dimensions == 2:
                cluster = Cluster2D(0, 0, self.dimensions, i)
            elif self.dimensions == 3:
                cluster = Cluster3D(0, 0, self.dimensions, i)
            else:
                cluster = Cluster(0, 0, self.dimensions, i)
          
            # Push in relevant points
            for j in range(len(data)):
                if assign[j] == i:
                    cluster.points += [data[j]]
            
            # Update radius, length, centre
            cluster.radius = None
            cluster.length = len(cluster.points)
            cluster.centre = centres[i]

            # Push cluster in array
            self.clusterArray.append(cluster)

        return


class Cluster:
    
    def __init__(self, radius: float, length: int, dimension: int, number: int) -> None:
        self.radius = radius
        self.length = length
        self.points = []            # Array of tuples
        self.dimension = dimension
        self.number = number

        #Initialise centre
        self.centre = []
        for _ in range(self.dimension):
            self.centre.append(random.uniform(self.radius, WIDTH - self.radius))
        self.centre = tuple(self.centre)

    def generate(self) -> None:
        # Will generate cuboid clusters where radius is side length
        self.points = []
        for _ in range(self.length):
            coordinate = []
            for i in range(self.dimension):
                coordinate += [self.centre[i] + random.uniform(-1*self.radius, self.radius)]
            coordinate = tuple(coordinate)
            self.points.append(coordinate)

    def print(self) -> None:
        output = ""
        for coordinate in self.points:
            for i in range(self.dimension):
                output += str(coordinate[i])
                output += "\t"
            output += f"{self.number}\n"
        print(output)

    def read(self, filename: str) -> None:
        pass

    def write(self, filename: str) -> None:
        pass

    def addToScatterplot(self, ax, colours, data) -> None:
        for coordinate in data:
            c = colours[self.number]
            a = coordinate[0]
            b = coordinate[1]
            d = coordinate[2]
            ax.scatter(coordinate[0], coordinate[1], coordinate[2], marker = '+', color = c, s = 5)


class Cluster2D(Cluster):
    
    def __init__(self, radius: float, length: int, dimension: int, number: int) -> None:
        super().__init__(radius, length, dimension, number)

    def generate(self) -> None:
        self.points = []
        for _ in range(self.length):
            r = self.radius * sqrt(random.random())
            theta = random.uniform(0, 2*pi)
            x = self.centre[0] + r*cos(theta)
            y = self.centre[1] + r*sin(theta)
            coordinate = (x, y)
            self.points.append(coordinate)

    def addToScatterplot(self, ax, colours) -> None:
        for coordinate in self.points:
            c = colours[self.number]
            ax.scatter(coordinate[0], coordinate[1], marker = '+', color = c, s = 5)

    def enclose(self, ax) -> None:
        r = self.radius
        for angle in range(THETA_DENSITY):
            angle = (2*pi*angle)/THETA_DENSITY
            ax.scatter(self.centre[0] + r*cos(angle), self.centre[1] + r*sin(angle), marker = '+', color = "k", s = 1)


class Cluster3D(Cluster):
    
    def __init__(self, radius: float, length: int, dimension: int, number: int) -> None:
        super().__init__(radius, length, dimension, number)

    def generate(self) -> None:
        self.points = []
        for _ in range(self.length):
            r = self.radius * sqrt(random.random())
            theta = random.uniform(0, 2*pi)
            phi = random.uniform(0, pi)
            x = self.centre[0] + r*sin(phi)*cos(theta)
            y = self.centre[1] + r*sin(phi)*sin(theta)
            z = self.centre[2] + r*cos(phi)
            coordinate = (x, y, z)
            self.points.append(coordinate)

    def addToScatterplot(self, ax, colours) -> None:
        for coordinate in self.points:
            c = colours[self.number]
            ax.scatter(coordinate[0], coordinate[1], coordinate[2], marker = '+', color = c, s = 5)

    def enclose(self, ax) -> None:
        r = self.radius
        for theta in range(THETA_DENSITY):
            theta = (2*pi*theta)/THETA_DENSITY
            for phi in range(PHI_DENSITY):
                phi = (pi*phi)/PHI_DENSITY
                ax.scatter(self.centre[0] + r*sin(phi)*cos(theta), self.centre[1] + r*sin(phi)*sin(theta), self.centre[2] + r*cos(phi), marker = '+', color = "k", s = 1)


dim = 2
clustersGenerated = 8
clustersWanted = 4
ALLOW_ENCLOSE = False

c = ClusterGroup(dim, clustersGenerated)
c.generate()
c.draw()

k = KMeansClusterGroup(dim, clustersWanted)
k.KMeansClustering(c)
k.draw()

plt.show()

# data = c.getAllData()
# final, percentages = PCA(data, 2)
# print(final)

