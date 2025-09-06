package meteordevelopment.meteorclient.systems.modules.fun;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

import static meteordevelopment.meteorclient.MeteorClient.mc;

// made by 0113

public class ParticleAura extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<ParticleType> particleType = sgGeneral.add(new EnumSetting.Builder<ParticleType>()
        .name("particle-type")
        .description("What type of particle to spawn.")
        .defaultValue(ParticleType.HEART)
        .build()
    );

    private final Setting<Double> spawnRate = sgGeneral.add(new DoubleSetting.Builder()
        .name("spawn-rate")
        .description("How many particles to spawn per tick.")
        .defaultValue(1.0)
        .min(0.1)
        .sliderMax(10.0)
        .build()
    );

    private final Setting<Double> radius = sgGeneral.add(new DoubleSetting.Builder()
        .name("radius")
        .description("Radius around the player to spawn particles.")
        .defaultValue(0.5)
        .min(0.1)
        .sliderMax(2.0)
        .build()
    );

    private final Random random = new Random();

    public ParticleAura() {
        super(Categories.Fun, "particle-aura", "Spawns particles around you.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        for (int i = 0; i < spawnRate.get(); i++) {
            double x = mc.player.getX() + (random.nextDouble() * 2 - 1) * radius.get();
            double y = mc.player.getY() + (random.nextDouble() * 2 - 1) * radius.get();
            double z = mc.player.getZ() + (random.nextDouble() * 2 - 1) * radius.get();

            mc.particleManager.addParticle(getParticleEffect(), x, y, z, 0.0, 0.0, 0.0);
        }
    }

    private ParticleEffect getParticleEffect() {
        return switch (particleType.get()) {
            case HEART -> ParticleTypes.HEART;
            case CRIT -> ParticleTypes.CRIT;
            case HAPPY_VILLAGER -> ParticleTypes.HAPPY_VILLAGER;
            case FLAME -> ParticleTypes.FLAME;
            case SMOKE -> ParticleTypes.SMOKE;
            case SPIT -> ParticleTypes.SPIT;
        };
    }

    public enum ParticleType {
        HEART,
        CRIT,
        HAPPY_VILLAGER,
        FLAME,
        SMOKE,
        SPIT
    }
}
