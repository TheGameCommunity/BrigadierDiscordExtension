package com.thegamecommunity.discord.event.receiver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.thegamecommunity.discord.command.DiscordContext;
import com.thegamecommunity.discord.command.DiscordDispatcher;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class DefaultDiscordEventReceiver extends ListenerAdapter {
	
	private final Sub sub = new Sub();
	private final DiscordDispatcher<DiscordContext> dispatcher;
	private final boolean development;
	
	public DefaultDiscordEventReceiver(DiscordDispatcher dispatcher, boolean development) {
		this.dispatcher = dispatcher;
		this.development = development;
	}
	
	@Override
	public void onGenericEvent(GenericEvent ge) {
		
		//you can do some logic here if necessary before the event is processed if necessary (ex: logging)
		sub.onEvent(ge);
	}
	
	protected DiscordContext createContext(Object o) {
		return new DiscordContext(o);
	}
	
	protected class Sub extends ListenerAdapter {
		@Override
		public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
			CompletableFuture.runAsync(() -> {
				StringBuilder c = new StringBuilder(e.getName());
				for(OptionMapping arg : e.getOptions()) {
					c.append(' ');
					c.append(arg.getAsString().trim());
				}
				DiscordContext context = createContext(e);
				try {
					dispatcher.execute(c.toString() , context);
				} catch (Throwable t) {
					if(t.getMessage() != null && !t.getMessage().isBlank()) {
						context.sendThrowable(t);
					}
					else {
						context.sendThrowable(t);
					}
					if(!(t instanceof CommandSyntaxException)) {
						throw new RuntimeException(t);
					}
				}
			});
		}
		
		
		@Override
		public void onButtonInteraction(ButtonInteractionEvent e) {
			CompletableFuture.runAsync(() -> {
				try {
					dispatcher.execute(e);
				} catch (CommandSyntaxException e1) {
					e1.printStackTrace();
				}
			});
		}
		
		@Override
		public void onModalInteraction(ModalInteractionEvent e) {
			CompletableFuture.runAsync(() -> {
				try {
					dispatcher.execute(e);
				} catch (CommandSyntaxException e1) {
					e1.printStackTrace();
				}
			});
		}
		
		@Override
		public void onStringSelectInteraction(StringSelectInteractionEvent e) {
			CompletableFuture.runAsync(() -> {
				try {
					dispatcher.execute(e);
				} catch (CommandSyntaxException e1) {
					e1.printStackTrace();
				}
			});
		}
		
		@Override
		public void onGuildReady(GuildReadyEvent e) { //for development
			List<CommandData> commands = new ArrayList<>();
			dispatcher.getRoot().getChildren().forEach((command) -> {
				if(!development) {
					return; //Don't register commands as guild commands if we're not in a dev environment
				}
				SlashCommandData data = net.dv8tion.jda.api.interactions.commands.build.Commands.slash(command.getName(), command.getUsageText());
				
				Command<DiscordContext> base = (Command<DiscordContext>) command.getCommand();
				
				if(command.getChildren().size() > 0) {
					if(command.getChildren().size() == 1) {
						data.addOption(OptionType.STRING, "arguments", "arguments", base == null, true); //todo: regular discord args?
					}
					else {
						data.addOption(OptionType.STRING, "arguments", "arguments", base == null, true);
					}
					
					/*
					for(CommandNode node : command.getChildren()) {
						System.out.println(node);
					}
					*/
				}
				
				commands.add(data);
			});
			
			e.getGuild().updateCommands().addCommands(commands).queue();
		}
		
		@Override
		public void onReady(ReadyEvent e) {
			List<CommandData> commands = new ArrayList<>();
			dispatcher.getRoot().getChildren().forEach((command) -> {
				if(development) {
					return; //Don't register commands as global commands if we're in a dev environment
				}
				SlashCommandData data = net.dv8tion.jda.api.interactions.commands.build.Commands.slash(command.getName(), command.getUsageText());
				
				Command<DiscordContext> base = (Command<DiscordContext>) command.getCommand();
				
				if(command.getChildren().size() > 0) {
					if(command.getChildren().size() == 1) {
						data.addOption(OptionType.STRING, "arguments", "arguments", base == null, true); //todo: regular discord args?
					}
					else {
						data.addOption(OptionType.STRING, "arguments", "arguments", base == null, true);
					}
					
					/*
					for(CommandNode node : command.getChildren()) {
						System.out.println(node);
					}
					*/
				}
				
				commands.add(data);
			});
			
			e.getJDA().updateCommands().addCommands(commands).queue();
		}
		
		@Override
		public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {
			CompletableFuture.runAsync(() -> { //needed so discord user suggestions don't break, can't run on JDA even thread
				String command = e.getName() + " " + e.getFocusedOption().getValue();
				final String arguments = e.getFocusedOption().getValue();
				String fixedArguments = e.getFocusedOption().getValue();
				
				boolean spaceAdded = false;
				
				if(fixedArguments.indexOf(' ') != -1) {
					fixedArguments = fixedArguments.substring(0, fixedArguments.lastIndexOf(' '));
				}
				else {
					fixedArguments = "";
				}
				ParseResults<DiscordContext> parseResults = dispatcher.parse(command, createContext(e));
				List<Suggestion> suggestions;
				List<String> returnedSuggestions = new ArrayList<String>();
				try {
					suggestions = dispatcher.getCompletionSuggestions(parseResults, command.length()).get().getList();
				} catch (InterruptedException | ExecutionException ex) {
					ex.printStackTrace();
					return;
				}
				if(suggestions.size() > 25) {
					suggestions = suggestions.subList(0, 25);
				}

				for(Suggestion suggestion : suggestions) {

					if(!spaceAdded) {
						returnedSuggestions.add(fixedArguments + " " + suggestion.getText());
					}
					else {
						returnedSuggestions.add(fixedArguments + arguments + " " + suggestion.getText());
					}
				}
				e.replyChoiceStrings(returnedSuggestions).queue();
			});
		}
	}
	
}
