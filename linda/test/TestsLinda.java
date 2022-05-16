package linda.test;

import linda.Linda;
import linda.Tuple;
import linda.shm.CentralizedLinda;

public class TestsLinda {
    public static void main(String[] args) {
        Tuple tupleA = new Tuple(1, "1");
        Tuple tupleB = new Tuple(2, "1");
        Tuple tupleC = new Tuple(1, "2");
        Tuple motifABC = new Tuple(Integer.class, String.class);
        Tuple motifA = new Tuple(1, "1");
        Tuple motifAB = new Tuple(Integer.class, "1");
        Tuple motifAC = new Tuple(1, String.class);

        var cw1 = new Test(){
            Linda linda = new CentralizedLinda();
            @Override
            public void test() {
                orderedRun(() -> {
                    linda.eventRegister(Linda.eventMode.READ, Linda.eventTiming.FUTURE, motifA, t -> {
                        System.out.println("Ok cw1 : " + t);
                    });
                });

                orderedRun(() -> {
                    linda.write(tupleA);
                });

                orderedRun(()->{
                    if (linda.readAll(motifABC).size() == 1){
                        System.out.println("Ok cw1");
                    } else {
                        System.out.println("Err cw1 : missing tuple");
                    }
                });
            }
        };
        //cw1.run();

        var cw2 = new Test(){
            Linda linda = new CentralizedLinda();
            @Override
            public void test() {
                orderedRun(() -> {
                    linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.FUTURE, motifA, t -> {
                        System.out.println("Ok cw2 : " + t);
                    });
                });

                orderedRun(() -> {
                    linda.write(tupleA);
                });

                orderedRun(()->{
                    if (linda.readAll(motifABC).size() == 0){
                        System.out.println("Ok cw2");
                    } else {
                        System.out.println("Err cw2 : missing tuple");
                    }
                });
            }
        };
        //cw2.run();

