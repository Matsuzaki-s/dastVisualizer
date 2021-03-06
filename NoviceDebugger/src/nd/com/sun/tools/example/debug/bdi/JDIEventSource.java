/*
 * @(#)JDIEventSource.java	1.10 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */
package nd.com.sun.tools.example.debug.bdi;

import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import nd.com.sun.tools.example.debug.event.AbstractEventSet;
import nd.com.sun.tools.example.debug.event.AccessWatchpointEventSet;
import nd.com.sun.tools.example.debug.event.ClassPrepareEventSet;
import nd.com.sun.tools.example.debug.event.ClassUnloadEventSet;
import nd.com.sun.tools.example.debug.event.ExceptionEventSet;
import nd.com.sun.tools.example.debug.event.JDIListener;
import nd.com.sun.tools.example.debug.event.LocationTriggerEventSet;
import nd.com.sun.tools.example.debug.event.ModificationWatchpointEventSet;
import nd.com.sun.tools.example.debug.event.ThreadDeathEventSet;
import nd.com.sun.tools.example.debug.event.ThreadStartEventSet;
import nd.com.sun.tools.example.debug.event.VMDeathEventSet;
import nd.com.sun.tools.example.debug.event.VMDisconnectEventSet;
import nd.com.sun.tools.example.debug.event.VMStartEventSet;

import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ModificationWatchpointRequest;

import dastvisualizer.ObjectManager;
import dastvisualizer.ReadDAST;

/**
 */
class JDIEventSource extends Thread {

	private /*final*/ EventQueue queue;
	private /*final*/ Session session;
	private /*final*/ ExecutionManager runtime;
	private final JDIListener firstListener = new FirstListener();
	private boolean wantInterrupt;  //### Hack

	/*追加部分*/
	private final ObjectManager objm;
	private String[] excludes = {"java.*", "javax.*", "sun.*", 
	 "com.sun.*"};
	
	/**
	 * Create event source.
	 */
	JDIEventSource(Session session) {
		super("JDI Event Set Dispatcher");
		this.session = session;
		this.runtime = session.runtime;
		this.queue = session.vm.eventQueue();
		this.objm = session.objm;
	}

	public void run() {
		try {
			runLoop();
		} catch (Exception exc) {
			//### Do something different for InterruptedException???
			// just exit
			System.out.println(exc);
		}
		session.running = false;
	}

	private void runLoop() throws InterruptedException {
		AbstractEventSet es;
		do {
			EventSet jdiEventSet = queue.remove();
			es = AbstractEventSet.toSpecificEventSet(jdiEventSet);
			//objm.updateArray();
			session.interrupted = es.suspendedAll();
			dispatchEventSet(es);
			
		} while(!(es instanceof VMDisconnectEventSet));
	}

	//### Gross foul hackery!
	private void dispatchEventSet(final AbstractEventSet es) {
		//System.out.println(es);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				boolean interrupted = es.suspendedAll();
				es.notify(firstListener);
				boolean wantInterrupt = JDIEventSource.this.wantInterrupt;
				for (Iterator<JDIListener> it = session.runtime.jdiListeners.iterator(); 
						it.hasNext(); ) {
					JDIListener jl = (JDIListener)it.next();
					es.notify(jl);
				}
				if (interrupted && !wantInterrupt) {
					session.interrupted = false;
					//### Catch here is a hack
					try {
						session.vm.resume();
					} catch (VMDisconnectedException ee) {}
				}
				if (es instanceof ThreadDeathEventSet) {
					ThreadReference t = ((ThreadDeathEventSet)es).getThread();
					session.runtime.removeThreadInfo(t);
				}
			}
		});
	}

	/*
    private void finalizeEventSet(AbstractEventSet es) {
        if (session.interrupted && !wantInterrupt) {
            session.interrupted = false;
            //### Catch here is a hack
            try {
                session.vm.resume();
            } catch (VMDisconnectedException ee) {}
        }
        if (es instanceof ThreadDeathEventSet) {
            ThreadReference t = ((ThreadDeathEventSet)es).getThread();
            session.runtime.removeThreadInfo(t);
        }
    }*/

	//### This is a Hack, deal with it
	private class FirstListener implements JDIListener {

		int CPcount = 0;
		
		public void accessWatchpoint(AccessWatchpointEventSet e) {
			session.runtime.validateThreadInfo();
			wantInterrupt = true;
		}

		public void classPrepare(ClassPrepareEventSet e)  {
			System.out.println(CPcount +  " "  + e.getReferenceType().name());
			CPcount++;
			//追加
			if(objm.classPrepare(e.getReferenceType())){
			EventRequestManager mgr = session.vm.eventRequestManager();
			List fields = e.getReferenceType().visibleFields(); // visibeFields() → allFields()
			for (Iterator it = fields.iterator(); it.hasNext();) {
				Field field = (Field) it.next();
				ModificationWatchpointRequest req = mgr
						.createModificationWatchpointRequest(field);
				for (int i = 0; i < excludes.length; ++i) {
					req.addClassExclusionFilter(excludes[i]);
				}
				req.setSuspendPolicy(EventRequest.SUSPEND_NONE);
				req.enable();
			}
			fields = null;
			
			}
			/////////////////////////
			wantInterrupt = false;
			runtime.resolve(e.getReferenceType());
			
		}

		public void classUnload(ClassUnloadEventSet e)  {
			wantInterrupt = false;
		}

		public void exception(ExceptionEventSet e)  {
			wantInterrupt = true;
		}

		public void locationTrigger(LocationTriggerEventSet e)  {
			session.runtime.validateThreadInfo();
			wantInterrupt = true;
		}

		public void modificationWatchpoint(ModificationWatchpointEventSet e)  {
			objm.updateArray();
			objm.renew(e.getObject(), e.getField(), e.getValueToBe());
	    	objm.setLink();
	    	objm.draw();
			session.runtime.validateThreadInfo();
			wantInterrupt = true;
		}

		public void threadDeath(ThreadDeathEventSet e)  {
			wantInterrupt = false;
		}

		public void threadStart(ThreadStartEventSet e)  {
			wantInterrupt = false;
		}

		public void vmDeath(VMDeathEventSet e)  {
			//### Should have some way to notify user
			//### that VM died before the session ended.
			wantInterrupt = false;
		}

		public void vmDisconnect(VMDisconnectEventSet e)  {
			//### Notify user?
			wantInterrupt = false;
			session.runtime.endSession();
		}

		public void vmStart(VMStartEventSet e)  {
			//### Do we need to do anything with it?
			wantInterrupt = false;
		}
	}
}
