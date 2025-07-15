package se233.labadvancepro.model.character;

import se233.labadvancepro.model.DamageType;
import se233.labadvancepro.model.item.Armor;
import se233.labadvancepro.model.item.Weapon;

public abstract class BasedCharacter {
    public BasedCharacter(String name, String imgpath){
        this.name = name;
        this.imgpath = imgpath;
        this.hp = 0;
        this.power = 0;
        this.defense = 0;
        this.resistance = 0;

        this.weapon = null;
        this.armor = null;
    }
    protected String name, imgpath;
    protected DamageType damageType;
    protected Integer fullHp, basedPow, basedDef, basedRes;
    protected Integer hp, power, defense, resistance;
    protected Weapon weapon;
    protected Armor armor;
    public String getName () { return name; }
    public String getImagepath() { return imgpath; }
    public Integer getHp() { return hp; }
    public Integer getFullHp() { return fullHp; }
    public Integer getPower() { return power; }
    public Integer getDefense() { return defense; }
    public Integer getResistance() { return resistance; }
    public void equipWeapon( Weapon weapon) {
        this.weapon = weapon;
        this.power = this.basedPow + weapon.getPower();
    }
    public void equipArmor( Armor armor) {
        this.armor = armor;
        this.defense = this.basedDef + armor.getDefense();
        this.resistance= this.basedRes + armor.getResistance();
    }
    @Override
    public String toString() { return name; }
    public DamageType getDamageType() {
        return damageType;
    }
}