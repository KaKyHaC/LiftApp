package com.example.universalliftappsetting.settingmenu;

import com.example.universalliftappsetting.settingmenu.SettingItem;

import static com.example.universalliftappsetting.settingmenu.SettingItem.Key.up;

/**
 * Created by Димка on 11.01.2017.
 */

public class SpecialSetting extends SettingItem {
    public enum TypeSpecialItem{EXIT,DEFAULT,STATION,INSTRUCTION};
    public static final String[] stationNames={"ШУЛМ","ШК6000","УЭЛ","УЛ"};
    public int indexCurStation=0;
    private int indexBufStation=indexCurStation;

    TypeSpecialItem typeSpecialItem;
    SpecialActivity specialActivity;//TODO bad idea
    String Name;

    public SpecialSetting(TypeSpecialItem typeSpecialItem, SpecialActivity specialActivity, String name) {
        this.typeSpecialItem = typeSpecialItem;
        this.specialActivity = specialActivity;
        Name = name;
    }


    @Override
    public void onClick(Key key) {
        if(hasFocus){
            if (typeSpecialItem==TypeSpecialItem.STATION)
            {
                if(indexCurStation<stationNames.length)
                    indexCurStation+=stationNames.length;
                if(indexBufStation<stationNames.length)
                    indexBufStation+=stationNames.length;
                switch (key) {
                    case up:indexCurStation++;indexBufStation=indexCurStation;specialActivity.SendByte((byte)(indexCurStation%stationNames.length+1));
                        specialActivity.SetIndexCurStation(indexCurStation);
                        break;
                    case down:indexCurStation--;indexBufStation=indexCurStation;specialActivity.SendByte((byte)(indexCurStation%stationNames.length+1));
                        specialActivity.SetIndexCurStation(indexCurStation);
                        break;
                    case left:indexBufStation--;
                        break;
                    case right:indexCurStation++;
                        break;
                }


            }
            switch (key)
            {
                case ok:
                case up:
                case down:
                    switch (typeSpecialItem){
                        case EXIT:specialActivity.SendByte();
                            specialActivity.Exit();
                            break;
                        case DEFAULT:specialActivity.MakeDefaultSetting();
                            break;
                        case STATION:
                            indexCurStation=indexBufStation;
//                            specialActivity.SendByte((byte)(indexCurStation%stationNames.length+1));
                            break;
                        case INSTRUCTION:specialActivity.OpenInstruction();
                            break;
                    }
                    break;

            }
        }
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getValue() {
        if(typeSpecialItem==TypeSpecialItem.STATION){
            return stationNames[indexBufStation%stationNames.length];
        }
        else  return "-";
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public void setFocus(boolean isFocus) {
        hasFocus=isFocus;
        if(typeSpecialItem==TypeSpecialItem.STATION) {
            if (isFocus)
                indexCurStation = specialActivity.GetIndexCurStation(); //TODO it's bad code
            if (!isFocus)
                specialActivity.SetIndexCurStation(indexCurStation);
            indexBufStation = indexCurStation;
        }
    }

    @Override
    public String toString() {
        return  Name ;
    }

    @Override
    public void setValue(String value) {
        Name=value;
    }
    public interface SpecialActivity{
        public void Exit();
        public void MakeDefaultSetting();
        public void SendByte();
        public void SendByte(byte b);
        public void OpenInstruction();
        public void SetIndexCurStation(int index);
        public int GetIndexCurStation();
    }
}

