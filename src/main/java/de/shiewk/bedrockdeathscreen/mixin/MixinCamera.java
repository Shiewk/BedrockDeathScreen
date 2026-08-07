package de.shiewk.bedrockdeathscreen.mixin;

import de.shiewk.bedrockdeathscreen.client.screen.BedrockDeathScreen;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class MixinCamera {

    @Redirect(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewXRot(F)F"))
    public float onPitchGet(Entity instance, float a) {
        if (Minecraft.getInstance().screen instanceof BedrockDeathScreen bedrockDeathScreen){
            return bedrockDeathScreen.calcCamPitch();
        } else {
            return instance.getViewXRot(a);
        }
    }

    @Redirect(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewYRot(F)F"))
    public float onYatGet(Entity instance, float a){
        if (Minecraft.getInstance().screen instanceof BedrockDeathScreen bedrockDeathScreen){
            return bedrockDeathScreen.calcCamYaw();
        } else {
            return instance.getViewYRot(a);
        }
    }

    @Redirect(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    public double onUpdateThirdPerson(LivingEntity instance, Holder<Attribute> attribute){
        Minecraft client = Minecraft.getInstance();
        if (client.screen instanceof BedrockDeathScreen bedrockDeathScreen){
            return instance.getAttributeValue(attribute) * bedrockDeathScreen.calcCameraOffset();
        } else {
            return instance.getAttributeValue(attribute);
        }
    }

    @Redirect(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getCameraType()Lnet/minecraft/client/CameraType;"))
    public CameraType updateThirdPerson(Options instance){
        return Minecraft.getInstance().screen instanceof BedrockDeathScreen ?
                CameraType.THIRD_PERSON_BACK :
                instance.getCameraType();
    }

    @Inject(at = @At("TAIL"), method = "getFov", cancellable = true)
    public void onFovGet(CallbackInfoReturnable<Float> cir){
        if (Minecraft.getInstance().screen instanceof BedrockDeathScreen bedrockDeathScreen){
            cir.setReturnValue(Math.min(60 + bedrockDeathScreen.getTotalScreenTime() / (405f), 80));
        }
    }

}
