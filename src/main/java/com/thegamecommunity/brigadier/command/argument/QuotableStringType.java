package com.thegamecommunity.brigadier.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.thegamecommunity.brigadier.command.CommandUtils;

public class QuotableStringType implements ArgumentType<String> {

	public static final QuotableStringType TYPE = new QuotableStringType();
	
	private QuotableStringType() {}
	
	@Override
	public <S> String parse(S context, StringReader reader) throws CommandSyntaxException {
		if(reader.peek() == '"') {
			return CommandUtils.readQuotedString(reader);
		}
		return CommandUtils.readString(reader);
	}

}
