package linda.secure;

import linda.AnyTuple;
import linda.Linda;
import linda.Tuple;
import linda.server.LindaRemote;
import linda.server.LindaServer;
import linda.server.RemoteCallback;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class LindaBackupServer extends LindaServer {
    private LindaRemote reference;
    private boolean follow;
    private Timer update;
    private static final int CHANGE_TILL_SAVE = 5;
    private static final long SAVE_INTERVAL = 1000L;
    private int changeTillSave;

    private class ModificationCallback extends UnicastRemoteObject implements ChangeCallback {
        protected ModificationCallback() throws RemoteException {}

        @Override
        public void call(int i) throws RemoteException {
            changeTillSave-=i;
            if (changeTillSave <= 0 ){
                changeTillSave = CHANGE_TILL_SAVE;
                loadRemoteState();
            }

        }
    }

    public LindaBackupServer(LindaRemote referenceLinda) throws RemoteException {
        super(referenceLinda.copy());
        reference = referenceLinda;
        follow = true;

        update = new Timer();
        var task = new TimerTask(){

            @Override
            public void run() {
                loadRemoteState();
            }
        };

        update.schedule(task, 0, SAVE_INTERVAL);
    }

    private void loadRemoteState(){
        if (!follow) {return;}
        try {
            inner = reference.copy();
        } catch (RemoteException e) {
            follow = false;
            update.cancel();
            e.printStackTrace();
        }
    }

    public void listenChangeServer() {

        try {
            reference.changeRegister(new ModificationCallback());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3){
            System.err.println("Usage : prog <port> <path> <source-uri>");
            System.exit(1);
        }

        var it = Arrays.stream(args).iterator();
        var port = Integer.valueOf(it.next());
        LindaServer.savePath = it.next();
        var sourceUri = it.next();

        try {
            LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            var remote = (LindaRemote) Naming.lookup(sourceUri);

            LindaBackupServer backupServer = new LindaBackupServer(remote);

            Registry registry = LocateRegistry.getRegistry(port);
            registry.bind("LindaBackupServer", backupServer);
        } catch (Exception exception){
            System.err.println(exception);
        }
    }



}