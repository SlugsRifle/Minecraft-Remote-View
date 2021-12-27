package io.github.slugsrifle;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

public class TabCompletes implements TabCompleter{
	
	ArrayList<String> cl = new ArrayList<>();
	
	public TabCompletes() {
		cl.add("build");
		cl.add("wand");
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		return cl;
	}

}
