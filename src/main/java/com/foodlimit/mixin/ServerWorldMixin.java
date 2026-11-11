package com.foodlimit.mixin;

import com.foodlimit.FoodLimit;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    // Vanilla llama a este metodo SOLO cuando la noche realmente se salta (todos los necesarios duermen).
    // Si te acuestas y no pasa el dia, no se llama y no recargas.
    @Inject(method = "wakeSleepingPlayers", at = @At("HEAD"))
    private void foodlimit$resetOnSkippedNight(CallbackInfo ci) {
        for (ServerPlayerEntity player : ((ServerWorld) (Object) this).getPlayers()) {
            if (player.isSleeping()) {
                FoodLimit.resetEaten(player);
            }
        }
    }
}
