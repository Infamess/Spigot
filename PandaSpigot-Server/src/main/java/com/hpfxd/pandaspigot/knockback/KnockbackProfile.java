package com.hpfxd.pandaspigot.knockback;

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
}
