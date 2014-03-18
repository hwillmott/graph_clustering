import java.awt.Point;
import java.util.Arrays;

public class Graph 
{
	public Vertex[] vertices;
	public int[] clusterIndices;
	public int[] clusterSizes;
	public int[][] VPM; //vertex proximity matrix
	public int m;
	public int n;
	public int genres;
	public int artists;
	
	public Graph()
	{
		m = n = genres = artists = 0;
	}
	
	
	/*************************************************************************
	 * indexOf
	 * 
	 * @param name
	 * @returns index of the vertex in vertices with the given name, or -1 
	 * if the vertex was not found
	 * 
	 *************************************************************************/
	public int indexOf(String name)
	{
		for(int i = 0; i < vertices.length; i++)
		{
			if (vertices[i].name.equals(name)) return i;
		}
		return -1;
	}
	
	/*************************************************************************
	 * printMatrix
	 * 
	 * @param matrix
	 * @returns void
	 * 
	 * prints a 2x2 int matrix
	 * 
	 *************************************************************************/
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
	
	/*************************************************************************
	 * maxCluster
	 * 
	 * @param iterations
	 * @returns void
	 * 
	 * For the number of iterations given, finds the maximum number in the 
	 * vertex proximity matrix, excluding the diagonal, and merges the 
	 * corresponding clusters. Will halt if and when there are no connections 
	 * left between the existing clusters.
	 * 
	 *************************************************************************/
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
		printClusters();
	}
	
	/*************************************************************************
	 * minCluster
	 * 
	 * @param iterations
	 * @returns void
	 * 
	 * For the number of iterations given, finds the minimum number greater 
	 * than 0 in the vertex proximity matrix, excluding the diagonal, and merges 
	 * the corresponding clusters. Will halt if and when there are no 
	 * connections left between the existing clusters. Prints the cluster data.
	 * 
	 *************************************************************************/
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
		printClusters();
	}
	
	/*************************************************************************
	 * idealCluster
	 * 
	 * @param iterations, size
	 * @returns void
	 * 
	 * For the number of iterations given, finds the two clusters with the most
	 * connections between them that, when added, will still be smaller than
	 * the specified size
	 * 
	 *************************************************************************/
	public void idealCluster(int iterations, int size)
	{
		clusterIndices = new int[vertices.length];
		for(int i = 0; i < vertices.length; i++) clusterIndices[i] = i;
		clusterSizes = new int[vertices.length];
		Arrays.fill(clusterSizes, 1);
		for(int j = 0; j < iterations; j++)
		{
			Point p = findIdeal(VPM, size);
			if (p == null) 
			{
				System.out.println("No more merges available after " + j + " merges");
				break;
			}
			mergeClusters(p.x, p.y);
		}
		printClusters();		
		
	}

	/*************************************************************************
	 * findMax
	 * 
	 * @param array
	 * @returns Point
	 * 
	 * Finds the largest int greater than 0 in the given array and returns
	 * the indices as a Point. Returns null if there are no ints greater than
	 * 0
	 * 
	 *************************************************************************/
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

	/*************************************************************************
	 * findMin
	 * 
	 * @param array
	 * @returns Point
	 * 
	 * Finds the smallest int greater than 0 in the given array and returns
	 * the indices as a Point. Returns null if there are no ints greater than
	 * 0
	 * 
	 *************************************************************************/
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
	
	/*************************************************************************
	 * findIdeal
	 * 
	 * @param array
	 * @returns Point
	 * 
	 * Finds the largest int greater than 0 in the vertex proximity matrix 
	 * where the two corresponding clusters, when added together, would not
	 * exceed the specified size
	 * 
	 *************************************************************************/
	private Point findIdeal(int[][] array, int size) 
	{
		int x, y, max;
		x = y = max = -1;
		
		for(int i = 0; i < array.length; i++)
		{
			for(int j = 0; j < array[0].length; j++)
			{
				if(i != j && clusterSizes[i] + clusterSizes[j] <= size)
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
	
	/*************************************************************************
	 * mergeClusters
	 * 
	 * @param index1, index2
	 * @returns void
	 * 
	 * Finds the smallest int greater than 0 in the given array and returns
	 * the indices as a Point. Returns null if there are no ints greater than
	 * 0
	 * 
	 *************************************************************************/
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
		clusterSizes[smallest] += clusterSizes[largest];
		clusterSizes[largest] = 0;
	}
	
	/*************************************************************************
	 * printClusterStats
	 * 
	 * @param clusterIndex
	 * @returns void
	 * 
	 * Iterates through the clusterIndices matrix and counts the number of 
	 * vertices, the number of artists, and the number of genres with the 
	 * given cluster index. Prints the cluster index and those numbers at the 
	 * end. This is useful for inserting the data into a spreadsheet to 
	 * analyze the clusters
	 * 
	 *************************************************************************/
	public void printClusterStats(int clusterIndex)
	{
		int numGenres = 0;
		int numArtists = 0;
		int size = 0;
		for(int i = 0; i < clusterIndices.length; i++)
		{
			if(clusterIndices[i] == clusterIndex)
			{
				if(vertices[i].type == VertexType.GENRE) numGenres++;
				if(vertices[i].type == VertexType.ARTIST) numArtists++;
				size++;
			}
		}
		System.out.println(clusterIndex + " " + size + " " + numGenres + " " + numArtists);
	}
	
	/*************************************************************************
	 * printCluster
	 * 
	 * @param clusterIndex
	 * @returns void
	 * 
	 * Iterates through the clusterIndex array and prints every vertex with 
	 * the given cluster index and its type.
	 * 
	 *************************************************************************/
	public void printCluster(int clusterIndex)
	{
		System.out.println("Printing cluster: " + clusterIndex + "------------------XXXXX");
		for(int i = 0; i < clusterIndices.length; i++)
		{
			if(clusterIndices[i] == clusterIndex)
			{
				System.out.println(vertices[i].name + " " + vertices[i].type);
			}
		}
	}
	
	/*************************************************************************
	 * printClusters
	 * 
	 * @param 
	 * @returns void
	 * 
	 * Makes a copy of the clusterIndices array and sorts it, to group all the
	 * cluster indices (to make it easy to count how many vertices are in each
	 * cluster and print all the clusters in order). It then iterates through
	 * the copy (indices) and prints every cluster by either calling
	 * printClusterStats or printCluster (one is commented out), depending
	 * on what you want to analyze. Prints some statistics about the clusters
	 * at the end.
	 * 
	 *************************************************************************/
	public void printClusters()
	{
		int[] indices = new int[clusterIndices.length];
		System.arraycopy(clusterIndices, 0, indices, 0, clusterIndices.length); // make a temporary array of indices and sort
		Arrays.sort(indices);
		int currentIndex = indices[0];
		int currentIndexCount = 0;
		int numClusters = 0;
		int largestClusterSize = 0;
		for(int i = 0; i < indices.length; i++)
		{
			if (currentIndex != indices[i]) // cluster change
			{
				//printClusterStats(currentIndex);
				if(currentIndexCount >= 20 && currentIndexCount <= 100) printCluster(currentIndex);
				if(currentIndexCount > largestClusterSize) largestClusterSize = currentIndexCount;
				currentIndexCount = 1;
				currentIndex = indices[i];
				numClusters++;
			}
			else
			{
				currentIndexCount++;
			}
		}
		System.out.println("Number of clusters: " + numClusters);
		System.out.println("Largest cluster: " + largestClusterSize + " vertices");
		System.out.println("Number of artist vertices: " + artists);
		System.out.println("Number of genre vertices: " + genres);
	}
}
