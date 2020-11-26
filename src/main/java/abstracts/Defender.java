package abstracts;

public interface Defender {
    int getHP();
    void setHP(int hp);
    void doDefend(Attacker attacker, GameEffectVisitor visitor);
    void takeDamage(int damage, GameEffectVisitor visitor);
}
