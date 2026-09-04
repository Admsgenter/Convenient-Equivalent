package admsgenter.convenient_projecte;

import admsgenter.convenient_storage.api.常量;
import admsgenter.convenient_storage.api.数据.复合存储器;
import admsgenter.convenient_storage.api.检索键.检索键;
import admsgenter.convenient_storage.api.检索键.流体键;
import admsgenter.convenient_storage.api.检索键.物品键;
import admsgenter.convenient_storage.api.配方.配方信息;
import admsgenter.convenient_storage.api.配方.配方记录;
import admsgenter.convenient_storage.api.配方.配方记录器;
import admsgenter.convenient_storage.api.面板.面板空间;
import admsgenter.convenient_storage.lib.工具.自动加载;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.WorldTransmutationEntry;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidType;

import java.util.Collections;
import java.util.List;

@自动加载
public class 转化配方 implements 配方记录 {
    public static final 配方记录器 记录器 = new 配方记录器.构建器<>(1, 1, "tooltip.convenient_projecte.recipe.world_transmute", 转化配方::new, 转化配方::new).命名空间(转化功能.页面.命名空间()).配方获取((容器, 世界) -> {
        BlockState 状态 = 获取(容器.获取键(0));
        if(!状态.isAir()) for(WorldTransmutationEntry 配方 : WorldTransmutations.getWorldTransmutations())
            if(配方.origin() == 状态) return 配方.result() == 配方.altResult()? Collections.singletonList(new 转化配方(状态, 配方.result())):List.of(new 转化配方(状态, 配方.result()), new 转化配方(状态, 配方.altResult()));
        return Collections.emptyList();
    }).图标(Component.translatable("jei.projecte.world_transmute"), 转化功能.页面.图标).构建();
    private final 检索键 输入, 输出;
    private boolean 有效;
    private Component 描述;
    private ResourceLocation 命名空间;

    private static 检索键 获取(BlockState 状态) { return 状态.getFluidState().isEmpty()? 物品键.创建(状态.getBlock().asItem()):流体键.创建(状态.getFluidState().getType()); }

    private static BlockState 获取(检索键 键) {
        return 键.类型() == 物品键.class? ((物品键) 键).物品() instanceof BlockItem 方块? 方块.getBlock().defaultBlockState():Blocks.AIR.defaultBlockState():
            键.类型() == 流体键.class? ((流体键) 键).流体().defaultFluidState().createLegacyBlock():Blocks.AIR.defaultBlockState();
    }

    public 转化配方(BlockState 输入, BlockState 输出) {
        this.输入 = 获取(输入);
        this.输出 = 获取(输出);
    }

    public 转化配方(CompoundTag 数据) {
        输入 = 检索键.读取(数据.getCompound("input"));
        输出 = 检索键.读取(数据.getCompound("output"));
    }

    public 转化配方(FriendlyByteBuf 数据) {
        输入 = 检索键.读取(数据);
        输出 = 检索键.读取(数据);
    }

    @Override
    public Object2IntMap<? extends 检索键> 催化剂() { return Object2IntMaps.singleton(物品键.创建(PEItems.PHILOSOPHERS_STONE.asItem()), 1); }

    @Override
    public void 使用(面板空间 空间, int 次数) { 空间.存入(输出, 空间.取出(输入, 次数)*输出总量()); }

    @Override
    public boolean 使用(面板空间 空间, 配方信息 信息) {
        int 次数 = 信息.合成上限 < 0? Integer.MAX_VALUE:常量.上除(信息.合成上限-空间.安全总量(输出()), 输出总量());
        if(次数 <= 0 || (次数 = Math.min(次数, 空间.安全总量(输入)-信息.保留下限)) <= 0) return false;
        使用(空间, 次数);
        return true;
    }

    @Override
    public 配方记录器 记录器() { return 记录器; }

    @Override
    public 检索键 输出() { return 输出; }

    @Override
    public int 输出总量() { return 输出.类型() == 物品键.class? 1:FluidType.BUCKET_VOLUME; }

    @Override
    public 检索键 输入(int x, int y) { return 输入; }

    @Override
    public Object2IntMap<? extends 检索键> 输入表() { return Object2IntMaps.singleton(输入, 输入.类型() == 物品键.class? 1:FluidType.BUCKET_VOLUME); }

    @Override
    public void 刷新(Level 世界, Player 玩家) {
        BlockState 输入 = 获取(this.输入), 输出 = 获取(this.输出);
        for(WorldTransmutationEntry 配方 : WorldTransmutations.getWorldTransmutations())
            if(配方.origin() == 输入) {
                有效 = 配方.result() == 输出 || 配方.altResult() == 输出;
                return;
            }
        有效 = false;
    }

    @Override
    public 复合存储器 序列化() { return new 复合存储器().存储("input", 检索键.序列化(输入)).存储("output", 检索键.序列化(输出)); }

    @Override
    public void 序列化(FriendlyByteBuf 数据包) {
        检索键.写入(数据包, 输入);
        检索键.写入(数据包, 输出);
    }

    @Override
    public Component 描述() {
        if(描述 == null) 描述 = 创建描述();
        return 描述;
    }

    @Override
    public ResourceLocation 命名空间() {
        if(命名空间 == null) 命名空间 = PECore.rl(输入.命名空间().toLanguageKey()+"-"+输出.命名空间().toLanguageKey());
        return 命名空间;
    }

    @Override
    public boolean 有效() { return 有效; }

    @Override
    public boolean equals(Object 实例) { return 实例 instanceof 转化配方 配方 && 配方.输入.equals(输入) && 配方.输出.equals(输出); }

    @Override
    public int hashCode() { return 输入.hashCode()^输出.hashCode(); }
}