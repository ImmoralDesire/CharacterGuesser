package me.alithernyx.bot.managers;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.ArrayList;
import java.util.List;

public class TrackSelector {
	public List<AudioTrackInfo> songs = new ArrayList<>();
	public boolean setting = false;

	public AudioTrackInfo getById(int id) {
		return this.songs.get(id);
	}
}
