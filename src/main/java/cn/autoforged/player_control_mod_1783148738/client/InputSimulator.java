package cn.autoforged.player_control_mod_1783148738.client;

import cn.autoforged.player_control_mod_1783148738.ControllerState;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class InputSimulator {
    private static final Minecraft mc = Minecraft.getInstance();

    public static void pressKey(KeyMapping keyMapping) {
        KeyMapping.set(keyMapping.getKey(), true);
        KeyMapping.click(keyMapping.getKey());
    }

    public static void releaseKey(KeyMapping keyMapping) {
        KeyMapping.set(keyMapping.getKey(), false);
    }

    public static void setKeyDown(KeyMapping keyMapping, boolean pressed) {
        KeyMapping.set(keyMapping.getKey(), pressed);
    }

    public static void look(float yaw, float pitch) {
        LocalPlayer player = mc.player;
        if (player != null) {
            player.setYRot(yaw);
            player.setXRot(pitch);
        }
    }

    public static void attack() {
        LocalPlayer player = mc.player;
        if (player != null && mc.gameMode != null) {
            if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
                mc.gameMode.attack(player, ((EntityHitResult) mc.hitResult).getEntity());
            }
            player.swing(InteractionHand.MAIN_HAND);
        }
    }

    public static void use() {
        LocalPlayer player = mc.player;
        if (player != null && mc.gameMode != null) {
            if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) mc.hitResult;
                mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, blockHit);
            } else if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) mc.hitResult).getEntity();
                mc.gameMode.interact(player, entity, InteractionHand.MAIN_HAND);
            } else {
                mc.gameMode.useItem(player, InteractionHand.MAIN_HAND);
            }
        }
    }

    public static void jump() {
        pressKey(mc.options.keyJump);
    }

    public static void setSneaking(boolean sneaking) {
        setKeyDown(mc.options.keyShift, sneaking);
    }

    public static void dropItem(boolean dropAll) {
        if (dropAll) {
            KeyMapping.set(mc.options.keyDrop.getKey(), true);
            KeyMapping.click(mc.options.keyDrop.getKey());
            KeyMapping.set(mc.options.keyDrop.getKey(), false);
        } else {
            pressKey(mc.options.keyDrop);
            releaseKey(mc.options.keyDrop);
        }
    }

    public static void selectHotbar(int slot) {
        if (slot >= 0 && slot <= 8) {
            pressKey(mc.options.keyHotbarSlots[slot]);
            releaseKey(mc.options.keyHotbarSlots[slot]);
        }
    }

    public static void openInventory() {
        pressKey(mc.options.keyInventory);
    }

    public static void mineBlock(BlockPos pos) {
        LocalPlayer player = mc.player;
        if (player == null || mc.gameMode == null) return;
        Direction dir = Direction.UP;
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            dir = ((BlockHitResult) mc.hitResult).getDirection();
        }
        mc.gameMode.startDestroyBlock(pos, dir);
        mc.gameMode.stopDestroyBlock();
    }

    public static void placeBlock(BlockPos pos) {
        LocalPlayer player = mc.player;
        if (player == null || mc.gameMode == null || mc.level == null) return;
        BlockState targetState = mc.level.getBlockState(pos);
        if (!targetState.isAir() && !(ControllerState.isReplaceModeEnabled() && targetState.canBeReplaced())) return;
        Direction dir = Direction.UP;
        Vec3 location = player.position();
        if (mc.hitResult != null) {
            BlockHitResult hit = mc.hitResult instanceof BlockHitResult b ? b : new BlockHitResult(location, Direction.UP, pos, false);
            mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hit);
        } else {
            BlockHitResult hit = new BlockHitResult(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.UP, pos, false);
            mc.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hit);
        }
    }

    public static void closeScreen() {
        if (mc.screen != null) {
            mc.setScreen(null);
        }
    }

    public static boolean pressKeyByName(String keyName) {
        InputConstants.Key key = resolveKeyByName(keyName);
        if (key == null) return false;
        KeyMapping.set(key, true);
        KeyMapping.click(key);
        return true;
    }

    public static InputConstants.Key resolveKeyByName(String name) {
        return switch (name.toLowerCase()) {
            case "0" -> InputConstants.Type.KEYSYM.getOrCreate(48);
            case "1" -> InputConstants.Type.KEYSYM.getOrCreate(49);
            case "2" -> InputConstants.Type.KEYSYM.getOrCreate(50);
            case "3" -> InputConstants.Type.KEYSYM.getOrCreate(51);
            case "4" -> InputConstants.Type.KEYSYM.getOrCreate(52);
            case "5" -> InputConstants.Type.KEYSYM.getOrCreate(53);
            case "6" -> InputConstants.Type.KEYSYM.getOrCreate(54);
            case "7" -> InputConstants.Type.KEYSYM.getOrCreate(55);
            case "8" -> InputConstants.Type.KEYSYM.getOrCreate(56);
            case "9" -> InputConstants.Type.KEYSYM.getOrCreate(57);
            case "a" -> InputConstants.Type.KEYSYM.getOrCreate(65);
            case "b" -> InputConstants.Type.KEYSYM.getOrCreate(66);
            case "c" -> InputConstants.Type.KEYSYM.getOrCreate(67);
            case "d" -> InputConstants.Type.KEYSYM.getOrCreate(68);
            case "e" -> InputConstants.Type.KEYSYM.getOrCreate(69);
            case "f" -> InputConstants.Type.KEYSYM.getOrCreate(70);
            case "g" -> InputConstants.Type.KEYSYM.getOrCreate(71);
            case "h" -> InputConstants.Type.KEYSYM.getOrCreate(72);
            case "i" -> InputConstants.Type.KEYSYM.getOrCreate(73);
            case "j" -> InputConstants.Type.KEYSYM.getOrCreate(74);
            case "k" -> InputConstants.Type.KEYSYM.getOrCreate(75);
            case "l" -> InputConstants.Type.KEYSYM.getOrCreate(76);
            case "m" -> InputConstants.Type.KEYSYM.getOrCreate(77);
            case "n" -> InputConstants.Type.KEYSYM.getOrCreate(78);
            case "o" -> InputConstants.Type.KEYSYM.getOrCreate(79);
            case "p" -> InputConstants.Type.KEYSYM.getOrCreate(80);
            case "q" -> InputConstants.Type.KEYSYM.getOrCreate(81);
            case "r" -> InputConstants.Type.KEYSYM.getOrCreate(82);
            case "s" -> InputConstants.Type.KEYSYM.getOrCreate(83);
            case "t" -> InputConstants.Type.KEYSYM.getOrCreate(84);
            case "u" -> InputConstants.Type.KEYSYM.getOrCreate(85);
            case "v" -> InputConstants.Type.KEYSYM.getOrCreate(86);
            case "w" -> InputConstants.Type.KEYSYM.getOrCreate(87);
            case "x" -> InputConstants.Type.KEYSYM.getOrCreate(88);
            case "y" -> InputConstants.Type.KEYSYM.getOrCreate(89);
            case "z" -> InputConstants.Type.KEYSYM.getOrCreate(90);
            case "f1" -> InputConstants.Type.KEYSYM.getOrCreate(290);
            case "f2" -> InputConstants.Type.KEYSYM.getOrCreate(291);
            case "f3" -> InputConstants.Type.KEYSYM.getOrCreate(292);
            case "f4" -> InputConstants.Type.KEYSYM.getOrCreate(293);
            case "f5" -> InputConstants.Type.KEYSYM.getOrCreate(294);
            case "f6" -> InputConstants.Type.KEYSYM.getOrCreate(295);
            case "f7" -> InputConstants.Type.KEYSYM.getOrCreate(296);
            case "f8" -> InputConstants.Type.KEYSYM.getOrCreate(297);
            case "f9" -> InputConstants.Type.KEYSYM.getOrCreate(298);
            case "f10" -> InputConstants.Type.KEYSYM.getOrCreate(299);
            case "f11" -> InputConstants.Type.KEYSYM.getOrCreate(300);
            case "f12" -> InputConstants.Type.KEYSYM.getOrCreate(301);
            case "f13" -> InputConstants.Type.KEYSYM.getOrCreate(302);
            case "f14" -> InputConstants.Type.KEYSYM.getOrCreate(303);
            case "f15" -> InputConstants.Type.KEYSYM.getOrCreate(304);
            case "f16" -> InputConstants.Type.KEYSYM.getOrCreate(305);
            case "f17" -> InputConstants.Type.KEYSYM.getOrCreate(306);
            case "f18" -> InputConstants.Type.KEYSYM.getOrCreate(307);
            case "f19" -> InputConstants.Type.KEYSYM.getOrCreate(308);
            case "f20" -> InputConstants.Type.KEYSYM.getOrCreate(309);
            case "f21" -> InputConstants.Type.KEYSYM.getOrCreate(310);
            case "f22" -> InputConstants.Type.KEYSYM.getOrCreate(311);
            case "f23" -> InputConstants.Type.KEYSYM.getOrCreate(312);
            case "f24" -> InputConstants.Type.KEYSYM.getOrCreate(313);
            case "f25" -> InputConstants.Type.KEYSYM.getOrCreate(314);
            case "space" -> InputConstants.Type.KEYSYM.getOrCreate(32);
            case "apostrophe" -> InputConstants.Type.KEYSYM.getOrCreate(39);
            case "comma" -> InputConstants.Type.KEYSYM.getOrCreate(44);
            case "minus" -> InputConstants.Type.KEYSYM.getOrCreate(45);
            case "period" -> InputConstants.Type.KEYSYM.getOrCreate(46);
            case "slash" -> InputConstants.Type.KEYSYM.getOrCreate(47);
            case "semicolon" -> InputConstants.Type.KEYSYM.getOrCreate(59);
            case "equals" -> InputConstants.Type.KEYSYM.getOrCreate(61);
            case "lbrace" -> InputConstants.Type.KEYSYM.getOrCreate(91);
            case "backslash" -> InputConstants.Type.KEYSYM.getOrCreate(92);
            case "rbrace" -> InputConstants.Type.KEYSYM.getOrCreate(93);
            case "grave" -> InputConstants.Type.KEYSYM.getOrCreate(96);
            case "enter" -> InputConstants.Type.KEYSYM.getOrCreate(257);
            case "tab" -> InputConstants.Type.KEYSYM.getOrCreate(258);
            case "backspace" -> InputConstants.Type.KEYSYM.getOrCreate(259);
            case "insert" -> InputConstants.Type.KEYSYM.getOrCreate(260);
            case "delete" -> InputConstants.Type.KEYSYM.getOrCreate(261);
            case "right" -> InputConstants.Type.KEYSYM.getOrCreate(262);
            case "left" -> InputConstants.Type.KEYSYM.getOrCreate(263);
            case "down" -> InputConstants.Type.KEYSYM.getOrCreate(264);
            case "up" -> InputConstants.Type.KEYSYM.getOrCreate(265);
            case "pageup" -> InputConstants.Type.KEYSYM.getOrCreate(266);
            case "pagedown" -> InputConstants.Type.KEYSYM.getOrCreate(267);
            case "home" -> InputConstants.Type.KEYSYM.getOrCreate(268);
            case "end" -> InputConstants.Type.KEYSYM.getOrCreate(269);
            case "capslock" -> InputConstants.Type.KEYSYM.getOrCreate(280);
            case "scrolllock" -> InputConstants.Type.KEYSYM.getOrCreate(281);
            case "numlock" -> InputConstants.Type.KEYSYM.getOrCreate(282);
            case "printscreen" -> InputConstants.Type.KEYSYM.getOrCreate(283);
            case "pause" -> InputConstants.Type.KEYSYM.getOrCreate(284);
            case "kp_0" -> InputConstants.Type.KEYSYM.getOrCreate(320);
            case "kp_1" -> InputConstants.Type.KEYSYM.getOrCreate(321);
            case "kp_2" -> InputConstants.Type.KEYSYM.getOrCreate(322);
            case "kp_3" -> InputConstants.Type.KEYSYM.getOrCreate(323);
            case "kp_4" -> InputConstants.Type.KEYSYM.getOrCreate(324);
            case "kp_5" -> InputConstants.Type.KEYSYM.getOrCreate(325);
            case "kp_6" -> InputConstants.Type.KEYSYM.getOrCreate(326);
            case "kp_7" -> InputConstants.Type.KEYSYM.getOrCreate(327);
            case "kp_8" -> InputConstants.Type.KEYSYM.getOrCreate(328);
            case "kp_9" -> InputConstants.Type.KEYSYM.getOrCreate(329);
            case "kp_decimal" -> InputConstants.Type.KEYSYM.getOrCreate(330);
            case "kp_divide" -> InputConstants.Type.KEYSYM.getOrCreate(331);
            case "kp_multiply" -> InputConstants.Type.KEYSYM.getOrCreate(332);
            case "kp_subtract" -> InputConstants.Type.KEYSYM.getOrCreate(333);
            case "kp_add" -> InputConstants.Type.KEYSYM.getOrCreate(334);
            case "kp_enter" -> InputConstants.Type.KEYSYM.getOrCreate(335);
            case "kp_equal" -> InputConstants.Type.KEYSYM.getOrCreate(336);
            case "lshift" -> InputConstants.Type.KEYSYM.getOrCreate(340);
            case "lctrl" -> InputConstants.Type.KEYSYM.getOrCreate(341);
            case "lalt" -> InputConstants.Type.KEYSYM.getOrCreate(342);
            case "lsuper" -> InputConstants.Type.KEYSYM.getOrCreate(343);
            case "rshift" -> InputConstants.Type.KEYSYM.getOrCreate(344);
            case "rctrl" -> InputConstants.Type.KEYSYM.getOrCreate(345);
            case "ralt" -> InputConstants.Type.KEYSYM.getOrCreate(346);
            case "rsuper" -> InputConstants.Type.KEYSYM.getOrCreate(347);
            case "shift" -> InputConstants.Type.KEYSYM.getOrCreate(340);
            case "ctrl" -> InputConstants.Type.KEYSYM.getOrCreate(341);
            case "alt" -> InputConstants.Type.KEYSYM.getOrCreate(342);
            case "super" -> InputConstants.Type.KEYSYM.getOrCreate(343);
            case "menu" -> InputConstants.Type.KEYSYM.getOrCreate(348);
            case "escape" -> InputConstants.Type.KEYSYM.getOrCreate(256);
            case "world1" -> InputConstants.Type.KEYSYM.getOrCreate(161);
            case "world2" -> InputConstants.Type.KEYSYM.getOrCreate(162);
            default -> null;
        };
    }

    public static void stopAll() {
        setSneaking(false);
        ControllerState.stopAll();
    }
}
