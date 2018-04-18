package me.alithernyx.bot.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface ICommand {

	String[] alias();

	String description();

	String[] syntax();

	boolean reqArgs();

	boolean receiveMsgs();

	boolean call(String raw, String args[], MessageReceivedEvent e);
	
	void perform(MessageReceivedEvent e);
	
	boolean isValid();
}
