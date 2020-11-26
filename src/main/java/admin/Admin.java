package admin;

import abstracts.GameEffect;
import configs.*;
import enums.*;
import enums.Class;
import logic.CardEffects;
import logic.CardFilter;
import logic.GameEffects.*;
import models.cards.Minion;
import models.cards.Mission;
import models.cards.Spell;
import models.cards.Weapon;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import models.heroes.*;
import models.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Admin {


    public static void main(String[] args) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();

        objectMapper.registerSubtypes(new NamedType(Spell.class));
        objectMapper.registerSubtypes(new NamedType(Minion.class));
        objectMapper.registerSubtypes(new NamedType(Weapon.class));

        //  Creating Cards ......
        Card[] c = new Card[41];

        c[39] = new Minion(1, 0, "Sheep", "", Rarity.Free, Class.Natural, 0, 1, 1);

        c[40] = new Minion(2, 10, "Mech", "", Rarity.Common, Class.Natural, 0, 2, 3);
        //////////////////// Raid Leader
        c[0] = new Minion(3, 0, "RaidLeader", "Your Other Minions have +1 attack", Rarity.Free,
                Class.Natural, 0, 1, 3);

        CardFilter cardFilter0_1 = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.FRIEND_MINIONS_EXCEPT_SELF);
        CardFilter cardFilter0_2 = new CardFilter(SelectingCardMethod.RECENTLY_PLAYED_CARD, null);
        CardFilter cardFilter0_3 = new CardFilter(SelectingCardMethod.RECENTLY_SUMMONED_CARD, null);
        CardFilter cardFilter0_4 = new CardFilter(SelectingCardMethod.RECENTLY_TRANSFORMED_CARD, null);

        c[0].getCardEffects().getBattleCry().getEffects().add(new ChangeHpAttack(0, 1, cardFilter0_1));
        c[0].getCardEffects().getOnPlay().getEffects().add(new ChangeHpAttack(0, 1, cardFilter0_2));
        c[0].getCardEffects().getOnSummon().getEffects().add(new ChangeHpAttack(0, 1, cardFilter0_3));
        c[0].getCardEffects().getOnTransform().getEffects().add(new ChangeHpAttack(0, 1, cardFilter0_4));
        c[0].getCardEffects().getOnExit().getEffects().add(new ChangeHpAttack(0, -1, cardFilter0_1));
        ////////////////////// Fireball
        c[1] = new Spell(4, 0, "Fireball", "Deal 6 damage", Rarity.Free, Class.Natural,
                0);

        CardFilter filter1 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.ALL);
        c[1].getCardEffects().getBattleCry().getEffects().add(new ChangeHpAttack(-6, 0, filter1));
        /////////////////////////////// Void Walker
        c[2] = new Minion(1, 50, "Voidwalker", "Taunt", Rarity.Rare, Class.Warlock,
                0, 1, 3);
        ((Minion) c[2]).getMainAbilities().add(MinionAbility.TAUNT);
        ///////////////////// Amani War Bear
        c[3] = new Minion(7, 0, "AmaniWarBear", "Rush, Taunt", Rarity.Free, Class.Natural,
                0, 5, 7);

        ((Minion) c[3]).getMainAbilities().add(MinionAbility.TAUNT);
        ((Minion) c[3]).getMainAbilities().add(MinionAbility.RUSH);

        /////////////////// Polymorph
        c[4] = new Spell(4, 0, "Polymorph", "Transform a minion into 1/1 Sheep", Rarity.Free,
                Class.Mage, 0);

        CardFilter filter4 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.ALL_MINIONS);
        CardFilter filter4_target = new CardFilter(SelectingCardMethod.DETERMINED_CARD, null);
        ArrayList<GameModel> models4 = new ArrayList<>(); models4.add(c[39].cloned());
        filter4_target.setSourceCard(models4);
        c[4].getCardEffects().getBattleCry().getEffects().add(new Transform(filter4, filter4_target));
        /////////////////// Friendly Smith
        c[5] = new Spell(1, 20, "Friendly Smith", "Discover a weapon from any class. Add it to your " +
                "Adventure deck with +2/+2", Rarity.Common, Class.Rogue, 0);

        ArrayList<GameEffect> effects5_inner2 = new ArrayList<>();
        effects5_inner2.add(new Discover(new CardFilter()));
        effects5_inner2.add( new ChangeHpAttack(2, 2, new CardFilter()));
        CardFilter filter5Main = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.TYPE_CARD, new Type[]{Type.Weapon}, null);
        c[5].getCardEffects().getBattleCry().getEffects().add(new RelatedEffects(effects5_inner2, filter5Main));
        /////////////////////// DreadScale
        c[6] = new Minion(3, 200, "Dreadscale", "At the end of your turn deal 1 damage to all other minions",
                Rarity.Legendary, Class.Warlock, 0, 4, 2);

        CardFilter filter6 = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.ALL_MINIONS_EXCEPT_SELF);
        ArrayList<GameModel> models6 = new ArrayList<>(); models4.add(c[6].cloned());
        filter6.setSourceCard(models6);

        c[6].getCardEffects().getOnTurnEnd().getEffects().add(new ChangeHpAttack(-1, 0, filter6));
        /////////////////////// Blast Wave
        c[7] = new Spell(5, 100, "Blast Wave", "Deal 2 damage to all minions. overkill : Add a random Mage spell to your hand.",
                Rarity.Epic, Class.Mage, 0);

        CardFilter filter7_1 = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.ALL_MINIONS);
        c[7].getCardEffects().getBattleCry().getEffects().add(new ChangeHpAttack(-2, 0, filter7_1));
        CardFilter filter7_2 = new CardFilter(SelectingCardMethod.ONE_RANDOM, CardGroup.TYPE_CARD, new Type[]{Type.Spell}, Class.Mage);
        c[7].getCardEffects().getOverKill().getEffects().add(new AddToHand(filter7_2));
        //////////////////////// Walk the plank
        c[8] = new Spell(4, 20, "Walk the Plank", "Destroy an undamaged minion",
                Rarity.Common, Class.Rogue, 0);

        CardFilter filter8 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.ALL_MINIONS_UNDAMAGED);
        c[8].getCardEffects().getBattleCry().getEffects().add(new ChangeHpAttack(-50, 0, filter8));
        //////////////////////// Strength in Numbers

        c[9] = new Mission(1, 0, "Strength in Numbers", "Side Quest: Spend 10 Mana on minions\n" +
                "Reward: Summon a minion from your deck", Rarity.Free, Class.Natural, 10);

        c[9].getCardEffects().getOnPlay().getEffects().add(new MissionHandler(MissionType.MANA_ON_MINION));

        CardFilter filter9 = new CardFilter(SelectingCardMethod.ONE_RANDOM, CardGroup.FRIENDS_ONLY_DECK, new Type[]{Type.Minion}, null);
        c[9].getCardEffects().getMissionReward().getEffects().add(new Summon(filter9, 1));
        ///////////////////// Healing Torch
        c[10] = new Spell(3, 0, "Healing Touch", "Restore 8 health", Rarity.Free, Class.Natural,
                0);

        CardFilter filter10 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.ALL);
        c[10].getCardEffects().getBattleCry().getEffects().add(new RestoreLife(filter10, 8));
        ///////////////////////////// Star Fire
        c[11] = new Spell(6, 0, "Starfire", "Deal 5 damage. Draw a card", Rarity.Free, Class.Natural,
                0);

        CardFilter filter11_1 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.ALL);
        ChangeHpAttack hpAttack11 = new ChangeHpAttack(-5, 0, filter11_1);
        DrawCard drawCard11 = new DrawCard(1, false, new CardFilter());

        c[11].getCardEffects().getBattleCry().getEffects().add(hpAttack11);
        c[11].getCardEffects().getBattleCry().getEffects().add(drawCard11);
        /////////////////// Pyro Blast
        c[12] = new Spell(10, 100, "Pyroblast", "Deal 10 damage", Rarity.Epic, Class.Natural,
                0);

        CardFilter filter12 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.ALL);
        c[12].getCardEffects().getBattleCry().getEffects().add(new ChangeHpAttack(-10, 0, filter12));
        ///////////////////// Sorcerer's Apprentice
        c[13] = new Minion(2, 10, "Sorcerer's Apprentice", "Your Spells cost (1) less", Rarity.Common, Class.Natural,
                0, 3, 2);

        CardFilter filter13 = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.FRIENDS_ONLY_HAND, new Type[]{Type.Spell}, null);
        CardFilter filter13_2 = new CardFilter(SelectingCardMethod.RECENTLY_RECEIVED_CARD, null, new Type[]{Type.Spell}, null);

        c[13].getCardEffects().getBattleCry().getEffects().add(new AddCost(-1, filter13));
        c[13].getCardEffects().getOnCardReceive().getEffects().add(new AddCost(-1, filter13_2));
        c[13].getCardEffects().getOnExit().getEffects().add(new AddCost(+1, filter13));
        /////////////// Crazed chemist
        c[14] = new Minion(5, 40, "Crazed Chemist", "Combo: Give a friendly minion +4 Attack", Rarity.Rare, Class.Natural,
                0, 4, 4);

        CardFilter filter14 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.FRIEND_MINIONS);
        c[14].getCardEffects().getCombo().getEffects().add(new ChangeHpAttack(0, 4, filter14));
        ////////////////////////////// Hir'eek the Bat
        c[15] = new Minion(8, 200, "Hir'eek the Bat", "Fill your board with copies of this minion", Rarity.Legendary,
                Class.Natural, 0, 1, 1);

        CardFilter filter15 = new CardFilter(SelectingCardMethod.DETERMINED_CARD, null);
        filter15.setSourceCard(new ArrayList<>()); filter15.getSourceCard().add(c[15].cloned());

        c[15].getCardEffects().getBattleCry().getEffects().add(new Summon(filter15, 7));
        ////////////////////////////
        c[16] = new Weapon(1, 0, "Light's Justice", "", Rarity.Free,
                Class.Natural, 0, 4, 1);
        ////////////////// Ironfur Grizzly
        c[17] = new Minion(3, 0, "Ironfur Grizzly", "Taunt", Rarity.Free,
                Class.Natural, 0, 3, 3);

        ((Minion) c[17]).getMainAbilities().add(MinionAbility.TAUNT);
        ///////////////////// Light Warden
        c[18] = new Minion(1, 45, "Lightwarden", "Whenever a character is healed, gain +1 Attack",
                Rarity.Rare, Class.Natural, 0, 1, 2);

        CardFilter filter18 = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.SELF);
        c[18].getCardEffects().getOnAnyCharacterHeal().getEffects().add(new ChangeHpAttack(0, 1, filter18));
        //////////////////// Arcane missiles
        c[19] = new Spell(1, 0, "Arcane Missiles", "Deal 3 damage randomly split among all enemies",
                Rarity.Free, Class.Natural, 0);

        CardFilter filter19 = new CardFilter(SelectingCardMethod.ONE_RANDOM, CardGroup.ENEMY);
        c[19].getCardEffects().getBattleCry().getEffects().add(new ChangeHpAttack(-1, 0, filter19, 3));
        /////////////////////////// Sprint
        c[20] = new Spell(7, 35, "Sprint", "Draw 4 models.cards.", Rarity.Rare,
                Class.Natural, 0);

        CardFilter filter20 = new CardFilter(new Type[]{Type.Minion, Type.Spell, Type.Weapon, Type.Mission}, null);
        c[20].getCardEffects().getBattleCry().getEffects().add(new DrawCard(4, false, filter20));
        //////////////////////// Swarm Of Locusts
        c[21] = new Spell(6, 65, "Swarm of locusts", "Summon seven 1/1 Locusts with Rush.",
                Rarity.Rare, Class.Natural, 0);

        Minion locust = (Minion) c[39].cloned(); locust.getMainAbilities().add(MinionAbility.RUSH);
        ArrayList<GameModel> models21 = new ArrayList<>(); models21.add(locust);
        CardFilter filter21 = new CardFilter(SelectingCardMethod.DETERMINED_CARD, null); filter21.setSourceCard(models21);

        c[21].getCardEffects().getBattleCry().getEffects().add(new Summon(filter21, 7));
        /////////////////// Pharaoh's Blessing
        c[22] = new Spell(6, 85, "Pharaoh's blessing", "Give a minion +4/+4, Divine Shield, and Taunt.",
                Rarity.Epic, Class.Natural, 0);

        CardFilter filter22 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.ALL_MINIONS);
        ArrayList<GameEffect> effects = new ArrayList<>();
        effects.add(new ChangeHpAttack(4, 4, new CardFilter()));
        ArrayList<MinionAbility> abilities = new ArrayList<>(); abilities.add(MinionAbility.DIVINE_SHIELD); abilities.add(MinionAbility.TAUNT);
        effects.add(new ChangeAbility(new CardFilter(), abilities, new ArrayList<>()));
        c[22].getCardEffects().getBattleCry().getEffects().add(new RelatedEffects(effects, filter22));
        ////////////// Book of Specters
        c[23] = new Spell(2, 90, "Book of specters", "Draw 3 cards Discard any spells drawn.",
                Rarity.Epic, Class.Natural, 0);

        CardFilter filter23 = new CardFilter(new Type[]{Type.Minion, Type.Weapon, Type.Mission});
        c[23].getCardEffects().getBattleCry().getEffects().add(new DrawCard(3, true, filter23));
        /////////////////////// Sathrovar
        c[24] = new Minion(9, 240, "Sathrovarr", "Battlecry : Choose a friendly minion. Add a copy of it to your hand, deck and battlefield.",
                Rarity.Legendary, Class.Natural, 0, 5, 5);

        CardFilter cardFilter24 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.FRIEND_MINIONS_EXCEPT_SELF);
        ArrayList<GameEffect> effects24 = new ArrayList<>();
        effects24.add(new Summon(new CardFilter(), 1));
        effects24.add(new AddToHand(new CardFilter()));
        effects24.add(new AddToDeck(new CardFilter()));
        c[24].getCardEffects().getBattleCry().getEffects().add(new RelatedEffects(effects24, cardFilter24));
        ////////////////////// Tomb Warden
        c[25] = new Minion(8, 100, "Tomb Warden", "Taunt\nBattlecry: Summon a copy of this minion.",
                Rarity.Legendary, Class.Natural, 0, 3, 6);

        ((Minion) c[25]).getMainAbilities().add(MinionAbility.TAUNT);
        CardFilter filter25 = new CardFilter(SelectingCardMethod.DETERMINED_CARD, null);
        ArrayList<GameModel> models25 = new ArrayList<>(); models25.add(c[25].cloned());
        filter25.setSourceCard(models25);

        c[25].getCardEffects().getBattleCry().getEffects().add(new Summon(filter25, 1));
        //////////////////// Security Rover
        c[26] = new Minion(6, 90, "Security Rover", "Whenever this minion takes damage, summon a 2/3 Mech with Taunt.",
                Rarity.Rare, Class.Natural, 0, 2, 6);

        Card targetCard26 = c[40].cloned();
        ((Minion) targetCard26).getMainAbilities().add(MinionAbility.TAUNT);
        ArrayList<GameModel> models26 = new ArrayList<>(); models26.add(targetCard26);

        CardFilter filter26 = new CardFilter(SelectingCardMethod.DETERMINED_CARD, null);
        filter26.setSourceCard(models26);

        c[26].getCardEffects().getOnTakingDamage().getEffects().add(new Summon(filter26, 1));
        ////////////////////// Collector
        c[27] = new Minion(5, 80, "Curio Collector", "Whenever you draw a card, gain +1/+1.",
                Rarity.Rare, Class.Natural, 0, 4, 4);

        CardFilter filter27 = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.SELF);
        c[27].getCardEffects().getOnDraw().getEffects().add(new ChangeHpAttack(1, 1, filter27));
        //////////////////////////////////
        c[28] = new Mission(1, 40, "Learn Draconic", "Side Quset: Spend 8 Mana On Spells\n" +
                "Reward: Summon a 6/6 Sheep", Rarity.Common, Class.Natural, 8);

        Minion dragon = (Minion) c[40].cloned(); dragon.setMainHP(6); dragon.setMainAttack(6);

        c[28].getCardEffects().getOnPlay().getEffects().add(new MissionHandler(MissionType.MANA_ON_SPELL));
        CardFilter filter28 = new CardFilter(SelectingCardMethod.DETERMINED_CARD, null);
        filter28.setSourceCard(new ArrayList<>()); filter28.getSourceCard().add(dragon);

        c[28].getCardEffects().getMissionReward().getEffects().add(new Summon(filter28, 1));

        ////////////////////// Incanter's Flow
        c[29] = new Spell(2, 50, "Incanter's Flow", "Reduce the Cost of spells in your deck by (1).",
                Rarity.Common, Class.Natural, 0);

        CardFilter filter29 = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.FRIENDS_DECK_HAND, new Type[]{Type.Spell}, null);
        c[29].getCardEffects().getBattleCry().getEffects().add(new AddCost(-1, filter29));
        ///////////////////////
        c[30] = new Weapon(1, 80, "Wicked Knife", "", Rarity.Common, Class.Natural,
                0, 2, 1);
        c[31] = new Weapon(2, 70, "Gearblade", "", Rarity.Rare, Class.Natural,
                0, 3, 2);
        c[32] = new Weapon(3, 200, "Blood Fury", "", Rarity.Legendary, Class.Natural,
                0, 8, 3);
        /////////////////////// Swamp King Dread
        c[33] = new Minion(7, 190, "Swamp King Dred", "After your opponent plays a minion, attack it.",
                Rarity.Legendary, Class.Hunter, 0, 9, 9);

        CardFilter filter33 = new CardFilter(SelectingCardMethod.RECENTLY_PLAYED_CARD, null);
        c[33].getCardEffects().getOnOpponentPlay().getEffects().add(new Attack(filter33));
        ///////////////////// Gnomish Army Knife
        c[34] = new Spell(5, 0, "Gnomish Army Knife", "Gives a minion charge, windfury, divine shield, lifesteal, " +
                "poisonous, taunt, and stealth", Rarity.Free, Class.Paladin, 0);

        CardFilter filter34 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.FRIEND_MINIONS);
        MinionAbility[] added = new MinionAbility[]{MinionAbility.CHARGE, MinionAbility.WIND_FURY, MinionAbility.DIVINE_SHIELD,
                MinionAbility.LIFE_STEAL, MinionAbility.POISONOUS, MinionAbility.STEALTH, MinionAbility.TAUNT};
        ArrayList<MinionAbility> abilities34 = new ArrayList<>(); abilities34.addAll(Arrays.asList(added));
        c[34].getCardEffects().getBattleCry().getEffects().add(new ChangeAbility(filter34, abilities34, new ArrayList<>() ));
        //////////////////////// High Priest Amet
        c[35] = new Minion(4, 170, "High Priest Amet", "Whenever you summoned a minion set its health equal to this minion",
                Rarity.Legendary, Class.Priest, 0, 2, 7);

        CardFilter filter36_1 = new CardFilter(SelectingCardMethod.RECENTLY_SUMMONED_CARD, null);
        CardFilter filter36_2 = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.SELF);
        c[35].getCardEffects().getOnSummon().getEffects().add(new ChangeHpAttack(0, 0, filter36_1, 1, filter36_2, null, 0));
        //////////////////// Brazen Zealot
        c[36] = new Minion(1, 70, "Brazen zealot", "Whenever you summon a minion, gain +1 Attack.",
                Rarity.Rare, Class.Paladin, 0, 2,1);

        CardFilter filter36 = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.SELF);
        c[36].getCardEffects().getOnSummon().getEffects().add(new ChangeHpAttack(0, 1, filter36));
        ///////////////// Temple Enforcer
        c[37] = new Minion(5,30 ,"Temple Enforcer", "Battlecry: Give a friendly minion +3 Health.",
                Rarity.Common, Class.Priest, 0, 5, 6);

        CardFilter filter37 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.FRIEND_MINIONS_EXCEPT_SELF);
        c[37].getCardEffects().getBattleCry().getEffects().add(new ChangeHpAttack(3, 0, filter37));
        //////////////////// Scavenger
        c[38] = new Spell(2, 40, "Scavenger's Ingenuity", "Draw a Beast.\nGive it +3/+3.",
                Rarity.Common, Class.Hunter, 0);

        CardFilter filter38 = new CardFilter(SelectingCardMethod.DETERMINED_LATER, null, new Type[]{Type.Minion}, null);
        ArrayList<GameEffect> effects38 = new ArrayList<>();
        effects38.add(new DrawCard(1, false, new CardFilter()));
        effects38.add(new ChangeHpAttack(3, 3, new CardFilter(), 1));
        c[38].getCardEffects().getBattleCry().getEffects().add(new RelatedEffects(effects38, filter38));


        List<Card> A1 = new ArrayList<>(Arrays.asList(c));

        FileWriter fileWriter2 = new FileWriter("src/main/resources/Data/Cards.json");
        ObjectMapper objectMapper1 = new ObjectMapper();
        objectMapper1.enableDefaultTyping();
        objectMapper1.writerWithDefaultPrettyPrinter().writeValue(fileWriter2, A1);
        fileWriter2.close();

        //  Test to see if models.cards are fine ......

        FileReader fileReader = new FileReader("src/main/resources/Data/Cards.json");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        objectMapper1.enableDefaultTyping();
        List<Card> list;
        list = Arrays.asList(objectMapper1.readValue(bufferedReader, Card[].class));

        List<Card> list2 = new ArrayList<>();

        System.out.println(list.size());
        for (Card card :
                list) {
            if (card.getCardCost() == 0)
                list2.add(card);
        }
        list2.clear();
        bufferedReader.close();
        fileReader.close();

        // Creating Heroes ........

        List<Deck> list1 = new ArrayList<>();
        List<Heroes> A3 = new ArrayList<>();

        /////// HeroPower 1
        HeroPower h1 = new HeroPower("Fire Blast", 2, "Deal 1 damage", 1, false);

        CardFilter filterH1 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.ALL);
        h1.getEffects().getBattleCry().getEffects().add(new ChangeHpAttack(-1, 0, filterH1));
        /////// HeroPower 2
        HeroPower h2 = new HeroPower("Life Tap", 2, "Deal 2 damage to your hero. Then " +
                "add +1/+1 to a random minion on field OR draw a card and add it to your hand.", 1, false);

        EffectList randomEffects = new EffectList(); randomEffects.setChooseRandom(true);
        CardFilter filterH2 = new CardFilter(SelectingCardMethod.ONE_RANDOM, CardGroup.FRIEND_MINIONS);
        randomEffects.getEffects().add(new ChangeHpAttack(1, 1, filterH2));
        randomEffects.getEffects().add(new DrawCard(1, false, new CardFilter(new Type[]{Type.Weapon, Type.Mission, Type.Spell})));

        h2.getEffects().getBattleCry().getEffects().add(
                new ChangeHpAttack(-2, 0, new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.FRIEND_HERO)));
        h2.getEffects().getBattleCry().getEffects().add(randomEffects);
        /////// HeroPower 3
        HeroPower h3 = new HeroPower("Thief", 3, "Steal 1 card from enemy's hand", 1, false);

        h3.getEffects().getBattleCry().getEffects().add(new Steal(1));
        /////// HeroPower 4
        HeroPower h4 = new HeroPower("Caltrops", 0, "After your opponent plays a minion " +
                "deal 1 damage to it.", 1, true);

        CardFilter filterH4 = new CardFilter(SelectingCardMethod.RECENTLY_PLAYED_CARD, null);
        CardEffects effectsH4 = new CardEffects();
        effectsH4.getOnOpponentPlay().getEffects().add(new ChangeHpAttack(-1, 0, filterH4));
        h4.setEffects(effectsH4);

        /////// HeroPower 5
        HeroPower h5 = new HeroPower("The Silver Hand", 2, "Summon two 1/1 minion", 1, false);

        ArrayList<GameModel> modelsH5 = new ArrayList<>(); modelsH5.add(c[39].cloned());
        CardFilter filterH5 = new CardFilter(SelectingCardMethod.DETERMINED_CARD, null);
        filterH5.setSourceCard(modelsH5);

        h5.getEffects().getBattleCry().getEffects().add(new Summon(filterH5, 2));
        /////// HeroPower 6
        HeroPower h6 = new HeroPower("Heal", 2, "Restore 4 health", 1, false);

        CardFilter filterH6 = new CardFilter(SelectingCardMethod.PLAYER_SELECTS, CardGroup.ALL);
        h6.getEffects().getBattleCry().getEffects().add(new RestoreLife(filterH6, 4));


        //////// Hero 1
        Heroes hero1 = new Mage(list1, 30, h1, "Mana cost for spells are 2 mana less than other heroes");

        CardFilter heroFilter1 = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.FRIENDS_DECK_HAND, new Type[]{Type.Spell}, null);
        hero1.getSpecialPower().getPassive().getEffects().add(new AddCost(-2, heroFilter1));

        //////// Hero 2
        Heroes hero2 = new Warlock(list1, 35, h2, "Have 35 Hp");

        //////// Hero 3
        Heroes hero3 = new Rogue(list1, 30, h3, "Other class cards cost 2 less");

        //////// Hero 4
        Heroes hero4 = new Hunter(list1, 30, h4, "All minions have Rush");

        ArrayList<MinionAbility> abilities4 = new ArrayList<>(); abilities4.add(MinionAbility.RUSH);

        CardFilter heroFilter4 = new CardFilter(SelectingCardMethod.RECENTLY_SUMMONED_CARD, null);
        hero4.getSpecialPower().getOnSummon().getEffects().add(new ChangeAbility(heroFilter4, abilities4, new ArrayList<>()));

        CardFilter heroFilter4_2 = new CardFilter(SelectingCardMethod.RECENTLY_PLAYED_CARD, null);
        hero4.getSpecialPower().getOnPlay().getEffects().add(new ChangeAbility(heroFilter4_2, abilities4, new ArrayList<>()));

        //////// Hero 5
        Heroes hero5 = new Paladin(list1, 30, h5, "At the start of your turn: Add +1/+1 to a random minion");

        CardFilter heroFilter5 = new CardFilter(SelectingCardMethod.ONE_RANDOM, CardGroup.FRIEND_MINIONS);
        hero5.getSpecialPower().getOnTurnBegin().getEffects().add(new ChangeHpAttack(1, 1, heroFilter5));

        //////// Hero 6
        Heroes hero6 = new Priest(list1, 30, h6, "All Spells powers are doubled");
        hero6.getSpecialPower().getPassive().getEffects().add(new DoubleSpellPowers());

        A3.add(hero4);
        A3.add(hero6);
        A3.add(hero1);
        A3.add(hero2);
        A3.add(hero3);
        A3.add(hero5);

        FileWriter fileWriter = new FileWriter("src/main/resources/Data/Heroes.json");
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(fileWriter, A3);
        fileWriter.close();

        // creating Passives
        InfoPassive[] passives = new InfoPassive[7];

        ///////// 0
        passives[0] = new InfoPassive("Free Power", "You can use your hero power twice and it costs 1 less");

        passives[0].getEffects().getPassive().getEffects().add(new HeroPowerModifier(-1, 2));
        ///////// 1
        passives[1] = new InfoPassive("Mana Jump", "You have 1 more mana each turn");

        passives[1].getEffects().getPassive().getEffects().add(new ManaJump(1));
        ///////// 2
        passives[2] = new InfoPassive("Nurse", "After each turn, restore 1 health of a damaged minion");

        CardFilter filter2 = new CardFilter(SelectingCardMethod.ONE_RANDOM, CardGroup.FRIEND_MINIONS_DAMAGED);
        passives[2].getEffects().getPassive().getEffects().add(new RestoreLife(filter2, 1));
        ///////// 3
        passives[3] = new InfoPassive("Off Card", "Your Cards cost (1) less");

        CardFilter filter3 = new CardFilter(SelectingCardMethod.RECENTLY_RECEIVED_CARD, null);
        passives[3].getEffects().getOnCardReceive().getEffects().add(new AddCost(-1, filter3));
        ///////// 4
        passives[4] = new InfoPassive("Twice Draw", "Draw 2 card each turn instead of 1");

        passives[4].getEffects().getPassive().getEffects().add(new MultipleDraw(2));
        ///////// 5
        passives[5] = new InfoPassive("Warriors", "if a minion dies adds 2 defense to your hero");

        passives[5].getEffects().getOnAnyMinionDeath().getEffects().add(new DefenseAdder(2));
        ///////// 6
        passives[6] = new InfoPassive("Zombie", "Change your hero power and it costs 0");

        HeroPower heroPower6 = new HeroPower("Zombie", 0, "Uses your whole mana. Revive a minion who" +
                " died in this game with mana less those of yours", 1, false);
        heroPower6.getEffects().getBattleCry().getEffects().add(new ReviveDead());
        passives[6].getEffects().getPassive().getEffects().add(new HeroPowerChanger(heroPower6));

        ////////// Done.

        List<InfoPassive> passives1 = new ArrayList<>(Arrays.asList(passives));

        FileWriter fileWriter1 = new FileWriter("src/main/resources/Data/Passives.json");
        objectMapper1.writerWithDefaultPrettyPrinter().writeValue(fileWriter1, passives1);
        fileWriter1.close();
        // Testing

        fileReader = new FileReader("src/main/resources/Data/Passives.json");
        List<InfoPassive> passives2 = Arrays.asList(objectMapper1.readValue(fileReader, models.InfoPassive[].class));
        fileReader.close();
        System.out.println("Passives : ");
        for (InfoPassive passive : passives2) {
            System.out.println(passive.getName());
        }


        // testing configs
//        ModelsConfigs configs1 = new ModelsConfigs();
        LogicConstants configs2 = new LogicConstants();
        GameGraphicConstants configs3 = new GameGraphicConstants();
        PanelsConfigs configs4 = new PanelsConfigs();
        PathConfigs configs5 = new PathConfigs();

        System.out.println("Test Over. Successful!");

    }
}