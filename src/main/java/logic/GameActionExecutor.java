package logic;

import Util.Administrator;
import Util.Monitor;
import abstracts.Attacker;
import abstracts.Defender;
import configs.LogicConstants;
import enums.*;
import models.*;
import models.cards.Minion;
import models.cards.Mission;
import models.cards.Weapon;
import network.server.GameServer;

import java.util.*;

public class GameActionExecutor {

    private final MyGameState gameState;
    private final LogicConstants constants;
    private final Monitor monitor;

    private GameServer gameServer;
    private MyGameVisitor effectsVisitor;
    private SituationHandler situationHandler;
    private InputTranslator translator;

    private boolean isGameFromFile;

    private CPUPlayer cpuPlayer;

    public GameActionExecutor(MyGameState gameState, GameServer gameServer) {
        this.gameState = gameState;
        this.constants = new LogicConstants();
        this.monitor = new Monitor();

        this.gameServer = gameServer;
        this.effectsVisitor = new MyGameVisitor(gameState, this, gameServer.getLogger(1));
        this.situationHandler = new SituationHandler(gameState, this);
        this.translator = new InputTranslator(gameState, this);

        makeTimer();
    }

    ////////////////////////////////
    private void writeLog(LogType type, String message) {
        gameServer.getLogger(1).writeLog(type, message);
        gameServer.getLogger(2).writeLog(type, message);
    }

    private void makeTimer() {
        gameState.getTimer().setUsualRunnable(this::endTurn);
        gameState.getTimer().setStaticRunnable(gameServer::updateCounter);
    }

    public void beginGame(boolean isFromFile, boolean isPlayer2CPU) {
//        PlayerLogger.writeLog("game begins");
        isGameFromFile = isFromFile;
        writeLog(LogType.GAME_REPORT, "Game Begins");
        getPassive(1);
        if (!isPlayer2CPU) getPassive(2);
        setEffectsIndex();
        applyPassives();
        handleCPUPlayer(isPlayer2CPU);
        if (!isFromFile) shuffleCards();
        letPlayerChangeInitialCards(isPlayer2CPU);
        givePlayersStartingCards();
        beginTurn();
        updateGraphics();

        gameState.getTimer().start();
    }

    private void handleCPUPlayer(boolean isPlayer2CPU) {
        if (!isPlayer2CPU) return;

        cpuPlayer = new CPUPlayer(gameState, this);
        cpuPlayer.selectPassive(initialPassives());
        cpuPlayer.start();
    }

    private void setEffectsIndex() {
        for (GameModel model : getAllModels(Group.PLAYER1_INCLUDING_DECK)) {
            model.findEffects().setSourcePlayerIndex(1);
        }
        for (GameModel model : getAllModels(Group.PLAYER2_INCLUDING_DECK)) {
            model.findEffects().setSourcePlayerIndex(2);
        }
    }

    private void getPassive(int playerIndex) {
        InfoPassive passive = gameServer.askForPassive(initialPassives(), playerIndex);
        if (playerIndex == 1)
            gameState.getPlayer1().setPassive(passive);
        else if (playerIndex == 2)
            gameState.getPlayer2().setPassive(passive);

//        PlayerLogger.writeLog("Passive Activated", "Passive : " + passive.getName());
        writeLog(LogType.GAME_REPORT, "Activated Passive -> " + passive.getName());
    }

    private void applyPassives() {
        for (GameModel model : getAllModels(Group.ALL_INCLUDING_DECK)) {
            model.findEffects().getPassive().accept(effectsVisitor);
        }
    }

    private void letPlayerChangeInitialCards(boolean isPlayer2CPU) {
        ArrayList<Card> p1Cards = new ArrayList<>();
        ArrayList<Card> p2Cards = new ArrayList<>();
        for (int i = 0; i < constants.getStartingCardsNumber(); i++) {
            p1Cards.add(gameState.getPlayer1().getDeck().get(i));
            p2Cards.add(gameState.getPlayer2().getDeck().get(i));
        }

        String message = "Select to remove card from starting deck.";

        ArrayList<Card> toBeRemovedCards1 = gameServer.askForMultipleCards(p1Cards, message, 1);
        changeStartingCards(toBeRemovedCards1, 1);

        if (!isPlayer2CPU) {
            ArrayList<Card> toBeRemovedCards2 = gameServer.askForMultipleCards(p2Cards, message, 2);
            changeStartingCards(toBeRemovedCards2, 2);
        }
    }

