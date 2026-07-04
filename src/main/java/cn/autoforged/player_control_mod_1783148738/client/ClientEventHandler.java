package cn.autoforged.player_control_mod_1783148738.client;

import cn.autoforged.player_control_mod_1783148738.ControllerState;
import cn.autoforged.player_control_mod_1783148738.PlayerControlMod;
import cn.autoforged.player_control_mod_1783148738.command.PctrlCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = PlayerControlMod.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        PctrlCommand.register(dispatcher);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        if (mc.player == null) return;

        String holdAction = ControllerState.getHoldAction();
        if (holdAction != null) {
            executeAction(holdAction, null);
        }

        if (!ControllerState.isRepeating()) return;
        if (!ControllerState.shouldExecuteRepeat()) return;

        String action = ControllerState.getRepeatAction();
        executeAction(action, ControllerState.getRepeatPos());
    }

    private static void executeAction(String action, BlockPos pos) {
        ControllerState effectiveMode = ControllerState.getEffectiveMode(action);
        switch (action) {
            case "attack" -> {
                if (effectiveMode == ControllerState.PACKET) {
                    PacketSender.sendAttackPacket();
                } else {
                    InputSimulator.attack();
                }
            }
            case "use" -> {
                if (effectiveMode == ControllerState.PACKET) {
                    PacketSender.sendUsePacket();
                } else {
                    InputSimulator.use();
                }
            }
            case "mine" -> {
                if (pos != null) {
                    if (effectiveMode == ControllerState.PACKET) {
                        PacketSender.sendMineBlockPacket(pos);
                    } else {
                        InputSimulator.mineBlock(pos);
                    }
                }
            }
        }
    }
}
