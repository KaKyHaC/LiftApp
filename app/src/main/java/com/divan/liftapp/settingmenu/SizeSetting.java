package com.divan.liftapp.settingmenu;

/**
 * Created by Димка on 06.01.2017.
 */

public class SizeSetting extends SettingItem {
    public int value;
    private String Name;

    public SizeSetting(int value, String name) {
        this.value = value;
        Name = name;
    }

    @Override
    public void onClick(Key key) {
        switch (key){

            case up:value++;
                break;
            case down:value--;
                break;
            case left:
                break;
            case right:
                break;
            case ok:
                break;
        }
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public String getName() {
        return Name;
    }

    @Override
    public String getColor() {
        return null;
    }
}
