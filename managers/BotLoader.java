package me.alithernyx.bot.managers;

import me.alithernyx.bot.UserData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BotLoader {

    protected String path;

    public BotLoader(String path) {
        this.path = path;
    }

    public List<UserData> getAsUserdata() throws IOException {
        List<UserData> list = new ArrayList<>();
        InputStream is = this.getClass().getResourceAsStream(this.path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        for(String l; (l = br.readLine()) != null;) {
            if(l.startsWith(";")) continue;

            String[] a = l.split(":");

            UserData userData = new UserData(a[0], a[1]);
            list.add(userData);
        }

        return list;
    }
}
