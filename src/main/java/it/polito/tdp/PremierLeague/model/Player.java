package it.polito.tdp.PremierLeague.model;

public class Player 
{
	private Integer playerID;
	private String name;
	private Team team;
	
	public Player(Integer playerID, String name) 
	{
		this.playerID = playerID;
		this.name = name;
	}
	
	public Integer getPlayerID() 
	{
		return this.playerID;
	}

	public String getName() 
	{
		return this.name;
	}
	
	public void setTeam(Team team)
	{
		this.team = team;
	}

	public Team getTeam()
	{
		return this.team;
	}
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((playerID == null) ? 0 : playerID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (playerID == null) {
			if (other.playerID != null)
				return false;
		} else if (!playerID.equals(other.playerID))
			return false;
		return true;
	}

	@Override
	public String toString() 
	{
		return this.playerID + " - " + this.name;
	}
	
}
