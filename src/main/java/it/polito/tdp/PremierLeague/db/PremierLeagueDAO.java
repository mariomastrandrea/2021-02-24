package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
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
	
	public List<Team> listAllTeams()
	{
		String sql = "SELECT * FROM Teams";
		List<Team> result = new ArrayList<Team>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Team team = new Team(res.getInt("TeamID"), res.getString("Name"));
				result.add(team);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches()
	{
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID "
				+ "ORDER BY m.MatchID ASC";
		
		List<Match> result = new ArrayList<Match>();

		try 
		{
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Collection<Player> getVertices(Match match, Map<Integer, Player> playersIdMap)
	{
		final String sqlQuery = "SELECT PlayerID FROM Actions WHERE MatchID = ?";
		
		Collection<Player> players = new ArrayList<>();
		
		try 
		{
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sqlQuery);
			st.setInt(1, match.getMatchID());
			ResultSet res = st.executeQuery();
			
			while (res.next()) 
			{
				if(playersIdMap.containsKey(res.getInt("PlayerId")))
				{
					players.add(playersIdMap.get(res.getInt("PlayerId")));
				}
			}
			
			conn.close();
			return players;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			throw new RuntimeException("Error in getVertices()", e);
		}
	}
	
	public Collection<Adiacenza> getAdiacenze(Match match, Map<Integer, Player> playersIdMap)
	{
		final String sqlQuery = String.format("%s %s %s %s %s %s",
				"SELECT a1.PlayerID AS id1, a2.PlayerID AS id2,",
						"((a1.totalSuccessfulPassesAll + a1.assists)/a1.timePlayed - ",
						"(a2.totalSuccessfulPassesAll + a2.assists)/a2.timePlayed) AS peso",
				"FROM Actions a1, Actions a2",
				"WHERE a1.MatchID = a2.MatchID AND a1.MatchID = ?",
						"AND a1.PlayerID > a2.PlayerID AND a1.TeamID <> a2.TeamID");
		
		Collection<Adiacenza> adiacenze = new ArrayList<>();
		
		try 
		{
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sqlQuery);
			st.setInt(1, match.getMatchID());
			ResultSet res = st.executeQuery();
			
			while (res.next()) 
			{
				int id1 = res.getInt("id1");
				int id2 = res.getInt("id2");
				
				if(playersIdMap.containsKey(id1) && playersIdMap.containsKey(id2))
				{
					Player player1 = playersIdMap.get(id1);
					Player player2 = playersIdMap.get(id2);
					double weight = res.getDouble("peso");
					
					adiacenze.add(new Adiacenza(player1, player2, weight));
				}
			}
			
			conn.close();
			return adiacenze;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			throw new RuntimeException("Error in getAdiacenze()", e);
		}
	}
	
}
