package me.alithernyx.bot;

public class UserData {

    private final String email;
    private final String password;

    public UserData(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }
}
