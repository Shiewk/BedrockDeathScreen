package de.shiewk.bedrockdeathscreen.mixin;

import de.shiewk.bedrockdeathscreen.client.screen.BedrockDeathScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow
    @Nullable
    public ClientWorld world;

    @Redirect(
            method = "setScreen",
            at = @At(value = "NEW", target = "(Lnet/minecraft/text/Text;Z)Lnet/minecraft/client/gui/screen/DeathScreen;")
    )
    public DeathScreen bedrockdeathscreen$createModifiedDeathScreen(Text message, boolean isHardcore){
        return new BedrockDeathScreen(null, world != null && world.getLevelProperties().isHardcore());
    }

}
