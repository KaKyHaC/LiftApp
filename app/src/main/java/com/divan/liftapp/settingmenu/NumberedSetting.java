package com.divan.liftapp.settingmenu;

/**
 * Created by Димка on 06.01.2017.
 */

public class NumberedSetting extends SettingItem {
    public enum NumberedType{Text,Volume,Buffer};
    public int value;
    private int bufValue;
    private String Name;
    //private boolean isVolume=false;
    private NumberedType type=NumberedType.Text;

    public NumberedSetting(int value, String name) {
        this.value = value;
        Name = name;
        bufValue=value;
    }

    public NumberedSetting(int value, String name, NumberedType Type) {
        Name = name;
        type=Type;
        this.value = value;
        bufValue=value;
        Invalidate();
    }

    private void Invalidate(){
        if(value<0)value=0;
        if(bufValue<0)bufValue=0;
        switch (type){

            case Text:
                break;
            case Volume:if(value>100)value=100;if(bufValue>100)bufValue=100;
                break;
            case Buffer:if(value>100)value=100;if(bufValue>100)bufValue=100;
                break;
        }
    }
    @Override
    public void onClick(Key key) {
        if(hasFocus) {
            switch (key) {
                case up:
                    value++;
                    bufValue=value;
                    break;
                case down:
                    value--;
                    bufValue=value;
                    break;
                case left:
                    bufValue--;
                    break;
                case right:
                    bufValue++;
                    break;
                case ok:
                    value = bufValue;
                    break;
            }
        }
       Invalidate();
    }

    @Override
    public String getValue() {
        return String.valueOf(bufValue)+((type==NumberedType.Volume)?"%":"");
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public String getName() {
        return Name;
    }

    @Override
    public void setFocus(boolean isFocus) {
        hasFocus=isFocus;
        bufValue=value;
    }

    @Override
    public String getColor() {
        return null;
    }
}
