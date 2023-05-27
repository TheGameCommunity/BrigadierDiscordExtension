package com.gamebuster19901.brigadier.command;

import com.gamebuster19901.brigadier.command.argument.LiteralArgument;
import com.gamebuster19901.brigadier.command.argument.suggestion.AnyStringSuggestionProvider;
import com.gamebuster19901.brigadier.command.tree.BaseCommandNodeBuilder;
import com.gamebuster19901.brigadier.command.tree.BetterRequiredArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

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
