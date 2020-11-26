package logic;

import Util.GameLogger;
import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import enums.*;
import enums.Class;
import logic.GameEffects.*;
import models.Card;
import models.GameModel;
import models.HeroPower;
import models.Heroes;
import models.cards.Minion;
import models.cards.Mission;
import models.cards.Spell;
import models.cards.Weapon;

import java.util.*;

public class MyGameVisitor implements GameEffectVisitor {

    private MyGameState gameState;
    private GameActionExecutor executor;
    private Random random = new Random();
    private final int discoverCards = 3;

    private GameLogger logger;

    public MyGameVisitor(MyGameState gameState, GameActionExecutor executor, GameLogger logger) {
        this.gameState = gameState;
        this.executor = executor;
        this.logger = logger;
    }

    @Override
    public void visit(ChangeHpAttack hpAttack) {
        ArrayList<GameModel> models = getFilteredModels(hpAttack);

        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + hpAttack.toString() + ", on Models: " + Arrays.toString(models.toArray()));

        for (GameModel model : models) {
            if (model instanceof Minion) {
                Minion minion = (Minion) model;
                changeMinionStats(minion, hpAttack);
            }
            if (model instanceof Heroes) {
                Heroes hero = (Heroes) model;
                if (hpAttack.getLife() <= 0)
                    hero.takeDamage(-hpAttack.getLife(), this);
                else {
                    hero.setHP(hero.getHP() + hpAttack.getLife());
                    executor.performHealEffects();
                }
            }
            if (model instanceof Weapon) {
                Weapon weapon = (Weapon) model;
                weapon.setAttack(weapon.getAttack() + hpAttack.getAttack());
                weapon.setDurability(weapon.getDurability() + hpAttack.getLife());
            }
        }
    }

    private void changeMinionStats(Minion minion, ChangeHpAttack hpAttack) {
        if (hpAttack.getTargetHP() != null) {
            GameModel hpModel = filterGameCards(hpAttack.getTargetHP(), hpAttack.getSourcePlayerIndex()).get(0);
            minion.setHP(((Minion) hpModel).getHP());
        } else {
            if (hpAttack.getLife() > 0) {
                minion.setHP(minion.getHP() + hpAttack.getLife());
                executor.performHealEffects();
            } else if (hpAttack.getLife() < 0) {
                minion.takeDamage(-hpAttack.getLife(), this);
            }
            if (minion.getHP() < 0) {
                if (findSourceCard() != null) findSourceCard().findEffects().getOverKill().accept(this);
            }
        }

        if (hpAttack.getTargetAttack() != null) {
            GameModel hpModel = filterGameCards(hpAttack.getTargetAttack(), hpAttack.getSourcePlayerIndex()).get(0);
            minion.setAttack(((Minion) hpModel).getAttack());
        } else {
            minion.setAttack(minion.getAttack() + hpAttack.getAttack());
        }
    }

    @Override
    public void visit(AddToHand addToHand) {
        ArrayList<GameModel> models = getFilteredModels(addToHand);
        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + addToHand.toString() + ", on Models: " + Arrays.toString(models.toArray()));

        for (GameModel model : models) {
            Card card = (Card) model;

            boolean activePlayer = (addToHand.getSourcePlayerIndex() == gameState.getActivePlayerIndex());
            System.out.println("Add to hand for player who is: " + activePlayer);

            executor.giveCard(card.cloned(), activePlayer);
        }
    }

    @Override
    public void visit(DrawCard drawCard) {
        ArrayList<GameModel> groupModels = drawCard.getFilter().getSourceCard();
        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + drawCard.toString());

        for (int i = 0; i < drawCard.getNumber(); i++) {
            if (drawCard.isDiscardingBadCards()) {
                Card card = executor.drawCard(true);
                if (groupModels != null) groupModels.add(card);
                if (drawCard.getFilter() == null && Arrays.asList(drawCard.getFilter().getType()).contains(card.getCardType()))
                    executor.giveCard(card, true);
                else
                    executor.burnCard(card);

            } else {
                for (int j = 0; j < gameState.getActivePlayer().getDeck().size(); j++) {
                    Card card = gameState.getActivePlayer().getDeck().get(j);

                    if (drawCard.getFilter().getType() == null || Arrays.asList(drawCard.getFilter().getType()).contains(card.getCardType())) {
                        if (groupModels != null) groupModels.add(card);
                        executor.drawCard(true, j);
                        executor.giveCard(card, true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void visit(RelatedEffects groupEffect) {
        ArrayList<GameModel> models = getFilteredModels(groupEffect);

        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + groupEffect.toString() + ", on Models: " + Arrays.toString(models.toArray()));


        for (GameEffect effect : groupEffect.getEffects()) {
            effect.getFilter().setSelectingCardMethod(SelectingCardMethod.DETERMINED_CARD);
            effect.getFilter().setSourceCard(models);

            effect.getFilter().setType(groupEffect.getFilter().getType());
            effect.getFilter().setCardClass(groupEffect.getFilter().getCardClass());

            effect.accept(this);
        }
    }

    @Override
    public void visit(Discover discover) {
        ArrayList<GameModel> models = getFilteredModels(discover);

        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + discover.toString() + ", on Models: " + Arrays.toString(models.toArray()));

        Collections.shuffle(models);
        List<Card> cards = new ArrayList<>();
        for (GameModel model : models) {
            if (model instanceof Card)
                cards.add((Card) model);
            else
                System.out.println("Error in Visiting Discover...");

            if (cards.size() >= discoverCards)
                break;
        }
        Card card = null;
        while (card == null)
            card = executor.playerDiscover(cards);

        executor.giveCard(card, true);
    }

    @Override
    public void visit(ChangeAbility giveAbility) {
        ArrayList<GameModel> models = getFilteredModels(giveAbility);

        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + giveAbility.toString() + ", on Models: " + Arrays.toString(models.toArray()));

        for (GameModel model : models) {
            Minion minion = (Minion) model;
            minion.getAbilities().addAll(giveAbility.getAddedAbilities());
            minion.getAbilities().removeAll(giveAbility.getRemovedAbilities());

            if (giveAbility.getAddedAbilities().contains(MinionAbility.WIND_FURY) && minion.getRemainedAttacks() > 0)
                minion.setRemainedAttacks(2);
        }
    }

    @Override
    public void visit(RestoreLife restoreLife) {
        ArrayList<GameModel> models = getFilteredModels(restoreLife);

        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + restoreLife.toString() + ", on Models: " + Arrays.toString(models.toArray()));


        for (GameModel model : models) {
            if (model instanceof Heroes) {
                Heroes hero = (Heroes) model;
                int originalLife = Heroes.getHero(hero.getName()).getHP();
                hero.setHP(Math.min(originalLife, hero.getHP() + restoreLife.getLife()));
            } else if (model instanceof Minion) {
                Minion minion = (Minion) model;
                int originalLife = Math.max(((Minion) Card.getCard(minion.getName())).getHP(), minion.getHP());
                minion.setHP(Math.min(originalLife, minion.getHP() + restoreLife.getLife()));

                if (minion.getHP() > originalLife)
                    executor.performHealEffects();
            }
        }
    }

    @Override
    public void visit(Attack attack) {
        ArrayList<GameModel> models = getFilteredModels(attack);

        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + attack.toString() + ", on Models: " + Arrays.toString(models.toArray()));

        Card attacker = (Card) findSourceCard();

        for (GameModel model : models) {
            executor.performAttack(attacker, model);
        }
    }

    @Override
    public void visit(HeroPowerModifier heroPowerModifier) {
        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + heroPowerModifier.toString());

        PlayerFields player = findPlayer(heroPowerModifier.getSourcePlayerIndex());
        HeroPower power = player.getHero().getHeroPower();

        power.setActionEachTurn(heroPowerModifier.getUsageNumber());
        power.setMana(power.getMana() + heroPowerModifier.getAddedMana());
    }

    @Override
    public void visit(ManaJump manaJump) {
        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + manaJump.toString());

        PlayerFields player = findPlayer(manaJump.getSourcePlayerIndex());

        player.setEachTurnMana(player.getEachTurnMana() + manaJump.getAddedMana());
    }

    @Override
    public void visit(MultipleDraw multipleDraw) {
        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + multipleDraw.toString());

        PlayerFields player = findPlayer(multipleDraw.getSourcePlayerIndex());

        player.setNumberOfNewCardsEachTurn(multipleDraw.getDrawEachTurn());
    }

    @Override
    public void visit(HeroPowerChanger heroPowerChanger) {
        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + heroPowerChanger.toString());

        PlayerFields player = findPlayer(heroPowerChanger.getSourcePlayerIndex());

        player.getHero().setHeroPower(heroPowerChanger.getTargetHeroPower().cloned());
        player.getHero().getHeroPower().getEffects().setSourcePlayerIndex(heroPowerChanger.getSourcePlayerIndex());
        player.getHero().getSpecialPower().setSourcePlayerIndex(heroPowerChanger.getSourcePlayerIndex());
    }

    @Override
    public void visit(Steal steal) {
        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + steal.toString());

        List<Card> cards = gameState.getNotActivePlayer().getHand();
        if (cards.size() > 0) {
            int n = random.nextInt(cards.size());
            Card card = cards.remove(n);
            executor.giveCard(card, true);
        }
    }

    @Override
    public void visit(DoubleSpellPowers doubleSpellPowers) {
        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + doubleSpellPowers.toString());

        CardFilter filter = new CardFilter(SelectingCardMethod.DETERMINED_GROUP, CardGroup.FRIENDS_DECK_HAND, new Type[]{Type.Spell}, null);

        ArrayList<GameModel> models = filterGameCards(filter, doubleSpellPowers.getSourcePlayerIndex());

        for (GameModel model : models) {
            if (!(model instanceof Spell)) continue;
            Spell spell = (Spell) model;
            for (EffectList effectList : spell.getCardEffects().allLists()) {
                for (GameEffect effect : effectList.getEffects()) {
                    doubleEffectsPower(effect);
                }
            }
        }
    }

    @Override
    public void visit(DefenseAdder defenseAdder) {
        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + defenseAdder.toString());

        PlayerFields player = findPlayer(defenseAdder.getSourcePlayerIndex());

        player.getHero().setDefense(player.getHero().getDefense() + defenseAdder.getAddedDefense());
    }

    @Override
    public void visit(MissionHandler missionHandler) {
        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + missionHandler.toString());

        if (!(gameState.getAffectingModel() instanceof Mission)) return;
        Mission mission = (Mission) gameState.getAffectingModel();

        switch (missionHandler.getMissionType()) {
            case MANA_ON_SPELL:
                if (gameState.getPlayingModel() instanceof Spell) {
                    mission.setProgress(mission.getProgress() + ((Spell) gameState.getPlayingModel()).getMana());
                }
                break;
            case MANA_ON_MINION:
                if (gameState.getPlayingModel() instanceof Minion) {
                    mission.setProgress(mission.getProgress() + ((Minion) gameState.getPlayingModel()).getMana());
                }
                break;
        }
    }

    @Override
    public void visit(ReviveDead reviveDead) {
        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + reviveDead.toString());

        PlayerFields player = findPlayer(reviveDead.getSourcePlayerIndex());
        int mana = player.getThisTurnMana();
        player.setThisTurnMana(0);

        List<Minion> deadMinions = gameState.getDeadMinions();
        Collections.shuffle(deadMinions);

        for (Minion deadMinion : deadMinions) {
            if (deadMinion.getMana() <= mana) {
                Minion minion = deadMinion.cloned();
                handleCardSource(minion, reviveDead.getSourcePlayerIndex());
                summonMinion(minion, player);
                break;
            }
        }
    }

    private void summonMinion(Minion minion, PlayerFields player) {
        for (int i = 0; i < player.getPlayedCards().size(); i++) {
            if (player.getPlayedCards().get(i) == null) {
                executor.performSummonEffects(minion);
                player.getPlayedCards().set(i, minion);
                break;
            }
        }
    }


    private void doubleEffectsPower(GameEffect effect) {
        if (effect instanceof ChangeHpAttack) {
            ChangeHpAttack hpAttack = ((ChangeHpAttack) effect);
            hpAttack.setLife(2 * hpAttack.getLife());
            hpAttack.setAttack(2 * hpAttack.getAttack());
        }
        if (effect instanceof RestoreLife) {
            RestoreLife life = (RestoreLife) effect;
            life.setLife(2 * life.getLife());
        }
    }

    @Override
    public void visit(Summon summon) {
        ArrayList<GameModel> models = getFilteredModels(summon);

        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + summon.toString() + ", on Models: " + Arrays.toString(models.toArray()));


        for (GameModel model : models) {
            removeFromDeck((Card) model);

            if (model instanceof Minion) {
                Minion minion = ((Minion) model).cloned();
                handleCardSource(minion, summon.getSourcePlayerIndex());

                PlayerFields player = findPlayer(summon.getSourcePlayerIndex());
                summonMinion(minion, player);
            } else if (model instanceof Weapon) {
                Weapon weapon = ((Weapon) model).cloned();
                findPlayer(summon.getSourcePlayerIndex()).getHero().setWeapon(weapon);
            }
        }
    }

    private void removeFromDeck(Card card) {
        for (int i = 0; i < gameState.getActivePlayer().getHand().size(); i++) {
            if (gameState.getActivePlayer().getHand().get(i) == card) {
                gameState.getActivePlayer().getHand().remove(i);
                break;
            }
        }
        for (int i = 0; i < gameState.getActivePlayer().getDeck().size(); i++) {
            if (gameState.getActivePlayer().getDeck().get(i) == card) {
                gameState.getActivePlayer().getDeck().remove(i);
                break;
            }
        }
    }

    private void handleCardSource(GameModel card, int playerIndex) {
        if (card instanceof Card)
            ((Card) card).getCardEffects().setSourcePlayerIndex(playerIndex);
    }

    @Override
    public void visit(AddCost addCost) {
        ArrayList<GameModel> models = getFilteredModels(addCost);

        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + addCost.toString() + ", on Models: " + Arrays.toString(models.toArray()));


        for (GameModel model : models) {
            if (model instanceof Card) {
                Card card = (Card) model;
                card.setMana(Math.max(0, card.getMana() + addCost.getCost()));
            } else {
                System.out.println("Error in Visiting addCost!");
            }
        }
    }

    @Override
    public void visit(Transform transform) {
        ArrayList<GameModel> models = getFilteredModels(transform);

        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + transform.toString() + ", on Models: " + Arrays.toString(models.toArray()));

        GameModel targetModel = null;
        try {
            targetModel = filterGameCards(transform.getTargetFilter(), transform.getSourcePlayerIndex()).get(0);
        } catch (Exception e) {
            System.out.println("Error in visiting transform: target model null.");
            e.printStackTrace();
        }

        if (targetModel instanceof Minion) {
            executor.changeMinion(models, (Minion) targetModel);
        } else if (targetModel instanceof Heroes) {
            executor.changeHero((Heroes) models.get(0), (Heroes) targetModel);
        }
    }

    @Override
    public void visit(AddToDeck addToDeck) {
        ArrayList<GameModel> models = getFilteredModels(addToDeck);

        logger.writeLog(LogType.EFFECTS_VISITED, "Effect: " + addToDeck.toString() + ", on Models: " + Arrays.toString(models.toArray()));


        for (GameModel model : models) {
            Card addedCard = (Card) model;

            List<Card> deck = (addToDeck.getSourcePlayerIndex() == 1) ? gameState.getPlayer1().getDeck() : gameState.getPlayer2().getDeck();
            System.out.println("Source in filter" + addToDeck.getSourcePlayerIndex());

            deck.add(random.nextInt(deck.size()), addedCard.cloned());
        }
    }

    private ArrayList<GameModel> getFilteredModels(GameEffect effect) {
        CardFilter filter = effect.getFilter();
        int sourcePlayer = effect.getSourcePlayerIndex();
        return filterClass(filterType(filterGameCards(filter, sourcePlayer), filter.getType()), filter.getCardClass());
    }

    private ArrayList<GameModel> filterGameCards(CardFilter filter, int sourcePlayer) {
        ArrayList<GameModel> filteredCards = new ArrayList<>();

        switch (filter.getSelectingCardMethod()) {
            case ONE_RANDOM:
                ArrayList<GameModel> models = new ArrayList<>(filterGroups(filter, sourcePlayer));
                if (models.size() > 0) {
                    int n = random.nextInt(models.size());
                    filteredCards.add(models.get(n));
                }
                break;
            case PLAYER_SELECTS:
                GameModel gameModel = executor.playerChooseCard(filterGroups(filter, sourcePlayer));
                if (gameModel != null)
                    filteredCards.add(gameModel);
                break;
            case DETERMINED_CARD:
                filteredCards.addAll(filter.getSourceCard());
                break;
            case DETERMINED_LATER:
                break;
            case RECENTLY_PLAYED_CARD:
                filteredCards.add(gameState.getPlayingModel());
                break;
            case RECENTLY_SUMMONED_CARD:
                filteredCards.add(gameState.getSummoningModel());
                break;
            case LAST_DRAWN_CARD:
                List<Card> hand = gameState.getActivePlayer().getHand();
                if (hand.size() > 0)
                    filteredCards.add(hand.get(hand.size() - 1));
                break;
            case DETERMINED_GROUP:
                filteredCards.addAll(filterGroups(filter, sourcePlayer));
                break;
            case RECENTLY_RECEIVED_CARD:
                filteredCards.add(gameState.getReceivedCard());
                break;
            case RECENTLY_TRANSFORMED_CARD:
                filteredCards.add(gameState.getTransformedModel());
                break;
        }
        return filteredCards;
    }


    private ArrayList<GameModel> filterGroups(CardFilter filter, int sourcePlayer) {
        ArrayList<GameModel> creatures = new ArrayList<>();

        switch (filter.getCardGroup()) {
            case ALL:
                creatures.addAll(gameState.getActivePlayer().getPlayedCards());
                creatures.addAll(gameState.getNotActivePlayer().getPlayedCards());
                creatures.add(gameState.getActivePlayer().getHero());
                creatures.add(gameState.getNotActivePlayer().getHero());
                break;
            case ENEMY:
                creatures.addAll(findPlayer(3 - sourcePlayer).getPlayedCards());
                creatures.add(findPlayer(3 - sourcePlayer).getHero());
                break;
            case FRIENDS:
                creatures.addAll(findPlayer(sourcePlayer).getPlayedCards());
                creatures.add(findPlayer(sourcePlayer).getHero());
                break;
            case ALL_MINIONS:
                creatures.addAll(gameState.getActivePlayer().getPlayedCards());
                creatures.addAll(gameState.getNotActivePlayer().getPlayedCards());
                break;
            case ENEMY_MINIONS:
                creatures.addAll(findPlayer(3 - sourcePlayer).getPlayedCards());
                break;
            case FRIEND_MINIONS:
                creatures.addAll(findPlayer(sourcePlayer).getPlayedCards());
                break;
            case FRIEND_MINIONS_EXCEPT_SELF:
                creatures.addAll(findPlayer(sourcePlayer).getPlayedCards());
                removeSourceCard(creatures);
                break;
            case FRIEND_MINIONS_DAMAGED:
                creatures.addAll(findPlayer(sourcePlayer).getPlayedCards());
                Iterator<GameModel> iterator = creatures.iterator();

                removeNulls(creatures);

                while (iterator.hasNext()) {
                    Minion minion = (Minion) iterator.next();
                    if (!minion.isDamaged())
                        iterator.remove();
                }
                break;
            case ALL_MINIONS_UNDAMAGED:
                for (Minion minion : gameState.getActivePlayer().getPlayedCards()) {
                    if (minion == null) continue;
                    if ((minion.getHP() >= ((Minion) Card.getCard(minion.getName())).getHP())) {
                        creatures.add(minion);
                    }
                }
                for (Minion minion : gameState.getNotActivePlayer().getPlayedCards()) {
                    if (minion == null) continue;
                    if ((minion.getHP() >= ((Minion) Card.getCard(minion.getName())).getHP())) {
                        creatures.add(minion);
                    }
                }
                break;
            case ALL_MINIONS_EXCEPT_SELF:
                creatures.addAll(gameState.getActivePlayer().getPlayedCards());
                creatures.addAll(gameState.getNotActivePlayer().getPlayedCards());
                removeSourceCard(creatures);
                break;
            case SELF:
                creatures.add(findSourceCard());
                break;
            case FRIENDS_DECK_HAND:
                creatures.addAll(findPlayer(sourcePlayer).getDeck());
                creatures.addAll(findPlayer(sourcePlayer).getHand());
                break;
            case FRIENDS_ONLY_DECK:
                creatures.addAll(findPlayer(sourcePlayer).getDeck());
                break;
            case FRIENDS_ONLY_HAND:
                creatures.addAll(findPlayer(sourcePlayer).getHand());
                break;
            case FRIEND_HERO:
                creatures.add(findPlayer(sourcePlayer).getHero());
                break;
            case ENEMY_HERO:
                creatures.add(findPlayer(3 - sourcePlayer).getHero());
                break;
            case DEAD_MINIONS:
                creatures.addAll(gameState.getDeadMinions());
                break;
            case TYPE_CARD:
                List<Card> cards = (filter.getCardClass() != null) ? Card.getAllCardsOfClass(filter.getCardClass()) : Card.getAllCards();
                for (Card card : cards) {
                    if (Arrays.asList(filter.getType()).contains(card.getCardType()))
                        creatures.add(card);
                }
                break;
        }

        removeNulls(creatures);

        return creatures;
    }

    private void removeNulls(ArrayList<?> list) {
        while (list.contains(null))
            list.remove(null);
    }

    private PlayerFields findPlayer(int index) {
        if (index != 1 && index != 2)
            System.out.println("Finding player with index 0 Error");

        return (index == 1) ? gameState.getPlayer1() : gameState.getPlayer2();
    }

    private GameModel findSourceCard() {
        return gameState.getAffectingModel();
    }

    private void removeSourceCard(ArrayList<GameModel> models) {
        Card card = (Card) findSourceCard();
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i) == card) {
                models.remove(i);
                break;
            }
        }
    }

    private ArrayList<GameModel> filterType(ArrayList<GameModel> models, Type[] types) {
        if (types != null) {
            ArrayList<GameModel> filteredCards = new ArrayList<>();

            for (GameModel model : models) {
                if (!(model instanceof Card)) {
                    filteredCards.add(model);
                    continue;
                }
                List<Type> types_list = Arrays.asList(types);
                Card card = (Card) model;

                if (types_list.contains(card.getCardType()))
                    filteredCards.add(card);
            }

            return filteredCards;
        } else
            return models;
    }

    private ArrayList<GameModel> filterClass(ArrayList<GameModel> models, Class cardClass) {
        if (cardClass == null)
            return models;
        else {
            ArrayList<GameModel> finalModels = new ArrayList<>();

            for (GameModel model : models) {
                if (model instanceof Card && ((Card) model).getCardClass() == cardClass)
                    finalModels.add(model);
            }

            return finalModels;
        }
    }
}
