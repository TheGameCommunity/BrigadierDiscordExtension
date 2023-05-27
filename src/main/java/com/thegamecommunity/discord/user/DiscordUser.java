package com.thegamecommunity.discord.user;

import net.dv8tion.jda.api.entities.User;

public class DiscordUser {

	public static String getDetailedString(User user) {
		return user.getAsTag() + "(" + user.getIdLong() + ")";
	}
	
}
