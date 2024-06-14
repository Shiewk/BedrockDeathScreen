package de.shiewk.bedrockdeathscreen.mixin;

import com.mojang.brigadier.ParseResults;
import de.shiewk.bedrockdeathscreen.client.screen.BedrockDeathScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow private ClientWorld world;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"), method = "onDeathMessage", cancellable = true)
    private void onDeathScreenShow(DeathMessageS2CPacket packet, CallbackInfo ci){
        MinecraftClient.getInstance().setScreen(new BedrockDeathScreen(packet.message(), this.world.getLevelProperties().isHardcore()));
        ci.cancel();
    }
}
