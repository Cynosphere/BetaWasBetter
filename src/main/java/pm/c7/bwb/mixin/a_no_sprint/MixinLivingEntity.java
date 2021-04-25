package pm.c7.bwb.mixin.a_no_sprint;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "setSprinting", at = @At(value = "INVOKE", target = "net/minecraft/entity/attribute/EntityAttributeInstance.addTemporaryModifier(Lnet/minecraft/entity/attribute/EntityAttributeModifier;)V"), cancellable = true)
    private void noSprintBoost(boolean sprinting, CallbackInfo info) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            if (!(player.isSpectator() || player.isCreative())) {
                info.cancel();
            }
        }
    }

    @Redirect(method = "jump", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.isSprinting()Z"))
    private boolean noJumpSprintBoost(LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            return (player.isSpectator() || player.isCreative()) && livingEntity.isSprinting();
        } else {
            return livingEntity.isSprinting();
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.isSprinting()Z"))
    private boolean noSwimSprintBoost(LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            return (player.isSpectator() || player.isCreative()) && livingEntity.isSprinting();
        } else {
            return livingEntity.isSprinting();
        }
    }
}
