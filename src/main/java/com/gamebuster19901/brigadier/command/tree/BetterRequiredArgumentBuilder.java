package com.gamebuster19901.brigadier.command.tree;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

public class BetterRequiredArgumentBuilder<S, T> extends BetterArgumentBuilder<S, BetterRequiredArgumentBuilder<S, T>>{

	private final String name;
	private final ArgumentType<T> type;
	
	protected BetterRequiredArgumentBuilder(String name, ArgumentType<T> type) {
		this.name = name;
		this.type = type;
	}
	
	protected BetterRequiredArgumentBuilder(String name, ArgumentType<T> type, String description, String help) {
		this(name, type);
		setDescription(description);
		setHelp(help);
	}
	
    public static <S, T> BetterRequiredArgumentBuilder<S, T> argument(final String name, final ArgumentType<T> type) {
        return new BetterRequiredArgumentBuilder<>(name, type);
    }
    
	public String getName() {
		return name;
	}
	
	public ArgumentType<T> getType() {
		return type;
	}
	
	@Override
	protected BetterRequiredArgumentBuilder<S, T> getThis() {
		return this;
	}
	
	@Override
	public BetterArgumentNode<S, T> build() {
		BetterArgumentNode<S, T> result = new BetterArgumentNode<>(this);
		
		for(final CommandNode<S> argument : getArguments()) {
			result.addChild(argument);
		}
		
		return result;
	}

	public static class BetterArgumentNode<S, T> extends ArgumentCommandNode<S, T> implements PredicateNode<S>, DescriptiveNode {

		private final Map<Predicate<S>, Consumer<S>> failActions;
		private final String description;
		private final String help;
		
		public BetterArgumentNode(BetterRequiredArgumentBuilder<S, T> builder) {
			super(builder.getName(), builder.getType(), builder.getCommand(), builder.getRequirement(), builder.getRedirect(), builder.getRedirectModifier(), builder.isFork(), builder.getSuggestionsProvider());
			this.description = builder.getDescription();
			this.help = builder.getHelp();
			this.failActions = builder.getFailActions();
		}
		
		public BetterArgumentNode(String name, ArgumentType<T> type, String description, String help, Map<Predicate<S>, Consumer<S>> failActions, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean isFork, SuggestionProvider<S> suggestions) {
			super(name, type, command, requirement, redirect, modifier, isFork, suggestions);
			this.description = description;
			this.help = help;
			this.failActions = failActions;
		}

		@Override
		public String getDescription() {
			return description;
		}
		
		@Override
		public String getHelp() {
			return help;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Predicate<S>[] getRequirements() {
			return (Predicate<S>[]) failActions.keySet().toArray(new Predicate[]{});
		}

		@Override
		public Consumer<S> getFailAction(Predicate<S> predicate) {
			return failActions.get(predicate);
		}
		
	}

}
