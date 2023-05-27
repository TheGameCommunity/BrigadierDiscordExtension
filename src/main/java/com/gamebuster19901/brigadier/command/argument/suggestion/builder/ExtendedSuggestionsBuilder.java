package com.gamebuster19901.brigadier.command.argument.suggestion.builder;

import com.gamebuster19901.brigadier.command.argument.suggestion.AnyStringSuggestion;
import com.gamebuster19901.brigadier.command.argument.suggestion.MatchingStringSuggestion;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;


public class ExtendedSuggestionsBuilder extends SuggestionsBuilder {

	public ExtendedSuggestionsBuilder(SuggestionsBuilder builder) {
		this(builder.getInput(), builder.getStart());
	}
	
	public ExtendedSuggestionsBuilder(String input, int start) {
		super(input, start);
	}
	
	@Override
	public ExtendedSuggestionsBuilder suggest(final Suggestion suggestion) {
		return (ExtendedSuggestionsBuilder) super.suggest(suggestion); //super always returns this
	}
	
	public <S> ExtendedSuggestionsBuilder suggestAnyString(String name) {
		return suggest(new AnyStringSuggestion(getDefaultRange(), "<" + name + ">"));
	}
	
	public <S> ExtendedSuggestionsBuilder suggestAnyString(String name, Message tooltip) {
		return suggest(new AnyStringSuggestion(getDefaultRange(), "<" + name + ">", tooltip));
	}
	
	public <S> ExtendedSuggestionsBuilder suggestAsMatchable(String text) {
		return suggest(new MatchingStringSuggestion(getDefaultRange(), text));
	}
	
	protected StringRange getDefaultRange() {
		return StringRange.between(getStart(), getInput().length());
	}

}
