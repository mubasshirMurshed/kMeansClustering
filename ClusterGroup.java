import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;

class ClusterGroup
{
    // Attribute Field
    Cluster clusterArray[];
    int dimensions;
    int length;

    // Constructor
    public ClusterGroup(int dimensions, int length)
    {
        this.dimensions = dimensions;
        this.length = length;
        this.clusterArray = new Cluster[length];
    }

    // Method
    public void generate()
    {
        Scanner s = new Scanner(System.in);
        double radius;
        Cluster cluster;
        for (int i = 0; i < this.length; i++)
        {
            System.out.println("Radius for Cluster " + (i + 1) + "? ");
            radius = s.nextDouble(); // User input
            if (this.dimensions == 2)
            {
                cluster = new Cluster2D(radius, Main.CLUSTER_DENSITY, this.dimensions, i);
            }
            else if (this.dimensions == 3)
            {
                cluster = new Cluster3D(radius, Main.CLUSTER_DENSITY, this.dimensions, i);
            }
            else
            {
                cluster = new Cluster(radius, Main.CLUSTER_DENSITY, this.dimensions, i);
            }

            // Generate data
            cluster.generate();
            this.clusterArray[i] = cluster;
        }
        s.close();
    }

    public void print()
    {
        for (Cluster cluster : clusterArray)
        {
            cluster.print();
        }
    }

    public void readFromFile(String filename)
    {
        try
        {
            File myFile = new File(filename);
            Cluster cluster;
            for (int i = 0; i < this.length; i++)
            {
                Scanner myReader = new Scanner(myFile);
                if (this.dimensions == 2)
                {
                    cluster = new Cluster2D(0, 0, this.dimensions, i);
                }
                else if (this.dimensions == 3)
                {
                    cluster = new Cluster3D(0, 0, this.dimensions, i);
                }
                else
                {
                    cluster = new Cluster(0, 0, this.dimensions, i);
                }

                cluster.read(myReader, i);
                clusterArray[i] = cluster;
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("An error occured. Could not read from file.");
        }
    }

    public void writeToFile(String filename)
    {
        try
        {
            FileWriter myWriter = new FileWriter(filename);
            
            // Write each cluster
            for (Cluster cluster : clusterArray)
            {
                cluster.write(myWriter);
            }

            myWriter.close();
        }
        catch (IOException e)
        {
            System.out.println("An error occured. Could not write to file.");
        }
    }

    public void draw()
    {

    }

    public void enclose()
    {

    }
}

class KMeansClusterGroup extends ClusterGroup
{
    public KMeansClusterGroup(int dimensions, int length)
    {
        super(dimensions, length);
    }

    public void KMeansClustering(ClusterGroup clusterGroup)
    {
        // Get total length
        int lengthTotal = 0;
        for (Cluster cluster : clusterGroup.clusterArray)
        {
            lengthTotal += cluster.length;
        }

        // Get all points in data
        double data[][] = new double[lengthTotal][this.dimensions];
        int idx = 0;
        for (Cluster cluster : clusterGroup.clusterArray)
        {
            for (int i = 0; i < cluster.length; i++)
            {
                for (int j = 0; j < this.dimensions; j++)
                {
                    data[idx][j] = cluster.points[i][j];
                }
                idx++;  // Next point
            }
        }

        // Create n random points for n clusters
        double clusterCentres[][] = new double[this.length][this.dimensions];
        for (int i = 0; i < this.length; i++)
        {
            for (int j = 0; j < this.dimensions; j++)
            {
                clusterCentres[i][j] = Main.generator.nextDouble() * Main.WIDTH;
            }
        }

        // Initialise data
        int[] previousAssignedCluster = new int[lengthTotal];
        int[] currentAssignedCluster = new int[lengthTotal];
        int count = 0;

        // Assign data points to each cluster
        for (int i = 0; i < lengthTotal; i++)
        {
            currentAssignedCluster[i] = Main.generator.nextInt(this.length);
        }

        // Looped algorithm of finding closest, averaging for centre and repeat
        while (!Arrays.equals(previousAssignedCluster, currentAssignedCluster) || count <= 100)
        {
            // Keep record of current assigned points
            for (int i = 0; i < lengthTotal; i++)
            {
                previousAssignedCluster[i] = currentAssignedCluster[i];
            }

            // Calculate new cluster centres
            int num;
            for (int i = 0; i < this.length; i++)
            {
                double[] totals = new double[this.dimensions];
                num = 0;
                for (int j = 0; j < lengthTotal; j++)
                {
                    // Find sum of each dimension for cluster i
                    if (currentAssignedCluster[j] == i)
                    {
                        num++;
                        for (int k = 0; k < this.dimensions; k++)
                        {
                            totals[k] += data[j][k];
                        }
                    }
                }

                // Calculate average for cluster i
                if (num != 0)
                {
                    for (int j = 0; j < this.dimensions; j++)
                    {
                        totals[j] = totals[j]/num;
                    }
                }

                // Replace centre
                clusterCentres[i] = totals;
            }

            // Assign all data points to a particular centre based on closest distance
            for (int i = 0; i < lengthTotal; i++)
            {
                currentAssignedCluster[i] = this.findClosest(data[i], clusterCentres);
            }

            // Update counter
            count++;
        }
        this.updateClusterArray(data, currentAssignedCluster, clusterCentres);
    }

    public int findClosest(double coordinate[], double centres[][])
    {
        double min = this.calculateDistanceSq(coordinate, centres[0]);
        int minIdx = 0;
        for (int i = 1; i < centres.length; i++)
        {
            double value = this.calculateDistanceSq(coordinate, centres[i]);
            if (value < min)
            {
                min = value;
                minIdx = i;
            }
        }
        return minIdx;
    }

    public double calculateDistanceSq(double c1[], double c2[])
    {
        double total = 0;
        for (int i = 0; i < this.dimensions; i++)
        {
            total += Math.pow((c1[i] - c2[i]), 2);
        }
        return total;
    }

    public void updateClusterArray(double data[][], int assign[], double centres[][])
    {
        this.clusterArray = new Cluster[this.length];
        for (int i = 0; i < this.length; i++)
        {
            // Create clusters iteratively
            Cluster cluster;
            if (this.dimensions == 2)
            {
                cluster = new Cluster2D(0, 0, this.dimensions, i);
            }
            else if (this.dimensions == 3)
            {
                cluster = new Cluster3D(0, 0, this.dimensions, i);
            }
            else
            {
                cluster = new Cluster(0, 0, this.dimensions, i);
            }

            // Push in relevant points
            cluster.points = new double[data.length][this.dimensions];
            int idx = 0;
            for (int j = 0; j < data.length; j++)
            {
                if (assign[j] == i)
                {
                    cluster.points[idx] = data[j];
                    idx++;
                }
            }

            // Update length, centre
            cluster.length = idx;
            cluster.centre = centres[i];

            // Add cluster in array
            this.clusterArray[i] = cluster;
        }
    }
}