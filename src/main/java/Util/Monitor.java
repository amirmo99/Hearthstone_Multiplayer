package Util;

public class Monitor {
    private final Object object;
    private boolean mustWait = false;

    public Monitor() {
        object = new Object();
    }

    public void doWait(Object object) {
        mustWait = true;

        synchronized (object) {
            try {
                while (mustWait)
                    object.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void doNotify(Object object) {
        synchronized (object) {
            mustWait = false;
            object.notifyAll();
        }
    }

    public void doNotify() {
        doNotify(this.object);
    }

    public void doWait() {
        doWait(this.object);
    }
}
