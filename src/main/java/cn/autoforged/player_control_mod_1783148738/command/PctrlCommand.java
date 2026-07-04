package cn.autoforged.player_control_mod_1783148738.command;

import cn.autoforged.player_control_mod_1783148738.ControllerState;
import cn.autoforged.player_control_mod_1783148738.client.InputSimulator;
import cn.autoforged.player_control_mod_1783148738.client.PacketSender;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.concurrent.CompletableFuture;

public class PctrlCommand {
    private static final String PREFIX = "§7[§bPCTRL§7]§r ";
    private static final Minecraft mc = Minecraft.getInstance();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pctrl")
            .then(Commands.literal("mode")
                .then(Commands.literal("simulated").executes(ctx -> handleMode(ctx, "simulated")))
                .then(Commands.literal("packet").executes(ctx -> handleMode(ctx, "packet")))
            )
            .then(Commands.literal("look")
                .then(Commands.argument("yaw", DoubleArgumentType.doubleArg())
                    .then(Commands.argument("pitch", DoubleArgumentType.doubleArg())
                        .executes(ctx -> handleLook(ctx))
                    )
                )
            )
            .then(Commands.literal("attack")
                .executes(ctx -> handleAttackOnce(ctx))
                .then(Commands.literal("once").executes(ctx -> handleAttackOnce(ctx)))
                .then(Commands.literal("stop").executes(ctx -> handleAttackStop(ctx)))
                .then(Commands.literal("interval")
                    .then(Commands.argument("gt", IntegerArgumentType.integer(1))
                        .executes(ctx -> handleAttackInterval(ctx))
                    )
                )
                .then(Commands.literal("hold").executes(ctx -> handleAttackHold(ctx)))
            )
            .then(Commands.literal("use")
                .executes(ctx -> handleUseOnce(ctx))
                .then(Commands.literal("stop").executes(ctx -> handleUseStop(ctx)))
                .then(Commands.literal("hold").executes(ctx -> handleUseHold(ctx)))
            )
            .then(Commands.literal("jump").executes(ctx -> handleJump(ctx)))
            .then(Commands.literal("sneak")
                .executes(ctx -> handleSneakToggle(ctx))
                .then(Commands.literal("true").executes(ctx -> handleSneakSet(ctx, true)))
                .then(Commands.literal("false").executes(ctx -> handleSneakSet(ctx, false)))
            )
            .then(Commands.literal("drop")
                .executes(ctx -> handleDrop(ctx, false))
                .then(Commands.literal("all").executes(ctx -> handleDrop(ctx, true)))
            )
            .then(Commands.literal("hotbar")
                .then(Commands.argument("slot", IntegerArgumentType.integer(1, 9))
                    .executes(ctx -> handleHotbar(ctx))
                )
            )
            .then(Commands.literal("inventory").executes(ctx -> handleInventory(ctx)))
            .then(Commands.literal("feedback")
                .executes(ctx -> handleFeedbackToggle(ctx))
                .then(Commands.literal("true").executes(ctx -> handleFeedbackSet(ctx, true)))
                .then(Commands.literal("false").executes(ctx -> handleFeedbackSet(ctx, false)))
            )
            .then(Commands.literal("chat")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                    .executes(ctx -> handleChat(ctx))
                )
            )
            .then(Commands.literal("mine")
                .executes(ctx -> handleMineCrosshair(ctx))
                .then(Commands.argument("x", IntegerArgumentType.integer())
                    .then(Commands.argument("y", IntegerArgumentType.integer())
                        .then(Commands.argument("z", IntegerArgumentType.integer())
                            .executes(ctx -> handleMineOnce(ctx))
                            .then(Commands.literal("interval")
                                .then(Commands.argument("gt", IntegerArgumentType.integer(1))
                                    .executes(ctx -> handleMineInterval(ctx))
                                )
                            )
                        )
                    )
                )
                .then(Commands.literal("region").executes(ctx -> handleMineRegion(ctx)))
                .then(Commands.literal("stop").executes(ctx -> handleMineStop(ctx)))
            )
            .then(Commands.literal("place")
                .executes(ctx -> handlePlaceCrosshair(ctx))
                .then(Commands.argument("x", IntegerArgumentType.integer())
                    .then(Commands.argument("y", IntegerArgumentType.integer())
                        .then(Commands.argument("z", IntegerArgumentType.integer())
                            .executes(ctx -> handlePlaceOnce(ctx))
                        )
                    )
                )
                .then(Commands.literal("region").executes(ctx -> handlePlaceRegion(ctx)))
            )
            .then(Commands.literal("region")
                .then(Commands.literal("start")
                    .executes(ctx -> handleRegionStart(ctx))
                    .then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("y", IntegerArgumentType.integer())
                            .then(Commands.argument("z", IntegerArgumentType.integer())
                                .executes(ctx -> handleRegionStartPos(ctx))
                            )
                        )
                    )
                )
                .then(Commands.literal("end")
                    .executes(ctx -> handleRegionEnd(ctx))
                    .then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("y", IntegerArgumentType.integer())
                            .then(Commands.argument("z", IntegerArgumentType.integer())
                                .executes(ctx -> handleRegionEndPos(ctx))
                            )
                        )
                    )
                )
                .then(Commands.literal("clear").executes(ctx -> handleRegionClear(ctx)))
            )
            .then(Commands.literal("amode")
                .then(Commands.argument("action", StringArgumentType.word())
                    .suggests(PctrlCommand::suggestActions)
                    .then(Commands.literal("simulated").executes(ctx -> handleActionMode(ctx, "simulated")))
                    .then(Commands.literal("packet").executes(ctx -> handleActionMode(ctx, "packet")))
                    .then(Commands.literal("default").executes(ctx -> handleActionMode(ctx, "default")))
                )
            )
            .then(Commands.literal("replaceable")
                .executes(ctx -> handleReplaceableToggle(ctx))
                .then(Commands.literal("true").executes(ctx -> handleReplaceableSet(ctx, true)))
                .then(Commands.literal("false").executes(ctx -> handleReplaceableSet(ctx, false)))
            )
            .then(Commands.literal("finditem")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                    .executes(ctx -> handleFindItem(ctx))
                )
            )
            .then(Commands.literal("presskey")
                .then(Commands.argument("key", StringArgumentType.word())
                    .suggests(PctrlCommand::suggestKeys)
                    .executes(ctx -> handlePressKey(ctx))
                )
            )
            .then(Commands.literal("close").executes(ctx -> handleClose(ctx)))
            .then(Commands.literal("dist")
                .then(Commands.argument("distance", DoubleArgumentType.doubleArg(1))
                    .executes(ctx -> handleDist(ctx))
                )
            )
            .then(Commands.literal("stop").executes(ctx -> handleStop(ctx)))
            .executes(ctx -> handleHelp(ctx))
        );
    }

    private static CompletableFuture<Suggestions> suggestKeys(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(new String[]{
            "a","b","c","d","e","f","g","h","i","j","k","l","m",
            "n","o","p","q","r","s","t","u","v","w","x","y","z",
            "0","1","2","3","4","5","6","7","8","9",
            "space","enter","tab","shift","ctrl","alt","super",
            "lshift","lctrl","lalt","lsuper",
            "rshift","rctrl","ralt","rsuper",
            "escape","backspace","delete","insert",
            "up","down","left","right",
            "f1","f2","f3","f4","f5","f6","f7","f8","f9","f10","f11","f12",
            "f13","f14","f15","f16","f17","f18","f19","f20",
            "f21","f22","f23","f24","f25",
            "grave","minus","equals","lbrace","rbrace","backslash",
            "semicolon","apostrophe","comma","period","slash",
            "capslock","scrolllock","numlock","printscreen","pause",
            "pageup","pagedown","home","end","menu",
            "kp_0","kp_1","kp_2","kp_3","kp_4",
            "kp_5","kp_6","kp_7","kp_8","kp_9",
            "kp_decimal","kp_divide","kp_multiply",
            "kp_subtract","kp_add","kp_enter","kp_equal",
            "world1","world2"
        }, builder);
    }

    private static int handleHelp(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        sendFeedback(player, "§6===== PCTRL 命令帮助 =====");
        sendFeedback(player, "§e/pctrl mode <simulated|packet> §7切换模式");
        sendFeedback(player, "§e/pctrl amode <动作> <simulated|packet|default> §7动作模式");
        sendFeedback(player, "§e/pctrl look <yaw> <pitch> §7设置视角");
        sendFeedback(player, "§e/pctrl attack [once|interval <gt>|hold|stop] §7攻击");
        sendFeedback(player, "§e/pctrl use [hold|stop] §7使用/交互");
        sendFeedback(player, "§e/pctrl jump §7跳跃");
        sendFeedback(player, "§e/pctrl sneak [true|false] §7潜行切换");
        sendFeedback(player, "§e/pctrl drop [all] §7丢弃物品");
        sendFeedback(player, "§e/pctrl hotbar <1-9> §7切换热栏");
        sendFeedback(player, "§e/pctrl inventory §7打开背包");
        sendFeedback(player, "§e/pctrl feedback [true|false] §7反馈开关");
        sendFeedback(player, "§e/pctrl chat <消息> §7发送聊天");
        sendFeedback(player, "§e/pctrl mine [<x> <y> <z>|region|stop] §7挖掘");
        sendFeedback(player, "§e/pctrl place [<x> <y> <z>|region] §7放置");
        sendFeedback(player, "§e/pctrl region <start [x y z]|end [x y z]|clear> §7选区");
        sendFeedback(player, "§e/pctrl finditem <名称> §7检索物品");
        sendFeedback(player, "§e/pctrl presskey <键> §7模拟按键");
        sendFeedback(player, "§e/pctrl close §7关闭当前界面");
        sendFeedback(player, "§e/pctrl dist <距离> §7设置操作距离");
        sendFeedback(player, "§e/pctrl replaceable [true|false] §7可替换方块放置");
        sendFeedback(player, "§e/pctrl stop §7停止所有操作");
        return 1;
    }

    private static int handleMode(CommandContext<CommandSourceStack> ctx, String mode) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        if ("simulated".equals(mode)) {
            ControllerState.setMode(ControllerState.SIMULATED);
            sendFeedback(player, "§a已切换为模拟输入模式");
        } else {
            ControllerState.setMode(ControllerState.PACKET);
            sendFeedback(player, "§a已切换为数据包模式");
        }
        return 1;
    }

    private static int handleLook(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        float yaw = (float) DoubleArgumentType.getDouble(ctx, "yaw");
        float pitch = (float) DoubleArgumentType.getDouble(ctx, "pitch");
        if (ControllerState.getEffectiveMode("look") == ControllerState.PACKET) {
            PacketSender.sendLookPacket(yaw, pitch);
        } else {
            InputSimulator.look(yaw, pitch);
        }
        sendFeedback(player, "§a视角已设置为 yaw=" + String.format("%.1f", yaw) + " pitch=" + String.format("%.1f", pitch));
        return 1;
    }

    private static int handleAttackOnce(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        doAttack(player);
        sendFeedback(player, "§a执行攻击");
        return 1;
    }

    private static int handleAttackStop(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.stopRepeating();
        ControllerState.stopHolding();
        sendFeedback(player, "§a停止攻击");
        return 1;
    }

    private static int handleAttackInterval(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        int interval = IntegerArgumentType.getInteger(ctx, "gt");
        ControllerState.stopHolding();
        ControllerState.startRepeating("attack", interval);
        sendFeedback(player, "§a每 " + interval + " gt 重复攻击");
        return 1;
    }

    private static int handleAttackHold(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.stopRepeating();
        ControllerState.startHolding("attack");
        sendFeedback(player, "§a开始长按攻击");
        return 1;
    }

    private static void doAttack(LocalPlayer player) {
        if (ControllerState.getEffectiveMode("attack") == ControllerState.PACKET) {
            PacketSender.sendAttackPacket();
        } else {
            InputSimulator.attack();
        }
    }

    private static int handleUseOnce(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        doUse(player);
        sendFeedback(player, "§a执行使用/交互");
        return 1;
    }

    private static int handleUseStop(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.stopHolding();
        ControllerState.stopRepeating();
        sendFeedback(player, "§a停止使用");
        return 1;
    }

    private static int handleUseHold(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.stopRepeating();
        ControllerState.startHolding("use");
        sendFeedback(player, "§a开始长按使用");
        return 1;
    }

    private static void doUse(LocalPlayer player) {
        if (ControllerState.getEffectiveMode("use") == ControllerState.PACKET) {
            PacketSender.sendUsePacket();
        } else {
            InputSimulator.use();
        }
    }

    private static int handleJump(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        InputSimulator.jump();
        sendFeedback(player, "§a执行跳跃");
        return 1;
    }

    private static int handleSneakToggle(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.toggleSneak();
        boolean sneaking = ControllerState.isSneakToggled();
        if (ControllerState.getEffectiveMode("sneak") == ControllerState.PACKET) {
            PacketSender.sendSneakPacket(sneaking);
        } else {
            InputSimulator.setSneaking(sneaking);
        }
        sendFeedback(player, (sneaking ? "§a已潜行" : "§7已取消潜行"));
        return 1;
    }

    private static int handleSneakSet(CommandContext<CommandSourceStack> ctx, boolean sneaking) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.setSneakToggled(sneaking);
        if (ControllerState.getEffectiveMode("sneak") == ControllerState.PACKET) {
            PacketSender.sendSneakPacket(sneaking);
        } else {
            InputSimulator.setSneaking(sneaking);
        }
        sendFeedback(player, (sneaking ? "§a已潜行" : "§7已取消潜行"));
        return 1;
    }

    private static int handleDrop(CommandContext<CommandSourceStack> ctx, boolean all) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        if (ControllerState.getEffectiveMode("drop") == ControllerState.PACKET) {
            PacketSender.sendDropPacket(all);
        } else {
            InputSimulator.dropItem(all);
        }
        sendFeedback(player, "§a丢弃物品" + (all ? "(全部)" : ""));
        return 1;
    }

    private static int handleHotbar(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        int slot = IntegerArgumentType.getInteger(ctx, "slot") - 1;
        if (ControllerState.getEffectiveMode("hotbar") == ControllerState.PACKET) {
            PacketSender.sendHotbarPacket(slot);
        } else {
            InputSimulator.selectHotbar(slot);
        }
        sendFeedback(player, "§a切换到热栏槽位: " + (slot + 1));
        return 1;
    }

    private static int handleInventory(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        if (ControllerState.getEffectiveMode("inventory") == ControllerState.PACKET) {
            PacketSender.sendOpenInventoryPacket();
        } else {
            InputSimulator.openInventory();
        }
        sendFeedback(player, "§a打开背包");
        return 1;
    }

    private static int handleFeedbackToggle(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.setFeedbackEnabled(!ControllerState.isFeedbackEnabled());
        sendFeedback(player, "§a执行反馈: " + (ControllerState.isFeedbackEnabled() ? "§a开启" : "§c关闭"));
        return 1;
    }

    private static int handleFeedbackSet(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.setFeedbackEnabled(enabled);
        sendFeedback(player, "§a执行反馈: " + (enabled ? "§a开启" : "§c关闭"));
        return 1;
    }

    private static int handleChat(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        String message = StringArgumentType.getString(ctx, "message");
        if (ControllerState.getEffectiveMode("chat") == ControllerState.PACKET) {
            PacketSender.sendChatPacket(message);
        } else {
            player.connection.sendCommand(message);
        }
        sendFeedback(player, "§a发送消息: " + message);
        return 1;
    }

    private static boolean checkDistance(BlockPos pos) {
        LocalPlayer player = mc.player;
        if (player == null) return false;
        double dist = Math.sqrt(player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        return dist <= ControllerState.getOperationDistance();
    }

    private static void mineBlockAt(LocalPlayer player, BlockPos pos) {
        if (ControllerState.getEffectiveMode("mine") == ControllerState.PACKET) {
            PacketSender.sendMineBlockPacket(pos);
        } else {
            InputSimulator.mineBlock(pos);
        }
    }

    private static int handleMineOnce(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        int x = IntegerArgumentType.getInteger(ctx, "x");
        int y = IntegerArgumentType.getInteger(ctx, "y");
        int z = IntegerArgumentType.getInteger(ctx, "z");
        BlockPos pos = new BlockPos(x, y, z);
        if (!checkDistance(pos)) {
            sendFeedback(player, "§c目标超出操作距离 (§e" + ControllerState.getOperationDistance() + "§c)");
            return 1;
        }
        mineBlockAt(player, pos);
        sendFeedback(player, "§a挖掘方块 [" + x + "," + y + "," + z + "]");
        return 1;
    }

    private static int handleMineInterval(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        int x = IntegerArgumentType.getInteger(ctx, "x");
        int y = IntegerArgumentType.getInteger(ctx, "y");
        int z = IntegerArgumentType.getInteger(ctx, "z");
        int interval = IntegerArgumentType.getInteger(ctx, "gt");
        BlockPos pos = new BlockPos(x, y, z);
        if (!checkDistance(pos)) {
            sendFeedback(player, "§c目标超出操作距离 (§e" + ControllerState.getOperationDistance() + "§c)");
            return 1;
        }
        ControllerState.startRepeating("mine", pos, interval);
        sendFeedback(player, "§a开始挖掘 [" + x + "," + y + "," + z + "] 每 " + interval + " gt");
        return 1;
    }

    private static int handleMineStop(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.stopRepeating();
        sendFeedback(player, "§a停止挖掘");
        return 1;
    }

    private static int handleMineCrosshair(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) {
            sendFeedback(player, "§c请将准星对准一个方块");
            return 1;
        }
        BlockPos pos = ((BlockHitResult) mc.hitResult).getBlockPos();
        if (!checkDistance(pos)) {
            sendFeedback(player, "§c目标超出操作距离 (§e" + ControllerState.getOperationDistance() + "§c)");
            return 1;
        }
        mineBlockAt(player, pos);
        sendFeedback(player, "§a挖掘方块 [" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "]");
        return 1;
    }

    private static int handleMineRegion(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        if (!ControllerState.hasRegion()) {
            sendFeedback(player, "§c请先使用 /pctrl region start 和 region end 设置区域");
            return 1;
        }
        BlockPos start = ControllerState.getRegionStart();
        BlockPos end = ControllerState.getRegionEnd();
        int minX = Math.min(start.getX(), end.getX());
        int minY = Math.min(start.getY(), end.getY());
        int minZ = Math.min(start.getZ(), end.getZ());
        int maxX = Math.max(start.getX(), end.getX());
        int maxY = Math.max(start.getY(), end.getY());
        int maxZ = Math.max(start.getZ(), end.getZ());
        int count = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!checkDistance(pos)) continue;
                    mineBlockAt(player, pos);
                    count++;
                }
            }
        }
        sendFeedback(player, "§a区域挖掘完成，共挖掘 " + count + " 个方块");
        return 1;
    }

    private static void placeBlockAt(LocalPlayer player, BlockPos pos) {
        if (ControllerState.getEffectiveMode("place") == ControllerState.PACKET) {
            PacketSender.sendPlaceBlockPacket(pos);
        } else {
            InputSimulator.placeBlock(pos);
        }
    }

    private static int handlePlaceOnce(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        int x = IntegerArgumentType.getInteger(ctx, "x");
        int y = IntegerArgumentType.getInteger(ctx, "y");
        int z = IntegerArgumentType.getInteger(ctx, "z");
        BlockPos pos = new BlockPos(x, y, z);
        if (!checkDistance(pos)) {
            sendFeedback(player, "§c目标超出操作距离 (§e" + ControllerState.getOperationDistance() + "§c)");
            return 1;
        }
        placeBlockAt(player, pos);
        sendFeedback(player, "§a放置方块到 [" + x + "," + y + "," + z + "]");
        return 1;
    }

    private static int handlePlaceCrosshair(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) {
            sendFeedback(player, "§c请将准星对准一个方块");
            return 1;
        }
        BlockPos pos = ((BlockHitResult) mc.hitResult).getBlockPos();
        if (!checkDistance(pos)) {
            sendFeedback(player, "§c目标超出操作距离 (§e" + ControllerState.getOperationDistance() + "§c)");
            return 1;
        }
        placeBlockAt(player, pos);
        sendFeedback(player, "§a放置方块到 [" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "]");
        return 1;
    }

    private static int handlePlaceRegion(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        if (!ControllerState.hasRegion()) {
            sendFeedback(player, "§c请先使用 /pctrl region start 和 region end 设置区域");
            return 1;
        }
        BlockPos start = ControllerState.getRegionStart();
        BlockPos end = ControllerState.getRegionEnd();
        int minX = Math.min(start.getX(), end.getX());
        int minY = Math.min(start.getY(), end.getY());
        int minZ = Math.min(start.getZ(), end.getZ());
        int maxX = Math.max(start.getX(), end.getX());
        int maxY = Math.max(start.getY(), end.getY());
        int maxZ = Math.max(start.getZ(), end.getZ());
        int count = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!checkDistance(pos)) continue;
                    placeBlockAt(player, pos);
                    count++;
                }
            }
        }
        sendFeedback(player, "§a区域填充完成，共放置 " + count + " 个方块");
        return 1;
    }

    private static int handleRegionStart(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        BlockPos pos = player.blockPosition();
        ControllerState.setRegionStart(pos);
        sendFeedback(player, "§a区域起点已设置: [" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "]");
        return 1;
    }

    private static int handleRegionStartPos(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        int x = IntegerArgumentType.getInteger(ctx, "x");
        int y = IntegerArgumentType.getInteger(ctx, "y");
        int z = IntegerArgumentType.getInteger(ctx, "z");
        BlockPos pos = new BlockPos(x, y, z);
        ControllerState.setRegionStart(pos);
        sendFeedback(player, "§a区域起点已设置: [" + x + "," + y + "," + z + "]");
        return 1;
    }

    private static int handleRegionEnd(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        BlockPos pos = player.blockPosition();
        ControllerState.setRegionEnd(pos);
        sendFeedback(player, "§a区域终点已设置: [" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "]");
        return 1;
    }

    private static int handleRegionEndPos(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        int x = IntegerArgumentType.getInteger(ctx, "x");
        int y = IntegerArgumentType.getInteger(ctx, "y");
        int z = IntegerArgumentType.getInteger(ctx, "z");
        BlockPos pos = new BlockPos(x, y, z);
        ControllerState.setRegionEnd(pos);
        sendFeedback(player, "§a区域终点已设置: [" + x + "," + y + "," + z + "]");
        return 1;
    }

    private static int handleRegionClear(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.clearRegion();
        sendFeedback(player, "§a区域已清除");
        return 1;
    }

    private static int handleFindItem(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        String query = StringArgumentType.getString(ctx, "name").toLowerCase();
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.items.size(); i++) {
            ItemStack stack = inv.items.get(i);
            if (stack.isEmpty()) continue;
            String hoverName = stack.getHoverName().getString().toLowerCase();
            String regName = stack.getItem().toString().toLowerCase();
            if (hoverName.contains(query) || regName.contains(query)) {
                if (i < 9) {
                    inv.selected = i;
                    if (ControllerState.getEffectiveMode("hotbar") == ControllerState.PACKET) {
                        PacketSender.sendHotbarPacket(i);
                    } else {
                        InputSimulator.selectHotbar(i);
                    }
                } else {
                    int hotbarSlot = inv.selected;
                    PacketSender.sendSwapItemPacket(i, hotbarSlot);
                }
                sendFeedback(player, "§a已取出: " + stack.getHoverName().getString());
                return 1;
            }
        }
        sendFeedback(player, "§c未找到匹配的物品: " + query);
        return 1;
    }

    private static int handlePressKey(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        String keyName = StringArgumentType.getString(ctx, "key");
        boolean success = InputSimulator.pressKeyByName(keyName);
        if (success) {
            sendFeedback(player, "§a按下按键: " + keyName);
        } else {
            sendFeedback(player, "§c未知按键: " + keyName);
        }
        return 1;
    }

    private static int handleClose(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        if (ControllerState.getEffectiveMode("close") == ControllerState.PACKET) {
            PacketSender.sendCloseScreenPacket();
        } else {
            InputSimulator.closeScreen();
        }
        sendFeedback(player, "§a关闭当前界面");
        return 1;
    }

    private static int handleDist(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        double dist = DoubleArgumentType.getDouble(ctx, "distance");
        ControllerState.setOperationDistance(dist);
        sendFeedback(player, "§a操作距离已设置为: " + String.format("%.1f", dist));
        return 1;
    }

    private static int handleStop(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.stopAll();
        ControllerState.setSneakToggled(false);
        if (ControllerState.getEffectiveMode("sneak") == ControllerState.PACKET) {
            PacketSender.sendSneakPacket(false);
        } else {
            InputSimulator.setSneaking(false);
        }
        sendFeedback(player, "§a停止所有操作");
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestActions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(new String[]{
            "attack","use","mine","place","look","drop","hotbar","inventory","chat","jump","sneak","close"
        }, builder);
    }

    private static int handleActionMode(CommandContext<CommandSourceStack> ctx, String mode) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        String action = StringArgumentType.getString(ctx, "action");
        if (mode.equals("default")) {
            ControllerState.setActionMode(action, null);
            sendFeedback(player, "§a动作 §e" + action + " §a已恢复为默认模式");
        } else if (mode.equals("simulated")) {
            ControllerState.setActionMode(action, ControllerState.SIMULATED);
            sendFeedback(player, "§a动作 §e" + action + " §a已设为 §e模拟输入 §a模式");
        } else {
            ControllerState.setActionMode(action, ControllerState.PACKET);
            sendFeedback(player, "§a动作 §e" + action + " §a已设为 §e数据包 §a模式");
        }
        return 1;
    }

    private static int handleReplaceableToggle(CommandContext<CommandSourceStack> ctx) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.toggleReplaceMode();
        sendFeedback(player, "§a可替换方块放置: " + (ControllerState.isReplaceModeEnabled() ? "§a开启" : "§c关闭"));
        return 1;
    }

    private static int handleReplaceableSet(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        ControllerState.setReplaceModeEnabled(enabled);
        sendFeedback(player, "§a可替换方块放置: " + (enabled ? "§a开启" : "§c关闭"));
        return 1;
    }

    private static boolean canPlaceBlock(BlockPos pos) {
        if (mc.level == null) return false;
        BlockState state = mc.level.getBlockState(pos);
        return state.isAir() || (ControllerState.isReplaceModeEnabled() && state.canBeReplaced());
    }

    private static void sendFeedback(LocalPlayer player, String message) {
        if (ControllerState.isFeedbackEnabled()) {
            player.displayClientMessage(Component.literal(PREFIX + message), false);
        }
    }
}
