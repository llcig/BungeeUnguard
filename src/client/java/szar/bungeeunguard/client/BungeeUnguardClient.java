package szar.bungeeunguard.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.concurrent.CopyOnWriteArrayList;

public class BungeeUnguardClient implements ClientModInitializer {

//    public static final String MOD_ID = "bungeeunguard";
    public static final CopyOnWriteArrayList<Runnable> runWhenPlayer = new CopyOnWriteArrayList<>();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                for (Runnable runnable : runWhenPlayer) {
                    runnable.run();
                    runWhenPlayer.remove(runnable);
                }
            }
        });
    }
}
