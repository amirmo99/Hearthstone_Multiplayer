package UtilSwing;

import UI.GUI.MainPanels.MainGame.GameBoardPanel;
import configs.GameGraphicConstants;
import models.Card;
import models.cards.Minion;

public class AnimatorThread extends Thread {

    private AnimatorPanel animator;
    private final GameBoardPanel panel;
    private int X1, Y1, X2, Y2;
    private int centerX, centerY;
    private Card card;

    private final int FPS = 60;

    private final double period1 = 1.5;
    private final int delayTime = 1000;
    private final double period2 = 1;

    private GameGraphicConstants constants = new GameGraphicConstants();

    public AnimatorThread(GameBoardPanel panel) {
        super();
        this.panel = panel;
        this.animator = new AnimatorPanel();

        centerX = (int) (constants.getGameWidth() / 2 - constants.getCardWidth() / 2);
        centerY = (int) (constants.getGameHeight() / 2 - constants.getCardHeight() / 2);
    }

    @Override
    public void run() {
        moveCard(X1, Y1, centerX, centerY, period1);

        delay(delayTime);

//        if (card instanceof Minion)
//            moveCard(centerX, centerY, X2, Y2, period2);

        removeAnimator();
        panel.update(true);
    }

    private void moveCard(int X1, int Y1, int X2, int Y2, double period) {
        int n = (int) (FPS * period);
        double stepX = (double) (X2 - X1) / n;
        double stepY = (double) (Y2 - Y1) / n;

        for (int i = 0; i < n; i++) {
            int x = (int) (X1 + (i * stepX));
            int y = (int) (Y1 + (i * stepY));

            animator.draw(x, y);

            delay(1000 / FPS);
        }
    }



    public void animateMovement(Card card, int X1, int Y1, int X2, int Y2) {
        setPoints(X1, Y1, X2, Y2);
        createAnimator(card);

        start();
    }

    private void createAnimator(Card card) {
        this.card = card;
        animator.setCard(card);
        panel.add(animator);
        animator.setBounds(0, 0, (int) constants.getGameWidth(), (int) constants.getGameHeight());
    }
    private void removeAnimator() {
        panel.remove(animator);
        animator.removeAll();
    }

    private void setPoints(int X1, int Y1, int X2, int Y2) {
        this.X1 = X1;
        this.X2 = X2;
        this.Y1 = Y1;
        this.Y2 = Y2;
    }

    private void delay(int time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            System.out.println("Sleep Interrupted");
            e.printStackTrace();
        }
    }

}
