package me.alithernyx.bot.schedulers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Guild;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final List<AudioTrack> playlist;
    private final BlockingQueue<AudioTrack> queue;
    private final Guild guild;
    public boolean repeat;


    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.repeat = false;
        this.player = player;
        this.playlist = new ArrayList<>();
        this.queue = new LinkedBlockingQueue<>();
        this.guild = guild;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the
     * queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the
        // track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case
        // the player was already playing so this
        // track goes to the queue instead.
        this.playlist.add(track);
        if (!this.player.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing
        // or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply
        // stop the player.
        this.player.startTrack(this.queue.poll(), false);
    }

    public boolean isRepeat() {
        return this.repeat;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it
        // (FINISHED or LOAD_FAILED)

        System.out.println(this.repeat + " : " + endReason.mayStartNext + " : " + endReason);
        if (this.repeat && this.queue.isEmpty()) {
            for (AudioTrack t : this.playlist) {
                t.makeClone();
                this.queue.add(t.makeClone());
            }
            System.out.println(this.playlist.toString());
        }

        if (!this.repeat && this.queue.isEmpty() && endReason != AudioTrackEndReason.REPLACED) {
            //this.guild.getAudioManager().closeAudioConnection();
        }

        if (endReason.mayStartNext && endReason == AudioTrackEndReason.FINISHED) {
            nextTrack();
        }
    }
}
