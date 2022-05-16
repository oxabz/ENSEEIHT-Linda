package linda.shm;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.utils.Pair;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Serializable, Linda {

    private List<Tuple> tuples;
    private transient List<Tuple> reads = new LinkedList<>();
    private transient List<Tuple> takes = new LinkedList<>();
    private transient List<Pair<Tuple, Callback>> readCallbacks = new LinkedList<>();
    private transient List<Pair<Tuple, Callback>> takeCallbacks = new LinkedList<>();
    private transient ReentrantReadWriteLock everythingLock;

    public CentralizedLinda() {
        tuples = new LinkedList<>();
        takes = new LinkedList<>();
        reads = new LinkedList<>();
        readCallbacks = new LinkedList<>();
        takeCallbacks = new LinkedList<>();
        everythingLock = new ReentrantReadWriteLock();
    }

    public void initTransiant() {
        takes = new LinkedList<>();
        reads = new LinkedList<>();
        readCallbacks = new LinkedList<>();
        takeCallbacks = new LinkedList<>();
        everythingLock = new ReentrantReadWriteLock();

    }

    @Override
    public void write(Tuple t) {
        var lock = everythingLock.writeLock();
        lock.lock();
        tuples.add(t);
        lock.unlock();
        wake(t);
    }

    @Override
    public Tuple take(Tuple template) {
        var lock = everythingLock.writeLock();
        lock.lock();
        var res = tryTake(template);
        if (res!=null){
            lock.unlock();
            return res;
        }
        takes.add(template);
        synchronized (template){
            try {
                lock.unlock();
                template.wait();
                lock.lock();
            } catch (InterruptedException e) {}
        }
        takes.remove(template);
        lock.unlock();
        return take(template);
    }

    @Override
    public Tuple read(Tuple template) {
        var lock = everythingLock.readLock();
        lock.lock();
        var res = tryRead(template);
        if (res != null) {
            lock.unlock();
            return res;
        }
        reads.add(template);
        try {
            synchronized (template) {
                lock.unlock();
                template.wait();
                lock.lock();
            }
        } catch (InterruptedException e) {
        }
        reads.remove(template);
        lock.unlock();
        return read(template);
    }

    @Override
    public Tuple tryTake(Tuple template) {
        var lock = everythingLock.writeLock();
        lock.lock();
        for (Tuple t : tuples
        ) {
            if (t.matches(template)) {
                tuples.remove(t);
                lock.unlock();
                return t;
            }

        }
        lock.unlock();
        return null;
    }

    @Override
    public Tuple tryRead(Tuple template) {
        var lock = everythingLock.readLock();
        lock.lock();
        for (Tuple t : tuples
        ) {
            if (t.matches(template)) {
                lock.unlock();
                return t;
            }
        }
        lock.unlock();
        return null;
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        var lock = everythingLock.writeLock();
        lock.lock();
        LinkedList listetuple = new LinkedList();
        var it = tuples.iterator();
        while (it.hasNext()){
            var t = it.next();
            if (t.matches(template)) {
                it.remove();
                listetuple.add(t);
            }
        }
        lock.unlock();
        return listetuple;
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        var lock = everythingLock.readLock();
        lock.lock();
        LinkedList listetuple = new LinkedList();
        for (Tuple t : tuples
        ) {
            if (t.matches(template)) {
                listetuple.add(t);
            }
        }
        lock.unlock();
        return listetuple;
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        var lock = everythingLock.writeLock();
        lock.lock();
        if (timing == eventTiming.IMMEDIATE) {
            Tuple val;
            if (mode == eventMode.TAKE) {
                val = tryTake(template);
            } else {
                val = tryRead(template);
            }
            if (val != null) {
                lock.unlock();
                callback.call(val);
                return;
            }
        }
        if (mode == eventMode.TAKE) {
            synchronized (takeCallbacks) {
                takeCallbacks.add(new Pair<>(template, callback));
            }
        } else {
            synchronized (readCallbacks) {
                readCallbacks.add(new Pair<>(template, callback));
            }
        }
        lock.unlock();
    }

    @Override
    public void debug(String prefix) {
        System.out.println(prefix);
        var lock = everythingLock.readLock();
        lock.lock();
            for (Tuple tuple : tuples
            ) {
                System.out.print('\t');
                System.out.println(tuple.toString());
            }
        lock.unlock();
    }


    private void wakeReads(Tuple tuple){
        var lock = everythingLock.writeLock();
        lock.lock();
        var it = reads.iterator();
        while (it.hasNext() ) {
            var template = it.next();
            if(tuple.matches(template)){
                lock.unlock();
                synchronized (template) {
                    it.remove();
                    template.notifyAll();
                }
                lock.lock();
            }
        }
        lock.unlock();
    }

    private boolean wakeTakes(Tuple tuple){
        var lock = everythingLock.writeLock();
        lock.lock();
        var it = takes.iterator();
        while (it.hasNext() ) {
            var template = it.next();
            if(tuple.matches(template)){
                synchronized (template) {
                    it.remove();
                    template.notifyAll();
                    lock.unlock();
                    return true;
                }
            }
        }
        lock.unlock();
        return false;
    }

    private void wakeReadCallbacks(Tuple tuple){
        List<Callback> matches = new LinkedList<>();
        synchronized (readCallbacks) {
            var it = readCallbacks.iterator();
            while (it.hasNext()) {
                var pair = it.next();
                if (tuple.matches(pair.a)) {
                    matches.add(pair.b);
                    it.remove();
                }
            }
        }
        for (var cb:
             matches) {
            cb.call(tuple);
        }
    }

    private boolean wakeTakeCallbacks(Tuple tuple){
        Pair<Tuple, Callback> pair = null;
        synchronized (takeCallbacks) {
            var it = takeCallbacks.iterator();
            while (it.hasNext()) {
                var nxt = it.next();

                if (tuple.matches(nxt.a)) {
                    pair = nxt;
                    it.remove();
                    synchronized (tuples) {
                        tuples.remove(tuple);
                    }
                    break;
                }
            }
        }
        if (pair!=null){
            pair.b.call(tuple);
        }
        return false;
    }


    private boolean wake(Tuple tuple){
        wakeReads(tuple);
        wakeReadCallbacks(tuple);
        boolean taken = wakeTakes(tuple);
        taken = taken || wakeTakeCallbacks(tuple);
        return taken;
    }

    public String debugToString(String prefix) {
        String Etat= prefix+"\n";
        var lock = everythingLock.readLock();
        lock.lock();
        for (Tuple tuple : tuples
        ) {
            Etat = Etat+'\t'+tuple.toString();
          }
        lock.lock();
        return Etat;
    }

    public void save(String path){
        var lock = everythingLock.readLock();
        lock.lock();
        try {

            File tupleSpace = new File(path);

            if (!tupleSpace.exists()) {
                tupleSpace.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
        }
        catch(IOException e){

        }
        lock.unlock();
    }

    public static CentralizedLinda loadSaved(String path) {
        try {
            File tupleSpace = new File(path);

            if (!tupleSpace.exists()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            var o = (CentralizedLinda) ois.readObject();
            o.initTransiant();
            return o;
        }
        catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public int getTaille(){
        return tuples.size();
    }
}
