package meteordevelopment.meteorclient.systems.modules.fun;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;

import static meteordevelopment.meteorclient.MeteorClient.mc;

// made by 0113

public class FakeChatMessage extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> message = sgGeneral.add(new StringSetting.Builder()
        .name("message")
        .description("The message to send.")
        .defaultValue("Hello from Meteor Client!")
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay between messages in ticks.")
        .defaultValue(20)
        .min(1)
        .sliderMax(200)
        .build()
    );

    private int timer;

    public FakeChatMessage() {
        super(Categories.Fun, "fake-chat-message", "Sends fake chat messages.");
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
            mc.inGameHud.getChatHud().addMessage(Text.of(message.get()));
            timer = 0;
        }
    }
}
