package com.thegamecommunity.discord.command.brigadier.tree;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.thegamecommunity.brigadier.command.tree.BaseCommandNodeBuilder;
import com.thegamecommunity.brigadier.command.tree.BaseNodeBuilder;
import com.thegamecommunity.discord.command.DiscordContext;

import net.dv8tion.jda.api.Permission;

public class DiscordBaseCommandNodeBuilder<S extends DiscordContext> extends BaseCommandNodeBuilder<S> implements BaseNodeBuilder<S, BaseCommandNodeBuilder<S>> {
	
	private final String name;
	
	public DiscordBaseCommandNodeBuilder(String name) {
		this(name, null, null);
	}
	
	public DiscordBaseCommandNodeBuilder(String name, String description, String help) {
		super(name, description, help);
		this.name = name;
		setDescription(description);
		setHelp(help);
	}
	
	public DiscordBaseCommandNodeBuilder<S> requires(Predicate<S> requirement, String message) {
		getFailActions().put(requirement, (context) -> context.queueMessage(message));
		return getThis();
	}
	
	@Override
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
	
    public static <S extends DiscordContext> DiscordBaseCommandNodeBuilder<S> discord(final String name) {
        return new DiscordBaseCommandNodeBuilder<>(name);
    }
    
    public static <S extends DiscordContext> DiscordBaseCommandNodeBuilder<S> discord(final String name, final String description, final String help) {
    	return new DiscordBaseCommandNodeBuilder<>(name, description, help);
    }
    
    public String getName() {
    	return name;
    }

	@Override
	public DiscordBaseCommandNodeBuilder<S> getThis() {
		return this;
	}

	@Override
	public BaseCommandNode<S> build() {
		return new BaseCommandNode<S>(getName(), getDescription(), getHelp(), getFailActions(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork());
	}
	
}
