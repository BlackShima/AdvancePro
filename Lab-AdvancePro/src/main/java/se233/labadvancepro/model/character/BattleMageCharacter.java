package se233.labadvancepro.model.character;

import se233.labadvancepro.model.DamageType;

public class BattleMageCharacter extends BasedCharacter{
    public BattleMageCharacter(String name,String imgpath,int basedDef,int basedRes){
        super(name, imgpath);

        this.damageType = null;

        this.fullHp = 40;
        this.basedPow = 40;

        this.basedDef = basedDef;
        this.basedRes = basedRes;

        this.hp = this.fullHp;
        this.power = this.basedPow;
        this.defense = basedDef;
        this.resistance = basedRes;
    }
}
