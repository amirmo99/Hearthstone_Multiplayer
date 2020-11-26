package abstracts;

import logic.GameEffects.*;
import models.cards.Mission;

public interface GameEffectVisitor {
    void visit(Summon summon);
    void visit(ChangeHpAttack healDamage);
    void visit(AddToHand addToHand);
    void visit(DrawCard drawCard);
    void visit(Discover discover);
    void visit(AddCost addCost);
    void visit(Transform transform);
    void visit(ChangeAbility giveAbility);
    void visit(RelatedEffects groupEffect);
    void visit(AddToDeck addToDeck);
    void visit(RestoreLife restoreLife);
    void visit(Attack attack);

    // Info Passives & Hero Powers:
    void visit(HeroPowerModifier heroPowerModifier);
    void visit(ManaJump manaJump);
    void visit(MultipleDraw multipleDraw);
    void visit(HeroPowerChanger heroPowerChanger);
    void visit(Steal steal);
    void visit(DoubleSpellPowers doubleSpellPowers);
    void visit(DefenseAdder defenseAdder);
    void visit(MissionHandler missionHandler);
    void visit(ReviveDead reviveDead);
}
