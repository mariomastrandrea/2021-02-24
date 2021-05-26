package it.polito.tdp.PremierLeague.simulation;

import java.util.LinkedList;
import java.util.Queue;

import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;

public class Simulator implements SimulatorBuilder, SimulatorRunner, SimulatorResult
{
	private Team teamHome;
	private Team teamAway;
	private Player bestPlayer;
	
	private Queue<HighlightType> queue;
	
	private int numPlayersTeamHome;
	private int numPlayersTeamAway;
	private int numGoalsTeamHome;
	private int numGoalsTeamAway;
	
	private Simulator() { }
	
	public static SimulatorBuilder create()
	{
		return new Simulator();
	}

	public SimulatorRunner initialize(int numHighlights, Team teamHome, Team teamAway, Player bestPlayer)
	{
		this.teamAway = teamAway;
		this.teamHome = teamHome;
		this.bestPlayer = bestPlayer;
		
		this.queue = new LinkedList<>();
		this.createHighlights(numHighlights);
		
		this.numPlayersTeamHome = 11;
		this.numPlayersTeamAway = 11;
		this.numGoalsTeamHome = 0;
		this.numGoalsTeamAway = 0;
		
		return this;
	}
	
	private void createHighlights(int numHighlights)
	{
		for(int i=0; i<numHighlights; i++)
		{
			double random = Math.random();
			
			if(random < 0.5) //GOAL
				this.queue.add(HighlightType.GOAL);
			
			else if(random < 0.8)
				this.queue.add(HighlightType.REDCARD);
				
			else
				this.queue.add(HighlightType.INJURY);
		}
	}

	public SimulatorResult run()
	{
		HighlightType highlight = null;
		
		while((highlight = this.queue.poll()) != null)
		{
			switch(highlight)
			{
				case GOAL:
					
					Team goalTeam;
					
					if(this.numPlayersTeamHome > this.numPlayersTeamAway)
						goalTeam = teamHome;
					
					else if(this.numPlayersTeamAway > this.numPlayersTeamHome)
						goalTeam = teamAway;
					
					else //same num players
						goalTeam = this.bestPlayer.getTeam();
					
					// new goal
					if(goalTeam.equals(this.teamHome) && this.numPlayersTeamHome > 0)
						this.numGoalsTeamHome++;
					
					else if(goalTeam.equals(this.teamAway) && this.numPlayersTeamAway > 0)
						this.numGoalsTeamAway++;
					
					break;
					
				case REDCARD:
					
					Team redCardTeam;
					
					double random = Math.random();
					
					if(random < 0.6)
						redCardTeam = this.bestPlayer.getTeam();
					else
						redCardTeam = this.bestPlayer.getTeam().equals(this.teamHome) ? this.teamAway : this.teamHome;
					
					if(redCardTeam.equals(this.teamHome))
						this.numPlayersTeamHome = Math.max(0, this.numPlayersTeamHome - 1);
					
					else if (redCardTeam.equals(this.teamAway))
						this.numPlayersTeamAway = Math.max(0, this.numPlayersTeamAway - 1);
					
					break;
					
				case INJURY:
					
					if(this.numPlayersTeamAway > 0 || this.numPlayersTeamHome > 0)
					{
						int numNewHighlights = Math.random() < 0.5 ? 2 : 3;
						this.createHighlights(numNewHighlights);
					}
					break;
			}
		}
		return this;
	}

	@Override
	public int getGoalsTeamHome()
	{
		return this.numGoalsTeamHome;
	}

	@Override
	public int getGoalsTeamAway()
	{
		return this.numGoalsTeamAway;
	}

	@Override
	public int getRedCardsTeamHome()
	{
		return 11 - this.numPlayersTeamHome;
	}
	
	@Override
	public int getRedCardsTeamAway()
	{
		return 11 - this.numPlayersTeamAway;
	}

	@Override
	public Team getTeamHome()
	{
		return this.teamHome;
	}

	@Override
	public Team getTeamAway()
	{
		return this.teamAway;
	}
}
