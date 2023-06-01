package com.thegamecommunity.brigadier.command.argument;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.thegamecommunity.brigadier.command.CommandUtils;

public class LiteralArgument implements ArgumentType<String>{

	private final String arg;
	
	private LiteralArgument(String arg) {
		this.arg = arg;
	}
	
	public static LiteralArgument of(String name) {
		return new LiteralArgument(name);
	}
	
	@Override
	public <S> String parse(S context, StringReader reader) throws CommandSyntaxException {
		String s = CommandUtils.readString(reader);
		if(!s.equals(arg)) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, s);
		}
		return s;
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
			String input = CommandUtils.lastArgOf(builder.getInput());
			
			if(arg.toLowerCase().startsWith(input.toLowerCase())) {
				builder.suggest(arg);
			}
				
			return builder.buildFuture();
		
	}
}
