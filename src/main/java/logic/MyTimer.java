package logic;

import Util.Monitor;

import java.io.Serializable;

public class MyTimer extends Thread implements Serializable {

    private Runnable staticRunnable;
    private Runnable usualRunnable;
    private int initialCountDown;
    private int count;
    private boolean isAlive;
    private boolean isPaused;

    private Object monitorObject;
    private Monitor monitor;
    private boolean onlyOneTime;

    public MyTimer(int initialCountDown, boolean onlyOneTime) {
        this.initialCountDown = initialCountDown;
        usualRunnable = () -> { };
        staticRunnable = () -> { };

        this.monitorObject = new Object();
        this.monitor = new Monitor();

        this.onlyOneTime = onlyOneTime;
    }

    public MyTimer(int initialCountDown) {
        this(initialCountDown, false);
    }

    private MyTimer() {
    }

    public MyTimer securedTimer() {
        MyTimer timer = new MyTimer();
        timer.setCount(count);
        return timer;
    }

    @Override
    public void run() {
        init();

        while (isAlive) {
            try {
                sleep(1000);

                if (isPaused) monitor.doWait(monitorObject);

                tick();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        this.isAlive = true;
        this.isPaused = false;
        this.count = initialCountDown;
    }


    private void tick() {
        count--;
        doStatic();

        if (count <= 0) {
            doUsual();
            count = initialCountDown;
            if (onlyOneTime)
                stopCounting();
        }
    }

    private void doStatic() {
        try {
            staticRunnable.run();
        } catch (Exception ignore) { }
    }

    private void doUsual() {
        try {
            usualRunnable.run();
        } catch (Exception ignore) { }
    }

    public void stopCounting() {
        this.isAlive = false;
    }

    public void pauseCounting() {
        isPaused = true;
    }

    public void resumeCounting() {
        isPaused = false;
        monitor.doNotify(monitorObject);
    }

    public void reset() {
        count = initialCountDown;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setStaticRunnable(Runnable staticRunnable) {
        this.staticRunnable = staticRunnable;
    }

    public void setUsualRunnable(Runnable usualRunnable) {
        this.usualRunnable = usualRunnable;
    }
}
