package top.vrilhyc.plugins.chestshop.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.vrilhyc.plugins.chestshop.IChestShop;
import top.vrilhyc.plugins.chestshop.MundaneChestShop;
import top.vrilhyc.plugins.chestshop.utils.Utils;

import java.util.Optional;

@Mixin(ServerPlayerGameMode.class)
public class PlayerInteractionMixin {

    @Final
    @Shadow
    protected ServerPlayer player;

    @SuppressWarnings("rawtypes")
    @Inject(method = "handleBlockBreakAction",at=@At(value = "RETURN"))
    public void chestshop_right_click(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction face, int maxBuildHeight, int sequence, CallbackInfo ci) {
        if(action!=ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK){
            return;
        }
        BlockEntity blockState = player.level().getBlockEntity(pos);
        if(blockState instanceof ChestBlockEntity entity) {
            if(MundaneChestShop.isShop(entity)){
                //TODO
                MundaneChestShop shop = MundaneChestShop.fromBlockEntity(entity);
                shop.sendShopInformation(player);
//                shop.getShopType().trade(player,shop,shop.getIcon().getDefaultInstance().copyWithCount(1),10);
                return;
            }
            new MundaneChestShop(player.getMainHandItem().getItem(),10, IChestShop.ShopType.BUY).createShopOn(player,entity);
//            player.level().addFreshEntity(new Display.ItemDisplay(EntityType.ITEM,player.level()).tran);
        }
    }

}