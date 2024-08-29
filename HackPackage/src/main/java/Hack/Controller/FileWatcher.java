package Hack.Controller;

import Hack.Events.ProgramEvent;
import Hack.Events.ProgramEventListener;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

// From : https://stackoverflow.com/questions/16251273/can-i-watch-for-single-file-change-with-watchservice-not-the-whole-directory
public class FileWatcher extends Thread {
    private final File file;
    private AtomicBoolean stop = new AtomicBoolean(false);
    private long lastModifiedTime = 0;
    private static final long DEBOUNCE_THRESHOLD_MS = 3000;
    private ArrayList<ProgramEventListener> programEventListeners;

    public FileWatcher(File file) {
        this.file = file;
        programEventListeners = new ArrayList<>();
    }

    public boolean isStopped() { return stop.get(); }
    public void stopThread() { stop.set(true); }

    public void doOnChange() {
        ProgramEvent programEvent = new ProgramEvent(this,ProgramEvent.UPDATED, file.getAbsolutePath());
        for (ProgramEventListener listener : programEventListeners){
            listener.programChanged(programEvent);
        }
    }

    public void addListener(ProgramEventListener listener){
        programEventListeners.add(listener);
    }

    public void removeListener(ProgramEventListener listener){
        programEventListeners.add(listener);
    }

    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            Path path = file.toPath().getParent();
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!isStopped()) {
                WatchKey key;
                try { key = watcher.poll(25, TimeUnit.MILLISECONDS); }
                catch (InterruptedException e) { return; }
                if (key == null) { Thread.yield(); continue; }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        Thread.yield();
                        continue;
                    } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
                            && filename.toString().equals(file.getName())) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastModifiedTime > DEBOUNCE_THRESHOLD_MS) {
                            lastModifiedTime = currentTime;
                            doOnChange();
                        }
                    }
                    boolean valid = key.reset();
                    if (!valid) { break; }
                }
                Thread.yield();
            }
        } catch (Throwable e) {
            System.err.println(e.getMessage());
        }
    }
}
