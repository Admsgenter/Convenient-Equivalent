package admsgenter.convenient_projecte.mixin;

import admsgenter.convenient_projecte.空间知识;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KnowledgeImpl.Provider.class)
public abstract class KnowledgeImpl$ProviderMixin {
    @Unique
    private static Player CvE$玩家;

    @Redirect(at = @At(target = "Lmoze_intel/projecte/impl/capability/KnowledgeImpl$DefaultImpl;", value = "NEW"), method = "<init>", remap = false)
    private static KnowledgeImpl.DefaultImpl init(Player 玩家) {
        CvE$玩家 = 玩家;
        return (KnowledgeImpl.DefaultImpl) KnowledgeImpl.getDefault();
    }

    @ModifyArg(at = @At(target = "Lmoze_intel/projecte/capability/managing/SerializableCapabilityResolver;<init>(Lnet/minecraftforge/common/util/INBTSerializable;)V", value = "INVOKE"), method = "<init>", remap = false)
    private static INBTSerializable<CompoundTag> init(INBTSerializable<CompoundTag> 原值) {
        INBTSerializable<CompoundTag> 知识 = new 空间知识(CvE$玩家);
        CvE$玩家 = null;
        return 知识;
    }
}