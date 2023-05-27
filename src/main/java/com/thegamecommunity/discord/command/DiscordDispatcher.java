package com.thegamecommunity.discord.command;

import java.util.List;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.thegamecommunity.brigadier.command.Dispatcher;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.Component.Type;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

public abstract class DiscordDispatcher<S extends DiscordContext> extends Dispatcher<S> {

	public abstract S newContext(Object o);
	
	public int execute(ButtonInteractionEvent e) throws CommandSyntaxException {
		return this.execute(e.getComponentId(), newContext(e));
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
		return execute(command.toString(), newContext(e));
	}

	public int execute(StringSelectInteractionEvent e) throws CommandSyntaxException {
		StringBuilder command = new StringBuilder();
		command.append(e.getSelectMenu().getId());
		command.append(' ');
		command.append(e.getValues().get(0));
		return execute(command.toString(), newContext(e));
	}

}
