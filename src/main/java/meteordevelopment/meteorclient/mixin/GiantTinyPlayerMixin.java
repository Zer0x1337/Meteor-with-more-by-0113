package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.fun.GiantTinyPlayer;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(PlayerEntityRenderer.class)
public abstract class GiantTinyPlayerMixin {
    @Unique
    private GiantTinyPlayer giantTinyPlayer;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        giantTinyPlayer = Modules.get().get(GiantTinyPlayer.class);
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("RETURN"))
    private void updateRenderState(AbstractClientPlayerEntity player, PlayerEntityRenderState state, float f, CallbackInfo info) {
        if (!giantTinyPlayer.isActive()) return;
        if (player != mc.player) return; // Only affect the local player

        float scale = (float) giantTinyPlayer.getScale();
        state.baseScale *= scale;

        if (state.nameLabelPos != null)
            ((IVec3d) state.nameLabelPos).meteor$setY(state.nameLabelPos.y + (player.getHeight() * scale - player.getHeight()));
    }
}
