package linda.test;

import linda.Linda;
import linda.Tuple;
import linda.shm.CentralizedLinda;

import java.util.Collection;
import java.util.List;
// Partie 1 : Test des READ, Write , Take, TakeAll et ReadAll

public class TestsLinda1 {

    public static void main(String[] args) {
        Tuple tupleA = new Tuple(1, "1");
        Tuple tupleB = new Tuple(2, "1");
        Tuple tupleC = new Tuple(1, "2",10);
        Tuple motifA = new Tuple(1, String.class);
        Tuple motifB = new Tuple(2, String.class);
        Tuple motifAB = new Tuple(Integer.class, "1");
        Tuple motifC = new Tuple(1, String.class, 10);
        Tuple motifABC = new Tuple(Integer.class, String.class);


        var test1 = new Test() { //test des Read et Write
            Linda linda = new CentralizedLinda();

            @Override
            public void test() {
                orderedRun(() -> {
                    Tuple t = linda.read(motifA);
                    System.out.println("Tuple A lu : " + t);
                    linda.debug("read motif A");

                });
                orderedRun(() -> {
                    linda.write(tupleC);
                    linda.debug("write du Tuple C");
                });
                orderedRun(() -> {
                    linda.write(tupleA);
                    linda.debug("write de Tuple A");
                });
                orderedRun(() -> {
                    Tuple t = linda.read(motifC);
                    System.out.println("motif C lu : " + t);
                    linda.debug("read motif C");

                });
            }
        };
    // test1.run();

    // test du write, take et takeAll
     var test2= new Test(){
         Linda linda = new CentralizedLinda();

         @Override
         public void test() {
             orderedRun(() -> {
                 linda.write(tupleA);
                 linda.debug("write de A");

             });
             orderedRun(() -> {
                 linda.write(tupleB);
                 linda.debug("write de  B");
             });
             orderedRun(() -> {
                 linda.write(tupleC);
                 linda.debug("write de  C");
             });
             orderedRun(() -> {
                 Tuple t =linda.take(motifC);
                 linda.debug("take de motif C " + t);
             });
             orderedRun(() -> {
                 Collection<Tuple> t = linda.takeAll(motifAB);
                 System.out.println("TakeAll motif AB  : " + t);
                 linda.debug("takeall");
             });
         }
     };
    // test2.run();
        // Write et TryRead et Trytake

        var test3 = new Test(){
            Linda linda = new CentralizedLinda();
            @Override
            public void test() {

                orderedRun(()-> {
                    linda.write(tupleA);
                    linda.debug(" write de A");
                });

                orderedRun(()-> {
                    linda.write(tupleB);
                    linda.debug(" write de B");
                });

                 orderedRun(()-> {

                    System.out.println("tryread de  motif A " + linda.tryRead(motifA));
                    linda.debug(" tryread de motif A");
                });

                orderedRun(()-> {
                    System.out.println("  tryTake de motif  B " + linda.tryTake(motifB));
                    linda.debug(" tryTake de motif B");
                });
            }
        };
        //test3.run();

       // System.out.println("------------------------------------------------------");
        // Write takeAll et ReadAll

        var test4 = new Test(){
            Linda linda = new CentralizedLinda();
            @Override
            public void test(){
                orderedRun(()-> {
                    linda.debug("avant write");
                    linda.write(tupleA);
                    linda.write(tupleB);
                    linda.write(tupleC);
                    linda.write(tupleC);
                    linda.debug( "après write");
                });

                orderedRun(() -> {

                    Collection<Tuple> t=linda.takeAll(motifAB);
                    System.out.println("takeall de AB " +t.toString());
                    linda.debug(" après takeAll AB");
                });

                orderedRun(()-> {

                    Collection<Tuple> t=linda.readAll(motifC);
                    System.out.println("readAll de C " + t.toString());
                    linda.debug(" après readAll C");

                });

            }
        };
        test4.run();
    }

}









