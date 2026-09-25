package admsgenter.convenient_equivalent.mixin;

import moze_intel.projecte.events.PlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.capabilities.EntityCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEvents.class)
public class PlayerEventsMixin {
    @Redirect(at = @At(ordinal = 0, target = "Lnet/minecraft/server/level/ServerPlayer;getCapability(Lnet/neoforged/neoforge/capabilities/EntityCapability;)Ljava/lang/Object;", value = "INVOKE"), method = {"playerChangeDimension", "playerConnect", "respawnEvent"})
    private static Object getCapability(ServerPlayer 玩家, EntityCapability<?, ?> 能力) { return null; }
}