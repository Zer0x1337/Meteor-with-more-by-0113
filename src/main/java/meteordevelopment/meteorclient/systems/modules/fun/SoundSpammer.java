package meteordevelopment.meteorclient.systems.modules.fun;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.Random;

import static meteordevelopment.meteorclient.MeteorClient.mc;

// made by 0113

public class SoundSpammer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<SoundType> soundType = sgGeneral.add(new EnumSetting.Builder<SoundType>()
        .name("sound-type")
        .description("What type of sound to play.")
        .defaultValue(SoundType.COW_MOO)
        .build()
    );

    private final Setting<Double> volume = sgGeneral.add(new DoubleSetting.Builder()
        .name("volume")
        .description("Volume of the sound.")
        .defaultValue(1.0)
        .min(0.0)
        .sliderMax(2.0)
        .build()
    );

    private final Setting<Double> pitch = sgGeneral.add(new DoubleSetting.Builder()
        .name("pitch")
        .description("Pitch of the sound.")
        .defaultValue(1.0)
        .min(0.0)
        .sliderMax(2.0)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay between sounds in ticks.")
        .defaultValue(20)
        .min(1)
        .sliderMax(100)
        .build()
    );

    private final Random random = new Random();
    private int timer;

    public SoundSpammer() {
        super(Categories.Fun, "sound-spammer", "Spams sounds.");
    }

    @Override
    public void onActivate() {
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        timer++;
        if (timer >= delay.get()) {
            mc.world.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(), getSoundEvent(), mc.player.getSoundCategory(), volume.get().floatValue(), pitch.get().floatValue());
            timer = 0;
        }
    }

    private SoundEvent getSoundEvent() {
        return switch (soundType.get()) {
            case COW_MOO -> SoundEvents.ENTITY_COW_AMBIENT;
            case CHICKEN_AMBIENT -> SoundEvents.ENTITY_CHICKEN_AMBIENT;
            case PIG_AMBIENT -> SoundEvents.ENTITY_PIG_AMBIENT;
            case SHEEP_AMBIENT -> SoundEvents.ENTITY_SHEEP_AMBIENT;
            case CAT_PURR -> SoundEvents.ENTITY_CAT_PURR;
            case VILLAGER_AMBIENT -> SoundEvents.ENTITY_VILLAGER_AMBIENT;
            case SPLASH -> SoundEvents.ENTITY_GENERIC_SPLASH;
            case BELL -> SoundEvents.BLOCK_BELL_USE;
        };
    }

    public enum SoundType {
        COW_MOO,
        CHICKEN_AMBIENT,
        PIG_AMBIENT,
        SHEEP_AMBIENT,
        CAT_PURR,
        VILLAGER_AMBIENT,
        SPLASH,
        BELL
    }
}
