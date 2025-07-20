package top.vrilhyc.plugins.chestshop;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import top.vrilhyc.plugins.chestshop.utils.Utils;

import java.util.Set;

public interface IChestShop {
    Set<ItemStack> getItems();
    double getPrice();
    ShopType getShopType();
    Item getIcon();
    void createShopOn(ServerPlayer player,ChestBlockEntity entity);

    enum ShopType{
        BUY((player,shop, is, price) -> {
            if(!Utils.hasDeposit(player,price)){
                return false;
            }
            Utils.withdraw(player,price);
            MundaneChestShop mundaneChestShop = (MundaneChestShop)shop;
            Utils.removeItem(mundaneChestShop.getEntity(),is.getItem(),is.getCount());
            player.getInventory().add(is);
            return true;
        }),
        SELL((player, shop, is, price) ->{
            if(!Utils.hasStorage(shop,is)){
                return false;
            }
            Utils.deposit(player,price);
            player.getInventory().removeItem(player.getInventory().findSlotMatchingItem(is),1);
            return true;
        });

        private TradeImpl trade;

        ShopType(TradeImpl trade){
            this.trade = trade;
        }

        public void trade(ServerPlayer player,IChestShop shop,ItemStack is,double price){
            trade.trade(player,shop,is,price);
        }
    }
}
