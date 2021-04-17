package pm.c7.bwb.mixin.b_no_hunger;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;update(Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void noopHungerUpdate(HungerManager hungerManager, PlayerEntity player) {}

    @Redirect(method = "canConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;isNotFull()Z"))
    private boolean canConsume(HungerManager hungerManager) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return player.getHealth() < player.getMaxHealth();
    }

    @Redirect(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;eat(Lnet/minecraft/item/Item;Lnet/minecraft/item/ItemStack;)V"))
    private void eatFood(HungerManager hungerManager, Item item, ItemStack stack) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (item.isFood()) {
            FoodComponent foodComponent = item.getFoodComponent();
            player.setHealth(Math.min(player.getHealth() + foodComponent.getHunger(), player.getMaxHealth()));
        }
    }
}
