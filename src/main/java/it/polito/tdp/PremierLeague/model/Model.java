package it.polito.tdp.PremierLeague.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model 
{
	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> graph;
	private Map<Integer, Player> playersIdMap;
	
	
	public Model()
	{
		this.dao = new PremierLeagueDAO();
		this.playersIdMap = new HashMap<>();
		this.dao.listAllPlayers(playersIdMap);
	}
	
	public void createGraph(Match match)
	{
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiungo i vertici
		Graphs.addAllVertices(this.graph, this.dao.getVertices(match, this.playersIdMap));
		
		
		// aggiungo gli archi
		Collection<Adiacenza> adiacenze = this.dao.getAdiacenze(match, this.playersIdMap);
		
		for(Adiacenza a : adiacenze)
		{
			Player player1 = a.getP1();
			Player player2 = a.getP2();
			double weight = a.getWeight();
			
			if(a.getWeight() >= 0)
			{
				// p1 better than p2
				if(this.graph.containsVertex(player1) && this.graph.containsVertex(player2))
					Graphs.addEdgeWithVertices(this.graph, player1, player2, weight);
			}
			else 
			{
				// p2 better than p1
				if(this.graph.containsVertex(player1) && this.graph.containsVertex(player2))
					Graphs.addEdgeWithVertices(this.graph, player2, player1, -weight);
			}
		}
	}
	
	public int getNumVertices() { return this.graph.vertexSet().size(); }
	public int getNumEdges() { return this.graph.edgeSet().size(); }
	public List<Match> getAllMatches() { return this.dao.listAllMatches(); }
	
	public GiocatoreMigliore getBestPlayer()
	{
		if(this.graph == null) return null;
		
		Player bestPlayer = null;
		double maxDelta = Double.MIN_VALUE;
		
		
		for(Player p : this.graph.vertexSet())
		{
			// calcolo la somma dei pesi degli archi uscenti
			double sumOutgoing = 0.0;
			double sumIncoming = 0.0;
			
			for(var edge : this.graph.outgoingEdgesOf(p))
				sumOutgoing += this.graph.getEdgeWeight(edge);
			
			for(var edge : this.graph.incomingEdgesOf(p))
				sumIncoming += this.graph.getEdgeWeight(edge);
			
			double delta = sumOutgoing - sumIncoming;
			
			if(delta > maxDelta)
			{
				maxDelta = delta;
				bestPlayer = p;
			}
		}
		
		return new GiocatoreMigliore(bestPlayer, maxDelta);
	}
}
