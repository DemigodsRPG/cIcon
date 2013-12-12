package com.censoredsoftware.cicon;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class cIcon implements Listener, CommandExecutor
{
	// Public Static Access
	public static final cIconPlugin PLUGIN;

	// Private Static Access
	private static final Random RANDOM;

	// Immutable Constants
	private static String ICON_PATH;
	private static ImmutableList<CachedServerIcon> ICONS;

	// Load what is possible to load right away.
	static
	{
		// Toggle ready off.
		cIconPlugin.READY = false;

		// Allow static access.
		PLUGIN = (cIconPlugin) Bukkit.getServer().getPluginManager().getPlugin("cIcon");
		RANDOM = new Random();
	}

	protected cIcon()
	{
		// Define icon path
		ICON_PATH = PLUGIN.getDataFolder() + "/icons/"; // Don't change this.

		// Load icons
		ICONS = loadIcons();

		// Register listener and command
		if(!ICONS.isEmpty())
		{
			PLUGIN.getServer().getPluginManager().registerEvents(this, PLUGIN);
			PLUGIN.getCommand("ciconreload").setExecutor(this);
		}
		else
		{
			PLUGIN.getLogger().severe("There were no suitable icons found.");
			PLUGIN.getLogger().severe("This plugin will now disable.");
			PLUGIN.getServer().getPluginManager().disablePlugin(PLUGIN);
			return;
		}

		// Toggle ready on.
		cIconPlugin.READY = true;
	}

	/**
	 * Loads the icon files from the icon path.
	 * 
	 * @return ImmutableList of the icons.
	 */
	private static ImmutableList<CachedServerIcon> loadIcons()
	{
		// Create immediate list
		List<CachedServerIcon> icons = new ArrayList<>();

		// Create file for icon path
		File iconPath = new File(ICON_PATH);

		// Check if icon path exists
		if(!iconPath.exists())
		{
			if(iconPath.mkdirs()) PLUGIN.getLogger().info("Created icon directory.");
			else
			{
				PLUGIN.getLogger().severe("Could not create icon directory.");
				PLUGIN.getLogger().severe("This plugin will now disable.");
				PLUGIN.getServer().getPluginManager().disablePlugin(PLUGIN);
				return ImmutableList.of();
			}
		}

		// Iterate through all of the files in the icon path
		File[] iconFiles = iconPath.listFiles();
		if(iconFiles != null && iconFiles.length != 0) for(File icon : iconFiles)
		{
			if(!icon.getName().toLowerCase().endsWith(".png")) continue;
			try
			{
				BufferedImage iconImage = ImageIO.read(icon);
				if(iconImage.getWidth() == 64 && iconImage.getHeight() == 64) icons.add(PLUGIN.getServer().loadServerIcon(iconImage));
				else
				{
					PLUGIN.getLogger().warning(icon.getName() + " is not the correct size for an icon.");
					PLUGIN.getLogger().warning("An icon must be 64 pixels wide and 64 pixels high.");
				}
			}
			catch(Exception ignored)
			{}
		}

		// Create Immutable List
		return ImmutableList.copyOf(icons);
	}

	/**
	 * Generates an integer with a value between <code>min</code> and <code>max</code>.
	 * 
	 * @param min the minimum value of the integer.
	 * @param max the maximum value of the integer.
	 * @return Integer
	 */
	private static int generateIntRange(int min, int max)
	{
		return RANDOM.nextInt(max - min + 1) + min;
	}

	/**
	 * The listener method for the client's server list pinging the server.
	 * The plugin sets one of the cached icons to be the visible icon, at random.
	 * 
	 * @param event the list ping event.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onListPing(ServerListPingEvent event)
	{
		// Randomly assign the server icon
		event.setServerIcon(ICONS.get(generateIntRange(0, ICONS.size() - 1)));
	}

	/**
	 * The Bukkit server commands method.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(sender instanceof ConsoleCommandSender)
		{
			// Load icons
			ImmutableList<CachedServerIcon> newIcons = loadIcons();

			// Check if everything worked our alright.
			if(!newIcons.isEmpty())
			{
				// Replace current set of icons
				ICONS = newIcons;
				sender.sendMessage(ChatColor.YELLOW + "cIcon has found and cached icons successfully!");
			}
			else
			{
				// Don't replace current set of icons
				sender.sendMessage(ChatColor.GOLD + "There were no suitable icons found.");
				sender.sendMessage(ChatColor.GOLD + "The icon list will remain unchanged.");
			}
		}
		// Must be console to use this command
		else sender.sendMessage(ChatColor.RED + "This command may only be used from the console.");
		return true;
	}
}