    private void changeStartingCards(ArrayList<Card> cards, int playerIndex) {
        PlayerFields player = (playerIndex == 1) ? gameState.getPlayer1() : gameState.getPlayer2();
        Random random = new Random();

        for (int i = 0; i < constants.getStartingCardsNumber(); i++) {
            if (cards.contains(player.getDeck().get(i))) {
                cards.remove(player.getDeck().get(i));
                Collections.swap(player.getDeck(), i, 3 + random.nextInt(player.getDeck().size() - 3));
            }
        }
    }

    private void beginTurn() {
        gameState.addManaLimit(constants.getMaxMana());
        gameState.refreshMana();
        gameState.setComboEnabled(false);
        gameState.setSituation(GameSituation.NORMAL);

        performOnTurnBeginEffects();
        drawCards();
        resetAttacks();
        setCardsSituation();
        checkForCardsDeath();
        checkGameOver();
        if (cpuPlayer != null) cpuPlayer.check();
        writeEvent(LogType.GAME_REPORT, "Turn Begins");
    }

    private void resetAttacks() {
        for (Minion minion : getPlayedCards()) {
            if (minion != null) {
                minion.resetAttacks();
            }
        }
        getHero().resetAttacks();
        getHero().getHeroPower().reset();
    }

    public void endTurn() {
        synchronized (this) {
            if (!gameState.isGameOver()) {
                for (Minion minion : gameState.getActivePlayer().getPlayedCards()) {
                    if (minion != null) {
                        gameState.setAffectingModel(minion);
                        minion.getCardEffects().getOnTurnEnd().accept(effectsVisitor);
                    }
                }
                gameState.changeActivePlayer();
                beginTurn();
                gameState.getTimer().reset();
            } else {
                endGame(false);
            }
            updateGraphics();
//            PlayerLogger.writeLog("turn ends");
            writeLog(LogType.GAME_REPORT, "Turn Ended.");
        }
    }

    public List<InfoPassive> initialPassives() {
        List<InfoPassive> passives = InfoPassive.getAllInfoPassives();
        Collections.shuffle(passives);
        return passives.subList(0, 3);
    }

    private void shuffleCards() {
//        PlayerLogger.writeLog("Shuffling Cards");
        writeLog(LogType.GAME_REPORT, "Shuffling Cards...");
        Collections.shuffle(gameState.getPlayer1().getDeck());
        Collections.shuffle(gameState.getPlayer2().getDeck());
    }

    public void checkGameOver() {
        if (gameState.getPlayer1().getHero().getHP() <= 0 ||
                gameState.getPlayer2().getHero().getHP() <= 0) {
            endGame(false);
        }
    }

    private void givePlayersStartingCards() {
        writeLog(LogType.GAME_REPORT, "Giving players starting cards");
        for (int i = 0; i < constants.getStartingCardsNumber() - 1; i++) {
            giveCard(drawCard(true), true);
        }

        for (int i = 0; i < constants.getStartingCardsNumber() - 1; i++) {
            giveCard(drawCard(false), false);
        }
    }

    private void drawCards() {
        for (int i = 0; i < gameState.getActivePlayer().getNumberOfNewCardsEachTurn(); i++) {
            Card card = drawCard(true);
            giveCard(card, true);
        }
    }

    public Card drawCard(boolean activePlayer, int i) {
        PlayerFields playerFields = (activePlayer) ? gameState.getActivePlayer() : gameState.getNotActivePlayer();

        if (playerFields.getDeck().size() > 0) {
            performOnDrawEffects(activePlayer);
            writeLog(LogType.GAME_REPORT, "Draw Card -> " + playerFields.getDeck().get(i).getName());

            return playerFields.getDeck().remove(i);
        } else
            return null;
    }

    public Card drawCard(boolean activePlayer) {
        return drawCard(activePlayer, 0);
    }

    public void giveCard(Card card, boolean activePlayer) {

        PlayerFields playerFields = (activePlayer) ? gameState.getActivePlayer() : gameState.getNotActivePlayer();
        int playerIndex = (activePlayer) ? gameState.getActivePlayerIndex() : gameState.getNotActivePlayerIndex();

        if (card != null) {
            if (playerFields.getHand().size() < constants.getMaxHandCards()) {
                playerFields.getHand().add(card);
            }

            performOnCardReceiveEffect(card, activePlayer);
            card.getCardEffects().setSourcePlayerIndex(playerIndex);
        }
    }

