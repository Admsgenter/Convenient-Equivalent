package admsgenter.convenient_projecte.mixin;

import admsgenter.convenient_storage.api.检索键.物品键;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

/**
 * 让 {@link ItemInfo} 实现 {@link 物品键}, <br>
 * 减少检索过程中的装箱消耗
 */
@Mixin(ItemInfo.class)
public abstract class ItemInfoMixin implements 物品键 {
    @Final @Shadow(remap = false) private Item item;
    @Final @Shadow(remap = false) private CompoundTag nbt;

    @Unique private int CvE$ID = -1;
    @Unique private Component CvE$描述;
    @Unique private ItemStack CvE$物品组;

    @Shadow(remap = false) public abstract ItemStack createStack();

    @Redirect(at = @At(target = "Lnet/minecraft/nbt/CompoundTag;isEmpty()Z", value = "INVOKE"), method = "<init>")
    private boolean init(CompoundTag 标签) { return 物品键.空标签(标签); }

    @Override
    public int ID() {
        if(CvE$ID == -1) CvE$ID = 物品键.super.ID();
        return CvE$ID;
    }

    @Override
    public CompoundTag 标签() { return nbt; }

    @Override
    public Component 描述() {
        if(CvE$描述 == null) CvE$描述 = 创建描述();
        return CvE$描述;
    }

    @Override
    public Item 物品() { return item; }

    @Override
    public ItemStack 物品组() {
        if(CvE$物品组 == null) CvE$物品组 = createStack();
        return CvE$物品组;
    }

    /**
     * @author Admsgenter
     * @reason 适应 {@link 物品键}
     */
    @Overwrite(remap = false)
    public boolean equals(Object 实例) {
        return this == 实例 || 实例 instanceof 物品键 键 && item == 键.物品() &&
                               Objects.equals(nbt, 键.标签()) && 键.空能力();
    }

    /**
     * @author Admsgenter
     * @reason 适应 {@link 物品键}
     */
    @Overwrite(remap = false)
    public int hashCode() { return ID()+Objects.hashCode(nbt)*31; }

    /**
     * @author Admsgenter
     * @reason Optimize
     */
    @Overwrite(remap = false)
    private ResourceLocation getRegistryName() { return 命名空间(); }
}