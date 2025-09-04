package com.hpfxd.pandaspigot.knockback;

import com.hpfxd.pandaspigot.PandaSpigot;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public class CraftKnockbackProfile implements KnockbackProfile {

    private String name;

    private double horizontal = 0.5273;
    private double vertical = 0.6635;
    private double extraHorizontal = 0.45;
    private double extraVertical = 0.0;
    private double startRange = 1.4;
    private double rangeFactor = 0.1;
    private double maxRangeReduction = 0.4;
    private double tradeincrement = 0.1051;
    private double verticalLimit = 0.361375;


    @Override
    public double getHorizontal() {
        return horizontal;
    }

    @Override
    public double getVertical() {
        return vertical;
    }

    @Override
    public double getExtraHorizontal() {
        return extraHorizontal;
    }

    @Override
    public double getExtraVertical() {
        return extraVertical;
    }

    @Override
    public double getStartRange() {
        return startRange;
    }

    @Override
    public double getRangeFactor() {
        return rangeFactor;
    }

    @Override
    public double getMaxRangeReduction() {
        return maxRangeReduction;
    }

    @Override
    public double getTradeIncrement() {
        return tradeincrement;
    }


    @Override
    public double getVerticalLimit() {
        return verticalLimit;
    }

    public void setHorizontal(double horizontal) {
        this.horizontal = horizontal;
    }

    public void setVertical(double vertical) {
        this.vertical = vertical;
    }

    public void setExtraHorizontal(double extraHorizontal) {
        this.extraHorizontal = extraHorizontal;
    }

    public void setExtraVertical(double extraVertical) {
        this.extraVertical = extraVertical;
    }

    public void setStartRange(double startRange) {
        this.startRange = startRange;
    }

    public void setRangeFactor(double rangeFactor) {
        this.rangeFactor = rangeFactor;
    }

    public void setMaxRangeReduction(double maxRangeReduction) {
        this.maxRangeReduction = maxRangeReduction;
    }

    public void setTradeIncrement(double tradeIncrement) {
        this.tradeincrement = tradeIncrement;
    }


    public void setVerticalLimit(double verticalLimit) {
        this.verticalLimit = verticalLimit;
    }


    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CraftKnockbackProfile(String name) {
        this.name = name;
    }

    public String[] getValues() {
        return new String[]{
            "Horizontal: " + ChatColor.WHITE + this.horizontal,
            "Vertical: " + ChatColor.WHITE + this.vertical,
            "Extra Horizontal: " + ChatColor.WHITE + this.extraHorizontal,
            "Extra Vertical: " + ChatColor.WHITE + this.extraVertical,
            "Start Range: " + ChatColor.WHITE + this.startRange,
            "Range Factor: " + ChatColor.WHITE + this.rangeFactor,
            "Max Range Reduction: " + ChatColor.WHITE + this.maxRangeReduction,
            "Trade Increment: " + ChatColor.WHITE + this.tradeincrement,
            "Vertical Limit: " + ChatColor.WHITE + this.verticalLimit
        };
    }

    public void save() {
        String path = "profiles." + this.name;

        PandaSpigot.getInstance().getConfig().set(path + ".horizontal", horizontal);
        PandaSpigot.getInstance().getConfig().set(path + ".vertical", vertical);
        PandaSpigot.getInstance().getConfig().set(path + ".extra-horizontal", extraHorizontal);
        PandaSpigot.getInstance().getConfig().set(path + ".extra-vertical", extraVertical);
        PandaSpigot.getInstance().getConfig().set(path + ".start-range", startRange);
        PandaSpigot.getInstance().getConfig().set(path + ".range-factor", rangeFactor);
        PandaSpigot.getInstance().getConfig().set(path + ".max-range-reduction", maxRangeReduction);
        PandaSpigot.getInstance().getConfig().set(path + ".tradeincrement", tradeincrement);
        PandaSpigot.getInstance().getConfig().set(path + ".vertical-limit", verticalLimit);
        PandaSpigot.getInstance().getConfig().save();
    }

    private double friction(double range) {
        if (range < 1) return 2.0D;

        double startRange = 1.0D;

        double minFriction = 1.25D;
        double maxFriction = 2.75D;

        double t = (range - startRange) / (maxFriction - startRange);
        t = Math.max(0.0, Math.min(t, 1.0));
        double f = (maxFriction - minFriction) / 4.0f;

        if (t < 0.25) {
            return minFriction;
        } else if (t < 0.5) {
            return minFriction + f;
        } else if (t < 0.75) {
            return minFriction + (2 * f);
        } else {
            return maxFriction;
        }
    }

    public double verticalDistance(Entity attacked, EntityPlayer attacker) {
        return attacked.locY - attacker.locY;
    }

    private double calculateMultiplier(double distance) {
        if (distance <= startRange) {
            return 0.0;
        }
        return rangeFactor * (distance - maxRangeReduction);
    }


    @Override
    public void attackEntity(EntityPlayer attacker, Entity attacked, boolean shouldDealSprintKnockback, int i, double[] velocity) {
        attacked.ai = true;

        double friction = friction(verticalDistance(attacked, attacker));

        double yawX = -MathHelper.sin(attacker.yaw * (float) Math.PI / 180.0F);
        double yawZ = MathHelper.cos(attacker.yaw * (float) Math.PI / 180.0F);

        double distanceX = attacked.locX - attacker.locX;

        double distanceZ;

        for (distanceZ = attacked.locZ - attacker.locZ; distanceX * distanceX + distanceZ * distanceZ < 1.0E-4D; distanceZ = (Math.random() - Math.random()) * 0.01D) {
            distanceX = (Math.random() - Math.random()) * 0.01D;
        }

        double distance = MathHelper.sqrt(distanceX * distanceX + distanceZ * distanceZ);
        distanceX /= distance;
        distanceZ /= distance;

        double x = (yawX + distanceX) / 2.0D;
        double z = (yawZ + distanceZ) / 2.0D;

        attacked.motY /= friction;

        attacked.motX = 0;
        attacked.motZ = 0;
        attacked.motX += x * horizontal;
        attacked.motY += vertical;

        if (attacked.motY < -0.078) {
            attacked.motY = -0.078;
        }

        attacked.motZ += z * horizontal;

        if (attacked.motY > verticalLimit) {
            attacked.motY = verticalLimit;
        }

        double reduction = 1.0;
        if (distance > 1.0E-5D) {
            reduction = 1.0 - calculateMultiplier(distance);
            reduction = Math.max(1.0 - maxRangeReduction, Math.min(1.0, reduction));

            double d0 = -MathHelper.sin(attacker.yaw * (float) Math.PI / 180.0F) * i * extraHorizontal * reduction;
            double d1 = extraVertical;
            double d2 = MathHelper.cos(attacker.yaw * (float) Math.PI / 180.0F) * i * extraHorizontal * reduction;

            attacked.motX += d0;

            if (attacked.isSprintingState() && attacker.isSprintingState()) {
                Vector attackerDir = attacker.getBukkitEntity().getLocation().getDirection().setY(0F);
                Vector victimDir = attacked.getBukkitEntity().getLocation().getDirection().setY(0F);
                if (Math.abs(attackerDir.angle(victimDir)) >= 1) {
                    attacked.motX += d0 * getTradeIncrement() * reduction;
                }
            }

            attacked.motY += d1;
            attacked.motZ += d2;
            attacked.ai = true;

            attacker.motX *= 0.6D;
            attacker.motZ *= 0.6D;
            if (shouldDealSprintKnockback) attacker.shouldDealSprintKnockback = false;
        }

        if (attacked instanceof EntityPlayer && attacked.velocityChanged) {
            boolean cancelled = false;
            Player player = (Player) attacked.getBukkitEntity();
            org.bukkit.util.Vector velocity2 = new Vector(velocity[0], velocity[1], velocity[2]);

           /* double x2 = attacked.motX;
            double y2 = attacked.motY;
            double z2 = attacked.motZ;

            double xz = Math.sqrt(x2 * x2 + z2 * z2);

            Bukkit.broadcastMessage(String.format(player.getName() + " -> V: %.6f", y2));*/

            PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity2.clone());
            attacked.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                cancelled = true;
            } else if (!velocity2.equals(event.getVelocity())) {
                player.setVelocity(event.getVelocity());
            }

            if (!cancelled) {
                ((EntityPlayer) attacked).playerConnection.sendPacket(new PacketPlayOutEntityVelocity(attacked));
                attacked.velocityChanged = false;
                attacked.motX = velocity[0];
                attacked.motY = velocity[1];
                attacked.motZ = velocity[2];
            }
        }

    }

}