    private void performOnCardReceiveEffect(Card card, boolean activePlayer) {
        gameState.setReceivedCard(card);

        for (GameModel model : getAllModels(activePlayer)) {
            gameState.setAffectingModel(model);
            model.findEffects().getOnCardReceive().accept(effectsVisitor);
        }
    }

    private void performOnTransformEffect(Card card, boolean activePlayer) {
        gameState.setTransformedModel(card);

        for (GameModel model : getAllModels(activePlayer)) {
            model.findEffects().getOnTransform().accept(effectsVisitor);
        }
    }

    public void playCard(Card card, int index) {
        if (!canPlayCard(card).equalsIgnoreCase("Success")) {
            gameServer.sendStringMessage(canPlayCard(card));
            return;
        }

        gameState.setRecentlyPlayedCardIndexOnBoard(index);
        prePlayCard(card);
        placeCard(card, index);
        performBattleCry(card);
        performCombo(card);

        checkGameOver();
        writeEvent(LogType.CARD_PLAYED, card.getName());
    }

    private void prePlayCard(Card card) {
        gameState.setRecentlyPlayedCard(true);
        updateOriginalDecksForPlayedCard(card, card.getSourcePlayerIndex());
        setManaLeft(getManaLeft() - card.getMana());

        gameState.setPlayingModel(card);
        performOnPlayEffects();
        checkMissions();
    }

    private void performBattleCry(Card card) {
        gameState.setAffectingModel(card);
        card.getCardEffects().getBattleCry().accept(effectsVisitor);
    }

    private void performCombo(Card card) {
        if (card.getCardEffects().getCombo().getEffects().size() == 0) return;

        if (gameState.isComboEnabled()) {
            gameState.setAffectingModel(card);
            card.getCardEffects().getCombo().accept(effectsVisitor);
        } else {
            gameState.setComboEnabled(true);
        }
    }

    private void placeCard(Card card, int index) {
        if (card instanceof Minion)
            placeMinion((Minion) card, index);
        else if (card instanceof Weapon)
            placeWeapon((Weapon) card);
        else if (card instanceof Mission)
            placeMission((Mission) card);
        else
            placeSpell(card);
    }

    private void checkMissions() {
        Iterator<Mission> iterator = gameState.getActivePlayer().getActiveMissions().iterator();

        while (iterator.hasNext()) {
            Mission activeMission = iterator.next();

            if (activeMission.isAccomplished()) {
                activeMission.getCardEffects().getMissionReward().accept(effectsVisitor);
                iterator.remove();
            }
        }
    }

    private void performOnDrawEffects(boolean activePlayer) {
        for (GameModel model : getAllModels(activePlayer)) {
            gameState.setAffectingModel(model);
            model.findEffects().getOnDraw().accept(effectsVisitor);
        }
    }

    private void performOnTurnBeginEffects() {

        for (GameModel model : getAllModels(true)) {
            gameState.setAffectingModel(model);
            model.findEffects().getOnTurnBegin().accept(effectsVisitor);
        }
    }

    public void performOnPlayEffects() {
        if (!(gameState.getPlayingModel() instanceof Minion)) return;

        for (GameModel model : getAllModels(true)) {
            gameState.setAffectingModel(model);
            model.findEffects().getOnPlay().accept(effectsVisitor);
        }

        for (GameModel model : getAllModels(false)) {
            gameState.setAffectingModel(model);
            model.findEffects().getOnOpponentPlay().accept(effectsVisitor);
        }

    }

    public void performSummonEffects(Card summoningModel) {
        gameState.setSummoningModel(summoningModel);

        int summoningPlayerIndex = summoningModel.getCardEffects().getSourcePlayer();

        for (GameModel model : getAllModels(summoningPlayerIndex)) {
            gameState.setAffectingModel(model);
            model.findEffects().getOnSummon().accept(effectsVisitor);
        }
        for (GameModel model : getAllModels(3 - summoningPlayerIndex)) {
            gameState.setAffectingModel(model);
            model.findEffects().getOnOpponentSummon().accept(effectsVisitor);
        }
    }

    public void performHealEffects() {

        for (GameModel model : getAllModels(Group.ALL)) {
            gameState.setAffectingModel(model);
            model.findEffects().getOnAnyCharacterHeal().accept(effectsVisitor);
        }

    }

