package com.gamebuster19901.brigadier.command.tree;

/**
 * A base node represents the first node of a command (I.E. any
 * direct child of the root command node).
 * 
 * @author gamebuster
 *
 * @param <N> The type of your base node
 * @param <S> The type of your command source
 */
public interface BaseNode<S> extends PredicateNode<S>, DescriptiveNode {}
