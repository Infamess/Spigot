package com.hpfxd.pandaspigot.knockback;

import com.hpfxd.pandaspigot.PandaSpigot;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MathHelper;
import net.minecraft.server.PacketPlayOutEntityVelocity;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public class CraftKnockbackProfile implements KnockbackProfile {

    private String name;

    private double horizontal = 0.05273;
    private double vertical = 0.8835;
    private double extraHorizontal = 0.5;
    private double extraVertical = 0.0;
    private double startRange = 1.4;
    private double rangeFactor = 0.1;
    private double maxRangeReduction = 0.45;
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
        PandaSpigot.getInstance().getConfig().set(path + ".vertical-limit", verticalLimit);
        PandaSpigot.getInstance().getConfig().save();
    }

    private double friction(double range) {
        if (range < 1) return 2.0D;

        double startRange = getStartRange();

        double minFriction = 1.0D;
        double maxFriction = 2.0D;

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

    public double verticalDistance(Entity entity, EntityPlayer source) {
        return entity.locY - source.locY;
    }

    @Override
    public void attackEntity(EntityPlayer attacker, Entity attacked, boolean shouldDealSprintKnockback, int i, double[] velocity) {
        double friction = friction(verticalDistance(attacked, attacker));

        double yawX = -MathHelper.sin(attacker.yaw * (float) Math.PI / 180.0F);
        double yawZ = MathHelper.cos(attacker.yaw * (float) Math.PI / 180.0F);

        double distanceX = attacked.locX - attacker.locX;

        double distanceZ;

        for (distanceZ = attacked.locZ - attacker.locZ; distanceX * distanceX + distanceZ * distanceZ < 1.0E-4D; distanceZ = (Math.random() - Math.random()) * 0.01D) {
            distanceX = (Math.random() - Math.random()) * 0.01D;
        }

        double distance = MathHelper.sqrt(distanceX * distanceX + distanceZ + distanceZ);
        distanceX /= distance;
        distanceZ /= distance;

        double x = (yawX + distanceX) / 2.0D;
        double z = (yawZ + distanceZ) / 2.0D;

        // double x = (yawX + -d0) / 2.0D;
        //  double z = (yawZ + -d1) / 2.0D;

        double xDiff2 = attacked.locX - attacker.locX;
        double zDiff2 = attacked.locZ - attacker.locZ;

        //double distance2 = Math.sqrt(xDiff2 * xDiff2 + zDiff2 * zDiff2);

        double horizontalReduction = 0.0;

        if (distance >= getStartRange()) {
            double modifiedRange = getRangeFactor() * (distance - getStartRange());
            horizontalReduction = Math.min(modifiedRange, getMaxRangeReduction());
        }

        if (distance > 1.0E-5) {
            double finalHorizontal = i * extraHorizontal *  (1.0 - horizontalReduction);
            //attacked.g(-(xDiff2 / distance2 * finalHorizontal), 0.0, -(zDiff2 / distance2 * finalHorizontal));

            double d0 = -(distanceX / distance * finalHorizontal);
            double d1 = 0.0;
            double d2 = -(distanceZ / distance * finalHorizontal);


            attacked.motX += d0;
            attacked.motY += d1;
            attacked.motZ += d2;
            attacked.ai = true;
        }

        attacker.motX *= 0.6D;
        attacker.motZ *= 0.6D;
        if (shouldDealSprintKnockback) attacker.shouldDealSprintKnockback = false;

        attacked.motY /= friction;

        attacked.motX += x * getHorizontal();
        attacked.motY += getVertical();
        attacked.motZ += z * getHorizontal();

        if (attacked.motY > getVerticalLimit()) {
            attacked.motY = getVerticalLimit();
        }

        if (attacked instanceof EntityPlayer && attacked.velocityChanged) {
            boolean cancelled = false;
            Player player = (Player) attacked.getBukkitEntity();
            org.bukkit.util.Vector velocity2 = new Vector(velocity[0], velocity[1], velocity[2]);

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
                attacked.motX = velocity2.getX();
                attacked.motY = velocity2.getY();
                attacked.motZ = velocity2.getZ();
            }
        }
    }

}
