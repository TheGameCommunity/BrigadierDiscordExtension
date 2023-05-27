package com.gamebuster19901.brigadier.command;

import com.gamebuster19901.brigadier.command.tree.BaseCommandNodeBuilder;
import com.gamebuster19901.brigadier.command.tree.BaseCommandNodeBuilder.BaseCommandNode;
import com.mojang.brigadier.CommandDispatcher;

public abstract class Dispatcher<S> extends CommandDispatcher<S> {

	public BaseCommandNode<S> register(BaseCommandNodeBuilder<S> command) {
		final BaseCommandNode<S> baseNode = command.build();
		this.getRoot().addChild(baseNode);
		return baseNode;
	}
	
}