    private void placeMinion(Minion minion, int index) {
        removeFromHand(minion);
        getPlayedCards().set(index, minion);
    }

    private void placeSpell(Card card) {
        removeFromHand(card);
    }

    private void placeMission(Mission mission) {
        gameState.getActivePlayer().getActiveMissions().add(mission);
        removeFromHand(mission);
    }

    private void placeWeapon(Weapon weapon) {
        gameState.getActivePlayer().getHero().setWeapon(weapon);
        removeFromHand(weapon);
    }

    private void removeFromHand(Card card) {
        List<Card> cards = gameState.getActivePlayer().getHand();

        for (int i = 0; i < cards.size(); i++) {
            if (card == cards.get(i)) {
                cards.remove(i);
                break;
            }
        }
    }

    public String canPlayCard(Card card) {
        if (getManaLeft() < card.getMana())
            return "Not enough mana";
        else if (isBoardFull() && card instanceof Minion)
            return "Board is full";
        else
            return "Success";
    }

    private boolean isBoardFull() {
        for (int i = 0; i < constants.getMaxPlayedCards(); i++) {
            if (getPlayedCards().get(i) == null)
                return false;
        }
        return true;
    }


    public void playerEntry(CardInfo info) {
        writeLog(LogType.REPORT, "Input: " + info.getFieldType() + ", Index: " + info.getIndex());
        gameState.setRecentlyPlayedCard(false);

        if (info.getFieldType() == GameFieldType.other) {
            gameState.setSituation(GameSituation.NORMAL);
            return;
        }

        switch (gameState.getSituation()) {
            case NORMAL:
                translator.normalPlay(info);
                break;
            case PLAYING:
                translator.placeCard(info);
                break;
            case ATTACKING:
                translator.attack(info);
                break;
            case SELECTING:
                translator.select(info);
        }


        checkForCardsDeath();
        checkGameOver();
        setCardsSituation();
        updateGraphics();
    }

    public void checkForCardsDeath() {
        List<Minion> minions = new ArrayList<>(gameState.getActivePlayer().getPlayedCards());
        for (Minion minion : minions) {
            if (minion != null && minion.getHP() <= 0) {
                killMinion(minion, gameState.getActivePlayer().getPlayedCards());
            }
        }
        minions = new ArrayList<>(gameState.getNotActivePlayer().getPlayedCards());
        for (Minion minion : minions) {
            if (minion != null && minion.getHP() <= 0) {
                killMinion(minion, gameState.getNotActivePlayer().getPlayedCards());
            }
        }
        Heroes hero = gameState.getActivePlayer().getHero();
        if (hero.getWeapon() != null && hero.getWeapon().getDurability() <= 0) {
            killWeapon(hero.getWeapon(), hero);
        }
        hero = gameState.getNotActivePlayer().getHero();
        if (hero.getWeapon() != null && hero.getWeapon().getDurability() <= 0) {
            killWeapon(hero.getWeapon(), hero);
        }
    }

    private void killWeapon(Weapon weapon, Heroes hero) {
        gameState.setAffectingModel(weapon);
        weapon.getCardEffects().getDeathRattle().accept(effectsVisitor);
        weapon.getCardEffects().getOnExit().accept(effectsVisitor);
        hero.setWeapon(null);
    }

    public void killMinion(Minion minion, List<Minion> minions) {
        gameState.getPlayer1().getPassive().getEffects().getOnAnyMinionDeath().accept(effectsVisitor);
        gameState.getDeadMinions().add(minion.cloned());

        for (int i = 0; i < minions.size(); i++) {
            Minion targetMinion = minions.get(i);
            if (minion == targetMinion) {
                gameState.setAffectingModel(minion);
                minion.getCardEffects().getOnExit().accept(effectsVisitor);
                minion.getCardEffects().getDeathRattle().accept(effectsVisitor);
                minions.set(i, null);
            }
        }
    }

    public GameModel playerChooseCard(List<GameModel> cards) {
        synchronized (this) {
            gameState.setSelectableModels(cards);

            gameState.setSituation(GameSituation.SELECTING);
            setCardsSituation();
            updateGraphics();
            if (cards.size() > 0) {
                monitor.doWait();
                writeLog(LogType.CARD_SELECTED, "Model Selected -> " + gameState.getSelectedModel().getName());
                return gameState.getSelectedModel();
            } else
                return null;
        }
    }

    public Card playerDiscover(List<Card> cards) {
        return gameServer.discover(cards);
    }

