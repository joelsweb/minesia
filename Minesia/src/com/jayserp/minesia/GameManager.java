package com.jayserp.minesia;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.jayserp.minesia.PlayerDataClass;

/*
 * This is the Game Manager. It deals with the game logic as well as
 * tracking user scores and keeping track of the control point state.
 */

public class GameManager {

	//check if game has started or not
	private boolean hasGameStarted = false;
	private boolean hasGameFinished = false;
	private boolean hasTeamWon = false;
	private boolean startGame = false;
	
	private int timeToWait = 60;
	private int timeToEnd = 300;
	
	private int ghostScore = 0;
	private int teamScore = 0;
	private String winner;
	private String ghost;
	
	private boolean ghostLeft;
			
	private Minesia plugin;
	
	public GameManager(Minesia plugin) {
		this.plugin = plugin;
	}

	/**
	 * This functions deals with checking game logic every second to determine 
	 * what status
	 * 
	 * @param[in]	time	integer time value that should increment every second.
	 * @return 				returns true if game is in progress and false if game has ended.
	 */
	public boolean checkStatus(int time) {
		
		//check if game is still running
		if (hasGameFinished == false) {
			if (hasGameStarted == false && startGame == true) {
				if (timeToWait == 60 || timeToWait == 10) {
					plugin.getServer().broadcastMessage("Game starting in: " + timeToWait);
				}
				if (timeToWait == 30) {
					if (plugin.getUserList().size() < 3) {
						plugin.getServer().broadcastMessage("Not enough players to start, need " +
					(3 - plugin.getUserList().size()) + " more to start.");
						timeToWait = 60;
					} else {
						plugin.getServer().broadcastMessage("Game starting in: " + timeToWait);
						plugin.getServer().broadcastMessage("Selecting ghost...");
						Random random = new Random();
	        			List<PlayerDataClass> temp = plugin.getUserList();
	        			int randomNumber = random.nextInt(temp.size());       			
	        			ghost = temp.get(randomNumber).getName();
	        			plugin.getPlayerHandler().addGhost(ghost);

		    			for (int i = 0; i < plugin.getUserList().size(); i++) {
		    				PlayerDataClass playerData = plugin.getUserList().get(i);
		    				if (playerData.getTeam().equalsIgnoreCase("spec")) {
		    					if (playerData.getType() == null) {
		    						playerData.setType("medic");
		    					}
		    					plugin.getServer().getPlayer(playerData.getName()).setHealth(20);
		    					plugin.getServer().getPlayer(playerData.getName()).setExhaustion(20);
		    					plugin.getServer().getPlayer(playerData.getName()).setFoodLevel(20);
		    					plugin.getPlayerHandler().addPlayer(playerData.getName());
		    					plugin.getServer().getPlayer(playerData.getName()).setGameMode(GameMode.ADVENTURE);
		    				}
		    			}
		    			plugin.getPlayerHandler().spawnGhost(ghost);
		    			Player ghostp = plugin.getServer().getPlayer(ghost);
		    			ghostp.setHealth(20);
		    			ghostp.setExhaustion(20);
		    			ghostp.setFoodLevel(20);
		    			ghostp.setGameMode(GameMode.ADVENTURE);						
					}
				}
				timeToWait--;
				if (timeToWait <= 0) {
					startGame();
					Location location;
					location = new Location(plugin.getServer().getWorld("world"), -1014,11,139);
					plugin.getServer().broadcastMessage("The ghost is " + ghost + "! You have 5 minutes to run. Good luck.");
					plugin.getServer().getPlayer(ghost).teleport(location);
					hasGameStarted = true;
				}
			}
			
			if (hasGameStarted == true) {
				timeToEnd--;
				
				if (timeToEnd == 180 || timeToEnd == 60 || timeToEnd <= 10) {
					plugin.getServer().broadcastMessage(timeToEnd + " seconds remaining.");
				}
				
				if (plugin.getPlayerHandler().getHumanPlayers().isEmpty()) {
					plugin.getServer().broadcastMessage("The ghost [" + ghost + "] has won!");
					resetGame();
					startGame = true;
					return true;
				}
				
				if (ghostLeft || timeToEnd <= 0 || plugin.getServer().getPlayer(plugin.getGameManager().getGhost()).isDead()) {
					plugin.getServer().broadcastMessage("The humans have won!");
					resetGame();
					startGame = true;
					return true;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void setGhostLeft(boolean hasLeft) {
		this.ghostLeft = hasLeft;
	}
	
	private void printTime(int timeToEndRed, int timeToEndBlue) {
		if ((timeToEndRed == 180 || timeToEndBlue == 180 
				|| timeToEndRed == 90 || timeToEndBlue == 90
				|| timeToEndRed == 30 || timeToEndBlue == 30
				|| timeToEndRed <= 5 || timeToEndBlue <= 5) && hasGameFinished == false) {
			if (timeToEndRed != 0 || timeToEndBlue != 0) {

				plugin.getServer().broadcastMessage("Remaining Time - "
												  + ChatColor.RED + "Red: " + timeToEndRed + "s"
												  + ChatColor.GRAY + " | "
												  + ChatColor.BLUE + "Blue: " + timeToEndBlue + "s");
				/*if (timeToEndRed > 10 || timeToEndBlue > 10) {
					timeToEndRed--;
					timeToEndBlue--;
				}*/
			}
		}
	}
	
	public boolean hasTeamWon() {
		return hasTeamWon;
	}

	/**
	 * Gives collective points to a team
	 * 
	 * @param points number of points to give.
	 * @param team team to give it to.
	 */
	public void givePoints(int points, String team) {

	}
	
	public int getTeamScore() {
		return teamScore;
	}

	public void addTeamScore(int teamScore) {
		this.teamScore = this.teamScore + teamScore;
	}
	
	public void resetScores() {
		teamScore = 0;
	}
	
	/**
	 * Starts the game
	 */
	public void startGame() {
		startGame = true;
		onGameStart();
	}
	
	/**
	 * Triggered when game begins. Spawns players into the game.
	 */
	private void onGameStart() {
		/*List<PlayerDataClass> players = plugin.getUserList();
		for (int i = 0; i < players.size(); i++) {
			PlayerDataClass player = players.get(i);
			if (player.getTeam().equalsIgnoreCase("player")) {
				plugin.getPlayerHandler().spawnPlayer(player);
			}
		}*/
		
		plugin.getServer().getWorld("world").setTime(40000);
		
	}

	/**
	 * Resets the game so a new game can begin after the 
	 * old one has ended.
	 */
	public void resetGame() {
		hasGameStarted = false;
		hasGameFinished = false;
		hasTeamWon = false;
		startGame = false;
		
		timeToWait = 60;
		timeToEnd = 300;
		
		ghostScore = 0;
		teamScore = 0;
			
		plugin.getPlayerHandler().clearInventories();
		for (int i = 0; i < plugin.getPlayerHandler().getHumanPlayers().size(); i++) {
			plugin.getPlayerHandler().getHumanPlayers().get(i).resetScores();
		}
		for (int i = 0; i < plugin.getPlayerHandler().getGhostPlayers().size(); i++) {
			plugin.getPlayerHandler().getGhostPlayers().get(i).resetScores();
		}
		
		List<PlayerDataClass> players = plugin.getUserList();
		for (int i = 0; i < players.size(); i++) {
			PlayerDataClass player = players.get(i);
			player.setTeam("spec");
			plugin.getPlayerHandler().addSpecPlayer(plugin.getServer().
					getPlayer(player.getName()).getDisplayName());
		}
		
		//plugin.getPlayerHandler().removeAllPlayers();
	}
    
    public void playEffect() {
        plugin.getServer().getWorld("world").playEffect(new Location(plugin.getServer().getWorld("world"),  
        		0, 4, 0), 
        		Effect.CLICK2, 40);
    }
    
    public boolean hasGameStarted() {
    	return hasGameStarted;
    }
    
    public String getGhost() {
    	return ghost;
    }
    
    public void teleportToSpawn(Player player) {
    	//-942, 4, 33
    	player.teleport(new Location((plugin.getServer().getWorld("world")), -942, 4, 33));
    }
}