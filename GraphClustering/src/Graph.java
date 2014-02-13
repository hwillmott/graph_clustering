import java.awt.Point;
import java.util.Arrays;


public class Graph 
{
	public Vertex[] vertices;
	public int[] clusterIndices;
	public int[][] VPM; //vertex proximity matrix
	public int[][] clusterProximityMatrix;
	public boolean[] deprecated;
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
	
	public void maxCluster(int finalNum)
	{
		clusterIndices = new int[vertices.length];
		for(int i = 0; i < vertices.length; i++) clusterIndices[i] = i;
		deprecated = new boolean[clusterIndices.length];
		Arrays.fill(deprecated, false);
		for(int j = 0; j < vertices.length - finalNum; j++)
		{
			Point p = findMax(VPM);
			if (p == null) break;
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
		if (max <= 0) return null;
		return new Point(x, y);
	}
	
	public void mergeClusters(int index1, int index2)
	{
		int smallest = Math.min(index1, index2);
		int largest = Math.max(index1, index2);
		int newClusterIndex = clusterIndices[smallest];
		int oldClusterIndex = clusterIndices[largest];
		/* update cluster array and the proximity matrix
		for(int i = 0; i < clusterIndices.length; i++)
		{
			if (clusterIndices[i] == oldClusterIndex)
			{
				clusterIndices[i] = newClusterIndex; // update cluster number
				if(i == largest) // if the column isn't already nullified
				{
					for(int j = 0; j < VPM[i].length; j++) //traverse column and add values to final cluster column
					{
						if((j != smallest) && (VPM[i][j] != -1))
						{
							VPM[smallest][j] += VPM[largest][j]; //add connections
						}
					}
				}
				for(int j = 0; j < VPM[i].length; j++)
				{
					// take the maximum between the columns
					if(VPM[newClusterIndex][j] != -1 && VPM[i][j] != -1)
					{
						VPM[newClusterIndex][j] = VPM[j][newClusterIndex] = VPM[newClusterIndex][j] + VPM[i][j];
					}
					else if(VPM[newClusterIndex][j] != -1 && VPM[i][j] == -1)
					VPM[newClusterIndex][j] = VPM[j][newClusterIndex] = Math.max(VPM[newClusterIndex][j], VPM[i][j]);
					
				}
				for(int j = 0; j < VPM[i].length; j++)
				{
					VPM[i][j] = VPM[j][i] = -1;
				}
			}
			
		} */
		for(int i = 0; i < clusterIndices.length; i++)
		{
			if (clusterIndices[i] == oldClusterIndex) clusterIndices[i] = newClusterIndex; // update cluster number
		}
		for(int j = 0; j < VPM[largest].length; j++)
		{
			if((j != smallest) && (VPM[largest][j] != -1))
			{
				VPM[smallest][j] += VPM[largest][j]; //add connections
			}
		}
		for(int j = 0; j < VPM[largest].length; j++) 
		{
			VPM[largest][j] = VPM[j][largest] = -1; // nullify column, row
		}
	}
	
	public void printClusterStats(int minClusterSize)
	{
		int[] indices = new int[clusterIndices.length];
		System.arraycopy(clusterIndices, 0, indices, 0, clusterIndices.length);
		Arrays.sort(indices);
		int currentIndex = indices[0];
		int currentIndexCount = 0;
		for(int i = 0; i < indices.length; i++)
		{
			if (currentIndex != indices[i])
			{
				if(currentIndexCount >= minClusterSize);
			}
		}
	}
}
