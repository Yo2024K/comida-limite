package com.foodlimit.mixin;

import com.foodlimit.FoodLimit;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {
    // La tarta (y la tarta con vela) cuenta como una cosa comestible
    @Inject(method = "tryEat", at = @At("HEAD"), cancellable = true)
    private static void foodlimit$beforeEat(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player,
                                            CallbackInfoReturnable<ActionResult> cir) {
        if (player instanceof ServerPlayerEntity && FoodLimit.limitReached(player)) {
            FoodLimit.sendBlockedMessage(player);
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "tryEat", at = @At("RETURN"))
    private static void foodlimit$afterEat(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player,
                                           CallbackInfoReturnable<ActionResult> cir) {
        if (player instanceof ServerPlayerEntity sp && cir.getReturnValue().isAccepted()) {
            FoodLimit.registerEat(sp);
        }
    }
}
