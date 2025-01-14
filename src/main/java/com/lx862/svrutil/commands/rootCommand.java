package com.lx862.svrutil.commands;

import static com.lx862.svrutil.ModInfo.*;

import java.util.List;

import com.lx862.svrutil.Commands;
import com.lx862.svrutil.Mappings;
import com.lx862.svrutil.ModInfo;
import com.lx862.svrutil.config.CommandConfig;
import com.lx862.svrutil.config.Config;
import com.lx862.svrutil.data.CommandEntry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.Formatting;

public class rootCommand {
    private static final CommandEntry defaultEntry = new CommandEntry(MOD_ID, 2, MOD_ID + ".command."+MOD_ID, true);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        final CommandEntry entry = CommandConfig.getCommandEntry(defaultEntry);
        if (!entry.enabled)
            return;

        dispatcher.register(CommandManager.literal(entry.commandName)
                .requires(Permissions.require(entry.permApiNode, entry.permLevel))
                .executes(rootCommand::executeAbout)
                .then(CommandManager.literal("reload")
                        .executes(rootCommand::executeReload)));
    }

    private static int executeAbout(CommandContext<ServerCommandSource> context) {
        String version = ModInfo.getVersion();
        String descripton = ModInfo.getDescription();
        String authoString = ModInfo.getAuthors();
        String homepageUrl = ModInfo.getHomepage();

        Mappings.sendFeedback(context, Mappings.literalText(ModInfo.MOD_NAME).formatted(Formatting.GOLD), false);
        Mappings.sendFeedback(context, Mappings.literalText("Version " + version), false);
        Mappings.sendFeedback(context, Mappings.literalText("Description " + descripton), false);
        Mappings.sendFeedback(context, Mappings.literalText("Authored By " + authoString), false);
        final ClickEvent openHomepageEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, homepageUrl);
        Mappings.sendFeedback(context,
                Mappings.literalText(homepageUrl).styled(style -> style.withClickEvent(openHomepageEvent))
                        .formatted(Formatting.UNDERLINE).formatted(Formatting.GREEN),
                false);
        Commands.finishedExecution(context, defaultEntry);
        return 1;
    }

    private static int executeReload(CommandContext<ServerCommandSource> context) {
        Mappings.sendFeedback(context, Mappings.literalText("Reloading Config...").formatted(Formatting.GOLD), false);
        List<String> error = Config.loadAll();
        if (!error.isEmpty()) {
            String failed = String.join(",", error);
            Mappings.sendFeedback(context,
                    Mappings.literalText("Config Reloaded. " + failed + " failed to load.").formatted(Formatting.RED),
                    false);
            Mappings.sendFeedback(context,
                    Mappings.literalText("Please check whether the JSON syntax is correct!").formatted(Formatting.RED),
                    false);
        } else {
            Mappings.sendFeedback(context,
                    Mappings.literalText(ModInfo.MOD_NAME + " Config Reloaded!").formatted(Formatting.GREEN), false);
        }
        Commands.finishedExecution(context, defaultEntry);
        return 1;
    }
}
