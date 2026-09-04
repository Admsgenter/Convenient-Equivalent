package admsgenter.convenient_projecte.兼容;

import admsgenter.convenient_projecte.便捷等价;
import admsgenter.convenient_projecte.转化配方;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import moze_intel.projecte.integration.jei.world_transmute.WorldTransmuteRecipeCategory;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;

import static admsgenter.convenient_storage.common.兼容.插件.记录类型表;

@JeiPlugin
public class 插件 implements IModPlugin {
    private static final ResourceLocation 命名空间 = ResourceLocation.fromNamespaceAndPath(便捷等价.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() { return 命名空间; }

    @Override
    public void onRuntimeAvailable(IJeiRuntime 运行) {
        记录类型表.put(转化配方.记录器, Collections.singletonList(WorldTransmuteRecipeCategory.RECIPE_TYPE));
    }
}