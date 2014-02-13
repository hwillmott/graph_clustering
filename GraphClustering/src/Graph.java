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
			if (p == null) 
			{
				System.out.println("No more merges available after " + j + " merges");
				break;
			}
			System.out.println("merging " + p.x + " and " + p.y);
			mergeClusters(p.x, p.y);
			//printMatrix(vertexProximityMatrix);
		}
		System.out.println("Cluster Indices: ");
		for(int i = 0; i < clusterIndices.length; i++)
		{
			System.out.print(clusterIndices[i] + " ");
		}
		printClusters(20);
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
		System.out.println("Cluster " + clusterIndex + "------------------------------------------xxxxx");
		int numArtists = 0;
		int numGenres = 0;
		int size = 0;
		for(int i = 0; i < clusterIndices.length; i++)
		{
			if(clusterIndices[i] == clusterIndex)
			{
				System.out.println(vertices[i].name + " " + vertices[i].type);
				if(vertices[i].type == VertexType.ARTIST) numArtists++;
				else if(vertices[i].type == VertexType.GENRE) numGenres++;
				size++;
			}
		}
		System.out.println("Cluster size: " + size);
		System.out.println("Number of artists: " + numArtists);
		System.out.println("Number of genres: " + numGenres);
	}
	
	public void printClusters(int minClusterSize)
	{
		int[] indices = new int[clusterIndices.length];
		System.arraycopy(clusterIndices, 0, indices, 0, clusterIndices.length); // make a temporary array of indices and sort
		Arrays.sort(indices);
		int currentIndex = indices[0];
		int currentIndexCount = 0;
		int numClusters = 0;
		for(int i = 0; i < indices.length; i++)
		{
			if (currentIndex != indices[i]) // cluster change
			{
				if(currentIndexCount >= minClusterSize) // was the previous cluster big enough?
				{
					printClusterStats(currentIndex); // print the cluster
				}
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
	}
}
