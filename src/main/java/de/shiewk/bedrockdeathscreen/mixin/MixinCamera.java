package de.shiewk.bedrockdeathscreen.mixin;

import de.shiewk.bedrockdeathscreen.client.screen.BedrockDeathScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public class MixinCamera {

    @Shadow private float lastTickDelta;

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getYaw(F)F"))
    public float onYawGet(Entity instance, float tickDelta){
        if (MinecraftClient.getInstance().currentScreen instanceof BedrockDeathScreen bedrockDeathScreen){
            return bedrockDeathScreen.calcCamYaw();
        } else {
            return instance.getYaw(tickDelta);
        }
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPitch(F)F"))
    public float onPitchGet(Entity instance, float tickDelta){
        if (MinecraftClient.getInstance().currentScreen instanceof BedrockDeathScreen bedrockDeathScreen){
            return bedrockDeathScreen.calcCamPitch();
        } else {
            return instance.getPitch(tickDelta);
        }
    }

    @ModifyConstant(method = "update", constant = @Constant(floatValue = 4.0f))
    public float onUpdateThirdPerson(float constant){
        final MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof BedrockDeathScreen bedrockDeathScreen){
            final float delta = client.getTickDelta();
            return bedrockDeathScreen.calcCameraOffset(delta);
        } else {
            return 4.0f;
        }
    }
}
