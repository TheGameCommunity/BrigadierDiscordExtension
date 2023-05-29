package com.thegamecommunity.brigadier.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.thegamecommunity.brigadier.command.tree.BaseCommandNodeBuilder;
import com.thegamecommunity.brigadier.command.tree.BaseCommandNodeBuilder.BaseCommandNode;
import com.thegamecommunity.brigadier.command.tree.PredicateNode;

public abstract class Dispatcher<S> extends CommandDispatcher<S> {
    
    protected RootCommandNode<S> root = super.getRoot();
    
    public BaseCommandNode<S> register(BaseCommandNodeBuilder<S> command) {
        final BaseCommandNode<S> baseNode = command.build();
        this.getRoot().addChild(baseNode);
        return baseNode;
    }


    @Override
    public int execute(final ParseResults<S> parse) throws CommandSyntaxException {
    	for(ParsedCommandNode<S> parsedNode : parse.getContext().getNodes()) {
    		CommandNode<S> node = parsedNode.getNode();
    		if(node instanceof PredicateNode) {
    			PredicateNode<S> predicateNode = (PredicateNode<S>) node;
    			if(!predicateNode.test(parse.getContext().getSource())) {
    				return 0;
    			}
    		}
    	}
    	return super.execute(parse);
    }
    
}