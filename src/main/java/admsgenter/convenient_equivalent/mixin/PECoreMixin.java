package admsgenter.convenient_equivalent.mixin;

import admsgenter.convenient_equivalent.空间知识;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PECore.class)
public abstract class PECoreMixin {
    @ModifyArg(at = @At(target = "Lnet/neoforged/neoforge/capabilities/RegisterCapabilitiesEvent;registerEntity(Lnet/neoforged/neoforge/capabilities/EntityCapability;Lnet/minecraft/world/entity/EntityType;Lnet/neoforged/neoforge/capabilities/ICapabilityProvider;)V", ordinal = 1, value = "INVOKE"), index = 2, method = "registerCapabilities")
    private <E extends Player, C, T extends IKnowledgeProvider> ICapabilityProvider<E, C, T> registerEntity(ICapabilityProvider<E, C, T> 提供) { return (玩家, 信息) -> (T) new 空间知识(玩家); }
}