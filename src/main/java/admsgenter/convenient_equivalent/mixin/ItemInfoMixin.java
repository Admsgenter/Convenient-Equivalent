package admsgenter.convenient_equivalent.mixin;

import admsgenter.convenient_storage.api.检索键.物品键;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;

/**
 * 让 {@link ItemInfo} 实现 {@link 物品键}, <br>
 * 减少检索过程中的装箱消耗
 */
@Mixin(ItemInfo.class)
public abstract class ItemInfoMixin implements 物品键 {
    @Final @Shadow(remap = false) private Holder<Item> item;
    @Final @Shadow(remap = false) private DataComponentPatch componentsPatch;
    @Unique private int CvE$ID = -1, CvE$检索码 = -1;
    @Unique private Component CvE$描述;
    @Unique private ItemStack CvE$物品组;

    @Override
    public int ID() {
        if(CvE$ID == -1) CvE$ID = 物品键.super.ID();
        return CvE$ID;
    }

    @Override
    public Component 描述() {
        if(CvE$描述 == null) CvE$描述 = 创建描述();
        return CvE$描述;
    }

    @Override
    public Item 物品() { return item.value(); }

    @Override
    public DataComponentPatch 组件() { return componentsPatch; }

    @Override
    public ItemStack 物品组() {
        if(CvE$物品组 == null) CvE$物品组 = 创建组();
        return CvE$物品组;
    }

    /**
     * @author Admsgenter
     * @reason 适应 {@link 物品键}
     */
    @Overwrite(remap = false)
    public boolean equals(Object 实例) { return this == 实例 || 实例 instanceof 物品键 键 && 物品() == 键.物品() && componentsPatch.equals(键.组件()); }

    /**
     * @author Admsgenter
     * @reason 适应 {@link 物品键}
     */
    @Overwrite(remap = false)
    public int hashCode() {
        if(CvE$检索码 == -1) CvE$检索码 = ID()+componentsPatch.hashCode()*31;
        return CvE$检索码;
    }
}