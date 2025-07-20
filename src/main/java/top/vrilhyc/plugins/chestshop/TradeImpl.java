package top.vrilhyc.plugins.chestshop;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface TradeImpl{
    boolean trade(ServerPlayer player,IChestShop shop, ItemStack is,double price);
}
