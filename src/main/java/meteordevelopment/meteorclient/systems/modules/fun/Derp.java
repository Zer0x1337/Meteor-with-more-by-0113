
package meteordevelopment.meteorclient.systems.modules.fun;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.utils.player.Rotations;

import java.util.Random;

// made by 0113

public class Derp extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed")
        .description("Speed of head movement.")
        .defaultValue(10.0)
        .min(0.0)
        .sliderMax(20.0)
        .build()
    );

    private final Random random = new Random();

    public Derp() {
        super(Categories.Fun, "derp", "Makes your head move around randomly.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();

        yaw += (random.nextFloat() * 2 - 1) * speed.get();
        pitch += (random.nextFloat() * 2 - 1) * speed.get();

        if (pitch > 90) pitch = 90;
        if (pitch < -90) pitch = -90;

        Rotations.setCamRotation(yaw, pitch);
    }
}
