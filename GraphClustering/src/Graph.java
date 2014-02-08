import java.util.ArrayList;


public class Graph 
{
	public ArrayList<Vertex> vertices;
	public int[][] vertexProximityMatrix;
	public int[][] clusterProximityMatrix;
	
	public Graph()
	{
		vertices = new ArrayList<Vertex>();
	}
	
	public int indexOf(String name)
	{
		for(int i = 0; i < vertices.size(); i++)
		{
			if (vertices.get(i).name.equals(name)) return i;
		}
		return -1;
	}
	
	public void printMatrix(int[][] matrix)
	{
		for(int i = 0; i < matrix.length; i++)
		{
			for(int j = 0; j < matrix[0].length; j++)
			{
				System.out.format("%03d ", matrix[i][j]);
			}
			System.out.println();
		}
	}
}
