package tools;

import java.io.*;
import java.nio.channels.*;

/**
 * This class is written to allow only one instance of the application
 * @author Arpo Adhikari
 *
 */
public class CheckForLock {
    private String appName;
    private File file;
    private FileChannel channel;
    private FileLock lock;

    /**
     * Constructor of the class
     * @param appName
     */
    public CheckForLock(String appName) {
        this.appName = appName;
    }

    /**
     * Check whether application is running or not by holding a lock to a file
     * @return
     */
	@SuppressWarnings("resource")
	public boolean isAppActive() {
        try {
            file = new File
                 (System.getProperty("user.home"), appName + ".tmp");
            channel = new RandomAccessFile(file, "rw").getChannel();

            try {
                lock = channel.tryLock();
            }
            catch (OverlappingFileLockException e) {
                // already locked
                closeLock();
                return true;
            }

            if (lock == null) {
                closeLock();
                return true;
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                    // destroy the lock when the JVM is closing
                    public void run() {
                        closeLock();
                        deleteFile();
                    }
                });
            return false;
        }
        catch (Exception e) {
            closeLock();
            return true;
        }
    }

    /**
     * Close the file lock
     */
    private void closeLock() {
        try { 
        	lock.release();  
        	channel.close();
        }
        catch (Exception e) {
        	System.out.println("Unable to close lock : "+e.getMessage());
        }
    }

    /**
     * Delete the file
     */
    private void deleteFile() {
        try { 
        	file.delete(); 
        }
        catch (Exception e) {
        	System.out.println("Unable to delete the lock file : "+e.getMessage());
        }
    }
}
