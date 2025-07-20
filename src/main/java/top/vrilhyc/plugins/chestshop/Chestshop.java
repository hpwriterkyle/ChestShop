package top.vrilhyc.plugins.chestshop;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import top.vrilhyc.plugins.chestshop.commands.MemoryCommand;

public class Chestshop implements ModInitializer {
    public static final String MOD_ID = "chestshop";
    public static final Config config = new Config();

    @Override
    public void onInitialize() {
        loadData();
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
            MemoryCommand.register(commandDispatcher);
        });
        PlayerBlockBreakEvents.BEFORE.register((level, player, blockPos, blockState, blockEntity) -> {
            if(blockEntity instanceof ChestBlockEntity entity) {
                if(MundaneChestShop.isShop(entity)){
                    //TODO
                    MundaneChestShop shop = MundaneChestShop.fromBlockEntity(entity);
                    if(player!=null&&!shop.getOwnerID().equals(player.getUUID())){
                        return false;
                    }
                    shop.onBroken((ServerLevel) level);
                    return true;
                }
//            player.level().addFreshEntity(new Display.ItemDisplay(EntityType.ITEM,player.level()).tran);
            }
            return true;
        });
    }

    public static void loadData(){
        config.load();
    }
}
