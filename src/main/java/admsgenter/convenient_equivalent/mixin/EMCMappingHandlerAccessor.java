package admsgenter.convenient_equivalent.mixin;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.emc.EMCMappingHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EMCMappingHandler.class)
public interface EMCMappingHandlerAccessor {
    @Accessor(value = "emc", remap = false)
    static Object2LongMap<ItemInfo> emc() { return null; }
}