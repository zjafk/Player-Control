package cn.autoforged.player_control_mod_1783148738.client;

import cn.autoforged.player_control_mod_1783148738.ControllerState;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PacketSender {
    private static final Minecraft mc = Minecraft.getInstance();

    public static void sendLookPacket(float yaw, float pitch) {
        LocalPlayer player = mc.player;
        if (player != null && player.connection != null) {
            player.connection.send(new ServerboundMovePlayerPacket.Rot(yaw, pitch, player.onGround()));
        }
    }

    public static void sendAttackPacket() {
        LocalPlayer player = mc.player;
        if (player != null && player.connection != null) {
            HitResult hit = mc.hitResult;
            if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) hit).getEntity();
                double x = player.getX();
                double y = player.getY();
                double z = player.getZ();
                float yaw = player.getYRot();
                float pitch = player.getXRot();
                player.connection.send(new ServerboundMovePlayerPacket.PosRot(x, y - 0.1, z, yaw, pitch, false));
                player.connection.send(ServerboundInteractPacket.createAttackPacket(entity, player.isShiftKeyDown()));
                player.connection.send(new ServerboundMovePlayerPacket.PosRot(x, y, z, yaw, pitch, true));
            }
        }
    }

    public static void sendUsePacket() {
        LocalPlayer player = mc.player;
        if (player != null && player.connection != null) {
            HitResult hit = mc.hitResult;
            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hit;
                player.connection.send(new ServerboundUseItemOnPacket(
                    InteractionHand.MAIN_HAND, blockHit, 0
                ));
            } else if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) hit).getEntity();
                player.connection.send(
                    ServerboundInteractPacket.createInteractionPacket(entity, player.isShiftKeyDown(), InteractionHand.MAIN_HAND)
                );
            } else {
                player.connection.send(new ServerboundUseItemPacket(
                    InteractionHand.MAIN_HAND, 0, player.getYRot(), player.getXRot()
                ));
            }
        }
    }

    public static void sendSneakPacket(boolean sneaking) {
        LocalPlayer player = mc.player;
        if (player != null && player.connection != null) {
            player.connection.send(new ServerboundPlayerCommandPacket(player,
                sneaking ? ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY : ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY));
        }
    }

    public static void sendDropPacket(boolean dropAll) {
        LocalPlayer player = mc.player;
        if (player != null && player.connection != null) {
            player.connection.send(new ServerboundPlayerActionPacket(
                dropAll ? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS : ServerboundPlayerActionPacket.Action.DROP_ITEM,
                BlockPos.ZERO, Direction.DOWN));
        }
    }

    public static void sendHotbarPacket(int slot) {
        LocalPlayer player = mc.player;
        if (player != null && player.connection != null) {
            player.connection.send(new ServerboundSetCarriedItemPacket(slot));
        }
    }

    public static void sendOpenInventoryPacket() {
        LocalPlayer player = mc.player;
        if (player != null && player.connection != null) {
            player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY));
        }
    }

    public static void sendChatPacket(String message) {
        LocalPlayer player = mc.player;
        if (player != null && player.connection != null) {
            player.connection.sendChat(message);
        }
    }

    public static void sendMineBlockPacket(BlockPos pos) {
        LocalPlayer player = mc.player;
        if (player != null && player.connection != null) {
            Direction dir = Direction.UP;
            if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
                dir = ((BlockHitResult) mc.hitResult).getDirection();
            }
            player.connection.send(new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, pos, dir, 0));
            player.connection.send(new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, dir, 0));
            player.connection.send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
        }
    }

    public static void sendPlaceBlockPacket(BlockPos pos) {
        LocalPlayer player = mc.player;
        if (player != null && player.connection != null && mc.level != null) {
            BlockState targetState = mc.level.getBlockState(pos);
            if (!targetState.isAir() && !(ControllerState.isReplaceModeEnabled() && targetState.canBeReplaced())) return;
            Direction dir = Direction.UP;
            Vec3 loc = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult hit = (BlockHitResult) mc.hitResult;
                dir = hit.getDirection();
                loc = hit.getLocation();
            }
            BlockHitResult hit = new BlockHitResult(loc, dir, pos, false);
            player.connection.send(new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, hit, 0));
        }
    }

    public static void sendCloseScreenPacket() {
        LocalPlayer player = mc.player;
        if (player != null && player.connection != null) {
            int containerId = player.containerMenu.containerId;
            player.connection.send(new ServerboundContainerClosePacket(containerId));
        }
    }

    public static void sendSwapItemPacket(int inventoryIndex, int hotbarSlot) {
        LocalPlayer player = mc.player;
        if (player == null || player.connection == null) return;
        if (inventoryIndex < 9 || inventoryIndex > 35) return;
        if (hotbarSlot < 0 || hotbarSlot > 8) return;

        int containerMainSlot = -1;
        int containerHotbarSlot = -1;
        Inventory inv = player.getInventory();
        for (int s = 0; s < player.containerMenu.slots.size(); s++) {
            Slot slot = player.containerMenu.slots.get(s);
            if (slot.container == inv) {
                if (slot.getSlotIndex() == inventoryIndex) containerMainSlot = s;
                if (slot.getSlotIndex() == hotbarSlot) containerHotbarSlot = s;
            }
        }
        if (containerMainSlot < 0 || containerHotbarSlot < 0) return;

        int containerId = player.containerMenu.containerId;
        int stateId = player.containerMenu.getStateId();

        Int2ObjectMap<ItemStack> changedSlots = new Int2ObjectOpenHashMap<>();
        changedSlots.put(containerMainSlot, player.getInventory().items.get(hotbarSlot).copy());
        changedSlots.put(containerHotbarSlot, player.getInventory().items.get(inventoryIndex).copy());

        player.connection.send(new ServerboundContainerClickPacket(
            containerId, stateId, containerMainSlot, hotbarSlot,
            ClickType.SWAP, ItemStack.EMPTY, changedSlots
        ));

        ItemStack temp = player.getInventory().items.get(inventoryIndex);
        player.getInventory().items.set(inventoryIndex, player.getInventory().items.get(hotbarSlot));
        player.getInventory().items.set(hotbarSlot, temp);
    }
}
