package me.alithernyx.bot.managers;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

public class InviteManager {

    protected JDA jda;
    protected OkHttpClient client = new OkHttpClient();

    public InviteManager(JDA jda) {
        this.jda = jda;
    }

    public JDA getJDA() {
        return this.jda;
    }

    public void leaveGuild(final String guildId) throws IOException {
        Request request = new Request.Builder()
                .url("https://discordapp.com/api/users/@me/guilds/" + guildId)
                .header("authorization", this.getJDA().getToken())
                .delete().build();

        try (Response response = this.client.newCall(request).execute()) {
            String res = response.body().string();
        }
    }

    public Guild acceptInvite(final String code) {
        RequestBody body = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder()
                .url("https://discordapp.com/api/invite/" + code)
                .header("authorization", this.getJDA().getToken())
                .post(body).build();

        try (Response response = this.client.newCall(request).execute()) {
            String res = response.body().string();
            System.out.println(res);

            JDAImpl api = (JDAImpl) jda;
            JSONObject g = new JSONObject(res).getJSONObject("guild");

            GuildImpl guild = new GuildImpl(api, g.getLong("id"));
            guild.setName(g.getString("name"));
            guild.setVerificationLevel(Guild.VerificationLevel.fromKey(g.getInt("verification_level")));

            /*TextChannelImpl channel = new TextChannelImpl(new JSONObject(res).getJSONObject("channel").getLong("id"), guild);
            api.getEntityBuilder().handleGuildSync(guild);
            guild.setSystemChannel(channel);

            api.getEventManager().handle(new GuildJoinEvent(api, 1, guild));*/

            return new GuildImpl((JDAImpl) getJDA(), new JSONObject(res).getJSONObject("guild").getLong("id"));
        } catch(Exception e) {
            e.printStackTrace();
        }

        return new GuildImpl((JDAImpl) jda, 0);
    }
}
