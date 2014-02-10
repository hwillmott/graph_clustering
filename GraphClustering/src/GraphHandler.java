import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class GraphHandler 
{
	public static void main(String[] args)
	{
		try {
			PrintStream p = new PrintStream("output.txt");
			System.setOut(p);
			Graph G = new Graph();
			parseGraphInput("./album-artist-genre.txt", G);
			G.NewmanCluster(1000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void parseGraphInput(String filename, Graph G)
	{
		generateVertices(filename, G);
		computeEdges(filename, G);
		//G.printMatrix(G.vertexProximityMatrix);
		System.out.println("vertices: " + G.n + " edges: " + G.m);
	}

	public static void generateVertices(String filename, Graph G)
	{
		FileReader file = null;		  
		try {
			file = new FileReader(filename);
			BufferedReader reader = new BufferedReader(file);
			String line = "";
			String[] sections,artists,genres;
			ArrayList<Vertex> vertices = new ArrayList<Vertex>();
			Hashtable<String, Integer> hash = new Hashtable<String, Integer>();
			while ((line = reader.readLine()) != null)
			//for(int h = 0; h < 200; h++)
			{
				//line = reader.readLine();
				sections = line.split("\\*");
				artists = sections[1].split(",");
				genres = sections[2].split(",");
				for(int i = 0; i < artists.length; i++)
				{
					if (!hash.containsKey(artists[i]) && artists[i].length() != 0)
					{
						Vertex v = new Vertex(artists[i], VertexType.ARTIST);
						vertices.add(v);
						G.n++;
						hash.put(artists[i], 1);
						System.out.println("Adding artist: " + artists[i]);
					}
				}
				for(int j = 0; j < genres.length; j++)
				{
					if (!hash.containsKey(genres[j]) && genres[j].length() != 0)
					{
						Vertex v = new Vertex(genres[j], VertexType.GENRE);
						vertices.add(v);
						G.n++;
						hash.put(genres[j], 1);
						System.out.println("Adding genre: " + genres[j]);
					}
				}
			}
			G.vertices = vertices.toArray(new Vertex[vertices.size()]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void computeEdges(String filename, Graph G)
	{
		G.vertexProximityMatrix = new int[G.vertices.length][G.vertices.length];
		for(int i = 0; i < G.vertices.length; i++)
		{
			Arrays.fill(G.vertexProximityMatrix[i], 0);
		}
		FileReader file = null;		  
		try {
			file = new FileReader(filename);
			BufferedReader reader = new BufferedReader(file);
			String line = "";
			String[] sections,artists,genres;
			while ((line = reader.readLine()) != null) 
			//for(int h = 0; h < 200; h++)
			{
				//line = reader.readLine();
				sections = line.split("\\*");
				artists = sections[1].split(",");
				genres = sections[2].split(",");
				for(int i = 0; i < artists.length; i++)
				{
					for(int j = 0; j < genres.length; j++)
					{
						if(artists[i].length() != 0 && genres[j].length() != 0)
						{
							int a = G.indexOf(artists[i]);
							int g = G.indexOf(genres[j]);
							G.vertexProximityMatrix[a][g] += 1;
							G.m++;
						}
					}
				}
				for(int i = 0; i < artists.length; i++)
				{
					for(int j = 0; j < artists.length; j++)
					{
						if(artists[i].length() != 0 && artists[j].length() != 0)
						{
							int a = G.indexOf(artists[i]);
							int g = G.indexOf(artists[j]);
							G.vertexProximityMatrix[a][g] += 1;
							G.m++;
						}
					}
				}
				for(int i = 0; i < genres.length; i++)
				{
					for(int j = 0; j < genres.length; j++)
					{
						if(genres[i].length() != 0 && genres[j].length() != 0)
						{
							int a = G.indexOf(genres[i]);
							int g = G.indexOf(genres[j]);
							G.vertexProximityMatrix[a][g] += 1;
							G.m++;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
