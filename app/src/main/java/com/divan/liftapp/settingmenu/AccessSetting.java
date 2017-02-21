package com.divan.liftapp.settingmenu;

/**
 * Created by Димка on 21.01.2017.
 */

public class AccessSetting extends SettingItem {
    public static String [] typeAccess={"Разрешено","Запрещено"};//TODO remake for emun
    public boolean Access=false;
    private boolean tmpAccess;
    String Name;

    public AccessSetting(String name, String access) {
        Name = name;
        Access=(access.equals(typeAccess[0]));
    }

    @Override
    public void onClick(Key key) {
        switch (key){

            case up:Access=!Access;tmpAccess=Access;
                break;
            case down:Access=!Access;tmpAccess=Access;
                break;
            case left:tmpAccess=!tmpAccess;
                break;
            case right:tmpAccess=!tmpAccess;
                break;
            case ok:Access=tmpAccess;
                break;
        }
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getValue() {
        if(tmpAccess)
            return typeAccess[0];
        else
            return typeAccess[1];
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public void setFocus(boolean isFocus) {
        if(isFocus)
        {
            tmpAccess=Access;
        }
    }

    @Override
    public String toString() {
        return Boolean.toString(Access);
   }

    @Override
    public void setValue(String value) {
        this.Access=Boolean.valueOf(value);
    }
}
