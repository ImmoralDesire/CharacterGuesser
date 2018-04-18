package me.alithernyx.bot.commands.impl.game;

import me.alithernyx.bot.commands.Cmd;
import me.alithernyx.bot.commands.Command;
import me.alithernyx.bot.commands.category.IGame;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@Cmd(alias = "type", description = "", reqArgs = false)
public class Typer extends Command implements IGame {

    protected MessageChannel channel;

    @Override
    public boolean isValid() {
        return super.isValid();
    }

    @Override
    public void perform(MessageReceivedEvent e) {
        e.getChannel().getHistory().retrievePast(10).complete().stream().forEach((m) -> {
            if(m.getAuthor().getName().equalsIgnoreCase("Sebastian")) {
                String stripped = m.getContentRaw().replaceAll("\\u200b", "");
                m.getChannel().sendMessage(stripped).queue();
            }
        });
    }

    @Override
    public void receiveMessage(MessageReceivedEvent e) {

    }
}
