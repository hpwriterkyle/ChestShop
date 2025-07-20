package top.vrilhyc.plugins.chestshop.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.vrilhyc.plugins.chestshop.MundaneChestShop;

@Mixin(Level.class)
public class ServerLevelMixin {
    @SuppressWarnings("rawtypes")
    @Inject(method = "destroyBlock",at=@At(value = "HEAD"), cancellable = true)
    public void chestshop_destroyed(BlockPos pos, boolean dropBlock, Entity breaker, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level) (Object)this;
        if(!(level instanceof ServerLevel)){
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof ChestBlockEntity entity) {
            if(MundaneChestShop.isShop(entity)){
                //TODO
                MundaneChestShop shop = MundaneChestShop.fromBlockEntity(entity);
                if(breaker!=null&&!shop.getOwnerID().equals(breaker.getUUID())){
                    cir.cancel();
                    return;
                }
                shop.onBroken((ServerLevel) level);
                return;
            }
//            player.level().addFreshEntity(new Display.ItemDisplay(EntityType.ITEM,player.level()).tran);
        }
    }

}