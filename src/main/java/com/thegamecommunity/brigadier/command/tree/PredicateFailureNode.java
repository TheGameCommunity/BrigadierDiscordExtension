package com.thegamecommunity.brigadier.command.tree;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;

public class PredicateFailureNode<S, N extends CommandNode<S> & PredicateNode<S>> extends CommandNode<S> implements PredicateNode<S>, DescriptiveNode {
	
	N old;
	String description;
	String help;
	
	private PredicateFailureNode(Command<S> command, String description, String help, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
		super(command, requirement, redirect, modifier, forks);
	}
	
	private PredicateFailureNode(N node, String description, String help) {
		this(node.getCommand(), description, help, (source) -> true, node.getRedirect(), node.getRedirectModifier(), node.isFork());
		this.old = node;
	}
	
	public static <S, N extends CommandNode<S> & PredicateNode<S>> PredicateFailureNode<S, N> build(N node) {
		if(node instanceof DescriptiveNode) {
			DescriptiveNode dNode = (DescriptiveNode) node;
			return new PredicateFailureNode<S, N>(node, dNode.getDescription(), dNode.getHelp());
		}
		return new PredicateFailureNode<S, N>(node, null, null);
	}
	
	@Nullable
	public String getDescription() {
		return description;
	}
	
	@Nullable
	public String getHelp() {
		return help;
	}

	@Override
	public Predicate<S>[] getRequirements() {
		return old.getRequirements();
	}

	@Override
	public Consumer<S> getFailAction(Predicate<S> predicate) {
		return old.getFailAction(predicate);
	}

	@Override
	public boolean isValidInput(S source, String input) {
		return old.isValidInput(source, input);
	}

	@Override
	public String getName() {
		return old.getName();
	}

	@Override
	public String getUsageText() {
		return old.getUsageText();
	}

	@Override
	public void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
		final int start = reader.getCursor();
		old.parse(reader, contextBuilder);
        final int end = reader.getCursor();
		contextBuilder.getNodes().remove(contextBuilder.getNodes().size() - 1);
		contextBuilder.withNode(this, StringRange.between(start, end));
	}
	
	@Override
	public Collection<CommandNode<S>> getChildren() {
		return old.getChildren();
	}
	
	@Override
	public CommandNode<S> getChild(final String name) {
		return old.getChild(name);
	}
	
	@Override
	public void addChild(final CommandNode<S> node) {
		old.addChild(node);
	}

	@Override
	public CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		if(old.canExecute(context.getSource())) {
			return old.listSuggestions(context, builder);
		}
		return builder.buildFuture();
	}

	@Override
	public ArgumentBuilder<S, ?> createBuilder() {
		return old.createBuilder();
	}

	@Override
	public String getSortedKey() {
		return old.getSortedKey();
	}

	@Override
	public Collection<String> getExamples() {
		return old.getExamples();
	}
	
	@Override
	public Command<S> getCommand() {
		if(old.getCommand() != null) {
			return (command) -> {new AssertionError("A predicateFailureNode should never be executed").printStackTrace(); return 0;};
		}
		return null;
	}

}