    private void updateGraphics() {
//        gameServer.updateGraphics();
        gameServer.updateClientsState();
    }

    private void setCardsSituation() {
        situationHandler.handleCardsSituation();
    }

    public boolean hasTaunt(List<Minion> minions) {
        for (Minion minion : minions) {
            if (minion != null &&
                    minion.getAbilities().contains(MinionAbility.TAUNT) && !minion.getAbilities().contains(MinionAbility.STEALTH))
                return true;
        }
        return false;
    }

    public void powerRequest(HeroPower power) {
        if (!power.isPassive()) {
            if (power.getMana() <= getManaLeft()) {
                if (power.getActionsLeft() > 0) {
                    writeEvent(LogType.HERO_POWER_PLAYED, power.getName());
                    power.getEffects().getBattleCry().accept(effectsVisitor);
                    power.setActionsLeft(power.getActionsLeft() - 1);
                    setManaLeft(getManaLeft() - power.getMana());
                } else {
                    gameServer.sendStringMessage("No more actions left.");
                }
            } else
                gameServer.sendStringMessage("Not enough mana");
        }
    }

    public boolean canAttack(Attacker attacker) {
        if (attacker.getRemainedAttacks() > 0) return true;

        return attacker instanceof Minion && (
                ((Minion) attacker).getAbilities().contains(MinionAbility.RUSH) ||
                        ((Minion) attacker).getAbilities().contains(MinionAbility.CHARGE));
    }

    public void attackRequest(GameModel model) {
        if (((model instanceof Minion) && ((Minion) model).getSituation() == CardSituation.VULNERABLE) ||
                ((model instanceof Heroes) && ((Heroes) model).getSituation() == CardSituation.VULNERABLE))
            performAttack(gameState.getPlayingModel(), model);
        else
            handleCantAttackError(model);
    }

    private void handleCantAttackError(GameModel defender) {
        if (defender instanceof Minion)
            if (((Minion) defender).getAbilities().contains(MinionAbility.STEALTH))
                gameServer.sendStringMessage("Minion has Stealth!!!");
            else
                gameServer.sendStringMessage("There is a Taunt on the way!!!");
        else if (gameState.getPlayingModel() instanceof Minion && ((Minion) gameState.getPlayingModel()).getRemainedAttacks() == 0)
            gameServer.sendStringMessage("Can't Attack with RUSH!!!");
        else if (defender instanceof Heroes) {
            gameServer.sendStringMessage("There is a Taunt on the way!!!");
        }
    }

    public void performAttack(GameModel attacker, GameModel defender) {
        if (attacker == null || defender == null) {
            System.out.println("One model is null so attack cannot happen.");
            return;
        }
        if (attacker instanceof Attacker && defender instanceof Defender) {
            Attacker attackingModel = (Attacker) attacker;
            Defender defendingModel = (Defender) defender;

            attackingModel.doAttack(defendingModel, effectsVisitor);
            defendingModel.doDefend(attackingModel, effectsVisitor);

            handleOverKill(defendingModel);
            handleLifeSteal(attacker);

            writeEvent(LogType.ATTACK, attacker.getName() + " Attacked -> " + defender.getName());
        } else
            System.out.println("Instances are wrong...");
    }

    private void handleOverKill(Defender defender) {
        if (defender instanceof Minion) {
            Minion minion = (Minion) defender;
            minion.getCardEffects().getOverKill().accept(effectsVisitor);
        }
    }

    private void handleLifeSteal(GameModel attacker) {
        if (attacker instanceof Minion && ((Minion) attacker).getAbilities().contains(MinionAbility.LIFE_STEAL)) {
            Minion minion = (Minion) attacker;
            Heroes hero = (minion.getCardEffects().getSourcePlayer() == 1) ? gameState.getPlayer1().getHero() : gameState.getPlayer2().getHero();

            int attack = minion.getAttack();
            int originalLife = Heroes.getHero(hero.getName()).getHP();

            hero.setHP(Math.min(originalLife, hero.getHP() + attack));
        }
    }

    public void burnCard(Card card) {
//        PlayerLogger.writeLog("Card Burnt", "from deck card : " + card.getName());
        writeLog(LogType.GAME_REPORT, "Card Burnt -> " + card.getName());
    }

    public void endGame(boolean interrupted) {
        if (!interrupted) {
            int winner = (gameState.getPlayer1().getHero().getHP() <= 0) ? 2 : 1;
            makePlayerWinner(winner);
        }
        finishTasks();
    }

