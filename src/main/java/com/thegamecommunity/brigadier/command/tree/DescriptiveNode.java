package com.thegamecommunity.brigadier.command.tree;

import org.jetbrains.annotations.Nullable;

/**
 * A Descriptive command node is a node that
 * can be used to obtain description and help
 * information for a command.
 * 
 * Information provided in children descriptive
 * nodes overwrite information in parent descriptive
 * nodes.
 * 
 * @author Gamebuster
 *
 */
public interface DescriptiveNode {

	@Nullable
	public default String getDescription() {
		return null;
	}
	
	@Nullable
	public default String getHelp() {
		return null;
	}
	
	public default boolean hasDescription() {
		return getDescription() != null;
	}
	
	public default boolean hasHelp() {
		return getHelp() != null;
	}
	
}
