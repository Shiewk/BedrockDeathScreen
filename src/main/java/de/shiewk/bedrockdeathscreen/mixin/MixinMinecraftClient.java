package de.shiewk.bedrockdeathscreen.mixin;

import de.shiewk.bedrockdeathscreen.client.screen.BedrockDeathScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow @Nullable public ClientWorld world;

    @ModifyVariable(
            method = "setScreen",
            at = @At("STORE"),
            name = "screen",
            index = 1,
            argsOnly = true
    )
    public Screen onCreateDeathScreen(Screen value){
        assert world != null; // Always true
        return new BedrockDeathScreen(null, world.getLevelProperties().isHardcore());
    }

}
