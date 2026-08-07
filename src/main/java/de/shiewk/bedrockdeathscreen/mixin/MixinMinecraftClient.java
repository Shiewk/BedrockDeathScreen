package de.shiewk.bedrockdeathscreen.mixin;

import de.shiewk.bedrockdeathscreen.client.screen.BedrockDeathScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class MixinMinecraftClient {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(
            method = "setScreen",
            at = @At(value = "NEW", target = "(Lnet/minecraft/network/chat/Component;ZLnet/minecraft/client/player/LocalPlayer;)Lnet/minecraft/client/gui/screens/DeathScreen;")
    )
    public DeathScreen bedrockdeathscreen$createModifiedDeathScreen(Component causeOfDeath, boolean hardcore, LocalPlayer player){
        return new BedrockDeathScreen(causeOfDeath, minecraft.level != null && minecraft.level.getLevelData().isHardcore(), player);
    }

}
