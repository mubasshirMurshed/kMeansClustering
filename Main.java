import java.util.Random;
import org.leores.plot.JGnuplot;
import org.leores.plot.JGnuplot.Plot;
import org.leores.util.U;
import org.leores.util.able.Processable2;
import org.leores.util.data.DataTable;
import org.leores.util.data.DataTableSet;

public class Main
{
    static boolean ALLOW_ENCLOSE = true;
    static int WIDTH = 100;
    static int THETA_DENSITY = 20;
    static int PHI_DENSITY = 10;
    static int CLUSTER_DENSITY = 50;
    static Random generator = new Random(0);
    public static void main(String[] args)
    {
        int dimension = 2;
        int clustersGenerated = 4;
        int clustersWanted = 3;
        
        System.out.println("General Clustering\n");

        // Thread 1: Generate Clusters
        ClusterGroup c = new ClusterGroup(dimension, clustersGenerated);
        c.generate();
        //c.readFromFile("generalCluster.txt");
        //c.draw();
        c.print();
        //c.writeToFile("generalCluster.txt");

        System.out.println("K-Means Clustering\n");

        // Thread 2: K Mean Clustering
        KMeansClusterGroup k = new KMeansClusterGroup(dimension, clustersWanted);
        k.KMeansClustering(c);
        //k.draw()
        k.print();
        //k.writeToFile("kMeansCluster.txt");

        JGnuplot jg1 = new JGnuplot();
		Plot plot1 = new Plot("") {
			{
				xlabel = "x";
				ylabel = "y";
			}
		};

        DataTableSet dts1;
        dts1 = plot1.addNewDataTableSet("2D Plot");
        for (int t = 0; t < c.clusterArray.length; t++)
        {
            Cluster cluster = c.clusterArray[t];
            double[] x = new double[cluster.length];
            double[] y = new double[cluster.length];
            for (int i = 0; i < cluster.length; i++)
            {
                x[i] = cluster.points[i][0];
                y[i] = cluster.points[i][1];
            }
            
            dts1.addNewDataTable("C" + (t + 1), x, y);
        }   
		
        jg1.execute(plot1, jg1.plot2d);

        JGnuplot jg2 = new JGnuplot();
		Plot plot2 = new Plot("") {
			{
				xlabel = "x";
				ylabel = "y";
			}
		};
        DataTableSet dts2;
        dts2 = plot2.addNewDataTableSet("2D Plot");
        for (int t = 0; t < k.clusterArray.length; t++)
        {
            Cluster cluster = k.clusterArray[t];
            double[] x = new double[cluster.length];
            double[] y = new double[cluster.length];
            for (int i = 0; i < cluster.length; i++)
            {
                x[i] = cluster.points[i][0];
                y[i] = cluster.points[i][1];
            }
            
            dts2.addNewDataTable("C" + (t + 1), x, y);
        }   
		
        jg2.execute(plot2, jg2.plot2d);
    }
}
