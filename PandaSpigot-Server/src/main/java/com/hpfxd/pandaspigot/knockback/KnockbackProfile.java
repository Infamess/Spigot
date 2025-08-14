package com.hpfxd.pandaspigot.knockback;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;

public interface KnockbackProfile {
    String getName();

    String[] getValues();

    double getHorizontal();

    double getVertical();

    double getExtraHorizontal();

    double getExtraVertical();

    double getStartRange();

    double getRangeFactor();

    double getMaxRangeReduction();

    double getVerticalLimit();

    void setHorizontal(double horizontal);

    void setVertical(double vertical);

    void setExtraHorizontal(double extraHorizontal);

    void setExtraVertical(double extraVertical);

    void setStartRange(double startRange);

    void setRangeFactor(double rangeFactor);

    void setMaxRangeReduction(double maxRangeReduction);

    void setVerticalLimit(double verticalLimit);

    void save();

    void attackEntity(EntityPlayer attacker, Entity attacked, boolean shouldDealSprintKnockback, int i, double[] velocity);
}
