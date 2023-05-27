package com.gamebuster19901.brigadier.command.tree;

public interface DescriptiveNodeBuilder<N extends DescriptiveNodeBuilder<N>> {

	public N setDescription(String description);
	
	public N setHelp(String help);
	
	public String getDescription();
	
	public String getHelp();
	
}