        var cw3 = new Test(){
            Linda linda = new CentralizedLinda();
            @Override
            public void test() {
                orderedRun(() -> {
                    linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.FUTURE, motifA, t -> {
                        System.out.println("Err cw3 : " + t);
                    });
                });

                orderedRun(() -> {
                    linda.write(tupleB);
                });

                orderedRun(()->{
                    if (linda.readAll(motifABC).size() == 1){
                        System.out.println("Ok cw3");
                    } else {
                        System.out.println("Err cw3 : missing tuple");
                    }
                });
            }
        };
        //cw3.run();

        var cw4 = new Test(){
            Linda linda = new CentralizedLinda();
            @Override
            public void test() {
                orderedRun(() -> {
                    linda.eventRegister(Linda.eventMode.READ, Linda.eventTiming.FUTURE, motifA, t -> {
                        System.out.println("Err cw4 : " + t);
                    });
                });

                orderedRun(() -> {
                    linda.write(tupleB);
                });

                orderedRun(()->{
                    if (linda.readAll(motifABC).stream().count() == 1){
                        System.out.println("Ok cw4 ");
                    }else {
                        System.out.println("Err cw4 : missing tuple");
                    }
                });
            }
        };
        //cw4.run();

        var cw5 = new Test(){
            Linda linda = new CentralizedLinda();
            @Override
            public void test() {
                orderedRun(() -> {
                    linda.write(tupleA);
                });

                orderedRun(() -> {
                    linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.FUTURE, motifA, t -> {
                        System.out.println("Err cw5 : " + t);
                    });
                });

                orderedRun(()->{
                    if (linda.readAll(motifABC).size() == 1){
                        System.out.println("Ok cw5");
                    } else {
                        System.out.println("Err cw5 : missing tuple");
                    }
                });
            }
        };
        //cw5.run();

        var cw6 = new Test(){
            Linda linda = new CentralizedLinda();
            @Override
            public void test() {
                orderedRun(() -> {
                    linda.write(tupleA);
                });

                orderedRun(() -> {
                    linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE, motifA, t -> {
                        System.out.println("Ok cw6 : " + t);
                    });
                });

                orderedRun(()->{
                    if (linda.readAll(motifABC).size() == 0){
                        System.out.println("Ok cw6");
                    } else {
                        System.out.println("Err cw6 : missing tuple");
                    }
                });
            }
        };
        //cw6.run();

        var cw7 = new Test(){
            Linda linda = new CentralizedLinda();
            @Override
            public void test() {
                orderedRun(() -> {
                    linda.write(tupleA);
                });

                orderedRun(() -> {
                    linda.eventRegister(Linda.eventMode.READ, Linda.eventTiming.FUTURE, motifA, t -> {
                        System.out.println("Err cw7 : " + t);
                    });
                });

                orderedRun(()->{
                    if (linda.readAll(motifABC).size() == 1){
                        System.out.println("Ok cw7");
                    } else {
                        System.out.println("Err cw7 : missing tuple");
                    }
                });
            }
        };
        //cw7.run();

        var cw8 = new Test(){
            Linda linda = new CentralizedLinda();
            @Override
            public void test() {
                orderedRun(() -> {
                    linda.write(tupleA);
                });

                orderedRun(() -> {
                    linda.eventRegister(Linda.eventMode.READ, Linda.eventTiming.IMMEDIATE, motifA, t -> {
                        System.out.println("Ok cw8 : " + t);
                    });
                });

                orderedRun(()->{
                    if (linda.readAll(motifABC).size() == 1){
                        System.out.println("Ok cw8");
                    } else {
                        System.out.println("Err cw8 : missing tuple");
                    }
                });
            }
        };
        //cw8.run();

        var cw9 = new Test(){
            @Override
            public void test() {
                Linda linda = new CentralizedLinda();
                asyncRun(()->{
                    linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.FUTURE, motifA, t -> {
                        System.out.println("Ok cw9 : " + t);
                    });
                });

                asyncRun(()-> {
                    linda.write(tupleA);
                });
            }
        };

        var cw10 = new Test(){
            @Override
            public void test() {
                Linda linda = new CentralizedLinda();


                asyncRun(()-> {
                    linda.write(tupleA);
                });


                asyncRun(()->{
                    linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE, motifA, t -> {
                        System.out.println("Ok cw10 : " + t);
                    });
                });
            }
        };

        for (int i = 0; i <100; i++) {
            //cw9.run();
            //cw10.run();
        }


        var Tw1 = new Test(){
            @Override
            public void test() {
                Linda linda = new CentralizedLinda();

                orderedRun(()->{
                    System.out.println("Tw1 : Ok "+linda.takeAll(motifABC));
                });

                orderedRun(()->{
                    linda.write(tupleA);
                });

                orderedRun(()->{
                    System.out.println("Tw1 : Ok "+linda.takeAll(motifABC));
                });
            }
        };
        //Tw1.run();

        var Tw2 = new Test(){
            @Override
            public void test() {
                Linda linda = new CentralizedLinda();

                orderedRun(()->{
                    System.out.println("Tw2 : Ok "+linda.takeAll(motifA));
                });

                orderedRun(()->{
                    linda.write(tupleB);
                });

                orderedRun(()->{
                    System.out.println("Tw2 : Ok "+linda.takeAll(motifA));
                });
            }
        };
        //Tw2.run();

        var rrrw = new Test(){
            @Override
            public void test() {
                Linda linda = new CentralizedLinda();

                asyncRun(()->{
                    System.out.println("rrrw : Ok "+linda.read(motifA));
                });
                asyncRun(()->{
                    System.out.println("rrrw : Ok "+linda.read(motifA));
                });
                asyncRun(()->{
                    System.out.println("rrrw : Ok "+linda.read(motifA));
                });

                asyncRun(()->{
                    linda.write(tupleA);
                });
            }
        };
        rrrw.run();

    }



}

