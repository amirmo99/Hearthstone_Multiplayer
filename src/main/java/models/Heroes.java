package models;

import Util.Tools;
import abstracts.Attacker;
import abstracts.Defender;
import abstracts.GameEffectVisitor;
import abstracts.Prototype;
import com.fasterxml.jackson.annotation.*;
import enums.CardSituation;
import logic.CardEffects;
import logic.GameEffects.EffectList;
import models.cards.Minion;
import models.cards.Weapon;
import models.heroes.*;
import logic.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "models.heroes")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "models.heroes.Mage", value = Mage.class),
        @JsonSubTypes.Type(name = "models.heroes.Rogue", value = Rogue.class),
        @JsonSubTypes.Type(name = "models.heroes.Warlock", value = Warlock.class),
        @JsonSubTypes.Type(name = "models.heroes.Hunter", value = Hunter.class),
        @JsonSubTypes.Type(name = "models.heroes.Priest", value = Priest.class),
        @JsonSubTypes.Type(name = "models.heroes.Paladin", value = Paladin.class),
        @JsonSubTypes.Type(name = "models.Deck", value = Deck.class)
})

public abstract class Heroes extends GameModel implements Defender, Attacker, Prototype, Serializable {

    private List<Deck> myDecks;
    private int HP, defense;
    private HeroPower heroPower;
    private String specialPowerDescription;
    private CardSituation situation;
    private Weapon weapon;
    private CardEffects specialPower;

    protected Heroes(List<Deck> decks, int HP, int defense, HeroPower heroPower, String specialPowerDescription, CardSituation situation,
                     Weapon weapon, CardEffects specialPower) {
        this.myDecks = decks;
        this.HP = HP;
        this.defense = defense;
        this.heroPower = heroPower;
        this.specialPowerDescription = specialPowerDescription;
        this.situation = situation;
        this.weapon = weapon;
        this.specialPower = specialPower;
    }

    public Heroes() {
        myDecks = new ArrayList<>();
    }

    public static Heroes getHero(Player player, String myHeroName) {
        for (int i = 0; i < player.getMyAllHeroes().size(); i++) {
            if (myHeroName.equalsIgnoreCase(player.getMyAllHeroes().get(i).getName()))
                return player.getMyAllHeroes().get(i);
        }
        return null;
    }

    public static Heroes getHero(String myHero) {
        for (Heroes hero : getAllHeroes()) {
            if (hero.getName().equalsIgnoreCase(myHero))
                return hero;
        }

        return null;
    }


    @Override
    public void doDefend(Attacker attacker, GameEffectVisitor visitor) {
        takeDamage(attacker.getAttack(), visitor);
        if (attacker.getAttack() > 0)
            specialPower.getOnTakingDamage().accept(visitor);
    }

    @Override
    public void doAttack(Defender defender, GameEffectVisitor visitor) {
        if (weapon != null) {
            weapon.doAttack(defender);
            if (defender instanceof Minion)
                this.doDefend((Attacker) defender, visitor);
        }
    }

    @Override
    public void resetAttacks() {
        if (weapon != null) weapon.resetAttacks();
    }

    @Override
    public void takeDamage(int damage, GameEffectVisitor visitor) {
        if (damage > defense) {
            HP -= (damage - defense);
            defense = 0;
        }
        else {
            defense -= damage;
        }
    }

    @JsonIgnore
    @Override
    public void setAttack(int attack) {
        if (weapon != null) weapon.setAttack(attack);
    }

    @JsonIgnore
    @Override
    public int getAttack() {
        return (weapon == null) ? 0 : weapon.getAttack();
    }

    @JsonIgnore
    @Override
    public int getRemainedAttacks() {
        int n = 0;
        if (weapon != null) {
            n = weapon.getRemainedAttacks();
        }
        return n;
    }

    @JsonIgnore
    public boolean isDamaged() {
        try {
            return HP < Heroes.getHero(this.getName()).getHP();
        } catch (NullPointerException e) {
            return false;
        }
    }

    @JsonIgnore
    public boolean isHealed() {
        try {
            return HP > Heroes.getHero(this.getName()).getHP();
        } catch (NullPointerException e) {
            return false;
        }
    }



    public abstract Heroes cloned();

    public void addDeck(String deckName) {
        myDecks.add(new Deck(getName(), deckName));
    }

    @Override
    public CardEffects findEffects() {
        return specialPower;
    }

    @JsonIgnore
    public static List<Heroes> getAllHeroes() {
        return Tools.getAllHeroes();
    }


    @JsonIgnore
    public abstract String getName();

    public List<Deck> getMyDecks() {
        return myDecks;
    }

    public void setMyDecks(List<Deck> myDecks) {
        this.myDecks = myDecks;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public HeroPower getHeroPower() {
        return heroPower;
    }

    public void setHeroPower(HeroPower heroPower) {
        this.heroPower = heroPower;
    }

    public String getSpecialPowerDescription() {
        return specialPowerDescription;
    }

    public void setSpecialPowerDescription(String specialPowerDescription) {
        this.specialPowerDescription = specialPowerDescription;
    }

    public CardSituation getSituation() {
        return situation;
    }

    public void setSituation(CardSituation situation) {
        this.situation = situation;
    }

    @JsonIgnore
    public abstract List<Card> getAllCardsOfThisClass();

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public CardEffects getSpecialPower() {
        return specialPower;
    }

    public void setSpecialPower(CardEffects specialPower) {
        this.specialPower = specialPower;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }
}
