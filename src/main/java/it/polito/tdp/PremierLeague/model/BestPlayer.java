package it.polito.tdp.PremierLeague.model;

public class BestPlayer
{
	private Player bestPlayer;
	private double score;
	
	
	public BestPlayer(Player bestPlayer, double score)
	{
		this.bestPlayer = bestPlayer;
		this.score = score;
	}

	public Player getBestPlayer()
	{
		return this.bestPlayer;
	}

	public double getScore()
	{
		return this.score;
	}
}
