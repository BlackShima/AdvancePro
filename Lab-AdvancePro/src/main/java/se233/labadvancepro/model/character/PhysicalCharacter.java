package se233.labadvancepro.model.character;

import se233.labadvancepro.model.DamageType;

public class PhysicalCharacter extends BasedCharacter{
    public PhysicalCharacter(String name,String imgpath,int basedDef,int basedRes){
        super(name, imgpath);
        this.name = name;
        this.damageType = DamageType.physical;
        this.imgpath = imgpath;
        this.fullHp = 50;
        this.basedPow = 30;
        this.basedDef = basedDef;
        this.basedRes = basedRes;
        this.hp = this.fullHp;
        this.power = this.basedPow;
        this.defense = basedDef;
        this.resistance = basedRes;
    }
}