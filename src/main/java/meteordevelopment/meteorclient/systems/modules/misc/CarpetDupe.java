/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.item.BlockItem;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.CoralFanBlock;

public class CarpetDupe extends Module {

    private enum State {
        BUILDING,
        ACTIVATING,
        DONE
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay in ticks between actions.")
        .defaultValue(2)
        .min(0)
        .build()
    );

    private State state;
    private BlockPos startPos;
    private int timer;
    private int buildProgress;
    private int activationProgress;

    public CarpetDupe() {
        super(Categories.Misc, "carpet-dupe", "Builds a compact carpet dupe machine. Requires 1 sticky piston, 5 slime blocks, 1 observer, 1 lever, 3 solid blocks, 1 dead coral fan, and at least 1 carpet.");
    }

    @Override
    public void onActivate() {
        state = State.BUILDING;
        // Place the machine 2 blocks in front and 1 block down from the player
        startPos = mc.player.getBlockPos().offset(mc.player.getHorizontalFacing(), 2).down();
        timer = 0;
        buildProgress = 0;
        activationProgress = 0;

        // Check for required items
        if (!InvUtils.findInHotbar(Items.STICKY_PISTON).found() ||
            !InvUtils.findInHotbar(Items.SLIME_BLOCK).found() ||
            !InvUtils.findInHotbar(Items.OBSERVER).found() ||
            !InvUtils.findInHotbar(Items.LEVER).found() ||
            !InvUtils.findInHotbar(this::isSolidBlock).found() ||
            !InvUtils.findInHotbar(this::isDeadCoralFan).found() ||
            !InvUtils.findInHotbar(this::isCarpet).found()) {
            error("Required items not found in hotbar. Please ensure you have: 1 sticky piston, 5 slime blocks, 1 observer, 1 lever, 3 solid blocks (any type), 1 dead coral fan (any type), and at least 1 carpet (any color).");
            toggle();
            return;
        }
    }

    private boolean isSolidBlock(net.minecraft.item.ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock().getDefaultState().isSolid();
    }

    private boolean isDeadCoralFan(net.minecraft.item.ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof CoralFanBlock && stack.getItem().getTranslationKey().contains("dead");
    }

    private boolean isCarpet(net.minecraft.item.ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof CarpetBlock;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (timer > 0) {
            timer--;
            return;
        }

        Direction facing = mc.player.getHorizontalFacing();

        switch (state) {
            case BUILDING: {
                switch (buildProgress) {
                    case 0: // Place first solid block (base)
                        placeBlock(startPos, Items.STONE);
                        break;
                    case 1: // Place second solid block (behind piston)
                        placeBlock(startPos.offset(facing.getOpposite()), Items.STONE);
                        break;
                    case 2: // Place third solid block (for observer)
                        placeBlock(startPos.offset(facing.getOpposite()).up(), Items.STONE);
                        break;
                    case 3: // Place Lever on first solid block
                        BlockUtils.place(startPos, InvUtils.findInHotbar(Items.LEVER), 0, false);
                        break;
                    case 4: // Place Sticky Piston
                        placeBlock(startPos.up(), Items.STICKY_PISTON);
                        break;
                    case 5: // Place Observer (facing into sticky piston)
                        BlockUtils.place(startPos.offset(facing.getOpposite()).up(), InvUtils.findInHotbar(Items.OBSERVER), 0, false);
                        break;
                    case 6: // Place Slime Blocks (center)
                        BlockPos centerSlime = startPos.up().offset(facing);
                        placeBlock(centerSlime, Items.SLIME_BLOCK);
                        placeBlock(centerSlime.offset(facing.rotateYClockwise()), Items.SLIME_BLOCK);
                        placeBlock(centerSlime.offset(facing.rotateYCounterclockwise()), Items.SLIME_BLOCK);
                        placeBlock(centerSlime.offset(facing), Items.SLIME_BLOCK);
                        placeBlock(centerSlime.up(), Items.SLIME_BLOCK);
                        break;
                    case 7: // Place Dead Coral Fan
                        placeBlock(startPos.up().offset(facing), Items.DEAD_BRAIN_CORAL_FAN);
                        break;
                    case 8: // Place Carpets
                        BlockPos carpetPos = startPos.up().offset(facing);
                        placeBlock(carpetPos, mc.player.getMainHandStack().getItem()); // Assumes carpet in main hand
                        placeBlock(carpetPos.offset(facing.rotateYClockwise()), mc.player.getMainHandStack().getItem());
                        placeBlock(carpetPos.offset(facing.rotateYCounterclockwise()), mc.player.getMainHandStack().getItem());
                        placeBlock(carpetPos.offset(facing), mc.player.getMainHandStack().getItem());
                        break;
                    default:
                        state = State.ACTIVATING;
                        activationProgress = 0;
                        break;
                }
                buildProgress++;
                timer = delay.get();
                break;
            }
            case ACTIVATING: {
                BlockPos leverPos = startPos;
                if (activationProgress == 0) {
                    // First flick
                    BlockUtils.interact(new BlockHitResult(leverPos.toCenterPos(), Direction.UP, leverPos, true), Hand.MAIN_HAND, true);
                } else if (activationProgress == 1) {
                    // Second flick
                    BlockUtils.interact(new BlockHitResult(leverPos.toCenterPos(), Direction.UP, leverPos, true), Hand.MAIN_HAND, true);
                } else {
                    state = State.DONE;
                }
                activationProgress++;
                timer = delay.get();
                break;
            }
            case DONE: {
                info("Carpet dupe machine built and activated. Toggle the module to stop.");
                toggle();
                break;
            }
        }
    }

    private void placeBlock(BlockPos pos, net.minecraft.item.Item item) {
        FindItemResult itemResult = InvUtils.findInHotbar(item);
        if (itemResult.found()) {
            BlockUtils.place(pos, itemResult, false, 0, true);
        } else {
            error("Required item not found in hotbar: " + item.toString());
            toggle();
        }
    }
}
