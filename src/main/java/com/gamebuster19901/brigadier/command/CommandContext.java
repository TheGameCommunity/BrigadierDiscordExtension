package com.gamebuster19901.brigadier.command;

public abstract class CommandContext<E> {
	
	protected final E event;
	
	public CommandContext(E e) {
		this.event = e;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getEvent(Class<T> type) throws ClassCastException {	
		if(type.isAssignableFrom(event.getClass())) {
			return (T)event;
		}
		throw new ClassCastException(event + " cannot be cast to " + type.getCanonicalName());
	}
	
}

