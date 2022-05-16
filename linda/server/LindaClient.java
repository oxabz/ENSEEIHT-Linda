package linda.server;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {

    private LindaRemote server;

    private static class RemoteCallbackProxy extends UnicastRemoteObject implements RemoteCallback{
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
     *  @param serverURI the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
     */
    public LindaClient(String serverURI) {

        try {
            server = (LindaRemote) Naming.lookup(serverURI);
        } catch (Exception exception){
            System.err.println(exception);
        }
    }

    @Override
    public void write(Tuple t) {
        try{
            server.write(t);
        }catch (RemoteException exception){
            exception.printStackTrace();
        }
    }

    @Override
    public Tuple take(Tuple template) {
        try{
            return server.take(template);
        }catch (RemoteException exception){
            return null;
        }
    }

    @Override
    public Tuple read(Tuple template) {
        try{
            return server.read(template);
        }catch (RemoteException exception){
            return null;
        }
    }

    @Override
    public Tuple tryTake(Tuple template) {
        try{
            return server.tryTake(template);
        }catch (RemoteException exception){
            return null;
        }
    }

    @Override
    public Tuple tryRead(Tuple template) {
        try{
            return server.tryRead(template);
        }catch (RemoteException exception){
            return null;
        }
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        try{
            return server.takeAll(template);
        }catch (RemoteException exception){
            return null;
        }
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        try{
            return server.readAll(template);
        }catch (RemoteException exception){
            return null;
        }
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        try{
            var rcb = new RemoteCallbackProxy(callback);

            server.eventRegister(mode, timing, template, rcb);
        }catch (RemoteException exception){
            // Handle server change
        }
    }

    @Override
    public void debug(String prefix) {
        try{
           System.out.println(server.debug(prefix));
        }catch (RemoteException exception){

        }
    }

    // TO BE COMPLETED

}