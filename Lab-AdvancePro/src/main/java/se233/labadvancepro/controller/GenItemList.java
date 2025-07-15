package se233.labadvancepro.controller;

import se233.labadvancepro.model.DamageType;
import se233.labadvancepro.model.item.Armor;
import se233.labadvancepro.model.item.BasedEquipment;
import se233.labadvancepro.model.item.Weapon;

import java.util.ArrayList;

public class GenItemList {
    public static ArrayList<BasedEquipment> setUpItemList() {
        ArrayList<BasedEquipment> itemLists = new ArrayList<BasedEquipment>(5);
        itemLists.add(new Weapon("Sword",10,DamageType.physical,"assets/sword.png"));
        itemLists.add(new Weapon("Gun",20,DamageType.physical,"assets/gun.png"));
        itemLists.add(new Weapon("Staff",30,DamageType.magical,"assets/staff.png"));
        itemLists.add(new Armor("Shirt",0,50,"assets/shirt.png"));
        itemLists.add(new Armor("Armor",50,0,"assets/armor.png"));
        //ใส่ไอเทมเพิ่ม
        itemLists.add(new Weapon("Excalibur", 999,DamageType.physical,"assets/Excalibur.png"));
        itemLists.add(new Armor("Crown",999,999,"assets/crown.png"));
        return itemLists;
    }
}