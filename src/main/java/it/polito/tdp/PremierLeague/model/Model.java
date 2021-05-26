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
import it.polito.tdp.PremierLeague.simulation.Simulator;
import it.polito.tdp.PremierLeague.simulation.SimulatorBuilder;
import it.polito.tdp.PremierLeague.simulation.SimulatorResult;

public class Model 
{
	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> graph;
	private Map<Integer, Player> playersIdMap;
	private Map<Integer, Team> teamsIdMap;
	private Match match;
	private BestPlayer bestPlayer;
	private SimulatorBuilder simulator;
	
	
	public Model()
	{
		this.dao = new PremierLeagueDAO();
		this.playersIdMap = new HashMap<>();
		this.teamsIdMap = new HashMap<>();
		this.dao.listAllPlayers(this.playersIdMap);
		this.dao.listAllTeams(this.teamsIdMap);
		this.simulator = Simulator.create();
	}
	
	public void createGraph(Match match)
	{
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiungo i vertici
		Collection<Player> allVertices = this.dao.getVertices(match, this.playersIdMap);
		Graphs.addAllVertices(this.graph, allVertices);
		
		
		// aggiungo gli archi
		Collection<Adjacency> allAdjacencies = this.dao.getAdjacencies(match, this.playersIdMap);
		
		for(Adjacency a : allAdjacencies)
		{
			Player player1 = a.getP1();
			Player player2 = a.getP2();
			double weight = a.getWeight();
			
			if(a.getWeight() >= 0) // p1 better than p2
			{
				if(this.graph.containsVertex(player1) && this.graph.containsVertex(player2))
					Graphs.addEdge(this.graph, player1, player2, weight);
			}
			else // p2 better than p1
			{
				if(this.graph.containsVertex(player1) && this.graph.containsVertex(player2))
					Graphs.addEdge(this.graph, player2, player1, -weight);
			}
		}
		
		this.match = match;
		this.bestPlayer = null;
	}
	
	
	public int getNumVertices() { return this.graph.vertexSet().size(); }
	public int getNumEdges() { return this.graph.edgeSet().size(); }
	public List<Match> getAllMatches() { return this.dao.listAllOrderedMatches(); }
	
	public BestPlayer getBestPlayer()
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
		
		if(bestPlayer == null)
			return null;
		
		this.bestPlayer = new BestPlayer(bestPlayer, maxDelta);
		this.dao.setPlayerTeam(this.bestPlayer.getBestPlayer(), this.match, this.teamsIdMap);
		return this.bestPlayer;
	}
		
	public SimulatorResult runNewSimulation(int numHighlights)
	{
		if(this.bestPlayer == null)
			return null;
		
		Team teamHome = this.teamsIdMap.get(this.match.getTeamHomeID());
		Team teamAway = this.teamsIdMap.get(this.match.getTeamAwayID());
		
		SimulatorResult result = this.simulator.initialize(numHighlights, 
				teamHome, teamAway, this.bestPlayer.getBestPlayer()).run();
		
		return result;
	}
}
