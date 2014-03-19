import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphHandler 
{
	/*************************************************************************
	 * main
	 * 
	 * @param args
	 * @returns void
	 * 
	 * How to use:
	 * This method must be altered for the purposes of your experiment.
	 * PropertiesA, PropertiesB, and PropertiesC specify which of the properties
	 * from the text file you wish to use.
	 * p is the printstream to the text file you wish to print to.
	 * You should use richdata.txt, which has all the properties available.
	 * The available clustering algorithms on G are:
	 * idealCluster(<number of iterations>,<max size of cluster>)
	 * maxCluster(<number of iterations>)
	 * minCluster(<number of iterations>)
	 * Note: depending on what you want the program to print to the file (the
	 * contents of the clusters or the statistics of the clusters, you will 
	 * have to modify printClusters() in Graph.java
	 * 
	 *************************************************************************/
	public static void main(String[] args)
	{
		try {
			Graph G;
			PrintStream p;
			boolean[] propertiesA = {true, true, false, false}; // artist and genre only
			boolean[] propertiesB = {true, true, true, false}; // artist, genre, release year
			boolean[] propertiesC = {true, true, true, true}; // artist, genre, release year, awards
			
			// example run
			p = new PrintStream("ideal_20.txt"); // the output file you wish to use
			System.setOut(p);
			G = new Graph();
			parseGraphInput("./richdata.txt", G, propertiesC); // choose which properties to use
			G.idealCluster(6000,20); //choose which clustering algorithm to use
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*************************************************************************
	 * parseGraphInput
	 * 
	 * @param filename, G, propertiesToUse
	 * @returns void
	 * 
	 * Parses the input file given by filename twice - the first time to 
	 * generate all the vertices, then to generate the edges between them.
	 * propertiesToUse specifies which of the properties in the file are
	 * to be used in generating vertices and edges
	 * 
	 *************************************************************************/
	public static void parseGraphInput(String filename, Graph G, boolean[] propertiesToUse)
	{
		generateVertices(filename, G, propertiesToUse);
		computeEdges(filename, G, propertiesToUse);
		System.out.println("vertices: " + G.n + " edges: " + G.m);
	}

	/*************************************************************************
	 * generateVertices
	 * 
	 * @param filename, G, propertiesToUse
	 * @returns void
	 * 
	 * Iterates through every line in the file, creating vertices out of the
	 * specified properties and checks for duplicates
	 * 
	 *************************************************************************/
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
	
	/*************************************************************************
	 * addVertices
	 * 
	 * @param names, t, vertices, hash, G
	 * @returns void
	 * 
	 * Creates vertices of type t from the array of names given, checking
	 * for duplicates as it goes. Also adds to graph's count of artists and
	 * genres
	 * 
	 *************************************************************************/
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

	/*************************************************************************
	 * computeEdges
	 * 
	 * @param filename, G, propertiesToUse
	 * @returns void
	 * 
	 * Iterates through every line in the file, creating edges between existing
	 * vertices according to the properties specified. Edges are added together
	 * if two vertices are found to be connected in the results more than once
	 * 
	 *************************************************************************/
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
	
	/*************************************************************************
	 * addEdges
	 * 
	 * @param a, b, G
	 * @returns void
	 * 
	 * Adds to the vertex proximity matrix 1 edge where vertices a and b are found 
	 * 
	 *************************************************************************/
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
	
	/*************************************************************************
	 * getYearsFromStrings
	 * 
	 * @param releasedates
	 * @returns String[]
	 * 
	 * Uses regexes to get a year (i.e. 4 digits) from a date string of any 
	 * format in the date strings given and returns an array of the years
	 * as strings
	 * 
	 *************************************************************************/
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





