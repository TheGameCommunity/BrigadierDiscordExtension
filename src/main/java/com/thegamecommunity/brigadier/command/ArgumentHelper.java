package com.thegamecommunity.brigadier.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.thegamecommunity.brigadier.command.argument.LiteralArgument;
import com.thegamecommunity.brigadier.command.argument.suggestion.AnyStringSuggestionProvider;
import com.thegamecommunity.brigadier.command.tree.BaseCommandNodeBuilder;
import com.thegamecommunity.brigadier.command.tree.BetterRequiredArgumentBuilder;

public abstract class ArgumentHelper<S> {
	
	public BetterRequiredArgumentBuilder<S, String> suggestableString(String name) {
		return argument(name, LiteralArgument.of(name));
	}
	
	public BaseCommandNodeBuilder<S> base(String name) {
		return BaseCommandNodeBuilder.base(name);
	}
	
	public BaseCommandNodeBuilder<S> base(String name, String description, String help) {
		return BaseCommandNodeBuilder.base(name, description, help);
	}
	
	public <T> BetterRequiredArgumentBuilder<S, T> argument(String name, ArgumentType<T> type) {
		return BetterRequiredArgumentBuilder.argument(name, type);
	}
	
	public BetterRequiredArgumentBuilder<S, String> anyString(String name) {
		return argument(name, StringArgumentType.string()).suggests(new AnyStringSuggestionProvider<>(name));
	}
	
	public BetterRequiredArgumentBuilder<S, String> anyStringGreedy(String name) {
		return argument(name, StringArgumentType.greedyString()).suggests(new AnyStringSuggestionProvider<>(name, true));
	}
	
}
