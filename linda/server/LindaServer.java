package linda.server;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.secure.ChangeCallback;
import linda.shm.CentralizedLinda;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

public class LindaServer extends UnicastRemoteObject implements LindaRemote {
    protected CentralizedLinda inner;
    protected static String savePath;

    private static class CallbackProxy implements Callback{
        private RemoteCallback inner;

        public CallbackProxy(RemoteCallback inner) {
            this.inner = inner;
        }

        @Override
        public void call(Tuple t) {
            try {
                inner.call(t);
            } catch (RemoteException e) {
                e.printStackTrace();

            }
        }
    }

    public LindaServer() throws RemoteException {
        inner = new CentralizedLinda();
        var shutdownListener = new Thread(){
            public void run(){
                inner.save(savePath);
            }
        };
        Timer timer = new Timer("Timer");
        var task = new TimerTask(){
            @Override
            public void run() {
                inner.save(savePath);
            }
        };
        long delay = 10000L;
        timer.schedule(task, 0, delay);
        Runtime.getRuntime().addShutdownHook(shutdownListener);
    }

    public LindaServer(CentralizedLinda linda) throws RemoteException {
        inner = linda;
        if (inner==null){
            inner = new CentralizedLinda();
        }
        var shutdownListener = new Thread(){
            public void run(){
                inner.save(savePath);
            }
        };
        Timer timer = new Timer("Timer");
        var task = new TimerTask(){
            @Override
            public void run() {
                inner.save(savePath);
            }
        };
        long delay = 10000L;
        timer.schedule(task, delay);

        Runtime.getRuntime().addShutdownHook(shutdownListener);
    }

    @Override
    public void write(Tuple t) throws RemoteException {
        inner.write(t);
    }

    @Override
    public Tuple take(Tuple template) throws RemoteException {
        return inner.take(template);
    }

    @Override
    public Tuple read(Tuple template) throws RemoteException {
        return inner.read(template);
    }

    @Override
    public Tuple tryTake(Tuple template) throws RemoteException {
        return inner.tryTake(template);
    }

    @Override
    public Tuple tryRead(Tuple template) throws RemoteException {
        return inner.tryRead(template);
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
        return inner.takeAll(template);
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) throws RemoteException {
        return inner.readAll(template);
    }

    @Override
    public void eventRegister(Linda.eventMode mode, Linda.eventTiming timing, Tuple template, RemoteCallback callback) throws RemoteException {
        inner.eventRegister(mode, timing, template, new CallbackProxy(callback));
    }

    @Override
    public String debug(String prefix) throws RemoteException {
        return inner.debugToString(prefix);
    }

    @Override
    public void changeRegister(ChangeCallback callback){}

    public static void main(String[] args) throws Exception{
        if (args.length != 3){
            System.err.println("Usage : prog <port> <path> <resume>");
            System.exit(1);
        }
        var it = Arrays.stream(args).iterator();
        var port = Integer.valueOf(it.next());
        LindaServer.savePath = it.next();
        var resume = Boolean.valueOf(it.next());


        //  Création du serveur de noms
        try {
            LocateRegistry.createRegistry(port);
        } catch (java.rmi.server.ExportException e) {
            System.out.println("A registry is already running, proceeding...");
        }

        //  Création de l'objet Carnet,
        //  et enregistrement du carnet dans le serveur de nom
        LindaServer server;
        if(resume){
            server = new LindaServer(CentralizedLinda.loadSaved(LindaServer.savePath));
        } else {
            server = new LindaServer();
        }

        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry(port);
        registry.bind("LindaServer", server);

        // Service prêt : attente d'appels
        System.out.println ("Le systeme est pret.");
    }

    public CentralizedLinda copy() throws RemoteException{
        return inner;
    }
}
