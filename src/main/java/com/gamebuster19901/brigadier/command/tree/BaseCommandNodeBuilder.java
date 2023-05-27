package com.gamebuster19901.brigadier.command.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;

public class BaseCommandNodeBuilder<S> extends BetterArgumentBuilder<S, BaseCommandNodeBuilder<S>> implements BaseNodeBuilder<S, BaseCommandNodeBuilder<S>>{
	
	private final String name;
	
	public BaseCommandNodeBuilder(String name) {
		this(name, null, null);
	}
	
	public BaseCommandNodeBuilder(String name, String description, String help) {
		this.name = name;
		setDescription(description);
		setHelp(help);
	}
	
    public static <S> BaseCommandNodeBuilder<S> base(final String name) {
        return new BaseCommandNodeBuilder<>(name);
    }
    
    public static <S> BaseCommandNodeBuilder<S> base(final String name, final String description, final String help) {
    	return new BaseCommandNodeBuilder<>(name, description, help);
    }
    
    public String getName() {
    	return name;
    }
    
    @Override
    public BaseCommandNodeBuilder<S> requires(Predicate<S> requirement) {
    	super.requires(requirement);
    	return this;
    }
    
    @Override
    public BaseCommandNode<S> build() {
    	BaseCommandNode<S> result = new BaseCommandNode<>(name, getDescription(), getHelp(), getFailActions(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork());
    	
    	for(final CommandNode<S> argument : getArguments()) {
    		result.addChild(argument);
    	}
    	
    	return result;
    }
    
    @Override
    public BaseCommandNodeBuilder<S> getThis() {
    	return this;
    }
	
	public static class BaseCommandNode<S> extends CommandNode<S> implements BaseNode<S> {

		private final String literal;
		private final String literalLowerCase;
		private final Map<Predicate<S>, Consumer<S>> failActions;
		private final String description;
		private final String help;
		
		public BaseCommandNode(String literal, String description, String help, Map<Predicate<S>, Consumer<S>> failActions, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
			super(command, requirement, redirect, modifier, forks);
			this.literal = literal;
			this.literalLowerCase = literal.toLowerCase(Locale.ROOT);
			this.description = description;
			this.help = help;
			this.failActions = failActions;
		}

		@Override
		public String getDescription() {
			return description;
		}
		
		@Override
		public String getHelp() {
			return help;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Predicate<S>[] getRequirements() {
			return (Predicate<S>[]) failActions.keySet().toArray(new Predicate[]{});
		}

		@Override
		public Consumer<S> getFailAction(Predicate<S> predicate) {
			return failActions.get(predicate);
		}

		@Override
		protected boolean isValidInput(S source, String input) {
			return parse(new StringReader(input)) > -1;
		}
		
	    private int parse(final StringReader reader) {
	        final int start = reader.getCursor();
	        if (reader.canRead(literal.length())) {
	            final int end = start + literal.length();
	            if (reader.getString().substring(start, end).equals(literal)) {
	                reader.setCursor(end);
	                if (!reader.canRead() || reader.peek() == ' ') {
	                    return end;
	                } else {
	                    reader.setCursor(start);
	                }
	            }
	        }
	        return -1;
	    }

		@Override
		public String getName() {
			return literal;
		}

		@Override
		public String getUsageText() {
			return literal;
		}

		@Override
		public void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
	        final int start = reader.getCursor();
	        final int end = parse(reader);
	        if (end > -1) {
	            contextBuilder.withNode(this, StringRange.between(start, end));
	            return;
	        }

	        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, literal);
		}

		@Override
		public CompletableFuture<Suggestions> listSuggestions(com.mojang.brigadier.context.CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException {
			if(literalLowerCase.startsWith(builder.getRemainingLowerCase())) {
				return builder.suggest(literal).buildFuture();
			}
			return Suggestions.empty();
		}

		@Override
		public BaseCommandNodeBuilder<S> createBuilder() {
			BaseCommandNodeBuilder<S> builder = BaseCommandNodeBuilder.base(literal, description, help);
			for(Entry<Predicate<S>, Consumer<S>> e: builder.getFailActions().entrySet()) {
				builder.requires(e.getKey(), e.getValue());
			}
			builder.forward(getRedirect(), getRedirectModifier(), isFork());
			if(getCommand() != null) {
				builder.executes(getCommand());
			}
			return builder;
		}

		@Override
		protected String getSortedKey() {
			return literal;
		}

		@Override
		public Collection<String> getExamples() {
			return Collections.singleton(literal);
		}
		
		@Override
		public String toString() {
			return "<base " + literal + ">";
		}
		
		@Override
		public int hashCode() {
			int result = literal.hashCode();
			result = 31 * result + super.hashCode();
			return result;
		}
		
	}
    
}
