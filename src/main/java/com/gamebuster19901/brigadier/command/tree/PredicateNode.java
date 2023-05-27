package com.gamebuster19901.brigadier.command.tree;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A Predicate command node is a command node where you
 * can specify specific conditions that must be met
 * before the command can be executed.
 * 
 * You can also specify actions to take if the conditions
 * are not met. (For example, messaging a user that they
 * do not have permission to execute the command).
 * 
 * @author gamebuster
 *
 * @param <S> the type of your command source
 */
public interface PredicateNode<S> {
	
	public Predicate<S>[] getRequirements();
	
	public Consumer<S> getFailAction(Predicate<S> predicate);
	
	/**
	 * 
	 * Checks if the command can be executed without
	 * invoking any FailAction {@link Consumer}s.
	 * 
	 * @param context the command context
	 * @return true if the command is able to be executed, false otherwise
	 */
	public default boolean canExecute(S context) {
		for(Predicate<S> predicate : getRequirements()) {
			if(!predicate.test(context)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the command can be executed, and if it cannot
	 * be executed, invokes the relevant FailAction {@link Consumer}
	 * 
	 * @param context the command context
	 * @return true if the command can execute, false otherwise.
	 */
	public default boolean test(S context) {
		for(Predicate<S> predicate : getRequirements()) {
			if(!predicate.test(context)) {
				getFailAction(predicate).accept(context);
				return false;
			}
		}
		return true;
	}
	
}
