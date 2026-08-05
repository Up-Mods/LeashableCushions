package dev.upcraft.leashablecushions.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.upcraft.leashablecushions.client.util.CushionRenderStateAccess;
import net.minecraft.client.renderer.entity.CushionRenderer;
import net.minecraft.client.renderer.entity.state.CushionRenderState;
import net.minecraft.world.entity.decoration.Cushion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CushionRenderer.class)
public abstract class CushionRendererMixin {

    @ModifyArg(method = "submit(Lnet/minecraft/client/renderer/entity/state/CushionRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 0))
    private float fixRotation(float angle, @Local(argsOnly = true, name = "state") CushionRenderState state) {
        if(state.leashStates != null && !state.leashStates.isEmpty()) {
            return 180.0F - ((CushionRenderStateAccess) state).leashableCushions$getRotationY();
        }

        return angle;
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/decoration/Cushion;Lnet/minecraft/client/renderer/entity/state/CushionRenderState;F)V", at = @At("RETURN"))
    private void extractCushionRotation(Cushion cushion, CushionRenderState state, float partialTicks, CallbackInfo ci) {
        ((CushionRenderStateAccess) state).leashableCushions$setRotationY(cushion.getYRot(partialTicks));
    }
}
