package AttachingConnectorVersion;


//対象ファイル実行オプション　java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.Connector.IntegerArgument;
import com.sun.jdi.connect.Connector.StringArgument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

import dastvisualizer.ReadDAST;

public class Connect {
	private VirtualMachine vm;
	private ReadDAST rf;
	private Thread errThread = null;
	private Thread outThread = null;
	private int debugTraceMode = 0;
	
	private boolean attached = false;
	
	private String[] excludes = {"java.*", "javax.*", "sun.*", 
	 "com.sun.*"};
	
	private boolean watchFields = true;
	
	public static void main(String args[]){
		new Connect();
	}
	
	public Connect(){
		VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
		List acs = vmm.attachingConnectors();
		
		AttachingConnector ac = null;
		 Iterator it = acs.iterator();
	        while (it.hasNext()) {
	            AttachingConnector tmp = (AttachingConnector)  it.next();
	            if ("com.sun.jdi.SocketAttach".equals(tmp.name())) {
	               ac = tmp;
	               break;
	            }
	        }
	        
	        Map<String, Argument> arg = ac.defaultArguments();
	        IntegerArgument portNumber =
	            ( IntegerArgument ) arg.get("port");
	        portNumber.setValue(8000);
	        StringArgument hostname =
	            ( StringArgument ) arg.get("hostname");
	        hostname.setValue("localhost");

	     	while(attached == false){
	     		try {
	     			vm = ac.attach(arg);
	     			} catch (IOException | IllegalConnectorArgumentsException e) {
				// TODO Auto-generated catch block
	     				e.printStackTrace();
	     				}
	     		if(vm != null){
	     			attached = true;
	     			}
	     	}
	        
	        
	        generateTrace();
	}
	
	void generateTrace() {
		 try {
			//rf = new ReadDAST(new FileInputStream("TestTarget\\HashC\\Chain.dast"));
			//rf = new ReadDAST(new FileInputStream("TestTarget\\BST\\BST.dast"));
			//rf = new ReadDAST(new FileInputStream("C:\\Users\\student\\git\\DENOwithDastVisualizer\\RonproEditor\\testbase\\MyProjects\\BTree\\DASTFile"));
			 rf = new ReadDAST(new FileInputStream("C:\\Users\\student\\Desktop\\研究室\\B4ゼミ\\卒業研究\\SA\\TestTarget\\src\\ArrayTest\\DASTFile"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 PrintWriter writer = new PrintWriter(System.out);
	        vm.setDebugTraceMode(debugTraceMode);
	        EventThread eventThread = new EventThread(vm, excludes, writer, rf); 
	        eventThread.setEventRequests(watchFields);
	        eventThread.start();
	       // redirectOutput();
	        vm.resume();

	        // Shutdown begins when event thread terminates
		try {
		    eventThread.join();
		   // errThread.join(); // Make sure output is forwarded 
		   // outThread.join(); // before we exit
		} catch (InterruptedException exc) {
		    // we don't interrupt
		}
		writer.close();
	 }
	 
	 /*void redirectOutput() {
	        Process process = vm.process();

	        if(process == null){
	        	System.out.println("process null");
	        }
	        
	        // Copy target's output and error to our output and error.
	        errThread = new StreamRedirectThread("error reader",
	                                             process.getErrorStream(),
	                                             System.err);
	        outThread = new StreamRedirectThread("output reader",
	                                             process.getInputStream(),
	                                             System.out);
	        errThread.start();
	        outThread.start();
	    }*/
}
