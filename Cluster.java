import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

class Cluster
{
    // Attribute Field
    double radius;
    int length;
    double points[][];
    int dimension;
    int number;
    double centre[];

    // Constructor
    public Cluster(double radius, int length, int dimension, int number)
    {
        this.radius = radius;
        this.length = length;
        this.dimension = dimension;
        this.number = number;
        this.points = new double[this.length][this.dimension];
        
        this.centre = new double[dimension];
        double randomNum;
        for (int i = 0; i < this.dimension; i++)
        {
            randomNum = this.radius + Main.generator.nextDouble() * (Main.WIDTH - 2*this.radius);
            this.centre[i] = randomNum;
        }
    }

    // Methods
    public void generate()
    {
        double randomNum;
        for (int i = 0; i < this.length; i++)
        {
            for (int j = 0; j < this.dimension; j++)
            {
                randomNum = -1*this.radius + Main.generator.nextDouble() * (2*this.radius);
                this.points[i][j] = this.centre[j] + randomNum;
            }
        }
    }

    public void print()
    {
        String output = "";
        for (int i = 0; i < this.length; i++)
        {
            for (int j = 0; j < this.dimension; j++)
            {
                output += this.points[i][j];
                output += "\t";
            }
            output += this.number + "\n";
        }
        System.out.println(output);
    }

    public void read(Scanner myReader, int num)
    {
        ArrayList<double[]> data = new ArrayList<double[]>();
        while (myReader.hasNextLine())
        {
            String line = myReader.nextLine();
            double[] coordinate = new double[this.dimension];
            String[] result = line.split("\t"); // result = array of strings (each string is double, last is int)
            if (num == Integer.valueOf(result[result.length - 1]))
            {
                for (int i = 0; i < this.dimension; i++)
                {
                    coordinate[i] = Double.valueOf(result[i]);
                }
                data.add(coordinate);
            }
        }
        
        // Convert ArrayList to defined space
        this.length = data.size();
        this.points = new double[this.length][this.dimension];

        for (int i = 0; i < this.length; i++)
        {
            for (int j = 0; j < this.dimension; j++)
            {
                this.points[i][j] = data.get(i)[j];
            }
        }
        this.number = num;
    }

    public void write(FileWriter myWriter) throws IOException
    {
        String output = "";
        for (int i = 0; i < this.length; i++)
        {
            for (int j = 0; j < this.dimension; j++)
            {
                output += this.points[i][j];
                output += "\t";
            }
            output += this.number + "\n";
        }
        myWriter.write(output);
    }

    public void addToScatterplot()
    {

    }
}


class Cluster2D extends Cluster
{
    // Constructor
    public Cluster2D(double radius, int length, int dimension, int number)
    {
        super(radius, length, dimension, number);
    }

    // Methods
    public void generate()
    {
        double r;
        double theta;
        double x;
        double y;
        for (int i = 0; i < this.length; i++)
        {
            r = this.radius * Math.sqrt(Main.generator.nextDouble());
            theta = 2 * Math.PI * Main.generator.nextDouble();
            x = this.centre[0] + r*Math.cos(theta);
            y = this.centre[1] + r*Math.sin(theta);
            this.points[i][0] = x;
            this.points[i][1] = y;
        }
    }

    public void addToScatterplot()
    {

    }

    public void enclose()
    {

    }
}

class Cluster3D extends Cluster
{
    // Constructor
    public Cluster3D(double radius, int length, int dimension, int number)
    {
        super(radius, length, dimension, number);
    }

    // Methods
    public void generate()
    {
        double r;
        double theta;
        double phi;
        double x;
        double y;
        double z;
        for (int i = 0; i < this.length; i++)
        {
            r = this.radius * Math.sqrt(Main.generator.nextDouble());
            theta = 2 * Math.PI * Main.generator.nextDouble();
            phi = Math.PI * Main.generator.nextDouble();
            x = this.centre[0] + r*Math.sin(phi)*Math.cos(theta);
            y = this.centre[1] + r*Math.sin(phi)*Math.sin(theta);
            z = this.centre[2] + r*Math.cos(phi);
            this.points[i][0] = x;
            this.points[i][1] = y;
            this.points[i][2] = z;
        }
    }

    public void addToScatterplot()
    {

    }

    public void enclose()
    {
        
    }
}