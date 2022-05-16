package linda.secure;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.server.LindaRemote;
import linda.server.RemoteCallback;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaSafeClient implements Linda {

    private LindaRemote server;
    private String[] serversURIs;
    private int serversIndex;

    private static class RemoteCallbackProxy extends UnicastRemoteObject implements RemoteCallback {
        Callback inner;

        public RemoteCallbackProxy(Callback inner) throws RemoteException {
            this.inner = inner;
        }

        @Override
        public void call(Tuple tuple) {
            inner.call(tuple);
        }
    }

    /** Initializes the Linda implementation.
     *  @param serversURIs the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
     */
    public LindaSafeClient(String[] serversURIs) {
        this.serversURIs = serversURIs;
        connectNext();
    }

    private void connectNext() {
        try {
            if(serversIndex>=serversURIs.length) return;
            var serverURI = serversURIs[serversIndex];
            serversIndex++;
            server = (LindaRemote) Naming.lookup(serverURI);
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            connectNext();
        }
        if(server==null){
            System.err.println("Couldnt connect to a valid server");
        }
    }

    @Override
    public void write(Tuple t) {
        try{
            server.write(t);
        }catch (RemoteException exception){
            connectNext();
            write(t);
        }
    }

    @Override
    public Tuple take(Tuple template) {
        try{
            return server.take(template);
        }catch (RemoteException exception){
            connectNext();
            return take(template);
        }
    }

    @Override
    public Tuple read(Tuple template) {
        try{
            return server.read(template);
        }catch (RemoteException exception){
            connectNext();
            return read(template);
        }
    }

    @Override
    public Tuple tryTake(Tuple template) {
        try{
            return server.tryTake(template);
        }catch (RemoteException exception){
            connectNext();
            return tryTake(template);
        }
    }

    @Override
    public Tuple tryRead(Tuple template) {
        try{
            return server.tryRead(template);
        }catch (RemoteException exception){
            connectNext();
            return tryRead(template);
        }
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        try{
            return server.takeAll(template);
        }catch (RemoteException exception){
            connectNext();
            return takeAll(template);
        }
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        try{
            return server.readAll(template);
        }catch (RemoteException exception){
            connectNext();
            return readAll(template);
        }
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        try{
            var rcb = new RemoteCallbackProxy(callback);

            server.eventRegister(mode, timing, template, rcb);
        }catch (RemoteException exception){
            connectNext();
            eventRegister(mode, timing, template, callback);
        }
    }

    @Override
    public void debug(String prefix) {
        try{
           System.out.println(server.debug(prefix));
        }catch (RemoteException exception){
            debug(prefix);
            connectNext();
        }
    }

    // TO BE COMPLETED

}