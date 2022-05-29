package linda.test;

import linda.Linda;
import linda.Tuple;
import linda.shm.CentralizedLinda;

import java.util.Collection;

// test des callbacks

public class TestsLinda2 {

        // partie 2 : Test des callbacks
    public static void main(String[] args) {

        Tuple tupleA = new Tuple(1, "1");
        Tuple tupleB = new Tuple(2, "1");
        Tuple tupleC = new Tuple(1, "2",10);
        Tuple motifA = new Tuple(1, String.class);
        Tuple motifB = new Tuple(2, String.class);
        Tuple motifAB = new Tuple(Integer.class, "1");
        Tuple motifC = new Tuple(1, String.class, 10);
        Tuple motifABC = new Tuple(Integer.class, String.class);

        var test1 = new Test(){
            Linda linda = new CentralizedLinda();

            public void test() {

                orderedRun(()-> {

                    linda.write(tupleB);
                    linda.debug("write B");

                } );

                orderedRun(()-> {

                    linda.write(tupleC);
                    linda.debug("write C");

                } );

                orderedRun(()-> {

                    linda.eventRegister(Linda.eventMode.READ, Linda.eventTiming.IMMEDIATE,motifA,t -> {
                        System.out.println("callback Read Immediate motifA " + t);
                        linda.debug("appel callback read Immediate motif A");
                    });

                        linda.debug("eventregistry read immediate motif A");

                });

                orderedRun(()-> {

                    linda.eventRegister(Linda.eventMode.READ, Linda.eventTiming.FUTURE,motifB,t -> {
                        System.out.println("callback Read immediate motifB " + t);
                        linda.debug("appel callback read future motif B");
                    });
                    linda.debug("eventregistry read future motif B");
                });

                orderedRun(()-> {

                    linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.FUTURE,motifC,t -> {
                        System.out.println("callback Take future motif C " + t);
                        linda.debug("appel callback take future motif C");
                    });
                    linda.debug(" eventregistry take future motif C");
                });

                orderedRun(()-> {

                    linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE,motifB,t -> {
                        System.out.println("callback Take immediate motifB " + t);
                        linda.debug("appel du  callback take immediate motif B");
                    });
                    linda.debug(" eventregistry take immediate motif B");
                });

                orderedRun(()-> {

                    linda.write(tupleC);
                    linda.debug("write C");

                } );

            }

        };
        test1.run();
    }
}
