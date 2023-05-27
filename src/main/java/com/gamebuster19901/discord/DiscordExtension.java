package com.gamebuster19901.discord;

import net.dv8tion.jda.api.JDA;

public abstract class DiscordExtension {

	private static JDA jda;
	
	public static void init(JDA jda) {
		DiscordExtension.jda = jda;
	}
	
	public static JDA getJDA() {
		if(jda != null) {
			return jda;
		}
		throw new IllegalStateException("DiscordJDAExtension not initialized! You must call DiscordExtension.init()");
	}
	
}
