import java.awt.Point;


public class Graph 
{
	public Vertex[] vertices;
	public int[] clusterIndices;
	public int[][] vertexProximityMatrix;
	public int[][] clusterProximityMatrix;
	public int m;
	public int n;
	
	public Graph()
	{
		m = n = 0;
	}
	
	public int indexOf(String name)
	{
		for(int i = 0; i < vertices.length; i++)
		{
			if (vertices[i].name.equals(name)) return i;
		}
		return -1;
	}
	
	public void printMatrix(int[][] matrix)
	{
		System.out.println("Printing Matrix");
		System.out.println("matrix length: " + matrix.length);
		System.out.println("matrix[0].length: " + matrix[0].length);
		for(int i = 0; i < matrix.length; i++)
		{
			for(int j = 0; j < matrix[0].length; j++)
			{
				System.out.format("%02d ", matrix[i][j]);
			}
			System.out.println();
		}
	}
	
	public void NewmanCluster(int finalNum)
	{
		clusterIndices = new int[vertices.length];
		for(int i = 0; i < vertices.length; i++) clusterIndices[i] = i;
		
		for(int j = 0; j < vertices.length - finalNum; j++)
		{
			Point p = findMax(vertexProximityMatrix);
			if (p.x == -1 || p.y == -1) break;
			System.out.println("merging " + p.x + " and " + p.y);
			mergeClusters(p.x, p.y);
			//printMatrix(vertexProximityMatrix);
		}
		System.out.println("Cluster Indices: ");
		for(int i = 0; i < clusterIndices.length; i++)
		{
			System.out.print(clusterIndices[i] + " ");
		}
	}
	
	public Point findMax(int[][] array)
	{
		int x, y, max;
		x = y = max = -1;
		
		for(int i = 0; i < array.length; i++)
		{
			for(int j = 0; j < array[0].length; j++)
			{
				if(i != j)
				{
					if (max < array[i][j])
					{
						max = array[i][j];
						x = i;
						y = j;
					}
				}
			}
		}
		if (max == 0) return new Point(-1,-1);
		return new Point(x, y);
	}
	
	public void mergeClusters(int index1, int index2)
	{
		int smallest = Math.min(index1, index2);
		int largest = Math.max(index1, index2);
		int newClusterIndex = clusterIndices[smallest];
		int oldClusterIndex = clusterIndices[largest];
		// update cluster array and the proximity matrix
		for(int i = 0; i < clusterIndices.length; i++)
		{
			if (clusterIndices[i] == oldClusterIndex)
			{
				clusterIndices[i] = newClusterIndex;
				for(int j = 0; j < vertexProximityMatrix[i].length; j++)
				{
					// take the maximum between the columns
					vertexProximityMatrix[newClusterIndex][j] = vertexProximityMatrix[j][newClusterIndex] = Math.max(vertexProximityMatrix[newClusterIndex][j], vertexProximityMatrix[i][j]);
				}
				for(int j = 0; j < vertexProximityMatrix[i].length; j++)
				{
					vertexProximityMatrix[i][j] = vertexProximityMatrix[j][i] = -1;
				}
			}
			
		}
	}
}
