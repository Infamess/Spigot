package com.hpfxd.pandaspigot.knockback;

import com.hpfxd.pandaspigot.PandaSpigot;
import com.hpfxd.pandaspigot.config.PandaSpigotConfig;
import org.bukkit.ChatColor;

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
        return new String[] {
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
}
