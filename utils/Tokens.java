package me.alithernyx.bot.utils;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class Tokens {

    protected final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected OkHttpClient.Builder builder;
    protected OkHttpClient client;
    protected String email;
    protected String password;

    public Tokens() {
        this(new OkHttpClient.Builder());
    }

    public Tokens(OkHttpClient.Builder builder) {
        this.builder = builder;
        this.client = this.builder.build();
    }


    public Tokens setEmail(String email) {
        this.email = email;
        return this;
    }

    public Tokens setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getToken() throws IOException {
        RequestBody body = RequestBody.create(JSON, new JSONObject()
                .put("email", email)
                .put("password", password)
                .toString());

        Request request = new Request.Builder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36")
                .url("https://discordapp.com/api/auth/login").post(body).build();

        try (Response response = this.client.newCall(request).execute()) {
            String res = response.body().string();
            System.out.println(res);
            return new JSONObject(res).getString("token");
        }
    }

    /*public RestAction<String> getToken() throws IOException {
        Route.CompiledRoute route = Route.Custom.POST_ROUTE.compile("https://discordapp.com/api/auth/login");
        RequestBody body = RequestBody.create(JSON, new JSONObject()
                .put("email", email)
                .put("password", password)
                .toString());

        return new RestAction<String>.(null, route, body) {
            @Override
            protected void handleResponse(Response response, Request<String> request) {
                if(response.isOk()) {
                    System.out.println(response.getString());
                    request.onSuccess(null);
                } else {
                    request.onFailure(response);
                }
            }
        };
    }*/
}
