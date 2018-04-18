package me.alithernyx.bot.handlers;

import me.alithernyx.bot.GameBot;
import me.alithernyx.bot.Reference;
import me.alithernyx.bot.commands.Command;
import me.alithernyx.bot.commands.CommandManager;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class CommandHandler {

    public static void handle(MessageReceivedEvent e) {
        String msg = e.getMessage().getContentRaw();

        for (CommandManager cmdMng : GameBot.getInstance().getCommandManager()) {
            for (Command cmd : cmdMng.commands) {
                String[] args = msg.split(" ", 2);

                if (cmd.receiveMsgs) {
                    cmd.receiveMessage(e);
                }
            }
        }

        if (msg.startsWith(Reference.PREFIX) && !e.getMessage().getAuthor().isBot()) {
            //System.out.println(GameBot.getInstance() + " : " + GameBot.getInstance().getCommandManager());
            for (CommandManager cmdMng : GameBot.getInstance().getCommandManager()) {
                if (cmdMng.jda == e.getJDA()) {
                    for (Command cmd : cmdMng.commands) {
                        String[] args = msg.split(" ", 2);

                        if (Arrays.asList(cmd.alias).contains(args[0].substring(Reference.PREFIX.length()))) {

                            if (args.length < 2) {
                                args = new String[]{};
                            } else {
                                args = args[1].split(" ");
                            }

                            cmd.call(msg, args, e);
                        }
                    }
                }
            }
        }
    }
}
