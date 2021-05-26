package it.polito.tdp.PremierLeague.simulation;

import it.polito.tdp.PremierLeague.model.Team;

public interface SimulatorResult
{
	int getGoalsTeamHome();
	int getGoalsTeamAway();
	int getRedCardsTeamHome();
	int getRedCardsTeamAway();
	Team getTeamHome();
	Team getTeamAway();
}
