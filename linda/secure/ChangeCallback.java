package linda.secure;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChangeCallback extends Remote {
    public void call(int i) throws RemoteException;
}
