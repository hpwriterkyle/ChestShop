package top.vrilhyc.plugins.chestshop.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import top.vrilhyc.plugins.chestshop.MundaneChestShop;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static top.vrilhyc.plugins.chestshop.commands.RequiredArgumentBuilder.argument;

public class MemoryCommand {
    private static final String MESSAGE_KEY = "message";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = literal("chestshop");
        builder.then(argument("operation", StringArgumentType.string())
                        .executes(a->{
                            if (!(a.getSource().getPlayer() instanceof ServerPlayer serverPlayer)) {
                                return 1;
                            }
                            handleOperation(a,serverPlayer);
                            return 1;
                        })
                        .then(argument("player",StringArgumentType.string())
                                .executes(a->{
                                    String playerName = StringArgumentType.getString(a,"player");
                                    ServerPlayer player = a.getSource().getServer().getPlayerList().getPlayerByName(playerName);
                                    if(player==null){
                                        return 1;
                                    }
                                    handleOperation(a,player);
                                    return 1;
                                })
                        )/**.suggests((a,b)->{**/
//                            for(ServerPlayer player:a.getSource().getLevel().getServer().getPlayerList().getPlayers()){
//                                b.suggest(player.getName().getString());
//                        }
//                            return b.buildFuture();
//                })
                );

        dispatcher.register(builder);
    }

    private static void acceptPlayer(ServerPlayer player,MundaneChestShop shop){
        shop.getShopType().trade(player,shop,shop.getIcon().getDefaultInstance().copyWithCount(1),shop.getPrice());
    }

    private static int handleOperation(String operation, ServerPlayer player){
        MundaneChestShop shop = MundaneChestShop.getSelectedChestShop(player.getUUID());
        if(shop==null){
            return 1;
        }
        switch (operation){
            case "accept":
                shop.getShopType().trade(player,shop,shop.getIcon().getDefaultInstance().copyWithCount(1),shop.getPrice());
        }
        return 1;
    }

    private static int handleOperation(CommandContext<CommandSourceStack> stack, ServerPlayer player){
        String operation = StringArgumentType.getString(stack,"operation");
        MundaneChestShop shop = MundaneChestShop.getSelectedChestShop(player.getUUID());
        if(shop==null){
            return 1;
        }
        switch (operation){
            case "accept":
                shop.getShopType().trade(player,shop,shop.getIcon().getDefaultInstance().copyWithCount(1),shop.getPrice());
        }
        return 1;
    }
}
