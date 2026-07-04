## Mod Introduction
**This mod is *entirely* AI-generated.**
PCTRL is a client-side only mod that allows you to automatically perform in-game actions (attacking, using, mining, placing, jumping, sneaking, dropping items, switching hotbar, key press simulation, etc.) via the /pctrl command using keyboard input. It is suitable for freeing your hands when you need to repeat certain actions in the game.

This mod does not add any new items, blocks, entities, or world generation; all functions are invoked through chat bar commands.

## Command Function List

All commands start with /pctrl. After execution, there will be colored feedback messages in the chat bar (can be turned off).

## Mode Switching

| Command | Description |
| --- | --- |
| /pctrl mode simulated | Switch to simulated input mode (default): simulates key/mouse events, suitable for most scenarios |
| /pctrl mode packet | Switch to packet mode: directly sends network packets to the server, faster, but may be detected by anti-cheat |

## Action-Specific Mode

You can specify a mode for each action individually; if not specified, it follows the global default:

| Command | Description |
| --- | --- |
| /pctrl amode \<action\> simulated | Set a certain action to simulated input mode |
| /pctrl amode \<action\> packet | Set a certain action to packet mode |
| /pctrl amode \<action\> default | Restore to global default |

Settable actions include: attack, use, mine, place, look, drop, hotbar, inventory, chat, jump, sneak, close.

presskey is not affected by mode switching and always executes via simulated key press.

## Player Actions

### Attack

| Command | Effect |
| --- | --- |
| /pctrl attack | Attack once (the target pointed at by the crosshair) |
| /pctrl attack once | Same as above |
| /pctrl attack hold | Continuously attack (long-press left click) until a stop command is received |
| /pctrl attack interval \<gt\> | Attack every N game ticks (gt), automatically repeating |
| /pctrl attack stop | Stop attacking |

In packet mode, attacking additionally sends a position correction packet to maximize hit rate.

### Use/Interact

| Command | Effect |
| --- | --- |
| /pctrl use | Use held item / interact with block / interact with entity once |
| /pctrl use hold | Continuously use (long-press right click) |
| /pctrl use stop | Stop using |

### Other Actions

| Command | Effect |
| --- | --- |
| /pctrl jump | Jump once |
| /pctrl sneak | Toggle sneak state (on/off cycle) |
| /pctrl sneak true | Enter sneak |
| /pctrl sneak false | Cancel sneak |
| /pctrl drop | Drop the currently held item (one) |
| /pctrl drop all | Drop the currently held item (all) |
| /pctrl hotbar \<1-9\> | Switch hotbar slot |
| /pctrl inventory | Open inventory screen |
| /pctrl close | Close the currently open screen |
| /pctrl chat \<message\> | Send chat message or command (without /) |

## Camera Control

| Command | Effect |
| --- | --- |
| /pctrl look \<yaw\> \<pitch\> | Set the camera directly to the specified yaw and pitch |

## Mining / Placing

### Mining

| Command | Effect |
| --- | --- |
| /pctrl mine | Mine the block the crosshair is pointing at |
| /pctrl mine \<x\> \<y\> \<z\> | Mine the block at the specified coordinates |
| /pctrl mine \<x\> \<y\> \<z\> interval \<gt\> | Repeat mining the block at the specified coordinates every N ticks |
| /pctrl mine region | Mine all blocks in the selected region |
| /pctrl mine stop | Stop the ongoing repeated mining |

### Placing

| Command | Effect |
| --- | --- |
| /pctrl place | Place a block at the block position the crosshair is pointing at |
| /pctrl place \<x\> \<y\> \<z\> | Place a block at the specified coordinates |
| /pctrl place region | Place a block at every position in the selected region |

When placing, positions with existing blocks are automatically skipped. You can override replaceable blocks like water, lava, grass, etc., via the "Replaceable Placement" toggle below.

## Replaceable Placement Mode

| Command | Effect |
| --- | --- |
| /pctrl replaceable | Toggle replaceable placement switch |
| /pctrl replaceable true | Enable: allow placing blocks at positions with non-air blocks (water, grass, vines, etc.) |
| /pctrl replaceable false | Disable: only place at air positions |

## Region System (Region Operations)

Region operations are used to mark a cuboid area for use with mine region and place region.

| Command | Effect |
| --- | --- |
| /pctrl region start | Set the player's current standing position as the region start point |
| /pctrl region start \<x\> \<y\> \<z\> | Set the specified coordinates as the region start point |
| /pctrl region end | Set the player's current standing position as the region end point |
| /pctrl region end \<x\> \<y\> \<z\> | Set the specified coordinates as the region end point |
| /pctrl region clear | Clear the selected region |

The start and end points have no order requirement.

## Item Search

| Command | Effect |
| --- | --- |
| /pctrl finditem \<name\> | Search the inventory for an item matching the name; if found, automatically switch it to the hand (if in the main inventory, it will be swapped into the hotbar) |

## Key Press Simulation

| Command | Effect |
| --- | --- |
| /pctrl presskey \<key name\> | Simulate pressing the specified keyboard key, supports Tab completion |

Supported popular key names include: all letters A-Z, numbers 0-9, F1-F25, Space, Enter, Tab, Shift/Ctrl/Alt/Super (with left/right distinction), Esc, Backspace, Delete, Insert, arrow keys (Up, Down, Left, Right), PgUp/PgDn, Home, End, CapsLock, ScrollLock, NumLock, PrintScreen, Pause, Menu, numpad (kp_0~kp_9), symbol keys (comma, period, slash, semicolon, equals, etc.), world1/world2.

presskey always uses simulated key press mode, unaffected by the global mode.

## Other Settings

| Command | Effect |
| --- | --- |
| /pctrl feedback | Toggle command feedback messages switch |
| /pctrl feedback true/false | Directly enable or disable command feedback |
| /pctrl dist \<distance\> | Set the maximum operation distance for mining/placing (default 6 blocks, minimum 1 block) |
| /pctrl stop | Stop all ongoing operations (attacking, using, mining) and cancel sneaking |
| /pctrl look \<yaw\> \<pitch\> | Set camera |

## Operation Distance Note

Mining and placing operations are limited by dist. If the target block is beyond the distance, the command will prompt and refuse to execute.

## Notes

- This mod is a client-side only mod; it does not require server-side installation and works with vanilla servers
- Packet mode may be falsely detected by anti-cheat systems on some servers, use with caution
- Region mining/placing does not differentiate block types and will attempt to operate on all blocks in the region
- Blocks in the region beyond the operation distance will be automatically skipped
- Continuous operations (hold/interval) only execute while the player is online and the client is not lagging
- Switching hotbar is also done via simulated key presses or packets and will not conflict with custom clients
- This mod does not automatically switch tools or consider optimal mining efficiency; please prepare suitable tools yourself
