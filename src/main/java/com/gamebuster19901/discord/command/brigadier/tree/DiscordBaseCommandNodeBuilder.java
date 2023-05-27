package com.gamebuster19901.discord.command.brigadier.tree;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.gamebuster19901.discord.command.DiscordContext;
import com.mojang.brigadier.tree.CommandNode;
import com.gamebuster19901.brigadier.command.tree.BaseCommandNodeBuilder;
import com.gamebuster19901.brigadier.command.tree.BaseNodeBuilder;
import com.gamebuster19901.brigadier.command.tree.BetterArgumentBuilder;

import net.dv8tion.jda.api.Permission;

public class DiscordBaseCommandNodeBuilder<S extends DiscordContext> extends BetterArgumentBuilder<S, DiscordBaseCommandNodeBuilder<S>> implements BaseNodeBuilder<S, DiscordBaseCommandNodeBuilder<S>> {
	
	private final String name;
	
	public DiscordBaseCommandNodeBuilder(String name) {
		this(name, null, null);
	}
	
	public DiscordBaseCommandNodeBuilder(String name, String description, String help) {
		this.name = name;
		setDescription(description);
		setHelp(help);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public DiscordBaseCommandNodeBuilder<S> requires(Predicate<S> requirement) { 
		getFailActions().put(requirement, (context) -> context.queueMessage("You are unable to execute this command - No reason specified."));
		return getThis();
	}

	public DiscordBaseCommandNodeBuilder<S> requires(Permission... permissions) {
		for(Permission permission : permissions) {
			Predicate<S> requiredPerms = (context) -> context.hasPermission(permission);
			Consumer<S> failAction = (context) -> context.queueMessage("You are unable to execute this command - You do not have the " + permission.getName() + " permission.", true, false);
			getFailActions().put(requiredPerms, failAction);
			
		}
		return getThis();
	}
	
    public static <S> BaseCommandNodeBuilder<S> base(final String name) {
        return new BaseCommandNodeBuilder<>(name);
    }
    
    public static <S> BaseCommandNodeBuilder<S> base(final String name, final String description, final String help) {
    	return new BaseCommandNodeBuilder<>(name, description, help);
    }
    
    public String getName() {
    	return name;
    }

	@Override
	protected DiscordBaseCommandNodeBuilder<S> getThis() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandNode<S> build() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
