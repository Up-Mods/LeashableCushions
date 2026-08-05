package dev.upcraft.leashablecushions.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityTypes.class)
public class EntityTypesMixin {

    @WrapOperation(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/world/entity/EntityTypeIds;CUSHION:Lnet/minecraft/resources/ResourceKey;"),
                    to = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/world/entity/EntityTypeIds;DARK_OAK_BOAT:Lnet/minecraft/resources/ResourceKey;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType$Builder;updateInterval(I)Lnet/minecraft/world/entity/EntityType$Builder;")
    )
    private static <T extends Entity> EntityType.Builder<T> onRegisterCushion_preventInfiniteUpdateInterval(EntityType.Builder<T> instance, int updateInterval, Operation<EntityType.Builder<T>> original) {
        return instance;
    }

    @WrapOperation(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/world/entity/EntityTypeIds;CUSHION:Lnet/minecraft/resources/ResourceKey;"),
                    to = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/world/entity/EntityTypeIds;DARK_OAK_BOAT:Lnet/minecraft/resources/ResourceKey;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType$Builder;dontTrackDeltas()Lnet/minecraft/world/entity/EntityType$Builder;")
    )
    private static <T extends Entity> EntityType.Builder<T> onRegisterCushion_preventNoDeltaTracking(EntityType.Builder<T> instance, Operation<EntityType.Builder<T>> original) {
        return instance;
    }
}
