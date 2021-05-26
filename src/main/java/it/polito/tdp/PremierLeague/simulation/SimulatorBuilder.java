package it.polito.tdp.PremierLeague.simulation;

import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;

public interface SimulatorBuilder
{
	SimulatorRunner initialize(int numHighlights, Team teamHome, Team teamAway, Player bestPlayer);
}
