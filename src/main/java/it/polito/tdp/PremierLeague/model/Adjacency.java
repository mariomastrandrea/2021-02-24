package it.polito.tdp.PremierLeague.model;

public class Adjacency
{
	private Player p1;
	private Player p2;
	private double weight;
	
	
	public Adjacency(Player p1, Player p2, double weight)
	{
		this.p1 = p1;
		this.p2 = p2;
		this.weight = weight;
	}

	public Player getP1()
	{
		return this.p1;
	}

	public Player getP2()
	{
		return this.p2;
	}

	public double getWeight()
	{
		return this.weight;
	}
}
