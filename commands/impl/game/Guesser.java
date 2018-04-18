package me.alithernyx.bot.commands.impl.game;

import me.alithernyx.bot.commands.Cmd;
import me.alithernyx.bot.commands.Command;
import me.alithernyx.bot.commands.category.IGame;
import me.alithernyx.bot.managers.CharacterLoader;
import me.alithernyx.bot.utils.ImageUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Cmd(alias = {"guess"}, description = "Guesses idk", syntax = "<>", receiveMsgs = true)
public class Guesser extends Command implements IGame {

    public boolean playing = false;
    public boolean waiting = false;
    public MessageChannel channel = null;
    public long cooldown = 5000;
    public HashMap<String, String> imageList = new HashMap<>();

    public Guesser() {
        File path = new File("resources/imgs/");
        imageList = new CharacterLoader(path.getAbsolutePath()).loadCharacters();
    }

    @Override
    public void perform(MessageReceivedEvent e) {
        if(this.args.length > 0) {
            if (this.args[0] == "reload") {
                imageList.clear();
                imageList = new CharacterLoader("").loadCharacters();
                return;
            }
        }
        this.playing = !this.playing;
        if(this.playing) {
            this.waiting = true;
            this.channel = e.getChannel();
            e.getChannel().sendMessage("->game character").queue();
        } else {
            this.waiting = false;
        }
    }

    @Override
    public void receiveMessage(MessageReceivedEvent e) {
        if(e.getAuthor() == e.getJDA().getSelfUser()) return;

        System.out.println("HI +" + this.playing + " : " + this.waiting);
        if(this.playing) {
            System.out.println("play and waiting");
            List<Message> msgs = this.channel.getHistory().retrievePast(1).complete();

            for(Message msg : msgs) {
                if (msg.getEmbeds().size() > 0) {
                    MessageEmbed embed = msg.getEmbeds().get(0);
                    String base64 = "";
                    try {
                        base64 = ImageUtils.encodeURLToBase64(embed.getImage().getUrl(), false);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    System.out.println(base64);
                    this.channel.sendMessage(getName(base64)).queue();
                    this.channel.sendMessage("->game character").queueAfter(5000, TimeUnit.MILLISECONDS);
                } else if(msg.getContentStripped().contains("You'll be able")) {
                    //this.channel.sendMessage("->game character").queueAfter(2000, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    public String getName(String base64) {
        for (Map.Entry<String, String> img : imageList.entrySet()) {
            if (img.getKey().equals(base64)) {
                return FilenameUtils.removeExtension(img.getValue());
            }
        }

        return "End";
    }
}
