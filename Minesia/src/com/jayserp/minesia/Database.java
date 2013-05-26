package com.jayserp.minesia;

import java.sql.*;

public class Database {
	
	private Minesia plugin;	
	
	private Connection con;
	private	String url;
	private String db;
	private String driver;
	
	public Database(Minesia plugin) {
		this.plugin = plugin;
			
		con = null;
		url = "jdbc:mysql://" + Constants.MYSQL_HOST + ":3306/";
		driver = "com.mysql.jdbc.Driver";
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url+Constants.MYSQL_NAME, 
					Constants.MYSQL_USER, Constants.MYSQL_PASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public GamesDataClass getGameById(int gameId) {	
		GamesDataClass data = new GamesDataClass();
		try {
			String query = "SELECT * FROM games WHERE id=?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, gameId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) { 
				data.setId(rs.getInt("id"));
				data.setTimestamp(rs.getTimestamp("timestamp"));
				data.setWinner(rs.getString("winner"));
				return data;
			}
			return null;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public SessionsDataClass getSessionById(int sessionId) {
		SessionsDataClass data = new SessionsDataClass();
		try {
			String query = "SELECT * FROM sessions WHERE id=?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, sessionId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) { 
				data.setId(rs.getInt("id"));
				data.setPlayerId(rs.getInt("player_id"));
				data.setGameId(rs.getInt("game_id"));
				data.setKills(rs.getInt("kills"));
				data.setDeaths(rs.getInt("deaths"));
				data.setWins(rs.getInt("wins"));
				data.setLosses(rs.getInt("losses"));
				data.setPoints(rs.getInt("points"));
				data.setTime(rs.getInt("time"));
				return data;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public UsersDataClass getUserById(int userId) {
		recheckCon();
		UsersDataClass data = new UsersDataClass();
		try {
			String query = "SELECT * FROM players WHERE id=?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) { 
				data.setId(rs.getInt("id"));
				data.setUsername(rs.getString("username"));
				data.setRank(rs.getInt("rank"));
				return data;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public UsersDataClass getUser(String username) {
		recheckCon();
		UsersDataClass data = new UsersDataClass();
		try {
			String query = "SELECT * FROM players WHERE username=?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) { 
				if (rs.getString("username") != null) { 
					data.setId(rs.getInt("id"));
					data.setUsername(rs.getString("username"));
					data.setRank(rs.getInt("rank"));
					return data;
				}
			}
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int insertUser(UsersDataClass user) {
		recheckCon();
		try {
			String query = "INSERT INTO players (username, rank) VALUES (?, ?)";
			PreparedStatement ps = con.prepareStatement(query, 
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, user.getUsername());
			ps.setInt(2, user.getRank());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}	
	}
	
	public int insertSession(SessionsDataClass s) {

		try {
			String query = "INSERT INTO sessions (player_id, game_id, " +
					"kills, deaths, wins, losses, points, time) VALUES " +
					"(?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = con.prepareStatement(query, 
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, s.getPlayerId());
			ps.setInt(2, s.getGameId());
			ps.setInt(3, s.getKills());
			ps.setInt(4, s.getDeaths());
			ps.setInt(5, s.getWins());
			ps.setInt(6, s.getLosses());
			ps.setInt(7, s.getPoints());
			ps.setFloat(8, s.getTime());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int insertGame(GamesDataClass g) {

		try {
			String query = "INSERT INTO games (winner) VALUES (?)";
			PreparedStatement ps = con.prepareStatement(query, 
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, g.getWinner());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}	
	}
	
	public Minesia getPlugin() {
		return plugin;
	}
	
	public void recheckCon() {
		try {
			if (!(con.isValid(0))) {
				con = DriverManager.getConnection(url+db, "root", "");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
}