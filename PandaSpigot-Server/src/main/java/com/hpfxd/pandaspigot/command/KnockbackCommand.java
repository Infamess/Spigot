package com.hpfxd.pandaspigot.command;

import com.hpfxd.pandaspigot.PandaSpigot;
import com.hpfxd.pandaspigot.knockback.CraftKnockbackProfile;
import com.hpfxd.pandaspigot.knockback.KnockbackProfile;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KnockbackCommand extends Command {

    public KnockbackCommand() {
        super("knockback");
        description = "Modify knockback";
        usageMessage = "/knockback";
        setPermission("spigot.command.knockback");
        setAliases(Collections.singletonList("kb"));

        setUsage(StringUtils.join(new String[]{
            ChatColor.AQUA + "Knockback help",
            ChatColor.AQUA + "/kb list " + ChatColor.GRAY + " - " + ChatColor.WHITE + "List all profiles",
            ChatColor.AQUA + "/kb create <name> " + ChatColor.GRAY + " - " + ChatColor.WHITE + "Create a new profile",
            ChatColor.AQUA + "/kb delete <name> " + ChatColor.GRAY + " - " + ChatColor.WHITE + "Delete a profile",
            ChatColor.AQUA + "/kb load <name> " + ChatColor.GRAY + " - " + ChatColor.WHITE + "Load existing profile",

            ChatColor.AQUA + "/kb horizontal <name> <value> " + ChatColor.GRAY + " - " + ChatColor.WHITE + "Set horizontal for knockback",
            ChatColor.AQUA + "/kb vertical <name> <value> " + ChatColor.GRAY + " - " + ChatColor.WHITE + "Set vertical for knockback",
            ChatColor.AQUA + "/kb extrahorizontal <name> <value> " + ChatColor.GRAY + " - " + ChatColor.WHITE + "Set extra horizontal for knockback",
            ChatColor.AQUA + "/kb extravertical <name> <value> " + ChatColor.GRAY + " - " + ChatColor.WHITE + "Set extra vertical for knockback",
            ChatColor.AQUA + "/kb startRange <name> <value> " + ChatColor.GRAY + " - " + ChatColor.WHITE + "Set start range for knockback",
            ChatColor.AQUA + "/kb rangeFactor <name> <value> " + ChatColor.GRAY + " - " + ChatColor.WHITE + "Set range factor for knockback",
            ChatColor.AQUA + "/kb maxRange <name> <value> " + ChatColor.GRAY + " - " + ChatColor.WHITE + "Set max range reduction for knockback",
            ChatColor.AQUA + "/kb verticalLimit <name> <value> " + ChatColor.GRAY + " - " + ChatColor.WHITE + "Set vertical limit for knockback"

        }, "\n"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
            return true;
        }

        if (!testPermission(sender)) return true;
        if (args.length == 0) {
            sender.sendMessage(usageMessage);
            return true;
        }

        List<String> messages;

        switch (args[0].toLowerCase()) {
            case "list":
                messages = new ArrayList<>();
                for (KnockbackProfile profile : PandaSpigot.getInstance().getConfig().getKbProfiles()) {
                    boolean current = PandaSpigot.getInstance().getConfig().getCurrentKb().getName().equals(profile.getName());

                    messages.add("");
                    messages.add(ChatColor.AQUA + profile.getName() + (current ? (ChatColor.GREEN + " [Active]") : ""));
                    for (String value : profile.getValues())
                        messages.add(ChatColor.AQUA + " * " + ChatColor.AQUA + value);
                }
                sender.sendMessage(ChatColor.AQUA + "Knockback profiles:");
                sender.sendMessage(StringUtils.join(messages, "\n"));
                return true;
            case "create":
                if (args.length > 1) {
                    String name = args[1];
                    for (KnockbackProfile knockbackProfile : PandaSpigot.getInstance().getConfig().getKbProfiles()) {
                        if (knockbackProfile.getName().equalsIgnoreCase(name)) {
                            sender.sendMessage(ChatColor.RED + "A knockback profile with that name already exists.");
                            return true;
                        }
                    }
                    CraftKnockbackProfile profile = new CraftKnockbackProfile(name);
                    profile.save();
                    PandaSpigot.getInstance().getConfig().getKbProfiles().add(profile);
                    sender.sendMessage(ChatColor.AQUA + "You created a new profile " + ChatColor.GREEN + name + ChatColor.AQUA + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /kb create <name>");
                }
                return true;
            case "delete":
                if (args.length > 1) {
                    String name = args[1];
                    if (PandaSpigot.getInstance().getConfig().getCurrentKb().getName().equalsIgnoreCase(name)) {
                        sender.sendMessage(ChatColor.RED + "You cannot delete the profile that is being used.");
                        return true;
                    }
                    if (PandaSpigot.getInstance().getConfig().getKbProfiles().removeIf(profile -> profile.getName().equalsIgnoreCase(name))) {
                        PandaSpigot.getInstance().getConfig().set("profiles." + name, null);
                        sender.sendMessage(ChatColor.AQUA + "You deleted the profile " + ChatColor.GREEN + name + ChatColor.AQUA + ".");
                    } else {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                    }
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "Usage: /kb delete <name>");
                return true;
            case "load":
                if (args.length > 1) {
                    KnockbackProfile profile = PandaSpigot.getInstance().getConfig().getKbProfileByName(args[1]);
                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }
                    PandaSpigot.getInstance().getConfig().setCurrentKb(profile);
                    PandaSpigot.getInstance().getConfig().set("current", profile.getName());
                    PandaSpigot.getInstance().getConfig().save();
                    sender.sendMessage(ChatColor.AQUA + "You loaded the profile " + ChatColor.GREEN + profile.getName() + ChatColor.AQUA + ".");
                    return true;
                }
                return true;
            case "horizontal":
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = PandaSpigot.getInstance().getConfig().getKbProfileByName(args[1]);
                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setHorizontal(Double.parseDouble(args[2]));
                    profile.save();
                    sender.sendMessage(ChatColor.AQUA + "You have updated " + ChatColor.GREEN + profile.getName() + ChatColor.AQUA + "'s values to:");
                    for (String value : profile.getValues())
                        sender.sendMessage(ChatColor.GRAY + "* " + value);
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
                return true;
            case "vertical":
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = PandaSpigot.getInstance().getConfig().getKbProfileByName(args[1]);
                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setVertical(Double.parseDouble(args[2]));
                    profile.save();
                    sender.sendMessage(ChatColor.AQUA + "You have updated " + ChatColor.GREEN + profile.getName() + ChatColor.AQUA + "'s values to:");
                    for (String value : profile.getValues())
                        sender.sendMessage(ChatColor.GRAY + "* " + value);
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
                return true;
            case "extrahorizontal":
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = PandaSpigot.getInstance().getConfig().getKbProfileByName(args[1]);
                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setExtraHorizontal(Double.parseDouble(args[2]));
                    profile.save();
                    sender.sendMessage(ChatColor.AQUA + "You have updated " + ChatColor.GREEN + profile.getName() + ChatColor.AQUA + "'s values to:");
                    for (String value : profile.getValues())
                        sender.sendMessage(ChatColor.GRAY + "* " + value);
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
                return true;
            case "extravertical":
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = PandaSpigot.getInstance().getConfig().getKbProfileByName(args[1]);
                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setExtraVertical(Double.parseDouble(args[2]));
                    profile.save();
                    sender.sendMessage(ChatColor.AQUA + "You have updated " + ChatColor.GREEN + profile.getName() + ChatColor.AQUA + "'s values to:");
                    for (String value : profile.getValues())
                        sender.sendMessage(ChatColor.GRAY + "* " + value);
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
                return true;
            case "startrange":
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = PandaSpigot.getInstance().getConfig().getKbProfileByName(args[1]);
                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setStartRange(Double.parseDouble(args[2]));
                    profile.save();
                    sender.sendMessage(ChatColor.AQUA + "You have updated " + ChatColor.GREEN + profile.getName() + ChatColor.AQUA + "'s values to:");
                    for (String value : profile.getValues())
                        sender.sendMessage(ChatColor.GRAY + "* " + value);
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
                return true;
            case "rangefactor":
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = PandaSpigot.getInstance().getConfig().getKbProfileByName(args[1]);
                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setRangeFactor(Double.parseDouble(args[2]));
                    profile.save();
                    sender.sendMessage(ChatColor.AQUA + "You have updated " + ChatColor.GREEN + profile.getName() + ChatColor.AQUA + "'s values to:");
                    for (String value : profile.getValues())
                        sender.sendMessage(ChatColor.GRAY + "* " + value);
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
                return true;
            case "maxrange":
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = PandaSpigot.getInstance().getConfig().getKbProfileByName(args[1]);
                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setMaxRangeReduction(Double.parseDouble(args[2]));
                    profile.save();
                    sender.sendMessage(ChatColor.AQUA + "You have updated " + ChatColor.GREEN + profile.getName() + ChatColor.AQUA + "'s values to:");
                    for (String value : profile.getValues())
                        sender.sendMessage(ChatColor.GRAY + "* " + value);
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
                return true;
            case "verticallimit":
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = PandaSpigot.getInstance().getConfig().getKbProfileByName(args[1]);
                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setVerticalLimit(Double.parseDouble(args[2]));
                    profile.save();
                    sender.sendMessage(ChatColor.AQUA + "You have updated " + ChatColor.GREEN + profile.getName() + ChatColor.AQUA + "'s values to:");
                    for (String value : profile.getValues())
                        sender.sendMessage(ChatColor.GRAY + "* " + value);
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
                return true;
        }
        sender.sendMessage(this.usageMessage);
        return true;
    }
}
