package top.vrilhyc.plugins.chestshop.mixin;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.vrilhyc.plugins.chestshop.IChestShop;
import top.vrilhyc.plugins.chestshop.MundaneChestShop;

@Mixin(Block.class)
public class BlockMixin {
    @SuppressWarnings("rawtypes")
    @Inject(method = "playerDestroy",at=@At(value = "HEAD"), cancellable = true)
    public void chestshop_destroyed(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if(blockEntity instanceof ChestBlockEntity entity) {
            if(MundaneChestShop.isShop(entity)){
                //TODO
                MundaneChestShop shop = MundaneChestShop.fromBlockEntity(entity);
                if(player!=null&&shop.getOwnerID().equals(player.getUUID())){
                    ci.cancel();
                    return;
                }
                shop.onBroken((ServerLevel) level);
                return;
            }
//            player.level().addFreshEntity(new Display.ItemDisplay(EntityType.ITEM,player.level()).tran);
        }
    }

}