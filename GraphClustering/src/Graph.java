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
	public int genres;
	public int artists;
	
	public Graph()
	{
		m = n = genres = artists = 0;
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
	
	public void maxCluster(int iterations)
	{
		clusterIndices = new int[vertices.length];
		for(int i = 0; i < vertices.length; i++) clusterIndices[i] = i;
		for(int j = 0; j < iterations; j++)
		{
			Point p = findMax(VPM);
			if (p == null) 
			{
				System.out.println("No more merges available after " + j + " merges");
				break;
			}
			mergeClusters(p.x, p.y);
		}
		printClusters(20);
	}
	
	public void minCluster(int iterations)
	{
		clusterIndices = new int[vertices.length];
		for(int i = 0; i < vertices.length; i++) clusterIndices[i] = i;
		for(int j = 0; j < iterations; j++)
		{
			Point p = findMin(VPM);
			if (p == null) 
			{
				System.out.println("No more merges available after " + j + " merges");
				break;
			}
			mergeClusters(p.x, p.y);
		}
		printClusters(20);
	}
	
	private Point findMin(int[][] array) 
	{
		int x, y, min;
		x = y = min = Integer.MAX_VALUE;
		
		for(int i = 0; i < array.length; i++)
		{
			for(int j = 0; j < array[0].length; j++)
			{
				if(i != j)
				{
					if (min > array[i][j] && array[i][j] > 0)
					{
						min = array[i][j];
						x = i;
						y = j;
					}
				}
			}
		}
		if (min == Integer.MAX_VALUE) return null;
		return new Point(x, y);
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
	
	public void printClusterStats(int clusterIndex)
	{
		int numGenres = 0;
		int size = 0;
		for(int i = 0; i < clusterIndices.length; i++)
		{
			if(clusterIndices[i] == clusterIndex)
			{
				if(vertices[i].type == VertexType.GENRE) numGenres++;
				size++;
			}
		}
		System.out.println(clusterIndex + " " + size + " " + numGenres);
	}
	
	public void printClusters(int minClusterSize)
	{
		int[] indices = new int[clusterIndices.length];
		System.arraycopy(clusterIndices, 0, indices, 0, clusterIndices.length); // make a temporary array of indices and sort
		Arrays.sort(indices);
		int currentIndex = indices[0];
		int currentIndexCount = 0;
		int numClusters = 0;
		int numLargeClusters = 0;
		int largestClusterSize = 0;
		int numLoneVertices = 0;
		for(int i = 0; i < indices.length; i++)
		{
			if (currentIndex != indices[i]) // cluster change
			{
				if(currentIndexCount >= minClusterSize) // was the previous cluster big enough?
				{ // print the cluster
					numLargeClusters++;
				}
				printClusterStats(currentIndex);
				if(currentIndexCount > largestClusterSize) largestClusterSize = currentIndexCount;
				if(currentIndexCount == 1) numLoneVertices++;
				currentIndexCount = 0;
				currentIndex = indices[i];
				numClusters++;
			}
			else
			{
				currentIndexCount++;
			}
		}
		System.out.println("Number of clusters: " + numClusters);
		System.out.println("Number of clusters with over " + minClusterSize + " vertices: " + numLargeClusters);
		System.out.println("Largest cluster: " + largestClusterSize + " vertices");
		System.out.println("Number of unclustered vertices: " + numLoneVertices);
		System.out.println("Number of artist vertices: " + artists);
		System.out.println("Number of genre vertices: " + genres);
		
	}
}
