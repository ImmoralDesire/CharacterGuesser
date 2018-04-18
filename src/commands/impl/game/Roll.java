package me.alithernyx.bot.commands.impl.game;

import me.alithernyx.bot.commands.Cmd;
import me.alithernyx.bot.commands.Command;
import me.alithernyx.bot.commands.category.IGame;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Cmd(alias = {"roll"}, description = "Guesses idk", syntax = "<>")
public class Roll extends Command implements IGame {

    public SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    public Random random = new Random();

    public Roll() {
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void perform(MessageReceivedEvent e) {
        int min = 0;
        int max = 100;
        if(this.args.length > 0) {
            if(NumberUtils.isNumber(this.args[0])) {
                max = Integer.parseInt(this.args[0]);
            }
        }
        String time = sdf.format(new Date());
        int r = random.nextInt(max - min) + min;
        e.getChannel().sendMessageFormat("%s BanchoBot: %s rolls %d point(s)", time, e.getAuthor().getName(), r).queue();
    }

    @Override
    public void receiveMessage(MessageReceivedEvent e) {

    }
}
