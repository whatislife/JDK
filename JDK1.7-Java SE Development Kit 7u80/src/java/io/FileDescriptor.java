/*
 * Copyright (c) 1995, 2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.io;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Instances of the file descriptor class serve as an opaque handle
 * to the underlying machine-specific structure representing an open
 * file, an open socket, or another source or sink of bytes. The
 * main practical use for a file descriptor is to create a
 * <code>FileInputStream</code> or <code>FileOutputStream</code> to
 * contain it.
 * <p>
 * Applications should not create their own file descriptors.
 *
 * @author  Pavani Diwanji
 * @see     java.io.FileInputStream
 * @see     java.io.FileOutputStream
 * @since   JDK1.0
 */
public final class FileDescriptor {

    private int fd;

    /**
     * A counter for tracking the FIS/FOS/RAF instances that
     * use this FileDescriptor. The FIS/FOS.finalize() will not release
     * the FileDescriptor if it is still under user by a stream.
     */
    private AtomicInteger useCount;

    /**
     * Constructs an (invalid) FileDescriptor
     * object.
     */
    public /**/ FileDescriptor() {
        fd = -1;
        useCount = new AtomicInteger();
    }

    private /* */ FileDescriptor(int fd) {
        this.fd = fd;
        useCount = new AtomicInteger();
    }

    /**
     * A handle to the standard input stream. Usually, this file
     * descriptor is not used directly, but rather via the input stream
     * known as <code>System.in</code>.
     *
     * @see     java.lang.System#in
     */
    public static final FileDescriptor in = new FileDescriptor(0);

    /**
     * A handle to the standard output stream. Usually, this file
     * descriptor is not used directly, but rather via the output stream
     * known as <code>System.out</code>.
     * @see     java.lang.System#out
     */
    public static final FileDescriptor out = new FileDescriptor(1);

    /**
     * A handle to the standard error stream. Usually, this file
     * descriptor is not used directly, but rather via the output stream
     * known as <code>System.err</code>.
     *
     * @see     java.lang.System#err
     */
    public static final FileDescriptor err = new FileDescriptor(2);

    /**
     * Tests if this file descriptor object is valid.
     *
     * @return  <code>true</code> if the file descriptor object represents a
     *          valid, open file, socket, or other active I/O connection;
     *          <code>false</code> otherwise.
     */
    public boolean valid() {
        return fd != -1;
    }

    /**
     * Force all system buffers to synchronize with the underlying
     * device.  This method returns after all modified data and
     * attributes of this FileDescriptor have been written to the
     * relevant device(s).  In particular, if this FileDescriptor
     * refers to a physical storage medium, such as a file in a file
     * system, sync will not return until all in-memory modified copies
     * of buffers associated with this FileDescriptor have been
     * written to the physical medium.
     *
     * sync is meant to be used by code that requires physical
     * storage (such as a file) to be in a known state  For
     * example, a class that provided a simple transaction facility
     * might use sync to ensure that all changes to a file caused
     * by a given transaction were recorded on a storage medium.
     *
     * sync only affects buffers downstream of this FileDescriptor.  If
     * any in-memory buffering is being done by the application (for
     * example, by a BufferedOutputStream object), those buffers must
     * be flushed into the FileDescriptor (for example, by invoking
     * OutputStream.flush) before that data will be affected by sync.
     *
     * @exception SyncFailedException
     *        Thrown when the buffers cannot be flushed,
     *        or because the system cannot guarantee that all the
     *        buffers have been synchronized with physical media.
     * @since     JDK1.1
     */
    public native void sync() throws SyncFailedException;

    /* This routine initializes JNI field offsets for the class */
    private static native void initIDs();

    static {
        initIDs();
    }

    // Set up JavaIOFileDescriptorAccess in SharedSecrets
    static {
        sun.misc.SharedSecrets.setJavaIOFileDescriptorAccess(
            new sun.misc.JavaIOFileDescriptorAccess() {
                public void set(FileDescriptor obj, int fd) {
                    obj.fd = fd;
                }

                public int get(FileDescriptor obj) {
                    return obj.fd;
                }

                public void setHandle(FileDescriptor obj, long handle) {
                    throw new UnsupportedOperationException();
                }

                public long getHandle(FileDescriptor obj) {
                    throw new UnsupportedOperationException();
                }
            }
        );
    }

    // package private methods used by FIS, FOS and RAF

    int incrementAndGetUseCount() {
        return useCount.incrementAndGet();
    }

    int decrementAndGetUseCount() {
        return useCount.decrementAndGet();
    }
}
