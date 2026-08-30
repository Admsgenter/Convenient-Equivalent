package admsgenter.convenient_projecte;

import admsgenter.convenient_projecte.mixin.EMCMappingHandlerAccessor;
import admsgenter.convenient_storage.api.工具.可变整数;
import admsgenter.convenient_storage.api.工具.数据.复合存储器;
import admsgenter.convenient_storage.api.工具.检索表;
import admsgenter.convenient_storage.api.检索键.检索键工具;
import admsgenter.convenient_storage.api.检索键.物品键;
import admsgenter.convenient_storage.api.注册;
import admsgenter.convenient_storage.api.网络.数据包类型;
import admsgenter.convenient_storage.api.网络.数据包频道;
import admsgenter.convenient_storage.api.网络.数据编码器;
import admsgenter.convenient_storage.api.面板.面板空间;
import admsgenter.convenient_storage.api.面板.面板页面;
import admsgenter.convenient_storage.api.页面.功能.数据功能;
import admsgenter.convenient_storage.api.页面.存储.外部存储;
import admsgenter.convenient_storage.api.页面.数据.面板数据;
import admsgenter.convenient_storage.api.页面.栏位.功能栏位;
import admsgenter.convenient_storage.common.页面.功能.配置功能;
import admsgenter.convenient_storage.lib.函数.判断;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static admsgenter.convenient_storage.api.网络.数据编码器.整数组;

public class 转化功能 extends 数据功能 implements 面板数据, 配置功能, 外部存储<物品键, ItemStack> {
    public static final int 容器尺寸 = 4;
    public static final ResourceLocation ID = PECore.rl("transmutation");
    public static final 面板页面<转化功能> 页面 = 面板页面.注册功能(转化功能::new, 转化功能::new, ID, new ItemStack(PEItems.PHILOSOPHERS_STONE), false);
    private static final BigInteger 最大整数 = BigInteger.valueOf(Integer.MAX_VALUE), 最大长整数 = BigInteger.valueOf(Long.MAX_VALUE);
    private static final ItemInfo 知识之书 = ItemInfo.fromItem(PEItems.TOME_OF_KNOWLEDGE);
    private static final 数据编码器<ItemInfo> 信息编码器 = new 数据编码器<>((数据, 信息) -> {
        数据.writeId(BuiltInRegistries.ITEM, 信息.getItem());
        数据.writeNbt(信息.getNBT());
    }, 数据 -> ItemInfo.fromItem(数据.readById(BuiltInRegistries.ITEM), 数据.readAnySizeNbt()));
    private static final 数据包频道 频道 = 数据包频道.创建(ResourceLocation.fromNamespaceAndPath("cve", ""), ModList.get().getModContainerById(便捷等价.MODID).get().getModInfo().getVersion());
    private static final 数据包类型 同步转化 = 频道.主线程((数据, 信息) -> {
        转化功能.页面.get().EMC.设置(数据.<int[]>下一参数());
        if(信息.玩家().containerMenu instanceof TransmutationContainer 菜单) 菜单.transmutationInventory.updateClientTargets();
    }, 整数组);
    private static final 数据包类型 学习知识 = 频道.主线程((数据, 信息) -> {
        ItemInfo 物品信息 = 数据.下一参数();
        if((物品信息.getItem() == PEItems.TOME_OF_KNOWLEDGE.asItem()? 转化功能.页面.get().学习全部():转化功能.页面.get().学习(物品信息)) &&
           信息.玩家().containerMenu instanceof TransmutationContainer 菜单) 菜单.transmutationInventory.itemLearned();
    }, 信息编码器);
    private static final 数据包类型 遗忘知识 = 频道.主线程((数据, 信息) -> {
        ItemInfo 物品信息 = 数据.下一参数();
        if((物品信息.getItem() == PEItems.TOME_OF_KNOWLEDGE.asItem()? 转化功能.页面.get().遗忘全部():转化功能.页面.get().遗忘(物品信息)) &&
           信息.玩家().containerMenu instanceof TransmutationContainer 菜单) 菜单.transmutationInventory.itemUnlearned();
    }, 信息编码器);
    public final Set<ItemInfo> 存储集, 无效集 = new ObjectOpenHashSet<>(10, .625F);
    public final 可变整数 EMC = new 可变整数();
    public boolean 全部;
    public ItemStack 充能组 = ItemStack.EMPTY, 放能组 = ItemStack.EMPTY, 遗忘组 = ItemStack.EMPTY;
    private boolean 启用;
    private long 最大价值;

    public 转化功能(Tag 数据, 面板空间 空间) {
        super(空间);
        if(数据 instanceof CompoundTag 等价) {
            EMC.设置(等价.getIntArray("transmutationEmc"));
            存储集 = ((List<CompoundTag>) (Object) 等价.getList("knowledge", Tag.TAG_COMPOUND)).stream().map(ItemInfo::read)
                .filter(Predicates.compose(Predicates.not(Predicates.equalTo(Items.AIR)), ItemInfo::getItem)).collect(检索表::O, ObjectOpenHashSet::add, ObjectOpenHashSet::addAll);
            充能组 = 读取物品(等价.get("charge"));
            放能组 = 读取物品(等价.get("discharge"));
            遗忘组 = 读取物品(等价.get("unlearn"));
            启用 = 等价.contains("enabled");
            全部 = 等价.contains("fullknowledge");
        } else 存储集 = 检索表.O(80);
        空间.物品().添加存储(this, 8);
    }

