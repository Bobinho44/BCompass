package fr.bobinho.BCompass;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private File messagesFile = new File(getDataFolder(), "messages.yml");
	private FileConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
	
	public FileConfiguration getMessagesConfig() {
		return messagesConfig;
	}
	
	public String getMessagesString(String message) {
		String unformattedMessage = messagesConfig.getString("messages." + message);
		String formattedMessage = ChatColor.translateAlternateColorCodes('&', unformattedMessage);
		return formattedMessage;
	}
	
	@Override
	public void onEnable() {
		saveResource("messages.yml", true);
		saveDefaultConfig();
		System.out.println("Le plugin vient de se lancer");
		getCommand("bcompass").setExecutor(new Commands());
	}
		
	@Override
	public void onDisable() {
		System.out.println("Le plugin vient de s'eteindre");
	}

}
