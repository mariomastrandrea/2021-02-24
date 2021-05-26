package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnect 
{
	private static final String jdbcURL = "jdbc:mariadb://localhost/PremierLeague";
	private static final HikariDataSource dataSource;
	private static final String username = "root";
	private static final String password = "root";
	
	static 
	{
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcURL);
		config.setUsername(username);
		config.setPassword(password);
		
		// MySQL configuration
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		
		dataSource = new HikariDataSource(config);
	}
	
	public static Connection getConnection() 
	{
		try 
		{
			return dataSource.getConnection();
		} 
		catch (SQLException sqle) 
		{
			System.err.println("DB connection error at: " + jdbcURL);
			throw new RuntimeException("DB connection error at: " + jdbcURL, sqle);
		}
	}
}
