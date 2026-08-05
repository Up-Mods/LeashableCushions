package dev.upcraft.leashablecushions.mixin.client;

import dev.upcraft.leashablecushions.client.util.CushionRenderStateAccess;
import net.minecraft.client.renderer.entity.state.CushionRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CushionRenderState.class)
public abstract class CushionRenderStateMixin implements CushionRenderStateAccess {

    @Unique
    private float leashableCushions$yRot;

    @Override
    public float leashableCushions$getRotationY() {
        return leashableCushions$yRot;
    }

    @Override
    public void leashableCushions$setRotationY(float value) {
        this.leashableCushions$yRot = value;
    }
}
