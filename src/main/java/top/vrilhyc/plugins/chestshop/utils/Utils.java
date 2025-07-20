package top.vrilhyc.plugins.chestshop.utils;

import com.google.gson.Gson;
import com.mojang.math.Transformation;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.core.economy.accounts.ImpactorAccount;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import top.vrilhyc.plugins.chestshop.IChestShop;

import java.awt.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

public class Utils {
    public static Gson gson = new Gson();
    public static CompoundTag getCompound(ChestBlockEntity entity){
        DataComponentMap map = entity.components();
        CustomData tag = map.getOrDefault(DataComponents.BLOCK_ENTITY_DATA,CustomData.of(saveCompound(entity)));
        CompoundTag data = tag.copyTag();
        return data;
    }

    public static <T>T modify(Function<CompoundTag,T> compoundTagConsumer, ChestBlockEntity entity){
        DataComponentMap map = entity.components();
        CustomData tag = map.getOrDefault(DataComponents.BLOCK_ENTITY_DATA,CustomData.of(saveCompound(entity)));
        CompoundTag data = tag.copyTag();
        T t = compoundTagConsumer.apply(data);
        map = DataComponentMap.builder().addAll(map).set(DataComponents.BLOCK_ENTITY_DATA,CustomData.of(data)).build();
        entity.setComponents(map);
        return t;
    }

    public static CompoundTag saveCompound(ChestBlockEntity chestBlockEntity){
        return chestBlockEntity.saveCustomAndMetadata(RegistryAccess.EMPTY);
    }

    public static UUID displayItem(ServerLevel level, BlockPos pos, Direction direction, Item item){
        Display.ItemDisplay display = new Display.ItemDisplay(EntityType.ITEM_DISPLAY,level);
        display.setItemStack(new ItemStack(item));
        display.setItemTransform(ItemDisplayContext.GUI);
        display.setTransformation(new Transformation(new Matrix4f().translation(new Vector3f()).scale(3.0f).rotate(new Quaternionf())));
        display.teleportTo(pos.getX(),pos.getY(),pos.getZ());
        level.addFreshEntity(display);
        return display.getUUID();
    }

    public static boolean hasDeposit(ServerPlayer player,double price){
        return getAccount(player).balance().doubleValue()>price;
    }

    public static boolean deposit(ServerPlayer player,double price){
        return getAccount(player).deposit(BigDecimal.valueOf(price)).successful();
    }

    public static boolean withdraw(ServerPlayer player,double price){
        return getAccount(player).withdraw(BigDecimal.valueOf(price)).successful();
    }

    public static boolean hasStorage(IChestShop chestShop, ItemStack is){
        return chestShop.getItems().stream().map(ItemStack::getCount).reduce(Integer::sum).stream().peek(System.out::println).findAny().orElse(0)>=is.getCount();
    }

    public static Component getRunCommandComponent(String s, String command){
        return Component.literal(s).withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,command)));
    }

    public static boolean removeItem(ChestBlockEntity entity,Item item,int amount){
        for(int var1=0;var1<27;var1++){
            if(amount<=0){
                return true;
            }
            ItemStack is = entity.getItem(var1);
            if(is.is(item)){
                int rest = Math.min(amount, is.getCount());
                entity.removeItem(var1,rest);
                amount = amount-rest;
            }
        }
        return false;
    }

    public static Account getAccount(ServerPlayer player){
        try {
            return Impactor.instance().services().provide(EconomyService.class).account(player.getUUID()).get();
            }catch (Exception e){
            return null;
        }
    }
}
