
package meteordevelopment.meteorclient.systems.modules.fun;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Twerk extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder()
        .name("speed")
        .description("Speed of twerking.")
        .defaultValue(2)
        .min(1)
        .sliderMax(20)
        .build()
    );

    private int timer;

    public Twerk() {
        super(Categories.Fun, "twerk", "Twerk it like there is no tomorrow.");
    }

    @Override
    public void onActivate() {
        timer = 0;
    }

    @Override
    public void onDeactivate() {
        mc.options.sneakKey.setPressed(false);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (timer >= speed.get()) {
            mc.options.sneakKey.setPressed(!mc.options.sneakKey.isPressed());
            timer = 0;
        } else {
            timer++;
        }
    }
}
