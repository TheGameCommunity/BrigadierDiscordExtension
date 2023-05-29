package com.thegamecommunity.discord.command.brigadier.tree;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.thegamecommunity.brigadier.command.tree.BetterRequiredArgumentBuilder;
import com.thegamecommunity.discord.command.DiscordContext;

import net.dv8tion.jda.api.Permission;

public class DiscordArgumentBuilder<S extends DiscordContext, T> extends BetterRequiredArgumentBuilder<S, T>{

	protected DiscordArgumentBuilder(String name, ArgumentType<T> type) {
		super(name, type);
	}
	
	protected DiscordArgumentBuilder(String name, ArgumentType<T> type, String description, String help) {
		super(name, type, description, help);
	}
	
	public static <S extends DiscordContext, T> DiscordArgumentBuilder<S, T> arg(String name, ArgumentType<T> type) {
		return new DiscordArgumentBuilder<>(name, type);
	}
	
	public static <S extends DiscordContext, T> DiscordArgumentBuilder<S, T> arg(String name, ArgumentType<T> type, String description, String help) {
		return new DiscordArgumentBuilder<>(name, type, description, help);
	}
	
	@Override
    public DiscordArgumentBuilder<S, T> suggests(final SuggestionProvider<S> provider) {
        this.suggestionsProvider = provider;
        return getThis();
    }
	
	public DiscordArgumentBuilder<S, T> botRequires(Permission... permissions) {
		for(Permission permission : permissions) {
			Predicate<S> requiredPerms = (context) -> context.getServer() != null ? context.getServer().getSelfMember().hasPermission(permission) : true;
			Consumer<S> failAction = (context) -> context.queueMessage("I'm unable to execute this command - I do not have the `" + permission.getName() + "` permission.", true, false);
			getFailActions().put(requiredPerms, failAction);
		}
		return getThis();
	}
	
	@Override
	public DiscordArgumentBuilder<S, T> requires(Predicate<S> requirement) { 
		getFailActions().put(requirement, (context) -> context.queueMessage("You are unable to execute this command - No reason specified."));
		return getThis();
	}

	public DiscordArgumentBuilder<S, T> requires(Permission... permissions) {
		for(Permission permission : permissions) {
			Predicate<S> requiredPerms = (context) -> context.hasPermission(permission);
			Consumer<S> failAction = (context) -> context.queueMessage("You are unable to execute this command - You do not have the `" + permission.getName() + "` permission.", true, false);
			getFailActions().put(requiredPerms, failAction);
			
		}
		return getThis();
	}
	
	public DiscordArgumentBuilder<S, T> requiresChannel(Permission... permissions) {
		for(Permission permission : permissions) {
			Predicate<S> requiredPerms = (context) -> context.hasPermission(context, permission);
			Consumer<S> failAction = (context) -> context.queueMessage("You are unable to execute this command - You do not have the `" + permission.getName() + "` permission.", true, false);
			getFailActions().put(requiredPerms, failAction);
			
		}
		return getThis();
	}

	public DiscordArgumentBuilder<S, T> requires(Predicate<S> requirement, String message) {
		getFailActions().put(requirement, (context) -> context.queueMessage(message));
		return getThis();
	}
	
	@Override
	protected DiscordArgumentBuilder<S, T> getThis() {
		return this;
	}

	@Override
	public BetterArgumentNode<S, T> build() {
		BetterArgumentNode<S, T> result = new BetterArgumentNode<>(getName(), getType(), getDescription(), getHelp(), getFailActions(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork(), getSuggestionsProvider());
		
    	for(final CommandNode<S> argument : getArguments()) {
    		System.out.println("ARGUMENT: " + argument);
    		result.addChild(argument);
    	}
    	
    	return result;
	}

}
