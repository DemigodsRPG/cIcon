package com.censoredsoftware.cicon;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class cIconPlugin extends JavaPlugin
{
	protected static Boolean READY;

	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		// Load everything
		new cIcon();

		if(READY) getLogger().info("Successfully enabled.");
	}

	/**
	 * The Bukkit disable method.
	 */
	@Override
	public void onDisable()
	{
		// Un-register listener
		if(READY) HandlerList.unregisterAll(this);

		getLogger().info("Successfully disabled.");
	}
}