    private void makePlayerWinner(int winner) {
        gameState.setGameOver(true);
        gameState.setWinner(winner);
        updateOriginalDecksForGameEnd(winner);
    }

    private void finishTasks() {
        gameState.getTimer().stopCounting();
        if (cpuPlayer != null) cpuPlayer.interrupt();

        updateGraphics();
        gameServer.gameEnded();
    }

    public void playerLeftMatch(int playerIndex) {

        int winner = 3 - playerIndex;
        makePlayerWinner(winner);
        finishTasks();
    }

    private void updateOriginalDecksForPlayedCard(Card playedCard, int playerIndex) {
        if (isDeckOriginal(playerIndex)) {
            for (Card card : gameState.getPlayer(playerIndex).getMainDeck().getCards()) {
                if (card.getName().equals(playedCard.getName()))
                    card.setUsedTimes(card.getUsedTimes() + 1);
            }
        }
    }

    private void updateOriginalDecksForGameEnd(int winnerIndex) {
//        Deck winner = gameState.getPlayer(winnerIndex).getPlayer().getActiveDeck();
//        Deck loser = gameState.getPlayer(3 - winnerIndex).getPlayer().getActiveDeck();
//
//        if (isDeckOriginal(winnerIndex)) {
//            winner.setPlayedGames(winner.getPlayedGames() + 1);
//            winner.setWonGames(winner.getWonGames() + 1);
//        }
//        if (isDeckOriginal(3 - winnerIndex)) {
//            loser.setPlayedGames(loser.getPlayedGames() + 1);
//            loser.setWonGames(loser.getWonGames() + 1);
//        }
        Player winner = (winnerIndex == 1) ? gameState.getPlayer1().getPlayer() : gameState.getPlayer2().getPlayer();
        Player loser = (winnerIndex == 1) ? gameState.getPlayer2().getPlayer() : gameState.getPlayer1().getPlayer();

        winner.winGame(isDeckOriginal(winnerIndex));
        loser.loseGame(isDeckOriginal(3 - winnerIndex));

        Administrator.updateDataModels(gameState.getPlayer1().getPlayer());
        Administrator.updateDataModels(gameState.getPlayer2().getPlayer());

    }

    private boolean isDeckOriginal(int playerIndex) {
        String deckName1 = gameState.getPlayer(playerIndex).getMainDeck().getName();
        String deckName2 = gameState.getPlayer(playerIndex).getPlayer().getActiveDeck().getName();

        return deckName1.equalsIgnoreCase(deckName2);
    }

    private void writeEvent(LogType type, String description) {
        gameState.addEvent(type + ": " + description + "\n");
        writeLog(type, description);
    }

    public Card useRecentlyPlayedCard() {
        if (gameState.isRecentlyPlayedCard()) {
            gameState.setRecentlyPlayedCard(false);
            return (Card) gameState.getPlayingModel();
        }
        return null;
    }

    public void changeMinion(ArrayList<GameModel> firstMinions, Minion finalMinion) {
        Minion minion;
        for (int i = 0; i < constants.getMaxPlayedCards(); i++) {
            minion = gameState.getActivePlayer().getPlayedCards().get(i);

            if (containsObject(firstMinions, minion)) {
                minion.getCardEffects().getOnExit().accept(effectsVisitor);

                Minion target = finalMinion.cloned();
                gameState.getActivePlayer().getPlayedCards().set(i, target);
                performOnTransformEffect(target, true);
            }

            minion = gameState.getNotActivePlayer().getPlayedCards().get(i);
            if (containsObject(firstMinions, minion)) {
                minion.getCardEffects().getOnExit().accept(effectsVisitor);

                Minion target = finalMinion.cloned();
                gameState.getNotActivePlayer().getPlayedCards().set(i, target);
                performOnTransformEffect(target, false);
            }
        }
        setEffectsIndex();
    }

    public void changeHero(Heroes firstHero, Heroes finalHero) {
        if (gameState.getActivePlayer().getHero() == firstHero) {
            gameState.getActivePlayer().setHero(finalHero);
        } else if (gameState.getNotActivePlayer().getHero() == firstHero) {
            gameState.getNotActivePlayer().setHero(finalHero);
        }
        setEffectsIndex();
    }

