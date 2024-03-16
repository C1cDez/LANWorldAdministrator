package com.cicdez.lanwa;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.*;
import net.minecraft.command.server.CommandBanIp;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.command.server.CommandPardonPlayer;
import net.minecraft.command.server.CommandTestFor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.List;

public class LanwaCommand extends CommandBase {
    @Override
    public String getName() {
        return "lanwa";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return server.getServerOwner().equals(sender.getName());
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/lanwa kick/ban/pardon <playerName> <reason>";
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            String action = args[0];
            String username = args[1];
            
            EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(username);
            GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(username);
            GameProfile bannedProfile = server.getPlayerList().getBannedPlayers().getBannedProfile(username);

            String reason = args.length > 2 ? makeReason(args, 2) : null;
            
            if (action.equalsIgnoreCase("kick")) kick(sender, player, reason);
            if (action.equalsIgnoreCase("ban")) ban(server, profile, player, sender, reason);
            if (action.equalsIgnoreCase("pardon")) pardon(sender, server, bannedProfile);
            if (action.equalsIgnoreCase("kill")) kill(player);
        } else throw new CommandException(getUsage(sender));
    }
    
    public void kick(ICommandSender sender, EntityPlayerMP player, String reason) throws CommandException {
        if (player != null) {
            if (reason == null) reason = "You was kicked by Admin!";
            player.connection.disconnect(new TextComponentString(reason));
            notifySender(player.getName() + " was kicked by Administrator", sender);
        }
    }
    public void ban(MinecraftServer server, GameProfile profile, EntityPlayerMP player,
                    ICommandSender sender, String reason) throws CommandException {
        if (profile != null && player != sender.getCommandSenderEntity()) {
            if (profile.getName().equals(server.getServerOwner())) return;
            if (reason == null) reason = "You was banned by Admin!";
            UserListBansEntry entry = new UserListBansEntry(profile, null, sender.getName(), null, reason);
            server.getPlayerList().getBannedPlayers().addEntry(entry);
            if (player != null) {
                player.connection.disconnect(new TextComponentString(reason));
            }
            notifySender(profile.getName() + " was banned by Admin", sender);
        }
    }
    public void pardon(ICommandSender sender, MinecraftServer server, GameProfile profile) throws CommandException {
        if (profile != null) {
            server.getPlayerList().getBannedPlayers().removeEntry(profile);
            notifySender(profile.getName() + " was unbanned by Administrator", sender);
        }
    }
    public void kill(EntityPlayerMP player) throws CommandException {
        if (player != null) {
            player.attackEntityFrom(DamageSource.MAGIC, Float.MAX_VALUE);
        }
    }

    public static String makeReason(String[] args, int start) {
        String[] reason = new String[args.length - start];
        System.arraycopy(args, start, reason, 0, reason.length);
        return String.join(" ", reason);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
                                          String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1: return getListOfStringsMatchingLastWord(args, "kick", "ban", "pardon", "kill");
            case 2: return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            default: return super.getTabCompletions(server, sender, args, targetPos);
        }
    }

    public static void notifySender(String message, ICommandSender sender) {
        sender.sendMessage(new TextComponentString(message));
    }
}
