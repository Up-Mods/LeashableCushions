package dev.upcraft.leashablecushions.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockAttachedEntity.class)
public abstract class BlockAttachedEntityMixin extends Entity {

    private BlockAttachedEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void onMove(MoverType moverType, Vec3 delta, CallbackInfo ci) {
        if (this instanceof Leashable leashable && leashable.mayBeLeashed()) {
            super.move(moverType, delta);
            ci.cancel();
        }
    }

    @Inject(method = "push", at = @At("HEAD"), cancellable = true)
    private void onPush(double xa, double ya, double za, CallbackInfo ci) {
        if (this instanceof Leashable leashable && leashable.mayBeLeashed()) {
            super.push(xa, ya, za);
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this instanceof Leashable leashable && leashable.mayBeLeashed()) {
            super.tick();
            var interpolation = this.getInterpolation();
            if(interpolation != null) {
                interpolation.interpolate();
            }

            if(this.isLocalInstanceAuthoritative()) {
                this.checkSupportingBlock(onGround(), this.getDeltaMovement());

                var supportingState = this.mainSupportingBlockPos.map(pos -> this.level().getBlockState(pos)).orElse(null);
                var friction = supportingState != null ? supportingState.getBlock().getFriction() : 0.75F;
                var movement = this.getDeltaMovement();
                this.setDeltaMovement(movement.x * friction, movement.y - this.getGravity(), movement.z * friction);

                this.move(MoverType.SELF, this.getDeltaMovement());
            }
        }
        else {
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    @Inject(method = "refreshDimensions", at = @At("HEAD"))
    private void onRefreshDimensions(CallbackInfo ci) {
        if (this instanceof Leashable leashable && leashable.mayBeLeashed()) {
            super.refreshDimensions();
        }
    }

    @Inject(method = "setPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/BlockAttachedEntity;recalculateBoundingBox()V"), cancellable = true)
    private void onSetPos(double x, double y, double z, CallbackInfo ci) {
        if (this instanceof Leashable leashable && leashable.mayBeLeashed()) {
            super.setPos(x, y, z);
        }
    }

    @ModifyReturnValue(method = "repositionEntityAfterLoad", at = @At("RETURN"))
    private boolean checkRepositionLeashed(boolean original) {
        if (this instanceof Leashable leashable && leashable.mayBeLeashed()) {
            return true;
        }

        return original;
    }
}