    public boolean containsObject(List<?> models, GameModel model) {
        if (model == null) return false;

        for (Object o : models) {
            if (o == model)
                return true;
        }
        return false;
    }

    private ArrayList<GameModel> getAllModels(Group group) {
        ArrayList<GameModel> models = new ArrayList<>();

        switch (group) {
            case PLAYER1_INCLUDING_DECK:
                models.addAll(gameState.getPlayer1().getDeck());
                models.addAll(gameState.getPlayer1().getHand());
            case PLAYER1:
                models.add(gameState.getPlayer1().getPassive());
                models.addAll(gameState.getPlayer1().getPlayedCards());
                models.addAll(gameState.getPlayer1().getActiveMissions());
                models.add(gameState.getPlayer1().getHero());
                models.add(gameState.getPlayer1().getHero().getHeroPower());
                break;
            case PLAYER2_INCLUDING_DECK:
                models.addAll(gameState.getPlayer2().getDeck());
                models.addAll(gameState.getPlayer2().getHand());
            case PLAYER2:
                models.add(gameState.getPlayer2().getPassive());
                models.addAll(gameState.getPlayer2().getPlayedCards());
                models.addAll(gameState.getPlayer2().getActiveMissions());
                models.add(gameState.getPlayer2().getHero());
                models.add(gameState.getPlayer2().getHero().getHeroPower());
                break;
            case ALL_INCLUDING_DECK:
                models.addAll(gameState.getPlayer1().getDeck());
                models.addAll(gameState.getPlayer1().getHand());

                models.addAll(gameState.getPlayer2().getDeck());
                models.addAll(gameState.getPlayer2().getHand());
            case ALL:
                models.add(gameState.getPlayer1().getPassive());
                models.addAll(gameState.getPlayer1().getPlayedCards());
                models.add(gameState.getPlayer1().getHero().getHeroPower());
                models.add(gameState.getPlayer1().getHero());
                models.addAll(gameState.getPlayer1().getActiveMissions());

                models.add(gameState.getPlayer2().getPassive());
                models.addAll(gameState.getPlayer2().getPlayedCards());
                models.add(gameState.getPlayer2().getHero().getHeroPower());
                models.add(gameState.getPlayer2().getHero());
                models.addAll(gameState.getPlayer2().getActiveMissions());
                break;
        }

        while (models.contains(null))
            models.remove(null);

        return models;
    }

    private ArrayList<GameModel> getAllModels(boolean activePlayer) {
        boolean player1 = (activePlayer && gameState.getActivePlayerIndex() == 1) || (!activePlayer && gameState.getActivePlayerIndex() != 1);

        return (player1) ? getAllModels(Group.PLAYER1) : getAllModels(Group.PLAYER2);
    }

    private ArrayList<GameModel> getAllModels(int playerIndex) {
        return (playerIndex == 1) ? getAllModels(Group.PLAYER1) : getAllModels(Group.PLAYER2);
    }

    private enum Group {
        ALL,
        PLAYER1,
        PLAYER2,

        ALL_INCLUDING_DECK,
        PLAYER1_INCLUDING_DECK,
        PLAYER2_INCLUDING_DECK,
    }

    ////////////////////////////////  Active Player Fast call
    private List<Card> getHand() {
        return gameState.getActivePlayer().getHand();
    }

    private List<Card> getDeck() {
        return gameState.getActivePlayer().getDeck();
    }

    private List<Minion> getPlayedCards() {
        return gameState.getActivePlayer().getPlayedCards();
    }

    private int getManaLimit() {
        return gameState.getActivePlayer().getEachTurnMana();
    }

    private int getManaLeft() {
        return gameState.getActivePlayer().getThisTurnMana();
    }

    private Heroes getHero() {
        return gameState.getActivePlayer().getHero();
    }

    private void setHand(List<Card> hand) {
        gameState.getActivePlayer().setHand(hand);
    }

    private void setDeck(List<Card> deck) {
        gameState.getActivePlayer().setDeck(deck);
    }

    private void setPlayedCards(List<Minion> playedCards) {
        gameState.getActivePlayer().setPlayedCards(playedCards);
    }

    private void setManaLimit(int manaLimit) {
        gameState.getActivePlayer().setEachTurnMana(manaLimit);
    }

    private void setManaLeft(int manaLeft) {
        gameState.getActivePlayer().setThisTurnMana(manaLeft);
    }

    public Monitor getMonitor() {
        return monitor;
    }

    ////////////////////////////////

}
