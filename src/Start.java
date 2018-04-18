package me.alithernyx.bot;

import me.alithernyx.bot.exceptions.BlacklistedExtensionException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Start {

    public GameBot gameBot;

    public Start() {
        this.gameBot = new GameBot();
    }

    public static void main(String[] args) throws LoginException, InterruptedException, RateLimitedException, IOException, BlacklistedExtensionException {
        Start start = new Start();
        start.gameBot.init();
    }
}
