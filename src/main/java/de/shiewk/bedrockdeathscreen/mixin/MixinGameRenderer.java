package de.shiewk.bedrockdeathscreen.mixin;

import de.shiewk.bedrockdeathscreen.client.screen.BedrockDeathScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow @Final MinecraftClient client;

    @Shadow private float fovMultiplier;

    @Shadow public abstract void tick();

    @Shadow protected abstract void renderHand(Camera camera, float tickDelta, Matrix4f matrix4f);

    @Inject(at = @At("TAIL"), method = "getFov", cancellable = true)
    public void onFovGet(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir){
        if (client.currentScreen instanceof BedrockDeathScreen bedrockDeathScreen){
            cir.setReturnValue((double) Math.min(60 + bedrockDeathScreen.getTotalDelta(client.getTickDelta()) / (405d), 80));
        }
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V"))
    public void onCameraUpdate(Camera instance, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta){
        if (client.currentScreen instanceof BedrockDeathScreen bedrockDeathScreen){
            instance.update(area, focusedEntity, true, false, tickDelta);
        } else {
            instance.update(area, focusedEntity, thirdPerson, inverseView, tickDelta);
        }
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/render/Camera;FLorg/joml/Matrix4f;)V"))
    public void onCameraUpdate(GameRenderer instance, Camera camera, float tickDelta, Matrix4f matrix4f){
        if (!(instance.getClient().currentScreen instanceof BedrockDeathScreen)){
            renderHand(camera, tickDelta, matrix4f);
        }
    }
}
