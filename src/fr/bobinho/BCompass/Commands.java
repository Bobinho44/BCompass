package fr.bobinho.BCompass;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Commands implements CommandExecutor {
	
	private static Main main = Main.getPlugin(Main.class);

	private boolean isDouble(String str) {
	    try {
	        Double.parseDouble(str);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	
	private String createMessage(String[] message, int start) {
		String compassName = "";
		for (int i = start; i < message.length; i++) {
			compassName += message[i] + " ";
		}
		return compassName;
	}
	
	public static Map<Player,Boolean> isEnable = new WeakHashMap<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		Player player = (Player)sender;
		
		//Command BCompass
		if(cmd.getName().equalsIgnoreCase("bcompass")) {
			if (args.length >= 1) {
				
				//Clear compass
				if (args[0].equalsIgnoreCase("clear")) {
					
					if (isEnable.get(player) != null && isEnable.get(player) == true) {
						isEnable.put(player, false);	
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
						String message = main.getMessagesString("compassClear");
						player.sendMessage(message);
						
					} else {
						String message = main.getMessagesString("compassAlreadyClear");
						player.sendMessage(message);
					}
					
					return true;
				}
				
				//Set compass
				else if (args[0].equalsIgnoreCase("set") && args.length > 1) {

					Location compassLocation = player.getLocation();	
					String compassName = "";
					Compass compass;
					
					//compass at coordinates
					if (args.length >= 4 && isDouble(args[1]) && isDouble(args[2]) && isDouble(args[3])) {
						String message = main.getMessagesString("compassSetCoord").replace("{coord}", args[1] + " " + args[2] + " " + args[3]);
						player.sendMessage(message);
						compassLocation = new Location(player.getWorld(), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
						compass = new Compass(player, compassLocation, createMessage(args, 4)); 
					}
					
					//compass at player's location
					else if (Bukkit.getPlayer(args[1]) != null) {
						Player target = Bukkit.getPlayer(args[1]);
						String message = main.getMessagesString("compassSetPlayer").replace("{player}", Bukkit.getPlayer(args[1]).getDisplayName());
						player.sendMessage(message);
						compass = new Compass(player, target.getLocation(), createMessage(args, 2)); 
					}
					
					//compass at own location
					else {
						String message = main.getMessagesString("compassSetOwn");
						player.sendMessage(message);
						compass = new Compass(player, player.getLocation(), createMessage(args, 1)); 
					}			
					
					
					//execution
					if (compass.getLocation() != null) {
						compassName = ChatColor.translateAlternateColorCodes('&', compassName);
						compass.sendCompass("false");
						    	
						return true;
					}
					
				}
				
				//compass follow and execution
				else if (args[0].equalsIgnoreCase("follow") && args.length > 1) {
					 if (Bukkit.getPlayer(args[1]) != null) {
							Player target = Bukkit.getPlayer(args[1]);
							String message = main.getMessagesString("compassFollowPlayer").replace("{player}", Bukkit.getPlayer(args[1]).getDisplayName());
							player.sendMessage(message);
							Compass compass = new Compass(player, target.getLocation(), createMessage(args, 2)); 
							compass.sendCompass(target.getDisplayName());
							
							return true;
						}
				}	
				
			}
		}
		
		//commands help
		for (int i = 1; i <= main.getMessagesConfig().getConfigurationSection("messages").getKeys(false).size() - 7; i++) {		
			String message = main.getMessagesString("compassHelp" + i);
			player.sendMessage(message);
		}
		
		return false;
		
	}
	
}
