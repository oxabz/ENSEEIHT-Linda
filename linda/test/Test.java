package linda.test;

public abstract class Test {
    private static final int WAIT_INC = 40;
    private int wait;

    public interface Instruction{
        void run();
    }

    public void run() {
        test();
    }

    public void orderedRun(Instruction i ){
        var wait = this.wait;
        this.wait += WAIT_INC;
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i.run();
            }
        }.start();
    }

    public void asyncRun(Instruction i){
        new Thread(){
            @Override
            public void run() {
                i.run();
            }
        }.start();
    }

    public abstract void test();


}
