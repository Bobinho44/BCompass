package fr.bobinho.BCompass;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.ChatColor.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Compass implements Listener {
	
	private static Main main = Main.getPlugin(Main.class);
	
	private Player player;
	private Location location;
	private String name;
	
	Compass(Player player, Location location, String name) {
		this.player = player;
		this.location = location;
		this.name = name;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void sendCompass(String isFollow) {
		Commands.isEnable.put(player, false);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			public void run() {
		    	 Commands.isEnable.put(player, true);
		    	 new BukkitRunnable() {
		 		    public void run() {
		 		    	
		 		    	if (Commands.isEnable.get(player) == true) {
		 		    		if (isFollow != "false") {
		 		    			if (Bukkit.getPlayer(isFollow) != null) {
		 		    				location = Bukkit.getPlayer(isFollow).getLocation();
		 		    			}
		 		    			else {
		 		    				String message = main.getMessagesString("compassFollowDisconnect");
		 							player.sendMessage(message);
		 		    				cancel();
		 		    			}
		 		    		}
		 		    		Double compassAngle = getAngle();
		 		    		String actionbarMessage = getMessage(compassAngle);
		 		    		
		 		    		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(name + " " + actionbarMessage));
		 		    		
		 		    	} else {
		 		    		cancel();
		 		    	}
		 		    	
		 		    }
		 		    
		 		}.runTaskTimer(main, 0L, 1L);
		     }
		}, 1);
	}
	
	public String getMessage(Double compassAngle) {
		String height = "";
		double playerY = player.getLocation().getBlockY();
		double compassLocationY = location.getBlockY();
		double heightSimplification = (playerY - compassLocationY)/Math.abs(playerY - compassLocationY);

		//Using texture pack
		if  (main.getConfig().getBoolean("texturePack")) {
			if ((int) (playerY - compassLocationY) != 0) {
				height = GOLD + " (" + RESET + new String(Character.toChars((0x1100 +  (int) (37.5 + heightSimplification * 0.5)))) + GOLD + ")";
			}
			return RESET + new String(Character.toChars((0x1100 +  (int) (compassAngle / (360/35))))) + height;
		}
		
		//Using unicode
		List<String> directionList = Arrays.asList("↑", "⬈", "→", "⬊", "↓", "⬋", "←", "⬉", "○", "⇧", "⇩");
		if ((int) (playerY - compassLocationY) != 0) {
			height = " (" + directionList.get((int) (9.5 + heightSimplification * 0.5)) + ")";
		}
		return directionList.get((int) (compassAngle / (360/8))) + height ;
	}
	
	public Double getAngle() {		
		double compassAngle = 0;
		
		if (player.getLocation().distance(location) <= main.getConfig().getDouble("radius")) {
			compassAngle = 361;
		}
		
		else {			
			float yaw = player.getLocation().getYaw();
			Byte Ax = 1;
			Double Tx = location.getZ() - player.getLocation().getZ();
			Double Ty = location.getX() - player.getLocation().getX();
			
			if (yaw < 0) {
				yaw += 360;
			}
				
			if (Ty > 0) {
				Ax = -1;
				compassAngle += 180;
			}
				
			Double Bx = Tx / (Math.sqrt(Math.pow(Tx, 2) + Math.pow(Ty, 2)));
			Double By = Ty / (Math.sqrt(Math.pow(Tx, 2) + Math.pow(Ty, 2)));
			Double theta = Math.toDegrees(Math.acos(Ax * Bx / (Math.sqrt((Math.pow(Bx, 2) + Math.pow(By, 2))))));
			compassAngle += (theta - yaw);
				
			if (compassAngle < 0) {
				compassAngle +=  360;
			}
		}		
		return compassAngle;		
	}

}
