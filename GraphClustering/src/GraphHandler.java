import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphHandler 
{
	public static void main(String[] args)
	{
		try {
			PrintStream p = new PrintStream("output.txt");
			System.setOut(p);
			Graph G = new Graph();
			parseGraphInput("./album-artist-genre.txt", G, false);
			G.maxCluster(1000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void parseGraphInput(String filename, Graph G, boolean moreProperties)
	{
		generateVertices(filename, G, moreProperties);
		computeEdges(filename, G, moreProperties);
		//G.printMatrix(G.vertexProximityMatrix);
		System.out.println("vertices: " + G.n + " edges: " + G.m);
	}

	public static void generateVertices(String filename, Graph G, boolean moreProperties)
	{
		FileReader file = null;		  
		try {
			file = new FileReader(filename);
			BufferedReader reader = new BufferedReader(file);
			String line = "";
			String[] sections,artists,genres,releasedates,awards;
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
						hash.put(genres[j], 1);
						System.out.println("Adding genre: " + genres[j]);
					}
				}
				if(moreProperties)
				{
					releasedates = sections[3].split(",");
					awards = sections[4].split(",");
					String s = "*/d{4}*";
					Pattern p = Pattern.compile(s);
					Matcher m;
					for(int i = 0; i < releasedates.length; i++)
					{
						m = p.matcher(releasedates[i]);
						if(m.find())
						{
							if(!hash.containsKey(m.group(1)))
							{
								Vertex v = new Vertex(m.group(1), VertexType.RELEASEDATE);
								vertices.add(v);
								hash.put(m.group(1), 1);
								System.out.println("Adding release date: " + m.group(1));
							}
						}
					}
					for(int i = 0; i < awards.length; i++)
					{
						if(!hash.containsKey(awards[i]) && awards[i].length() != 0)
						{
							Vertex v = new Vertex(awards[i], VertexType.AWARD);
							vertices.add(v);
							hash.put(awards[i], 1);
							System.out.println("Adding award: "+ awards[i]);
						}
					}
				}
			}
			G.vertices = vertices.toArray(new Vertex[vertices.size()]);
			G.n = vertices.size();
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

	public static void computeEdges(String filename, Graph G, boolean moreProperties)
	{
		G.VPM = new int[G.vertices.length][G.vertices.length];
		for(int i = 0; i < G.vertices.length; i++)
		{
			Arrays.fill(G.VPM[i], 0);
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
							G.VPM[a][g] += 1;
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
							G.VPM[a][g] += 1;
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
							G.VPM[a][g] += 1;
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
