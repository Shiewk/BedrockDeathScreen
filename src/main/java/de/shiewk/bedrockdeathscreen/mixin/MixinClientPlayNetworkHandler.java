package de.shiewk.bedrockdeathscreen.mixin;

import de.shiewk.bedrockdeathscreen.client.screen.BedrockDeathScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow private ClientLevel level;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"), method = "handlePlayerCombatKill", cancellable = true)
    private void onDeathScreenShow(ClientboundPlayerCombatKillPacket packet, CallbackInfo ci){
        Minecraft.getInstance().setScreen(new BedrockDeathScreen(packet.message(), this.level.getLevelData().isHardcore(), Minecraft.getInstance().player));
        ci.cancel();
    }
}
