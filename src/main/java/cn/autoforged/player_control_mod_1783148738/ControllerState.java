package cn.autoforged.player_control_mod_1783148738;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

public enum ControllerState {
    SIMULATED,
    PACKET;

    public static final boolean DEFAULT_FEEDBACK = true;
    public static final double DEFAULT_OPERATION_DISTANCE = 6.0;

    private static ControllerState currentMode = SIMULATED;
    private static boolean feedbackEnabled = DEFAULT_FEEDBACK;
    private static boolean sneakToggled = false;
    private static double operationDistance = DEFAULT_OPERATION_DISTANCE;

    private static BlockPos regionStart = null;
    private static BlockPos regionEnd = null;

    private static String repeatAction = null;
    private static BlockPos repeatPos = null;
    private static int repeatInterval = 0;
    private static int repeatTick = 0;

    private static String holdAction = null;

    private static final Map<String, ControllerState> perActionModes = new HashMap<>();
    private static boolean replaceModeEnabled = false;

    public static ControllerState getMode() { return currentMode; }
    public static void setMode(ControllerState mode) { currentMode = mode; }
    public static boolean isFeedbackEnabled() { return feedbackEnabled; }
    public static void setFeedbackEnabled(boolean enabled) { feedbackEnabled = enabled; }

    public static boolean isSneakToggled() { return sneakToggled; }
    public static void setSneakToggled(boolean toggled) { sneakToggled = toggled; }
    public static void toggleSneak() { sneakToggled = !sneakToggled; }

    public static double getOperationDistance() { return operationDistance; }
    public static void setOperationDistance(double dist) { operationDistance = Math.max(1.0, dist); }

    public static BlockPos getRegionStart() { return regionStart; }
    public static BlockPos getRegionEnd() { return regionEnd; }
    public static void setRegionStart(BlockPos pos) { regionStart = pos; }
    public static void setRegionEnd(BlockPos pos) { regionEnd = pos; }
    public static boolean hasRegion() { return regionStart != null && regionEnd != null; }
    public static void clearRegion() { regionStart = null; regionEnd = null; }

    public static boolean shouldExecuteRepeat() {
        if (repeatAction == null) return false;
        repeatTick++;
        if (repeatTick >= repeatInterval) {
            repeatTick = 0;
            return true;
        }
        return false;
    }

    public static String getRepeatAction() { return repeatAction; }
    public static BlockPos getRepeatPos() { return repeatPos; }
    public static int getRepeatInterval() { return repeatInterval; }

    public static void startRepeating(String action, int interval) {
        startRepeating(action, null, interval);
    }

    public static void startRepeating(String action, BlockPos pos, int interval) {
        repeatAction = action;
        repeatPos = pos;
        repeatInterval = Math.max(1, interval);
        repeatTick = 0;
    }

    public static void stopRepeating() {
        repeatAction = null;
        repeatPos = null;
        repeatInterval = 0;
        repeatTick = 0;
    }

    public static boolean isRepeating() { return repeatAction != null; }

    public static void startHolding(String action) { holdAction = action; }
    public static String getHoldAction() { return holdAction; }
    public static boolean isHolding() { return holdAction != null; }
    public static void stopHolding() { holdAction = null; }

    public static void stopAll() {
        stopRepeating();
        stopHolding();
        perActionModes.clear();
    }

    public static ControllerState getEffectiveMode(String action) {
        return perActionModes.getOrDefault(action, currentMode);
    }

    public static void setActionMode(String action, ControllerState mode) {
        if (mode == null) {
            perActionModes.remove(action);
        } else {
            perActionModes.put(action, mode);
        }
    }

    public static ControllerState getActionMode(String action) {
        return perActionModes.get(action);
    }

    public static Map<String, ControllerState> getPerActionModes() {
        return new HashMap<>(perActionModes);
    }

    public static void clearAllActionModes() {
        perActionModes.clear();
    }

    public static boolean isReplaceModeEnabled() { return replaceModeEnabled; }
    public static void setReplaceModeEnabled(boolean enabled) { replaceModeEnabled = enabled; }
    public static void toggleReplaceMode() { replaceModeEnabled = !replaceModeEnabled; }
}
