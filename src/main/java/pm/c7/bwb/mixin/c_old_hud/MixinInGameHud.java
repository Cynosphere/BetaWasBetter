package pm.c7.bwb.mixin.c_old_hud;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    @Shadow protected abstract PlayerEntity getCameraPlayer();
    @Shadow protected abstract LivingEntity getRiddenEntity();
    private boolean onMount = false;

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", args = "ldc=armor", shift = At.Shift.AFTER))
    private void repositionArmorBefore(MatrixStack matrices, CallbackInfo info) {
        matrices.push();
        matrices.translate(MinecraftClient.getInstance().getWindow().getScaledWidth(), 10, 0);
        matrices.scale(-1, 1, 1);
        GlStateManager.disableCull();
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=health", shift = At.Shift.AFTER))
    private void repositionArmorAfter(MatrixStack matrices, CallbackInfo info) {
        matrices.pop();
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=air", shift = At.Shift.AFTER))
    private void airBefore(MatrixStack matrices, CallbackInfo info) {
        matrices.push();
        matrices.translate(MinecraftClient.getInstance().getWindow().getScaledWidth(), 0, 0);
        matrices.scale(-1, 1, 1);
        GlStateManager.disableCull();
    }

    @Inject(method = "renderStatusBars", at = @At("TAIL"))
    private void airAfter(MatrixStack matrices, CallbackInfo info) {
        matrices.pop();
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartRows(I)I"))
    private int getHeartRows(InGameHud inGameHud, int heartCount) {
        return 1;
    }
    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
    private int getHeartCount(InGameHud inGameHud, LivingEntity entity) {
        return -1;
    }

    @Inject(method = "renderMountHealth", at = @At("HEAD"))
    private void mountHealthBefore(MatrixStack matrices, CallbackInfo info) {
        PlayerEntity player = this.getCameraPlayer();
        LivingEntity mount = this.getRiddenEntity();
        if (mount != null && player != null) {
            if (player.getArmor() > 0) {
                matrices.push();
                matrices.translate(0, -10, 0);
                onMount = true;
            }
        }
    }

    @Inject(method = "renderMountHealth", at = @At("TAIL"))
    private void mountHealthAfter(MatrixStack matrices, CallbackInfo info) {
        if (onMount) {
            matrices.pop();
            onMount = false;
        }
    }
}
