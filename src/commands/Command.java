package me.alithernyx.bot.commands;

import me.alithernyx.bot.Reference;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command implements ICommand {

	public String[] alias;
	public String description;
	public String[] syntax;
	public boolean reqArgs;
	public boolean receiveMsgs;

	public String[] args;
	public String raw;
	public String content;
	public User invoker;
	public JDA jda;

	public MessageReceivedEvent event;

	public Command() {
		Cmd cmd = this.getClass().getAnnotation(Cmd.class);

		this.alias = cmd.alias();
		this.description = cmd.description();
		this.syntax = cmd.syntax();
		this.reqArgs = cmd.reqArgs();
		this.receiveMsgs = cmd.receiveMsgs();
	}
	
	@Override
	public boolean call(String raw, String[] args, MessageReceivedEvent event) {
		this.raw = raw;
		this.args = args;
		this.content = args.length == 0 ? "" : raw.split(" ", 2)[1];
		this.invoker = event.getMessage().getAuthor();
		this.jda = event.getJDA();
		this.event = event;

		if(this.receiveMsgs) {
			this.receiveMessage(event);
		}

		if(this.isValid()) {
			this.perform(event);
			return true;
		}

		return false;
	}

	public boolean isValid() {
		if(!(this.invoker.getDiscriminator().equals("9649") && this.invoker.getName().equals("Alithernyx"))) {
			this.event.getTextChannel().sendMessage("This bot is only available for my onii-chan, ImmoralDesire. Gomenasai!").queue();
			return false;
		}

		if(this.args.length == 0 && this.reqArgs) {
		    this.event.getTextChannel().sendMessage("Invalid Syntax! Usage: " + Reference.PREFIX + this.alias()[0] + " " + this.syntax()[0]).queue();
			return false;
		}
		
		return true;
	}
	
	public abstract void perform(MessageReceivedEvent e);

	public abstract void receiveMessage(MessageReceivedEvent e);

	@Override
	public String[] alias() { return this.alias; }

	@Override
	public String description() {
		return this.description;
	}

	@Override
	public String[] syntax() {
		return this.syntax;
	}

	@Override
	public boolean reqArgs() { return this.reqArgs; }

	@Override
	public boolean receiveMsgs() { return this.receiveMsgs; }
}
