package com.thegamecommunity.brigadier.command.tree;

public interface BaseNodeBuilder<S, N extends PredicateNodeBuilder<S, N> & DescriptiveNodeBuilder<N>> extends PredicateNodeBuilder<S, N>, DescriptiveNodeBuilder<N> {}
