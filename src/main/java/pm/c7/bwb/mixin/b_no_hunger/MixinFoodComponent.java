package pm.c7.bwb.mixin.b_no_hunger;

import net.minecraft.item.FoodComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodComponent.class)
public class MixinFoodComponent {
    @Inject(method = "getSaturationModifier", at = @At("HEAD"), cancellable = true)
    private void noSaturationValue(CallbackInfoReturnable<Float> info) {
        info.setReturnValue(0.0F);
    }
}
