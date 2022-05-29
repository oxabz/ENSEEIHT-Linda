package linda.test;

import linda.Tuple;
import linda.Linda;
import linda.shm.CentralizedLinda;

public class TestsLinda3 {

    public static void main(String[] args) {

        Tuple tupleA = new Tuple(1, "1");
        Tuple tupleB = new Tuple(2, "1");
        Tuple tupleC = new Tuple(1, "2", 10);
        Tuple motifA = new Tuple(1, String.class);
        Tuple motifB = new Tuple(2, String.class);
        Tuple motifAB = new Tuple(Integer.class, "1");
        Tuple motifC = new Tuple(1, String.class, 10);
        Tuple motifABC = new Tuple(Integer.class, String.class);

        var test = new Test(){
            Linda linda = new CentralizedLinda();

            @Override
            public void test() {
                orderedRun(()->{
                    linda.write(tupleA);
                    linda.debug("write A");
                    linda.write(tupleB);
                    linda.debug("write B");
                    linda.write(tupleC);
                    linda.debug("write C");

                });

                orderedRun(()->{
                   Tuple tt = linda.read(motifAB);
                    System.out.println("read motif AB" + tt);
                    linda.debug("après read AB");

                    linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE,motifAB,t -> {
                        System.out.println("appel callback Take immediate motifB " + t);
                    });

                    tt =linda.take(motifAB);
                    System.out.println(" le take AB" + tt);
                    linda.debug("après take motif AB");


                    linda.debug("read, take et eventregister motif AB");

                });
            }
        };
        test.run();
    }
}

// le tuple C, et le seul à rester dans l'espace de tuples