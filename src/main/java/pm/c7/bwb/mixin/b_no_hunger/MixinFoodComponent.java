package pm.c7.bwb.mixin.b_no_hunger;

import net.minecraft.item.FoodComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FoodComponent.class)
public class MixinFoodComponent {
    @Redirect(method = "getSaturationModifier", at = @At("HEAD"))
    private float noSaturationValue() {
        return 0.0F;
    }
}
