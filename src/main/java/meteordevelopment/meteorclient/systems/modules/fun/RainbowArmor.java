package meteordevelopment.meteorclient.systems.modules.fun;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class RainbowArmor extends Module {
    public RainbowArmor() {
        super(Categories.Fun, "rainbow-armor", "Makes your armor cycle through rainbow colors.");
    }

    public static Color getRainbowColor() {
        double time = System.currentTimeMillis() / 1000.0;
        double hue = (time * 50) % 360;
        return Color.fromHsv(hue, 1.0, 1.0);
    }
}
