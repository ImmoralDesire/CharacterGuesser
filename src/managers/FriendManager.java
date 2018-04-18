package me.alithernyx.bot.managers;

import net.dv8tion.jda.core.JDA;
import okhttp3.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONObject;

import java.io.IOException;

public class FriendManager {

    protected JDA jda;
    protected final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected OkHttpClient.Builder builder;
    protected OkHttpClient client = new OkHttpClient();

    public FriendManager(JDA jda) {
        this.jda = jda;
    }

    public JDA getJDA() {
        return this.jda;
    }

    public void sendFriendRequest(String userId) throws IOException {
        RequestBody body = RequestBody.create(JSON, new JSONObject().toString());
        Request request = new Request.Builder()
                .header("authorization", this.jda.getToken())
                .url("https://discordapp.com/api/users/@me/relationships/" + userId)
                .put(body).build();

        try (Response response = this.client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }

   public void acceptFriendRequest(String userId) throws IOException {
        RequestBody body = RequestBody.create(JSON, new JSONObject().put("type", 1).toString());
        Request request = new Request.Builder()
                .header("authorization", this.jda.getToken())
                .url("https://discordapp.com/api/users/@me/relationships/" + userId)
                .put(body).build();

        try (Response response = this.client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                        append(jda).
                        toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FriendManager)) {
            System.out.println(obj + " : nigger");
            return false;
        }
        if (obj == this) {
            System.out.println(obj + " : this");
            return true;
        }

        FriendManager rhs = (FriendManager) obj;
        System.out.println(rhs.toString() + " : " + this.toString());
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(jda, rhs.jda).
                        isEquals();
    }

    @Override
    public String toString() {
        return "FriendManager(" + this.jda.getSelfUser().getEmail() + ")";
    }
}
