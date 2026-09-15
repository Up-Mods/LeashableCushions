package dev.upcraft.leashablecushions.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cushion.class)
public abstract class CushionMixin extends BlockAttachedEntity implements Leashable {

    @Unique
    private Leashable.@Nullable LeashData leashableCushions$leashData;

    private CushionMixin(EntityType<? extends BlockAttachedEntity> type, Level level) {
        super(type, level);
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportQuadLeash() {
        return true;
    }

    @Override
    public @Nullable LeashData getLeashData() {
        return this.leashableCushions$leashData;
    }

    @Override
    public void setLeashData(@Nullable LeashData leashData) {
        this.leashableCushions$leashData = leashData;
    }

    @Override
    public void onLeashRemoved() {
        if(this.level() instanceof ServerLevel level) {
            this.checkBelowWorld();

            // force the drop check early to not leave floating cushions
            this.tickAtCheckInterval();
            if (!this.isRemoved() && !this.survives()) {
                this.discard();
                this.dropItem(level, null);
            }
        }
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void onInteract(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        var superCall = super.interact(player, hand, location);
        if(superCall != InteractionResult.PASS) {
            cir.setReturnValue(superCall);
        }
    }

    @WrapOperation(method = "survives", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/Cushion;wouldSurviveAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/AABB;)Z"))
    private boolean checkSurvivesLeash(Level level, AABB boundingBox, Operation<Boolean> original) {
        return mayBeLeashed() || original.call(level, boundingBox);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void storeExtraData(ValueOutput output, CallbackInfo ci) {
        this.writeLeashData(output, this.leashableCushions$leashData);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readExtraData(ValueInput input, CallbackInfo ci) {
        this.readLeashData(input);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return AbstractBoat.canVehicleCollide(this, entity);
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity other) {
        return !(other instanceof Cushion);
    }

    @Override
    public int getDimensionChangingDelay() {
        return 10;
    }

    @Override
    public Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle portalArea) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(axis, portalArea));
    }
}
