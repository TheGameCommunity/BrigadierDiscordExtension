package com.thegamecommunity.brigadier.command.tree;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralNode;

/**
 * A base node represents the first node of a command (I.E. any
 * direct child of the root command node).
 * 
 * @author gamebuster
 *
 * @param <N> The type of your base node
 * @param <S> The type of your command source
 */
public interface BaseNode<S, N extends CommandNode<S> & LiteralNode<S, N>> extends PredicateNode<S>, DescriptiveNode, LiteralNode<S, N> {}
