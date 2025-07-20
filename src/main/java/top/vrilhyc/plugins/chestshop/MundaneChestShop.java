package top.vrilhyc.plugins.chestshop;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import top.vrilhyc.plugins.chestshop.utils.Utils;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MundaneChestShop implements IChestShop{
    protected String material;
    protected double price;
    protected ShopType type;
    protected UUID entityID;
    protected UUID ownerID;
    protected transient ChestBlockEntity entity;
    public static Map<UUID,MundaneChestShop> selectedShopMap = new ConcurrentHashMap<>();

    public MundaneChestShop(Item item,double price,ShopType type){
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
        material = location.getNamespace()+":"+location.getPath();
        this.price = price;
        this.type = type;
    }

    @Override
    public Set<ItemStack> getItems() {
        return IntStream.range(0,27).mapToObj(a-> entity.getItem(a)).collect(Collectors.toSet());
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public ShopType getShopType() {
        return type;
    }

    @Override
    public Item getIcon() {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(material));
    }

    @Override
    public void createShopOn(ServerPlayer player,ChestBlockEntity entity) {
        CompoundTag tag = new CompoundTag();
        ServerLevel level = (ServerLevel) entity.getLevel();
        this.entityID = Utils.displayItem(level,entity.getBlockPos().offset(0,1,0), Direction.NORTH,getIcon());
        this.ownerID = player.getUUID();
        this.entity = entity;
        tag.putString("shopData",Utils.gson.toJson(this,MundaneChestShop.class));
        Utils.modify(a->{
            a.put("ChestShop",tag);
            return null;
        },entity);
    }

    public void onBroken(ServerLevel level){
        level.getEntity(this.entityID).remove(Entity.RemovalReason.DISCARDED);
    }

    public int getStorageCount(){
        return getItems().stream().map(ItemStack::getCount).reduce(Integer::sum).orElse(0);
    }

    public Item getItem(){
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(material));
    }

    public void sendShopInformation(ServerPlayer player){
        selectedShopMap.put(player.getUUID(),this);
        player.displayClientMessage(getShopInformation(player),false);
    }

    public ChestBlockEntity getEntity() {
        return entity;
    }

    public UUID getOwnerID() {
        return ownerID;
    }

    private Component getShopInformation(ServerPlayer player){
        return Component.literal("There is %s %s\n".formatted(getStorageCount(),getItem().getDefaultInstance().getDisplayName().getString()))
                .append(Utils.getRunCommandComponent("Click to purchase one","/chestshop accept %s".formatted(player.getName().getString())));
    }

    public static MundaneChestShop fromBlockEntity(ChestBlockEntity block){
        String shopData = Utils.modify(a->{
            CompoundTag tag = a.getCompound("ChestShop");
           // return a.put("test", CompoundTag.)
            return tag.getString("shopData");
        },block);
        MundaneChestShop shop = Utils.gson.fromJson(shopData,MundaneChestShop.class);
        shop.entity = block;
        return shop;
    }

    public static boolean isShop(ChestBlockEntity entity){
        return Utils.modify(a->{
            return a.contains("ChestShop");
        },entity);
    }

    public static MundaneChestShop getSelectedChestShop(UUID uuid){
        return selectedShopMap.get(uuid);
    }
}
