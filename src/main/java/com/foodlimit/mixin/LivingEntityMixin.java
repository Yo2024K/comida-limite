package com.foodlimit.mixin;

import com.foodlimit.FoodLimit;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    // Se llama cuando termina de usarse un item (terminar de comer/beber)
    @Inject(method = "consumeItem", at = @At("HEAD"))
    private void foodlimit$onFinishConsume(CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            ItemStack stack = player.getActiveItem();
            if (stack.contains(DataComponentTypes.CONSUMABLE)) {
                FoodLimit.registerEat(player);
            }
        }
    }
}
