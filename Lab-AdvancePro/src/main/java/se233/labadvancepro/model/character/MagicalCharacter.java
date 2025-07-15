package se233.labadvancepro.model.character;

import se233.labadvancepro.model.DamageType;

// ใน MagicalCharacter.java
public class MagicalCharacter extends BasedCharacter {
    public MagicalCharacter(String name, String imgpath, int basedDef, int basedRes) {
        super(name, imgpath);
        this.fullHp = 50;
        this.basedPow = 10;
        this.basedDef = basedDef;
        this.basedRes = basedRes;
        this.hp = this.fullHp;
        this.power = this.basedPow;
        this.defense = this.basedDef;
        this.resistance = this.basedRes;
        this.damageType = DamageType.magical;
    }
}