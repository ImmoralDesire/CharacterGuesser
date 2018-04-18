package me.alithernyx.bot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import me.alithernyx.bot.commands.CommandManager;
import me.alithernyx.bot.exceptions.BlacklistedExtensionException;
import me.alithernyx.bot.handlers.AudioReceiver;
import me.alithernyx.bot.listeners.BotListener;
import me.alithernyx.bot.managers.*;
import me.alithernyx.bot.utils.Tokens;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameBot {

    public List<CommandManager> commandManager = new ArrayList<>();
    public List<FriendManager> friendManager = new ArrayList<>();
    public List<InviteManager> inviteManager = new ArrayList<>();
    public List<UserManager> userManager = new ArrayList<>();
    public List<CallManager> callManager = new ArrayList<>();
    public List<UserData> userList = new ArrayList<>();
    public AudioPlayerManager playerManager;
    public HashMap<Long, GuildMusicManager> musicManagerList = new HashMap<>();
    public HashMap<Long, AudioReceiver> audioReceiverList = new HashMap<>();
    public HashMap<JDA, UserData> jdaList = new HashMap<>();
    public static GameBot instance;

    public GameBot() {
        instance = this;
    }

    public void preInit() throws LoginException, InterruptedException, RateLimitedException, IOException, BlacklistedExtensionException {
        //for(String[] userData : info) {
            //UserData user = new UserData(userData[0], userData[1]);
            //this.userList.add(user);
        //}

        this.userList = new BotLoader("/bots.txt").getAsUserdata();
    }

    public void init() throws LoginException, InterruptedException, RateLimitedException, IOException, BlacklistedExtensionException {
        this.preInit();

        for(UserData user : this.userList) {
            String token = new Tokens()
                    .setEmail(user.getEmail())
                    .setPassword(user.getPassword())
                    .getToken();

            JDA jda = new JDABuilder(AccountType.CLIENT)
                    .setToken(token)
                    .addEventListener(new BotListener())
                    .setGame(Game.of(Game.GameType.DEFAULT, "idk"))
                    .setAutoReconnect(true)
                    .buildBlocking();

            this.jdaList.put(jda, user);

            Thread.sleep(1000);
        }

        this.postInit();
    }

    public void postInit() throws LoginException, InterruptedException, RateLimitedException, IOException, BlacklistedExtensionException {
        for(Map.Entry<JDA, UserData> set : this.jdaList.entrySet()) {
            CommandManager cmd = new CommandManager(set.getKey());
            FriendManager frd = new FriendManager(set.getKey());
            InviteManager inv  = new InviteManager(set.getKey());
            UserManager usr = new UserManager(set.getKey(), set.getValue());
            CallManager cal = new CallManager(set.getKey());

            this.commandManager.add(cmd);
            this.friendManager.add(frd);
            this.inviteManager.add(inv);
            this.userManager.add(usr);
            this.callManager.add(cal);

            Thread.sleep(1000);
        }
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(this.playerManager);
    }

    public AudioReceiver getAudioReceiver(Guild guild) {
        Long id = guild.getIdLong();
        AudioReceiver audioReceiver = this.audioReceiverList.get(id);

        if (audioReceiver == null) {
            audioReceiver = new AudioReceiver(guild);
            this.audioReceiverList.put(id, audioReceiver);
        }

        return audioReceiver;
    }

    public synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = this.musicManagerList.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(this.playerManager, guild);
            this.musicManagerList.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public static GameBot getInstance() { return instance; }

    public List<CommandManager> getCommandManager() { return this.commandManager; }

    public List<FriendManager> getFriendManager() { return this.friendManager; }

    public List<InviteManager> getInviteManager() { return this.inviteManager; }

    public List<UserManager> getUserManager() { return this.userManager; }

    public List<CallManager> getCallManager() { return this.callManager; }

    public AudioPlayerManager getPlayerManager() { return this.playerManager; }

    public JDA getJDAByUsername(String username) {
        for(JDA jda : this.jdaList.keySet()) {
            if(jda.getSelfUser().getName().equalsIgnoreCase(username)) {
                return jda;
            }
        }

        return null;
    }
}
