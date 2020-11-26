package abstracts;

public interface Attacker {
    int getAttack();
    void setAttack(int attack);

    int getRemainedAttacks();
    void resetAttacks();

    void doAttack(Defender defender, GameEffectVisitor visitor);
}
