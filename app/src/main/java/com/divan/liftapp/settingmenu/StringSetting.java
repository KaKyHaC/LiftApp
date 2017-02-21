package com.divan.liftapp.settingmenu;

/**
 * Created by Dima on 21.02.2017.
 */

public class StringSetting extends SettingItem {
    private String Name;
    public String value;

    public StringSetting(String name, String value) {
        Name = name;
        this.value = value;
    }

    @Override
    public void onClick(Key key) {
        //TODO
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value=value;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public void setFocus(boolean isFocus) {

    }
}
