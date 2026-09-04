package admsgenter.convenient_projecte;

import admsgenter.convenient_storage.api.客户端;
import admsgenter.convenient_storage.api.界面.功能界面;
import admsgenter.convenient_storage.api.配置.逻辑配置;
import admsgenter.convenient_storage.common.渲染.容器按钮;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(便捷等价.MODID)
@Mod.EventBusSubscriber
public class 便捷等价 {
    public static final String MODID = "convenient_projecte";
    public static final 逻辑配置 自动充能 = new 逻辑配置(PECore.rl("auto_charge"), false);

    public 便捷等价(final FMLJavaModLoadingContext 信息) {
        信息.getModEventBus().addListener((final FMLClientSetupEvent 事件) -> {
            Component EMC = PELang.EMC_TOOLTIP.translate("");
            ResourceLocation 背景 = PECore.rl("textures/gui/transmute.png");
            功能界面.注册(转化功能.页面, (GUI, 鼠标x, 鼠标y, 高级) -> {
                GUI.blit(背景, 96, 39, 88, 96, 36, 18);
                GUI.drawString(客户端.字体, EMC, 34, 36, 0x404040, false);
                GUI.drawString(客户端.字体, TransmutationEMCFormatter.formatEMC(转化功能.页面.get().EMC), 34, 46, 0x404040, false);
            }, new 容器按钮(33, 3, Component.translatable("tooltip.projecte.enabled"), 转化功能.页面));
        });
    }
}