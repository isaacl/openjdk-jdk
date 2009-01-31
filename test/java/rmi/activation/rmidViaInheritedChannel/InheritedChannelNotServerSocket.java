/* 
 * Copyright 2005 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/* @test
 * @bug 6261402
 * @summary If rmid has an inherited channel that is not a server
 * socket (such as it if was started using rsh/rcmd), then it should
 * function normally.
 * @author Peter Jones
 *
 * @library ../../testlibrary
 * @build RMID ActivationLibrary
 * @build InheritedChannelNotServerSocket
 * @run main/othervm/timeout=240 -Djava.rmi.activation.port=5398
 *     InheritedChannelNotServerSocket
 */

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationSystem;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class InheritedChannelNotServerSocket {

    private static final int PORT = 5398;
    private static final Object lock = new Object();
    private static boolean notified = false;

    private InheritedChannelNotServerSocket() { }

    public interface Callback extends Remote {
	void notifyTest() throws RemoteException;
    }

    public static class CallbackImpl implements Callback {
	CallbackImpl() { }
	public void notifyTest() {
	    synchronized (lock) {
		notified = true;
		System.err.println("notification received.");
		lock.notifyAll();
	    }
	}
    }

    public static void main(String[] args) throws Exception {
	System.err.println("\nRegression test for bug 6261402\n");

	RMID rmid = null;
	Callback obj = null;
	try {
	    /*
	     * Export callback object and bind in registry.
	     */
	    System.err.println("export callback object and bind in registry");
	    obj = new CallbackImpl();
	    Callback proxy =
		(Callback) UnicastRemoteObject.exportObject(obj, 0);
	    Registry registry =
		LocateRegistry.createRegistry(TestLibrary.REGISTRY_PORT);
	    registry.bind("Callback", proxy);

	    /*
	     * Start rmid.
	     */
	    System.err.println("start rmid with inherited channel");
	    RMID.removeLog();
	    rmid = RMID.createRMID(System.out, System.err, true, true, PORT);
	    rmid.addOptions(new String[]{
		"-Djava.nio.channels.spi.SelectorProvider=" +
		"InheritedChannelNotServerSocket$SP"});
	    rmid.start();

	    /*
	     * Get activation system and wait to be notified via callback
	     * from rmid's selector provider.
	     */
	    System.err.println("get activation system");
	    ActivationSystem system = ActivationGroup.getSystem();
	    System.err.println("ActivationSystem = " + system);
	    synchronized (lock) {
		while (!notified) {
		    lock.wait();
		}
	    }
	    System.err.println("TEST PASSED");
	} finally {
	    if (obj != null) {
		UnicastRemoteObject.unexportObject(obj, true);
	    }
	    ActivationLibrary.rmidCleanup(rmid, PORT);
	}
    }

    public static class SP extends SelectorProvider {
	private final SelectorProvider provider;
	private volatile SocketChannel channel = null;

	public SP() {
	    provider = sun.nio.ch.DefaultSelectorProvider.create();
	}

	public DatagramChannel openDatagramChannel() throws IOException {
	    return provider.openDatagramChannel();
	}

	public Pipe openPipe() throws IOException {
	    return provider.openPipe();
	}

	public AbstractSelector openSelector() throws IOException {
	    return provider.openSelector();
	}

	public ServerSocketChannel openServerSocketChannel() 
	    throws IOException
	{
	    return provider.openServerSocketChannel();
	}

	public SocketChannel openSocketChannel() throws IOException {
	    return provider.openSocketChannel();
	}
	
	public synchronized Channel inheritedChannel() throws IOException {
	    System.err.println("SP.inheritedChannel");
	    if (channel == null) {
		channel = SocketChannel.open();
		Socket socket = channel.socket();
		System.err.println("socket = " + socket);

		/*
		 * Notify test that inherited channel was created.
		 */
		try {
		    System.err.println("notify test...");
		    Registry registry =
			LocateRegistry.getRegistry(TestLibrary.REGISTRY_PORT);
		    Callback obj = (Callback) registry.lookup("Callback");
		    obj.notifyTest();
		} catch (NotBoundException nbe) {
		    throw (IOException)
			new IOException("callback object not bound").
			    initCause(nbe);
		}
	    }
	    return channel;
	}
    }
}
