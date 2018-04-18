package me.alithernyx.bot.managers;

import me.alithernyx.bot.commands.CommandManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.Route;
import org.json.JSONObject;

public class CallManager {

    protected JDA jda;

    public CallManager(JDA jda) {
        this.jda = jda;
    }

    public JDA getJDA() {
        return this.jda;
    }

    public RestAction<Void> joinCall(final String callId) {
        final Route.CompiledRoute route = Route.Channels.START_CALL.compile(callId);

        return new RestAction<Void>(getJDA(), route, new JSONObject()) {

            @Override
            protected void handleResponse(Response response, Request<Void> request) {
                if(response.isOk()) {
                    System.out.println(response.getString());
                    request.onSuccess(null);
                } else {
                    request.onFailure(response);
                }
            }
        };
    }

    public RestAction<Void> leaveCall(final String callId) {
        final Route.CompiledRoute route = Route.Channels.STOP_CALL.compile(callId);

        return new RestAction<Void>(getJDA(), route) {

            @Override
            protected void handleResponse(Response response, Request<Void> request) {
                if(response.isOk()) {
                    System.out.println(response.getString());
                    request.onSuccess(null);
                } else {
                    request.onFailure(response);
                }
            }
        };
    }

    @Override
    public int hashCode() {
        return this.jda.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CommandManager) {
            CommandManager cmd = (CommandManager) obj;
            return this.jda == cmd.jda;
        }

        return false;
    }
}
