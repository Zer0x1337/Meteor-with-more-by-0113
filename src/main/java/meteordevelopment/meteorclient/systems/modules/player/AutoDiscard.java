
package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class AutoDiscard extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Mode to use for discarding items.")
        .defaultValue(Mode.Blacklist)
        .build()
    );

    private final Setting<List<Item>> filter = sgGeneral.add(new ItemListSetting.Builder()
        .name("filter")
        .description("Items to discard or keep.")
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay in ticks between discarding items.")
        .defaultValue(1)
        .min(0)
        .sliderMax(20)
        .build()
    );

    private int timer;

    public AutoDiscard() {
        super(Categories.Player, "auto-discard", "Automatically discards items from your inventory.");
    }

    @Override
    public void onActivate() {
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (timer > 0) {
            timer--;
            return;
        }

        for (int i = 9; i < 36; i++) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (itemStack.isEmpty()) continue;

            boolean shouldDiscard = false;
            if (mode.get() == Mode.Blacklist) {
                if (filter.get().contains(itemStack.getItem())) {
                    shouldDiscard = true;
                }
            } else {
                if (!filter.get().contains(itemStack.getItem())) {
                    shouldDiscard = true;
                }
            }

            if (shouldDiscard) {
                InvUtils.drop().slot(i);
                timer = delay.get();
                return;
            }
        }
    }

    public enum Mode {
        Blacklist,
        Whitelist
    }
}
