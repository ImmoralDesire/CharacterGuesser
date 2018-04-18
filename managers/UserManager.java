package me.alithernyx.bot.managers;

import me.alithernyx.bot.UserData;
import me.alithernyx.bot.exceptions.BlacklistedExtensionException;
import me.alithernyx.bot.utils.ImageUtils;
import net.dv8tion.jda.core.JDA;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class UserManager {

    protected JDA jda;
    protected UserData user;
    protected MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected OkHttpClient client = new OkHttpClient();

    public UserManager(JDA jda, UserData user) {
        this.jda = jda;
        this.user = user;
    }

    public JDA getJDA() {
        return this.jda;
    }

    public void setProfilePic(String image, boolean stream) throws IOException, BlacklistedExtensionException {
        String uri = ImageUtils.encodeURLToBase64(image, stream
        );

        RequestBody body = RequestBody.create(JSON, new JSONObject()
                .put("avatar", uri)
                .toString());
        Request request = new Request.Builder()
                .url("https://discordapp.com/api/users/@me")
                .header("authorization", getJDA().getToken())
                .patch(body).build();
        System.out.println(uri);

        try (Response response = this.client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }

    public void setName(String name) throws IOException {
        RequestBody body = RequestBody.create(JSON, new JSONObject()
                .put("username", name)
                .put("password", this.user.getPassword())
                .toString());
        Request request = new Request.Builder()
                .url("https://discordapp.com/api/users/@me")
                .header("authorization", getJDA().getToken())
                .patch(body).build();

        try(Response response = this.client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }

    public void setPicAndName(String name, String image, boolean stream) throws IOException, BlacklistedExtensionException {
        String uri = ImageUtils.encodeURLToBase64(image, stream);

        RequestBody body = RequestBody.create(JSON, new JSONObject()
                .put("avatar", uri)
                .put("username", name)
                .put("password", this.user.getPassword())
                .toString());
        Request request = new Request.Builder()
                .url("https://discordapp.com/api/users/@me")
                .header("authorization", getJDA().getToken())
                .patch(body).build();
        System.out.println(uri);

        try(Response response = this.client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }
}
