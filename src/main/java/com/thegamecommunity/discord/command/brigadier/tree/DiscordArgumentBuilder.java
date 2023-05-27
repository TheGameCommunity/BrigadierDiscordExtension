package com.thegamecommunity.discord.command.brigadier.tree;

import java.util.function.Predicate;

import com.mojang.brigadier.arguments.ArgumentType;

import com.thegamecommunity.brigadier.command.tree.BetterRequiredArgumentBuilder;
import com.thegamecommunity.discord.command.DiscordContext;

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
		return new BetterArgumentNode<>(this);
	}

}
