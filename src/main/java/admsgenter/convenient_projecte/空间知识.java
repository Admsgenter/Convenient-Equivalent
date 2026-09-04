package admsgenter.convenient_projecte;

import admsgenter.convenient_storage.api.常量;
import admsgenter.convenient_storage.api.面板.面板配置;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class 空间知识 implements IKnowledgeProvider {
    private final IKnowledgeProvider 部分源 = KnowledgeImpl.getDefault();
    private final Player 玩家;
    private 面板配置 配置;

    public 空间知识(Player 玩家) { this.玩家 = 玩家; }

    private 转化功能 转化功能() {
        if(配置 == null) 配置 = 常量.获取配置(玩家);
        return 配置.空间().功能(转化功能.页面);
    }

    @Override
    public boolean addKnowledge(ItemInfo 信息) { return 信息.getItem() == PEItems.TOME_OF_KNOWLEDGE.asItem()? 转化功能().学习全部():转化功能().学习(信息); }

    @Override
    public void clearKnowledge() { 部分源.clearKnowledge(); }

    @Override
    public void deserializeNBT(CompoundTag 数据) { 部分源.deserializeNBT(数据); }

    @Override
    public BigInteger getEmc() { return 转化功能().EMC.精确值(); }

    @Override
    public IItemHandler getInputAndLocks() { return 部分源.getInputAndLocks(); }

    @Override
    public Set<ItemInfo> getKnowledge() { return 转化功能().内部存储集(); }

    @Override
    public boolean hasFullKnowledge() { return 转化功能().全部; }

    @Override
    public boolean hasKnowledge(ItemInfo 信息) { return 信息.getItem() == PEItems.TOME_OF_KNOWLEDGE.asItem()? hasFullKnowledge():getKnowledge().contains(IEMCProxy.INSTANCE.getPersistentInfo(信息)); }

    @Override
    public void receiveInputsAndLocks(Map<Integer, ItemStack> 变化) { 部分源.receiveInputsAndLocks(变化); }

    @Override
    public boolean removeKnowledge(ItemInfo 信息) { return 信息.getItem() == PEItems.TOME_OF_KNOWLEDGE.asItem()? 转化功能().遗忘全部():转化功能().遗忘(信息); }

    @Override
    public CompoundTag serializeNBT() { return 部分源.serializeNBT(); }

    @Override
    public void setEmc(BigInteger EMC) { 转化功能().EMC.设置(EMC); }

    @Override
    public void setFullKnowledge(boolean 全部) {
        if(全部) 转化功能().学习全部();
        else 转化功能().遗忘全部();
    }

    @Override
    public void sync(ServerPlayer 玩家) { 部分源.sync(玩家); }

    @Override
    public void syncEmc(ServerPlayer 玩家) { 转化功能().值变化(); }

    @Override
    public void syncInputAndLocks(ServerPlayer 玩家, List<Integer> 变化, TargetUpdateType 目标) { 部分源.syncInputAndLocks(玩家, 变化, 目标); }

    @Override
    public void syncKnowledgeChange(ServerPlayer 玩家, ItemInfo 变化, boolean 学习) { }
}