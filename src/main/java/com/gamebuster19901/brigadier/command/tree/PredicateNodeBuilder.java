package com.gamebuster19901.brigadier.command.tree;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface PredicateNodeBuilder<S, N extends PredicateNodeBuilder<S, N>> {
	
	@SuppressWarnings("rawtypes")
	public static Consumer NO_ACTION = (context) ->{};
	
	public N requires(Predicate<S> requirement, Consumer<S> onFailure);
	
	public N requires(Predicate<S> requirement);
	
}
