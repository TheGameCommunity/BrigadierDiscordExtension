package com.thegamecommunity.brigadier.command.tree;

import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public abstract class BetterArgumentBuilder<S, T extends BetterArgumentBuilder<S, T>>
	extends 
		ArgumentBuilder<S, T> 
	implements 
		PredicateNodeBuilder<S, T>,
		DescriptiveNodeBuilder<T>
{

	protected SuggestionProvider<S> suggestionsProvider = null;
	private final LinkedHashMap<Predicate<S>, Consumer<S>> failActions = new LinkedHashMap<>();
	private String description = null;
	private String help = null;
	
	@Override
	@SuppressWarnings("unchecked")
	public T requires(Predicate<S> requirement) { 
		failActions.put(requirement, NO_ACTION);
		return getThis();
	}
	
	@Override
	public T requires(Predicate<S> requirement, Consumer<S> onFailure) {
		failActions.put(requirement, onFailure);
		return getThis();
	}
	
	@Override
	public T setDescription(String description) {
		this.description = description;
		return getThis();
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public T setHelp(String help) {
		this.help = help;
		return getThis();
	}
	
	public String getHelp() {
		return help;
	}
	
	protected LinkedHashMap<Predicate<S>, Consumer<S>> getFailActions() {
		return failActions;
	}
	
    public SuggestionProvider<S> getSuggestionsProvider() {
        return suggestionsProvider;
    }
    
    public T suggests(final SuggestionProvider<S> provider) {
        this.suggestionsProvider = provider;
        return getThis();
    }
	
}
