package com.lx862.svrutil;

import com.lx862.svrutil.commands.fancyKick;
import com.lx862.svrutil.commands.feed;
import com.lx862.svrutil.commands.heal;
import com.lx862.svrutil.commands.msg;
import com.lx862.svrutil.commands.opLevel;
import com.lx862.svrutil.commands.r;
import com.lx862.svrutil.commands.rootCommand;
import com.lx862.svrutil.commands.selfkill;
import com.lx862.svrutil.commands.silentKick;
import com.lx862.svrutil.commands.silentTp;
import com.lx862.svrutil.commands.where;
import com.lx862.svrutil.config.CommandConfig;
import com.lx862.svrutil.data.CommandEntry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;

public class Commands {

    public static void register(CommandDispatcher<net.minecraft.server.command.ServerCommandSource> dispatcher) {
        fancyKick.register(dispatcher);
        feed.register(dispatcher);
        heal.register(dispatcher);
        rootCommand.register(dispatcher);
        msg.register(dispatcher);
        opLevel.register(dispatcher);
        r.register(dispatcher);
        selfkill.register(dispatcher);
        silentTp.register(dispatcher);
        silentKick.register(dispatcher);
        where.register(dispatcher);
    }

    public static void finishedExecution(CommandContext<ServerCommandSource> context, CommandEntry defaultEntry) {
        CommandEntry entry = CommandConfig.getCommandEntry(defaultEntry);
        if (entry != null) {
            if (context.getSource().isExecutedByPlayer()) {
                for (String playerCommand : entry.chainedPlayerCommand) {
                    context.getSource().getServer().getCommandManager()
                            .executeWithPrefix(context.getSource().getPlayer().getCommandSource(), playerCommand);
                }
            }

            for (String serverCommand : entry.chainedServerCommand) {
                String finalServerCommand = context.getSource().isExecutedByPlayer()
                        ? serverCommand.replace("{playerName}",
                                context.getSource().getPlayer().getGameProfile().getName())
                        : serverCommand;
                context.getSource().getServer().getCommandManager().executeWithPrefix(
                        context.getSource().getServer().getCommandSource().withSilent(), finalServerCommand);
            }
        }
    }
}
