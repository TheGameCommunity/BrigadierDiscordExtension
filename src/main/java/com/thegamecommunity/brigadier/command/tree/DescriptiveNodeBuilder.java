package com.thegamecommunity.brigadier.command.tree;

public interface DescriptiveNodeBuilder<N extends DescriptiveNodeBuilder<N>> {

	public N setDescription(String description);
	
	public N setHelp(String help);
	
	public String getDescription();
	
	public String getHelp();
	
}
