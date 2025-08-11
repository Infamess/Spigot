package com.hpfxd.pandaspigot;

import com.hpfxd.pandaspigot.command.KnockbackCommand;
import com.hpfxd.pandaspigot.knockback.KnockbackConfig;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;

import java.util.HashMap;
import java.util.Map;

public class PandaSpigot {

    private static PandaSpigot instance;

    private KnockbackConfig knockbackConfig;

    public PandaSpigot() {
        instance = this;
    }

    public void registerCommands() {
        Map<String, Command> commands = new HashMap<>();

        commands.put("kb", new KnockbackCommand());

        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Spigot", entry.getValue());
        }
    }

    public static PandaSpigot getInstance() {
        return instance == null ? new PandaSpigot() : instance;
    }

    public KnockbackConfig getConfig() {
        return knockbackConfig;
    }

    public void setConfig(KnockbackConfig knockbackConfig) {
        this.knockbackConfig = knockbackConfig;
    }
}
