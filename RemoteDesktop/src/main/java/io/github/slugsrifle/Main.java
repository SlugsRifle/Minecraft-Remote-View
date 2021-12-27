package io.github.slugsrifle;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.Dimension;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
	public static Dimension res;
	public static Logger logger;
	public static String prefix = ChatColor.WHITE + "[" + ChatColor.GREEN + "Remote" +ChatColor.WHITE + "] ";
	
	public static Main instance;
	public static ServerSocket serverSocket;
	public static int port = 0;
	public static ServerSocketThread serverSocketThread;
	public static HashMap<UUID, Location> pos1 = new HashMap<>();
	public static HashMap<UUID, Location> pos2 = new HashMap<>();
	public static HashMap<Integer, MapView> mapView = new HashMap<>();
	
	static MapDataSender mds;
	static MapRender mr;

	ProtocolManager protocolManager;
	
	public static int getSize() {
		int tw = res.width;
		int th = res.height;
		if (res.width % 128 != 0) {
			tw += 128 - res.width % 128;
		}
		if (res.height % 128 != 0) {
			th += 128 - res.height % 128;
		}
		return tw * th / 16384;
	}

	public void initCommand() {
		CommandExecutor ce = new Commands();
		Objects.requireNonNull(getCommand("map")).setExecutor(ce);
	}
	public void initTabComplete() {
		TabCompleter tc = new TabCompletes();
		Objects.requireNonNull(getCommand("map")).setTabCompleter(tc);
	}

	public void initEvent() {
		Events e = new Events(this);
		getServer().getPluginManager().registerEvents(e, this);
	}
	
	public void initConfig() {
		saveDefaultConfig();
		FileConfiguration fc = getConfig();
		port = fc.getInt("port");
		res = new Dimension(fc.getInt("resx"), fc.getInt("resy"));
	}
	
	public void initSocket() {
		try {
			serverSocket = new ServerSocket(port);
			serverSocketThread = new ServerSocketThread();
			serverSocketThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initMap() {
		mds = new MapDataSender(protocolManager);
		mr = new MapRender();
	}
	
	public void finSocket() {
		serverSocketThread.exit();
	}
	
	public void finMap() {
		mds.exit();
		mr.exit();
	}

	@Override
	public void onLoad() {
		logger = getLogger();
		protocolManager = ProtocolLibrary.getProtocolManager();
		getLogger().info("map is Loaded");
	}

	@Override
	public void onEnable() {
		instance = this;
		initConfig();
		initEvent();
		initCommand();
		initTabComplete();
		initSocket();
		initMap();
		getLogger().info("map is Enabled");
	}

	@Override
	public void onDisable() {
		finSocket();
		finMap();
		getLogger().info("map is Disabled");
	}
}
