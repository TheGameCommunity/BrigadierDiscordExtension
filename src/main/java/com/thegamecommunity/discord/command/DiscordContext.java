package com.thegamecommunity.discord.command;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import com.thegamecommunity.brigadier.command.CommandContext;
import com.thegamecommunity.discord.user.ConsoleUser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.managers.channel.attribute.IPermissionContainerManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class DiscordContext<E> extends CommandContext<E> implements IPermissionContainer {

	private EmbedBuilder embedBuilder = new EmbedBuilder();
	
	public DiscordContext(E e) {
		super(e);
		if(e instanceof MessageReceivedEvent || e instanceof Interaction || e instanceof GuildReadyEvent || e instanceof User || e instanceof Member || e instanceof InteractionHook) {
			//NO-OP
		}
		else {
			throw new IllegalArgumentException(e.getClass().getCanonicalName());
		}
	}
	
	public Member getMember() {
		if(event instanceof Interaction) {
			return ((Interaction) event).getMember();
		}
		if(event instanceof Member) {
			return (Member) event;
		}
		if(event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getMember();
		}
		if(event instanceof InteractionHook) {
			return ((InteractionHook) event).getInteraction().getMember();
		}
		return null;
	}
	
	public User getUser() {
		if(event instanceof Interaction) {
			return ((Interaction) event).getUser();
		}
		if(event instanceof User) {
			return (User) event;
		}
		if(event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getAuthor();
		}
		if(event instanceof InteractionHook) {
			return ((InteractionHook) event).getInteraction().getUser();
		}
		return null;
	}
	
	public Interaction getInteraction() {
		if(event instanceof Interaction) {
			return (Interaction) event;
		}
		if(event instanceof InteractionHook) {
			return ((InteractionHook) event).getInteraction();
		}
		return null;
	}
	
	/**
	 * Checks if the user has all specified permissions in the current discord server (and channel)
	 * 
	 * if the context is the console user, returns true
	 * 
	 * if the context is otherwise null for some reason does not contain a server, returns false
	 * 
	 * @param permissions the permissions to check
	 */
	public boolean hasPermission(Permission... permissions) {
		Member member = getMember();
		
		if(member == null) {
			return false;
		}
		
		return PermissionUtil.checkPermission(member, permissions);
		
	}
	
	/**
	 * Checks if the user has all specified permissions in the current discord server (and channel)
	 * 
	 * if the context is the console user, returns true
	 * 
	 * if the context is otherwise null for some reason does not contain a server, returns false
	 * 
	 * @param permissions the permissions to check
	 */
	public boolean hasPermission(IPermissionContainer channel, Permission... permissions) {
		Member member = getMember();
		
		if(member == null) {
			return false;
		}
		
		return PermissionUtil.checkPermission(channel, member, permissions);
		
	}
	
	public boolean isDiscordContext() {
		return (event instanceof ISnowflake || event instanceof InteractionHook) && !(event instanceof ConsoleUser);
	}
	
	public void queueMessage(MessageCreateData messageData) {
		queueMessage(messageData, false, false);
	}
	
	public void queueMessage(MessageCreateData messageData, boolean ephemeral, boolean silent) {
		if(event instanceof IReplyCallback) {
			((IReplyCallback) event).reply(messageData).setEphemeral(ephemeral).setSuppressedNotifications(silent).queue();
		}
		else if(event instanceof MessageReceivedEvent) {
			((MessageReceivedEvent) event).getChannel().sendMessage(messageData).queue();
		}
		else if(event instanceof User) {
			if(event instanceof ConsoleUser) {
				System.out.println(messageData.getContent());
			}
			else {
				((User)event).openPrivateChannel().queue((att) -> {((PrivateChannel)att).sendMessage(messageData).queue();});
			}
		}
		else if (event instanceof InteractionHook) {
			((InteractionHook) event).editOriginal(MessageEditData.fromCreateData(messageData)).queue();
		}
		else {
			throw new UnsupportedOperationException("Cannot reply to a " + event.getClass().getCanonicalName());
		}
	}
	
	/**
	 * 
	 * @param ephemeral whether the deferred message should be ephemral. Has no effect if this context does not have in interaction hook.
	 * @return a new DiscordContext based on this context's interaction hook, or this if the context does not have an interaction hook.
	 */
	public DiscordContext defer(boolean ephemeral) {
		Interaction interaction = getInteraction();
		if(interaction != null) {
			((IReplyCallback) interaction).deferReply(ephemeral).queue();
			return new DiscordContext<InteractionHook>(((IDeferrableCallback) interaction).getHook());
		}
		return this;
	}
	
	public void queueMessage(MessageCreateData messageData, boolean ephemeral, boolean silent, Consumer<? super Throwable> onFailure) {
		if(event instanceof IReplyCallback) {
			((IReplyCallback) event).reply(messageData).setEphemeral(ephemeral).setSuppressedNotifications(silent).queue();
		}
		else if(event instanceof MessageReceivedEvent) {
			((MessageReceivedEvent) event).getChannel().sendMessage(messageData).queue();
		}
		else if(event instanceof User) {
			if(event instanceof ConsoleUser) {
				System.out.println(messageData.getContent());
			}
			else {
				((User)event).openPrivateChannel().queue((att) -> {((PrivateChannel)att).sendMessage(messageData).queue();});
			}
		}
		else if (event instanceof InteractionHook) {
			((InteractionHook) event).editOriginal(MessageEditData.fromCreateData(messageData)).queue();
		}
		else {
			throw new UnsupportedOperationException("Cannot reply to a " + event.getClass().getCanonicalName());
		}
	}
	
	public void queueMessage(String message) {
		message = trim(message);
		queueMessage(new MessageCreateBuilder().setContent(message).build(), false, false);
	}
	
	public void queueMessage(String message, boolean ephemeral, boolean silent) {
		message = trim(message);
		queueMessage(new MessageCreateBuilder().setContent(message).build(), ephemeral, silent);
	}
	
	public void queueMessage(String message, boolean ephemeral, boolean silent, Consumer<? super Throwable> onFailure) {
		message = trim(message);
		queueMessage(new MessageCreateBuilder().setContent(message).build(), ephemeral, silent, onFailure);
	}
	
	public InteractionHook completeMessage(MessageCreateData messageData) {
		return completeMessage(messageData, false, false);
	}
	
	public InteractionHook completeMessage(MessageCreateData messageData, boolean ephemeral, boolean silent) {
		if(event instanceof IReplyCallback) {
			return ((IReplyCallback) event).reply(messageData).setEphemeral(ephemeral).setSuppressedNotifications(silent).complete();
		}
		else if(event instanceof MessageReceivedEvent) {
			((MessageReceivedEvent) event).getChannel().sendMessage(messageData).complete();
			return null;
		}
		else if(event instanceof User) {
			if(event instanceof ConsoleUser) {
				System.out.println(messageData.getContent());
			}
			else {
				PrivateChannel channel = ((User)event).openPrivateChannel().complete();
				channel.sendMessage(messageData).complete();
			}
			return null;
		}
		else if (event instanceof InteractionHook) {
			((InteractionHook) event).editOriginal(MessageEditData.fromCreateData(messageData)).complete();
			return (InteractionHook) event;
		}
		else {
			throw new UnsupportedOperationException("Cannot reply to a " + event.getClass().getCanonicalName());
		}
	}
	
	public InteractionHook completeMessage(String message) {
		message = trim(message);
		return completeMessage(new MessageCreateBuilder().setContent(message).build(), false, false);
	}
	
	public InteractionHook completeMessage(String message, boolean ephemeral, boolean silent) {
		message = trim(message);
		return completeMessage(new MessageCreateBuilder().setContent(message).build(), ephemeral, silent);
	}
	
	public void editMessage(MessageEditData messageEdit) {
		if(event instanceof IMessageEditCallback) {
			((IMessageEditCallback) event).editMessage(messageEdit);
		}
		else {
			completeMessage(MessageCreateData.fromEditData(messageEdit));
		}
	}
	
	public void editMessage(String message) {
		editMessage(new MessageEditBuilder().closeFiles().clear().setContent(message).build());
	}
	
	public void editMessage(String message, boolean clear) {
		if(!clear) {
			editMessage(new MessageEditBuilder().setContent(message).build());
		}
		else {
			editMessage(message);
		}
	}
	
	public void replyInsufficientPermissions() {
		queueMessage("You do not have permission to execute that command.");
	}
	
	public void replyDiscordOnlyCommand() {
		queueMessage("This command can only be executed in Discord.");
	}
	
	public void replyServerOnlyCommand() {
		queueMessage("This command must be executed in a server!");
	}
	
	public void sendThrowable(Throwable t) {
		IReplyCallback callback = (IReplyCallback) event;
		if(!callback.isAcknowledged()) {
			callback.deferReply().queue();
		}
		callback.getHook().editOriginal(t.toString()).queue();
	}
	
	public EmbedBuilder constructEmbedResponse(String command) {
		return constructEmbedResponse(command, null);
	}
	
	public EmbedBuilder constructEmbedResponse(String command, String title) {
		User user = getUser();
		embedBuilder = new EmbedBuilder();
		embedBuilder.setAuthor(user.getAsTag(), null, user.getAvatarUrl());
		embedBuilder.setTimestamp(Instant.now());
		return embedBuilder;
	}
	
	public MessageChannel getChannel() {
		if(event instanceof Interaction) {
			return ((Interaction) event).getMessageChannel();
		}
		else if (event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getChannel();
		}
		else if (event instanceof InteractionHook) {
			return getInteraction().getMessageChannel();
		}
		return null;
	}
	
	public GuildChannel getGuildChannel() {
		return (GuildChannel) getChannel();
	}
	
	public String getEffectiveName() {
		if(getMember() != null) {
			return getMember().getEffectiveName();
		}
		return getUser().getName();
	}
	
	public String getEffectiveNameOf(User user) {
		if(isGuildContext()) {
			Member member = getServer().getMember(user);
			if(member != null) {
				return member.getEffectiveName();
			}
		}
		return user.getName();
	}
	
	public IReplyCallback getReplyCallback() {
		return (IReplyCallback)event;
	}
	
	public EmbedBuilder getEmbed() {
		return embedBuilder;
	}
	
	public Guild getServer() {
		if(event instanceof Interaction) {
			return ((Interaction) event).getGuild();
		}
		if(event instanceof MessageReceivedEvent) {
			return ((MessageReceivedEvent) event).getGuild();
		}
		return null;
	}
	
	public boolean isGuildContext() {
		return getChannel() instanceof GuildChannel;
	}
	
	public boolean isPrivateContext() {
		return getChannel() instanceof PrivateChannel;
	}
	
	/**
	 * @return a new context in a private message channel with
	 * the current author.
	 * 
	 * If the author has no private context, returns this;
	 * 
	 * If you need to check if the context is a user context, just 
	 * call isPrivateContext() on the returned context
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DiscordContext getPrivateContext() {
		if(getUser() == null || !getUser().hasPrivateChannel()) {
			return this;
		}
		try {
			return new DiscordContext(getUser());
		}
		catch(UnsupportedOperationException e) {
			return this; //just in case
		}
	}
	
	public boolean isConsoleContext() {
		return event instanceof ConsoleUser;
	}

	private String trim(String s) {
		if(s.length() > 2000) {
			s = s.substring(0, 2000);
		}
		return s;
	}

	@Override
	public Guild getGuild() {
		return getServer();
	}

	@Override
	public AuditableRestAction<Void> delete() {
		return (AuditableRestAction<Void>) getChannel().delete();
	}

	@Override
	public IPermissionContainer getPermissionContainer() {
		return this;
	}

	@Override
	public String getName() {
		return getChannel().getName();
	}

	@Override
	public ChannelType getType() {
		return getChannel().getType();
	}

	@Override
	public JDA getJDA() {
		if(event instanceof GenericEvent) {
			return ((GenericEvent) event).getJDA();
		}
		if(event instanceof Interaction) {
			return ((Interaction) event).getJDA();
		}
		if(event instanceof User) {
			return ((User) event).getJDA();
		}
		if(event instanceof InteractionHook) {
			return ((InteractionHook) event).getJDA();
		}
		return null;
	}

	@Override
	public long getIdLong() {
		return ((ISnowflake) event).getIdLong();
	}

	@Override
	public int compareTo(GuildChannel o) {
		return getGuildChannel().compareTo(o);
	}

	@Override
	public IPermissionContainerManager<?, ?> getManager() {
		return getGuildChannel().getPermissionContainer().getManager();
	}

	@Override
	public PermissionOverride getPermissionOverride(IPermissionHolder permissionHolder) {
		return getGuildChannel().getPermissionContainer().getPermissionOverride(permissionHolder);
	}

	@Override
	public List<PermissionOverride> getPermissionOverrides() {
		return getGuildChannel().getPermissionContainer().getPermissionOverrides();
	}

	@Override
	public PermissionOverrideAction upsertPermissionOverride(IPermissionHolder permissionHolder) {
		return getGuildChannel().getPermissionContainer().upsertPermissionOverride(permissionHolder);
	}

}
