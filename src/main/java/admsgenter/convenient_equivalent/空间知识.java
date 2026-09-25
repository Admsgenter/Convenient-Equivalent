package admsgenter.convenient_equivalent;

import admsgenter.convenient_storage.api.常量;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.math.BigInteger;
import java.util.Set;

public class 空间知识 extends KnowledgeImpl {
    private final Player 玩家;

    public 空间知识(Player 玩家) {
        super(玩家);
        this.玩家 = 玩家;
    }

    private 转化功能 转化功能() { return 常量.获取配置(玩家).空间().功能(转化功能.页面); }

    @Override
    public boolean addKnowledge(ItemInfo 信息) { return 信息.getItem() == PEItems.TOME_OF_KNOWLEDGE.asItem()? 转化功能().学习全部():转化功能().学习(信息); }

    @Override
    public BigInteger getEmc() { return 转化功能().EMC.精确值(); }

    @Override
    public Set<ItemInfo> getKnowledge() { return 转化功能().内部存储集(); }

    @Override
    public boolean hasFullKnowledge() { return 转化功能().全部; }

    @Override
    public boolean hasKnowledge(ItemInfo 信息) { return 信息.getItem() == PEItems.TOME_OF_KNOWLEDGE.asItem()? hasFullKnowledge():getKnowledge().contains(IEMCProxy.INSTANCE.getPersistentInfo(信息)); }

    @Override
    public boolean removeKnowledge(ItemInfo 信息) { return 信息.getItem() == PEItems.TOME_OF_KNOWLEDGE.asItem()? 转化功能().遗忘全部():转化功能().遗忘(信息); }

    @Override
    public void setEmc(BigInteger EMC) { 转化功能().EMC.设置(EMC); }

    @Override
    public void setFullKnowledge(boolean 全部) {
        if(全部) 转化功能().学习全部();
        else 转化功能().遗忘全部();
    }

    @Override
    public void syncEmc(ServerPlayer 玩家) { 转化功能().值变化(); }

    @Override
    public void syncKnowledgeChange(ServerPlayer 玩家, ItemInfo 变化, boolean 学习) {}
}