    public 转化功能(FriendlyByteBuf 数据, 面板空间 空间) {
        super(空间);
        EMC.设置(数据编码器.整数组.读取(数据));
        int 数量 = 数据.readVarInt();
        存储集 = 检索表.O(数量);
        while(--数量 >= 0) 存储集.add(信息编码器.读取(数据));
        全部 = 数据.readBoolean();
        空间.物品().添加存储(this, 8);
    }

    public Set<ItemInfo> 内部存储集() { return 全部? Sets.union(存储集, EMCMappingHandlerAccessor.emc().keySet()):存储集; }

    public long 取出(long 值) {
        long 取出量 = EMC.减(值);
        if(取出量 > 0L) 值变化();
        return 取出量;
    }

    private void 检查价值(long 价值) { if(价值 > 最大价值) 最大价值 = 价值; }

    public boolean 学习(ItemInfo 信息) {
        ItemInfo 永久信息 = IEMCProxy.INSTANCE.getPersistentInfo(信息);
        if(存储集.add(永久信息)) {
            空间().同步数据(信息, 学习知识.创建(信息));
            long 价值 = IEMCProxy.INSTANCE.getValue(永久信息);
            if(EMC.longValue() < 价值) {
                无效集.add(永久信息);
                检查价值(价值);
            }
            return true;
        } else return false;
    }

    public boolean 学习全部() {
        if(全部) return false;
        空间().同步数据(知识之书, 学习知识.创建(知识之书));
        return 全部 = true;
    }

    public boolean 遗忘(ItemInfo 信息) {
        if(存储集.remove(信息)) {
            空间().同步数据(信息, 遗忘知识.创建(信息));
            无效集.remove(信息);
            return true;
        } else return false;
    }

    public boolean 遗忘全部() {
        if(!全部) return false;
        空间().同步数据(知识之书, 遗忘知识.创建(知识之书));
        全部 = false;
        return true;
    }

    public void 值变化() {
        空间().同步数据(同步转化.创建(new Object[]{EMC.值()}));
        long 长整数值 = EMC.longValue();
        if(长整数值 < 最大价值) {
            最大价值 = 0L;
            for(ItemInfo 信息 : 存储集) {
                long 价值 = IEMCProxy.INSTANCE.getValue(信息);
                if(长整数值 >= 价值) 检查价值(价值);
                else 无效集.add(信息);
            }
        } else {
            Iterator<ItemInfo> 迭代器 = 无效集.iterator();
            while(迭代器.hasNext()) {
                ItemInfo 信息 = 迭代器.next();
                long 价值 = IEMCProxy.INSTANCE.getValue(信息);
                if(长整数值 >= 价值) {
                    检查价值(价值);
                    迭代器.remove();
                }
            }
        }
    }

    public BigInteger 总量(物品键 键) {
        if(存储集().contains(键)) {
            long 价格 = IEMCProxy.INSTANCE.getValue(信息(键));
            if(价格 > 0L) return EMC.精确值().divide(BigInteger.valueOf(价格));
        }
        return BigInteger.ZERO;
    }

    @Override
    public int 安全总量(物品键 键) {
        BigInteger 总量 = 总量(键);
        return 总量.compareTo(最大整数) < 0? 总量.intValue():Integer.MAX_VALUE;
    }

    @Override
    public ResourceLocation ID() { return ID; }

    @Override
    public int 存入(物品键 键, int 量) {
        if(启用 && 有效(键)) {
            ItemInfo 信息 = 信息(键);
            long 价值 = IEMCProxy.INSTANCE.getValue(信息);
            if(价值 > 0L) {
                学习(信息);
                可变整数 整数 = new 可变整数(价值);
                整数.乘(量);
                EMC.加(整数);
                值变化();
                检查价值(价值);
                return 量;
            }
        }
        return 0;
    }

    @Override
    public boolean 存在(物品键 键) { return 存储集().contains(键) && EMC.longValue() >= IEMCProxy.INSTANCE.getValue(信息(键)); }

    @Override
    public int 堆叠数量(int 索引) { return 索引 == 0? Integer.MAX_VALUE:getMaxStackSize(); }

    @Override
    public int 取出(物品键 键, int 量) {
        if(启用) {
            ItemInfo 信息 = 信息(键);
            if(存在(键)) {
                long 价格 = IEMCProxy.INSTANCE.getValue(信息);
                if(价格 > 0L) {
                    int 数量 = 最小值(EMC.精确值().divide(BigInteger.valueOf(价格)), BigInteger.valueOf(量)).intValue();
                    可变整数 价值 = new 可变整数(价格);
                    价值.乘(数量);
                    EMC.减(价值);
                    值变化();
                    return 数量;
                }
            }
        }
        return 0;
    }

