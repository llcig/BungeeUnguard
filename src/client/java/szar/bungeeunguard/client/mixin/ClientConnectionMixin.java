package szar.bungeeunguard.client.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import szar.bungeeunguard.client.BungeeUnguardClient;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void onChannelRead0(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof BundleS2CPacket bundle) {
            for (Packet<?> p : bundle.getPackets()) {
                handlePacket(p);
            }
        } else {
            handlePacket(packet);
        }
    }

    @Unique
    private void handlePacket(Packet<?> packet) {
        if (packet instanceof LoginSuccessS2CPacket(com.mojang.authlib.GameProfile profile)) {
            String bungeeGuardToken = profile.getProperties().get("bungeeguard-token").toString();
            if (!bungeeGuardToken.contains("bungeeguard-token")) return;

            // print multiple times so it easier to see
            for (int i = 0; i < 16; i++)
                System.out.println("BungeeGuard token FOUND!!!: " + bungeeGuardToken);

            BungeeUnguardClient.runWhenPlayer.add(() -> {
                assert MinecraftClient.getInstance().player != null;

                for (int i = 0; i < 16; i++)
                    MinecraftClient.getInstance().player.sendMessage(Text.of("§aBungeeGuard token FOUND! OPEN LOGS!"), false);
            });
        }
    }
}
