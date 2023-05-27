package com.gamebuster19901.discord.command;

import java.util.List;

import com.gamebuster19901.brigadier.command.Dispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.Component.Type;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

public class DiscordDispatcher extends Dispatcher<DiscordContext> {

	public int execute(ButtonInteractionEvent e) throws CommandSyntaxException {
		return this.execute(e.getComponentId(), new DiscordContext<ButtonInteractionEvent>(e));
	}

	public int execute(ModalInteractionEvent e) throws CommandSyntaxException {
		List<ModalMapping> arguments = e.getValues();
		StringBuilder command = new StringBuilder(e.getModalId());
		for(ModalMapping arg : arguments) {
			command.append(' ');
			if(arg.getType() == Type.TEXT_INPUT) {
				command.append(arg.getAsString());
			}
			else if (arg.getType() == Type.BUTTON) {
				command.append(arg.getId());
			}
			else if (arg.getType() == Type.STRING_SELECT) {
				command.append(arg.getId());
			}
		}
		System.out.println(command);
		return execute(command.toString(), new DiscordContext<ModalInteractionEvent>(e));
	}

	public int execute(StringSelectInteractionEvent e) throws CommandSyntaxException {
		StringBuilder command = new StringBuilder();
		command.append(e.getSelectMenu().getId());
		command.append(' ');
		command.append(e.getValues().get(0));
		return execute(command.toString(), new DiscordContext<StringSelectInteractionEvent>(e));
	}

}
