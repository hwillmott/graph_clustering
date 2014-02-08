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
}
