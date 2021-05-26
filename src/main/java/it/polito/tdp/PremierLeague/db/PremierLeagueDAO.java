package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Adjacency;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;

public class PremierLeagueDAO 
{	
	public void listAllPlayers(Map<Integer,Player> playersIdMap)
	{
		String sql = "SELECT * FROM Players";
		Connection conn = DBConnect.getConnection();

		try 
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) 
			{
				if(!playersIdMap.containsKey(res.getInt("PlayerID")))
				{
					Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
					playersIdMap.put(player.getPlayerID(), player);
				}
			}
			conn.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public List<Match> listAllOrderedMatches()
	{
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, "
				+ "m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID "
				+ "ORDER BY m.MatchID ASC";
		
		List<Match> result = new ArrayList<Match>();

		try 
		{
			Connection connection = DBConnect.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet queryResult = statement.executeQuery();
			while (queryResult.next()) 
			{	
				Match match = new Match(queryResult.getInt("m.MatchID"), queryResult.getInt("m.TeamHomeID"), 
										queryResult.getInt("m.TeamAwayID"), queryResult.getInt("m.teamHomeFormation"), 
										queryResult.getInt("m.teamAwayFormation"),queryResult.getInt("m.resultOfTeamHome"), 
										queryResult.getTimestamp("m.date").toLocalDateTime(), queryResult.getString("t1.Name"),
										queryResult.getString("t2.Name"));
				result.add(match);
			}
			
			queryResult.close();
			statement.close();
			connection.close();
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
			throw new RuntimeException("Dao error in listAllMatches()", sqle);
		}
		
		return result;
	}

	public Collection<Player> getVertices(Match match, Map<Integer, Player> playersIdMap)
	{
		final String sqlQuery = "SELECT PlayerID FROM Actions WHERE MatchID = ?";
		
		Collection<Player> players = new ArrayList<>();
		
		try 
		{
			Connection connection = DBConnect.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, match.getMatchID());
			ResultSet queryResult = statement.executeQuery();
			
			while (queryResult.next()) 
			{
				int playerId = queryResult.getInt("PlayerID");
				
				if(!playersIdMap.containsKey(playerId))
					continue;
				
				Player player = playersIdMap.get(playerId);
				players.add(player);
			}
			
			queryResult.close();
			statement.close();
			connection.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			throw new RuntimeException("Error in getVertices()", e);
		}
		
		return players;
	}
	
	public Collection<Adjacency> getAdjacencies(Match match, Map<Integer, Player> playersIdMap)
	{
		final String sqlQuery = String.format("%s %s %s %s %s %s",
				"SELECT a1.PlayerID AS id1, a2.PlayerID AS id2,",
						"((a1.totalSuccessfulPassesAll + a1.assists)/a1.timePlayed - ",
						"(a2.totalSuccessfulPassesAll + a2.assists)/a2.timePlayed) AS peso",
				"FROM Actions a1, Actions a2",
				"WHERE a1.MatchID = a2.MatchID AND a1.MatchID = ?",
						"AND a1.PlayerID > a2.PlayerID AND a1.TeamID <> a2.TeamID");
		
		Collection<Adjacency> adiacenze = new ArrayList<>();
		
		try 
		{
			Connection connection = DBConnect.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, match.getMatchID());
			ResultSet queryResult = statement.executeQuery();
			
			while (queryResult.next()) 
			{
				int playerId1 = queryResult.getInt("id1");
				int playerId2 = queryResult.getInt("id2");
				
				Player player1 = playersIdMap.get(playerId1);
				Player player2 = playersIdMap.get(playerId2);
				double weight = queryResult.getDouble("peso");
				
				if(player1 == null || player2 == null)
					continue;
			
				Adjacency newAdjacency = new Adjacency(player1, player2, weight);
				adiacenze.add(newAdjacency);
			}
			
			queryResult.close();
			statement.close();
			connection.close();
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
			throw new RuntimeException("Error in getAdiacenze()", sqle);
		}
		
		return adiacenze;
	}
	
	public void setPlayerTeam(Player player, Match match, Map<Integer, Team> teamsIdMap)
	{
		final String sql = "SELECT TeamID, Name " +
							"FROM Teams " +
							"WHERE TeamID IN (SELECT TeamID "
											+ "FROM Actions "
											+ "WHERE PlayerID = ? AND MatchID = ?)";
		try
		{
			Connection connection = DBConnect.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, player.getPlayerID());
			statement.setInt(2, match.getMatchID());
			ResultSet queryResult = statement.executeQuery();
			
			if(queryResult.next())
			{
				int teamId = queryResult.getInt("TeamID");
				
				if(!teamsIdMap.containsKey(teamId))
				{
					String teamName = queryResult.getString("name");
					Team newTeam = new Team(teamId, teamName);
					teamsIdMap.put(teamId, newTeam);
				}
				
				player.setTeam(teamsIdMap.get(teamId));
			}
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
			throw new RuntimeException("Dao error in setPlayerTeam()", sqle);
		}
	}

	public void listAllTeams(Map<Integer, Team> teamsIdMap)
	{
		final String sql = "SELECT TeamID, Name FROM Teams";
		
		try 
		{
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			
			while (res.next()) 
			{
				if(!teamsIdMap.containsKey(res.getInt("TeamID")))
				{
					Team newTeam = new Team(res.getInt("TeamID"), res.getString("Name"));
					teamsIdMap.put(newTeam.getTeamID(), newTeam);
				}
			}
			
			res.close();
			st.close();
			conn.close();
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
			throw new RuntimeException("Dao Error in listAllTeams()" , sqle);
		}
	}
}
