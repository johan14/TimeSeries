package com.rayonit.times.hierarchic.levels;

public enum HierarchicLevel {
    DAY_LEVEL(true, BaseLevel.DAY, BaseLevel.HOUR, BaseLevel.MINUTE, BaseLevel.SECOND),
    HOUR_LEVEL(true, BaseLevel.HOUR, BaseLevel.MINUTE, BaseLevel.SECOND),
    MINUTE_LEVEL(false, BaseLevel.MINUTE, BaseLevel.SECOND);

    private final boolean configurable;
    private final BaseLevel[] levels;

    HierarchicLevel(boolean configurable, BaseLevel... levels) {
        this.configurable = configurable;
        this.levels = levels;
    }

    public BaseLevel[] getLevels() {
        return levels;
    }
}
