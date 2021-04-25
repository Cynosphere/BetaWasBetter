package pm.c7.bwb.mixin.d_combat;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    private static final EntityAttributeModifier classicSwingSpeed = new EntityAttributeModifier("Classic Swing Speed", Double.MAX_VALUE, EntityAttributeModifier.Operation.ADDITION);

    @Inject(method = "spawnSweepAttackParticles", at = @At("HEAD"), cancellable = true)
    private void noSweepParticles(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (EnchantmentHelper.getSweepingMultiplier(player) <= 0.0F)
            info.cancel();
    }

    @Inject(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"), cancellable = true)
    private void attackNoSweep(Entity target, CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (EnchantmentHelper.getSweepingMultiplier(player) <= 0.0F)
            info.cancel();
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void noopSounds(World world, PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {}

    @Inject(method = "tick", at = @At("TAIL"))
    private void noCooldown(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED).hasModifier(classicSwingSpeed)) {
            player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED).addTemporaryModifier(classicSwingSpeed);
        }
    }
}
