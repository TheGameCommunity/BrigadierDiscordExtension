package com.thegamecommunity.brigadier.command;

import com.mojang.brigadier.CommandDispatcher;
import com.thegamecommunity.brigadier.command.tree.BaseCommandNodeBuilder;
import com.thegamecommunity.brigadier.command.tree.BaseCommandNodeBuilder.BaseCommandNode;

public abstract class Dispatcher<S> extends CommandDispatcher<S> {

	public BaseCommandNode<S> register(BaseCommandNodeBuilder<S> command) {
		final BaseCommandNode<S> baseNode = command.build();
		this.getRoot().addChild(baseNode);
		return baseNode;
	}
	
}