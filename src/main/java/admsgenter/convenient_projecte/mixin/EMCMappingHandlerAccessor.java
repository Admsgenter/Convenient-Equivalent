package admsgenter.convenient_projecte.mixin;

import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.emc.EMCMappingHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(EMCMappingHandler.class)
public interface EMCMappingHandlerAccessor {
    @Accessor(value = "emc", remap = false)
    static Map<ItemInfo, Long> emc() { return null; }
}