package admsgenter.convenient_equivalent.mixin;

import admsgenter.convenient_equivalent.便捷等价;
import admsgenter.convenient_equivalent.转化功能;
import admsgenter.convenient_storage.api.常量;
import admsgenter.convenient_storage.api.面板.面板配置;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EMCHelper.class)
public class EMCHelperMixin {
    @Inject(at = @At("RETURN"), cancellable = true, method = "consumePlayerFuel", remap = false)
    private static void consumePlayerFuel(Player 玩家, long 最小值, CallbackInfoReturnable<Long> 信息) {
        if(信息.getReturnValueJ() >= 0L) return;
        面板配置 配置 = 常量.获取配置(玩家);
        if(配置 == null || !便捷等价.自动充能.获取(配置)) return;
        long EMC = 配置.空间().功能(转化功能.页面).取出(最小值);
        if(EMC > 0) 信息.setReturnValue(EMC);
    }
}