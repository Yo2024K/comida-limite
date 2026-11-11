package com.foodlimit;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class FoodLimit implements ModInitializer {
    public static final String MOD_ID = "foodlimit";
    public static final int MAX_ITEMS = 10;

    /** Cuantas cosas ha consumido el jugador desde la ultima vez que durmio. Se guarda en el jugador. */
    public static final AttachmentType<Integer> EATEN = AttachmentRegistry.create(
            Identifier.of(MOD_ID, "eaten"),
            builder -> builder
                    .initializer(() -> 0)
                    .persistent(Codec.INT)
                    .copyOnDeath());

    public static int getEaten(PlayerEntity player) {
        return player.getAttachedOrElse(EATEN, 0);
    }

    public static boolean limitReached(PlayerEntity player) {
        return getEaten(player) >= MAX_ITEMS;
    }

    public static void registerEat(ServerPlayerEntity player) {
        int n = getEaten(player) + 1;
        player.setAttached(EATEN, n);
        if (n >= MAX_ITEMS) {
            player.sendMessage(Text.literal("Has consumido " + n + "/" + MAX_ITEMS + ". Duerme para poder comer de nuevo.")
                    .formatted(Formatting.GOLD), true);
        } else {
            player.sendMessage(Text.literal("Consumido: " + n + "/" + MAX_ITEMS).formatted(Formatting.YELLOW), true);
        }
    }

    public static void resetEaten(ServerPlayerEntity player) {
        player.setAttached(EATEN, 0);
        player.sendMessage(Text.literal("Has descansado. Puedes volver a comer (0/" + MAX_ITEMS + ").")
                .formatted(Formatting.GREEN), true);
    }

    public static void sendBlockedMessage(PlayerEntity player) {
        player.sendMessage(Text.literal("Ya consumiste " + MAX_ITEMS + " cosas hoy. Duerme una noche completa para recargar.")
                .formatted(Formatting.RED), true);
    }

    @Override
    public void onInitialize() {
        // Bloquea empezar a comer/beber cualquier cosa consumible (comida, pociones, leche, miel, estofados...)
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!(player instanceof ServerPlayerEntity)) {
                return ActionResult.PASS;
            }
            ItemStack stack = player.getStackInHand(hand);
            if (stack.contains(DataComponentTypes.CONSUMABLE) && limitReached(player)) {
                sendBlockedMessage(player);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}
