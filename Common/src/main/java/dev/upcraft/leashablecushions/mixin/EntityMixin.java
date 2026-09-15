package dev.upcraft.leashablecushions.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LinearInterpolationHandler;
import net.minecraft.world.entity.decoration.Cushion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyReturnValue(method = "getDefaultGravity", at = @At("RETURN"))
    private double getModifiedGravity(double original) {
        if((Object) this instanceof Cushion && ((Leashable) this).mayBeLeashed()) {
            return 0.04;
        }

        return original;
    }

    @ModifyReturnValue(method = "getMovementEmission", at = @At("RETURN"))
    private Entity.MovementEmission cushionMovementEmissions(Entity.MovementEmission original) {
        if((Object) this instanceof Cushion) {
            return Entity.MovementEmission.EVENTS;
        }

        return original;
    }

    @WrapMethod(method = "createInterpolationHandler")
    private InterpolationHandler createInterpolationHandlerOverride(Operation<InterpolationHandler> original) {
        if((Object) this instanceof Cushion cushion) {
            return LinearInterpolationHandler.create(cushion);
        }

        return original.call();
    }
}
