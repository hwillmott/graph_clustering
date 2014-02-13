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
			PrintStream p = new PrintStream("output2.txt");
			System.setOut(p);
			Graph G = new Graph();
			parseGraphInput("./newrawdata.txt", G, true);
			G.maxCluster(1000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void parseGraphInput(String filename, Graph G, boolean moreProperties)
	{
		generateVertices(filename, G, moreProperties);
		computeEdges(filename, G, moreProperties);
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
			{
				sections = line.split("\\*");
				artists = sections[1].split(",");
				genres = sections[2].split(",");
				addVertices(artists, VertexType.ARTIST, vertices, hash);
				addVertices(genres, VertexType.GENRE, vertices, hash);
				if(moreProperties)
				{
					if(sections.length >= 4) 
					{
						releasedates = sections[3].split(",");
						String[] a = getYearsFromStrings(releasedates);
						addVertices(a, VertexType.RELEASEDATE, vertices, hash);
						if(sections.length >= 5) 
						{
							awards = sections[4].split(",");
							addVertices(awards, VertexType.AWARD, vertices, hash);
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
	
	public static void addVertices(String[] names, VertexType t, ArrayList<Vertex> vertices, Hashtable<String, Integer> hash)
	{
		for(int i = 0; i < names.length; i++)
		{
			if (!hash.containsKey(names[i]) && names[i].length() != 0)
			{
				Vertex v = new Vertex(names[i], t);
				vertices.add(v);
				hash.put(names[i], 1);
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
			String[] sections,artists,genres,releasedates,awards;
			while ((line = reader.readLine()) != null) 
			{
				sections = line.split("\\*");
				artists = sections[1].split(",");
				genres = sections[2].split(",");
				addEdges(artists, artists, G);
				addEdges(artists, genres, G);
				addEdges(genres, genres, G);
				if(moreProperties)
				{
					if(sections.length >= 4) 
					{
						releasedates = sections[3].split(",");
						String[] a = getYearsFromStrings(releasedates);
						addEdges(a, artists, G);
						if(sections.length >= 5) 
						{
							awards = sections[4].split(",");
							addEdges(awards, artists, G);
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





