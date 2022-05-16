package linda.server;

import linda.Callback;
import linda.Tuple;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteCallback extends Remote {
    void call(Tuple tuple) throws RemoteException;
}