    @Override
    public long 精确总量(物品键 键) {
        BigInteger 总量 = 总量(键);
        return 长整数值(总量);
    }

    @Override
    public Set<物品键> 存储集() { return 启用? (Set<物品键>) (Set<?>) Sets.filter(内部存储集(), Predicates.<Object>not(((Set<Object>)(Set<?>)无效集)::contains)):Collections.emptySet(); }

    @Override
    public int 添加栏位(UnaryOperator<Slot> 物品栏位, UnaryOperator<DataSlot> 数据栏位, int 存储坐标) {
        int 索引 = 0;
        Predicate<ItemStack> 条件 = 物品组 -> IEMCProxy.INSTANCE.getValue(ItemInfo.fromStack(物品组)) != 0L;
        Predicate<ItemStack> 全部 = 条件.or(判断.参数(ItemStack::is, PEItems.TOME_OF_KNOWLEDGE.asItem()));
        物品栏位.apply(功能栏位.条件(索引++, 115, 存储坐标+40, this, 全部));
        物品栏位.apply(功能栏位.条件(索引++, 97, 存储坐标+40, this, 全部));
        物品栏位.apply(功能栏位.条件(索引++, 61, 存储坐标+13, this, 条件));
        物品栏位.apply(功能栏位.条件(索引++, 151, 存储坐标+13, this, 条件));
        return 索引;
    }

    @Override
    public 面板页面<转化功能> 页面() { return 页面; }

    @Override
    public 复合存储器 序列化() {
        ListTag 列表 = new ListTag();
        for(ItemInfo 信息 : 存储集) 列表.add(信息.write(new CompoundTag(检索表.OO(2))));
        return new 复合存储器().列表("transmutationEmc", new IntArrayTag(EMC.值())).列表("knowledge", 列表)
            .物品组("charge", 充能组).物品组("discharge", 放能组).物品组("unlearn", 遗忘组).逻辑("enabled", 启用).逻辑("fullknowledge", 全部);
    }

    @Override
    public void 序列化(FriendlyByteBuf 数据) {
        数据编码器.整数组.写入(数据, EMC.值());
        数据.writeVarInt(存储集.size());
        for(ItemInfo 信息 : 存储集) 信息编码器.写入(数据, 信息);
        数据.writeBoolean(全部);
    }

    @Override
    public 检索键工具<物品键, ItemStack> 序列化工具() { return 注册.物品工具; }

    @Override
    public void 反转() {
        启用 = !启用;
        同步();
    }

    @Override
    public boolean 配置() { return 启用; }

    @Override
    public void 写入(FriendlyByteBuf 数据包) { 数据包.writeBoolean(启用); }

    @Override
    public void 读取(FriendlyByteBuf 数据包) { 启用 = 数据包.readBoolean(); }

    @Override
    public int getContainerSize() { return 容器尺寸; }

    @Override
    public ItemStack getItem(int 索引) {
        return switch(索引) {
            case 1 -> 遗忘组;
            case 2 -> 充能组;
            case 3 -> 放能组;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void setItem(int 索引, ItemStack 物品组) {
        switch(索引) {
            case 0 -> {
                if(!物品组.isEmpty()) if(!物品组.is(PEItems.TOME_OF_KNOWLEDGE.asItem())) {
                    ItemInfo 信息 = ItemInfo.fromStack(物品组);
                    EMC.加(IEMCProxy.INSTANCE.getValue(信息)*物品组.getCount());
                    值变化();
                    学习(信息);
                } else 学习全部();
            }
            case 1 -> {
                if(!(遗忘组 = 物品组).isEmpty()) if(物品组.is(PEItems.TOME_OF_KNOWLEDGE.asItem())) 遗忘全部();
                else 遗忘(ItemInfo.fromStack(物品组));
            }
            case 2 -> {
                if((充能组 = 物品组).isEmpty()) break;
                学习(ItemInfo.fromStack(物品组));
                物品组.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).ifPresent(电池 -> {
                    EMC.减(电池.insertEmc(物品组, EMC.longValue(), IEmcStorage.EmcAction.EXECUTE));
                    值变化();
                });
            }
            case 3 -> {
                if((放能组 = 物品组).isEmpty()) break;
                学习(ItemInfo.fromStack(物品组));
                物品组.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).ifPresent(电池 -> {
                    EMC.加(电池.extractEmc(物品组, Long.MAX_VALUE, IEmcStorage.EmcAction.EXECUTE));
                    值变化();
                });
            }
        }
    }

    private static long 长整数值(BigInteger 总量) { return 总量.compareTo(最大长整数) < 0? 总量.longValue():Long.MAX_VALUE; }

    private static ItemInfo 信息(物品键 键) { return ItemInfo.fromItem(键.物品(), 键.标签()); }

    private static boolean 有效(物品键 键) { return 键.空能力() && (键.空标签() || 键.标签().size() == 1 && 键.物品组().isDamageableItem()); }

    private static BigInteger 最小值(BigInteger a, BigInteger b) { return a.compareTo(b) < 0? a:b; }
}