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
			Graph G;
			PrintStream p;
			boolean[] propertiesA = {true, true, false, false};
			boolean[] propertiesB = {true, true, true, false};
			boolean[] propertiesC = {true, true, true, true};
			
			// max cluster
			p = new PrintStream("max_A.txt");
			System.setOut(p);
			G = new Graph();
			parseGraphInput("./richdata.txt", G, propertiesA);
			G.maxCluster(6000);

			p = new PrintStream("max_B.txt");
			System.setOut(p);
			G = new Graph();
			parseGraphInput("./richdata.txt", G, propertiesB);
			G.maxCluster(6000);

			p = new PrintStream("max_C.txt");
			System.setOut(p);
			G = new Graph();
			parseGraphInput("./richdata.txt", G, propertiesC);
			G.maxCluster(6000);
			// min cluster
			p = new PrintStream("min_A.txt");
			System.setOut(p);
			G = new Graph();
			parseGraphInput("./richdata.txt", G, propertiesA);
			G.minCluster(6000);

			p = new PrintStream("min_B.txt");
			System.setOut(p);
			G = new Graph();
			parseGraphInput("./richdata.txt", G, propertiesB);
			G.minCluster(6000);

			p = new PrintStream("min_C.txt");
			System.setOut(p);
			G = new Graph();
			parseGraphInput("./richdata.txt", G, propertiesC);
			G.minCluster(6000);
			
			/*
			for(int i = 5000; i <= 6000; i += 1000)
			{
				p = new PrintStream("simplemin_" + i + ".txt");
				System.setOut(p);
				G = new Graph();
				parseGraphInput("./simpledata.txt", G);
				G.minCluster(i);
			}
			*/
			/* rich data loop
			for(int i = 3000; i <= 6000; i += 1000)
			{ 
			int i = 6000;
				p = new PrintStream("test" + i + ".txt");
				System.setOut(p);
				G = new Graph();
				parseGraphInput("./simpledata.txt", G);
				G.minCluster(i);
			/*
			}
			for(int i = 1000; i <= 6000; i += 1000)
			{
				p = new PrintStream("middle_" + i + ".txt");
				System.setOut(p);
				G = new Graph();
				parseGraphInput("./richdata.txt", G);
				G.maxCluster(i);
			}
			*/
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void parseGraphInput(String filename, Graph G, boolean[] propertiesToUse)
	{
		generateVertices(filename, G, propertiesToUse);
		computeEdges(filename, G, propertiesToUse);
		System.out.println("vertices: " + G.n + " edges: " + G.m);
	}

	public static void generateVertices(String filename, Graph G, boolean[] propertiesToUse)
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
			{
				sections = line.split("\\*");
				artists = sections[1].split(",");
				genres = sections[2].split(",");
				if(propertiesToUse[0]) addVertices(artists, VertexType.ARTIST, vertices, hash, G);
				if(propertiesToUse[1]) addVertices(genres, VertexType.GENRE, vertices, hash, G);
				if(sections.length > 3)
				{
					releasedates = sections[3].split(",");
					String[] a = getYearsFromStrings(releasedates);
					if(propertiesToUse[2]) addVertices(a, VertexType.RELEASEDATE, vertices, hash, G);
					if(sections.length > 4)
					{
						awards = sections[4].split(",");
						if(propertiesToUse[3]) addVertices(awards, VertexType.AWARD, vertices, hash, G);
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
	
	public static void addVertices(String[] names, VertexType t, ArrayList<Vertex> vertices, Hashtable<String, Integer> hash, Graph G)
	{
		for(int i = 0; i < names.length; i++)
		{
			if (!hash.containsKey(names[i]) && names[i].length() != 0)
			{
				Vertex v = new Vertex(names[i], t);
				if(t == VertexType.ARTIST) G.artists++;
				else if(t == VertexType.GENRE) G.genres++;
				vertices.add(v);
				hash.put(names[i], 1);
			}
		}
	}

	public static void computeEdges(String filename, Graph G, boolean[] propertiesToUse)
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
			String[] sections,artists,genres,releasedates,awards;
			while ((line = reader.readLine()) != null) 
			{
				sections = line.split("\\*");
				artists = sections[1].split(",");
				genres = sections[2].split(",");
				if(propertiesToUse[0]) addEdges(artists, artists, G);
				if(propertiesToUse[1]) addEdges(artists, genres, G);
				if(sections.length > 3)
				{
					releasedates = sections[3].split(",");
					String[] a = getYearsFromStrings(releasedates);
					if(propertiesToUse[2]) addEdges(a, artists, G);
					if(sections.length > 4)
					{
						awards = sections[4].split(",");
						if(propertiesToUse[3]) addEdges(awards, artists, G);
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
	
	public static void addEdges(String[] a, String[] b, Graph G)
	{
		for(int i = 0; i < a.length; i++)
		{
			for(int j = 0; j < b.length; j++)
			{
				if(a[i].length() != 0 && b[j].length() != 0)
				{
					int x = G.indexOf(a[i]);
					int y = G.indexOf(b[j]);
					G.VPM[x][y] += 1;
					G.m++;
				}
			}
		}
	}
	
	public static String[] getYearsFromStrings(String[] releasedates)
	{
		String s = "(\\d{4})";
		Pattern p = Pattern.compile(s);
		Matcher m;
		ArrayList<String> dates = new ArrayList<String>();
		for(int i = 0; i < releasedates.length; i++)
		{
			m = p.matcher(releasedates[i]);
			if(m.find())
			{
				dates.add(m.group(0));
			}
		}
		String[] a = dates.toArray(new String[dates.size()]);
		return a;
	}
}





