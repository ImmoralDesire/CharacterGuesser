package me.alithernyx.bot.commands;

import me.alithernyx.bot.commands.impl.game.Guesser;
import me.alithernyx.bot.commands.impl.game.Roll;
import me.alithernyx.bot.commands.impl.game.Typer;
import net.dv8tion.jda.core.JDA;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    public JDA jda;
    public List<Command> commands = new ArrayList<Command>();

    public CommandManager(JDA jda) {
        this.jda = jda;

        this.commands.add(new Typer());
        this.commands.add(new Guesser());
        this.commands.add(new Roll());
    }
}
