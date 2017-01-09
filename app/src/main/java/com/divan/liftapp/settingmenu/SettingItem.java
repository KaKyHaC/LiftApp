package com.divan.liftapp.settingmenu;

/**
 * Created by Димка on 06.01.2017.
 */

public abstract class SettingItem {
    public enum Key{up,down,left,right,ok};
    public abstract void onClick(Key key);
    public abstract String getName();
    public abstract String getValue();
    public abstract String getColor();
}
