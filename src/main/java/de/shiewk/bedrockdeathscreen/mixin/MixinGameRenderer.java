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

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix);

    @Inject(at = @At("TAIL"), method = "getFov", cancellable = true)
    public void onFovGet(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir){
        if (client.currentScreen instanceof BedrockDeathScreen bedrockDeathScreen){
            cir.setReturnValue(Math.min(60 + bedrockDeathScreen.getTotalScreenTime() / (405f), 80));
        }
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V"))
    public void onCameraUpdate(Camera instance, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta){
        if (client.currentScreen instanceof BedrockDeathScreen){
            instance.update(area, focusedEntity, true, false, tickDelta);
        } else {
            instance.update(area, focusedEntity, thirdPerson, inverseView, tickDelta);
        }
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(FZLorg/joml/Matrix4f;)V"))
    public void onCameraUpdate(GameRenderer instance, float tickProgress, boolean sleeping, Matrix4f positionMatrix){
        if (!(instance.getClient().currentScreen instanceof BedrockDeathScreen)){
            renderHand(tickProgress, sleeping, positionMatrix);
        }
    }
}
