package LaunchingConnecterVersion;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

import com.sun.jdi.connect.*;
import com.sun.jdi.*;

import dastvisualizer.ReadDAST;


public class Connect {
	VirtualMachine VM;
	private Thread errThread = null;
	private Thread outThread = null;
	private File target;
	private String fileName;
	private String className;
	private String classpath;
	
	private boolean choosing = true;
	
	private int debugTraceMode = 0;
	
	private String[] excludes = {"java.*", "javax.*", "sun.*", 
	 "com.sun.*"};
	private boolean watchFields = true;
	private ReadDAST rf;
	
	public static void main(String args[]){
		new Connect();
	}

	
	Connect(){
		fileSelect();
		while(choosing){
		}
		/*Class targetClass = null;
		try {
			targetClass = getClass().getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println(targetClass.getName());
		*/
		VM = launchTarget(className);

		generateTrace();
	}
	
	VirtualMachine launchTarget(String mainArgs){
		LaunchingConnector connector = findLaunchingConnector();
		Map arguments = connectorArguments(connector, mainArgs); 
		
		((Connector.Argument) arguments.get("options")).setValue("-classpath \"" + classpath + ";.\"");


		
		try{
			return connector.launch(arguments);
		}catch(IOException e){
			throw new Error("Unable to launch targetVM: " +e);
		}catch(IllegalConnectorArgumentsException e){
			throw new Error("Internal eror:" + e);
		}catch(VMStartException e){
			throw new Error("Target VM failed to initialize: "+ e);
		}
	}
	
	LaunchingConnector findLaunchingConnector() {
        List connectors = Bootstrap.virtualMachineManager().allConnectors();
        Iterator iter = connectors.iterator();
        while (iter.hasNext()) {
            Connector connector = (Connector)iter.next();
            if (connector.name().equals("com.sun.jdi.CommandLineLaunch")) {
                return (LaunchingConnector)connector;
            }
        }
        throw new Error("No launching connector");
    }
	
	Map connectorArguments(LaunchingConnector connector, String mainArgs){
		Map arguments = connector.defaultArguments();
		Connector.Argument mainArg = (Connector.Argument)arguments.get("main");
		if(mainArg == null){
			throw new Error("Bad launching connector");
		}
		mainArg.setValue(mainArgs);
		
		/*
		 * if(watchFields){
		 * }
		 */
		
		return arguments;
		
	}
	
	
	void fileSelect(){
		FileChooser fc = new FileChooser(this);
	}
	
	void setTarget(File f){
		target = f;
		fileName = target.getName();
		className = fileName.replace(".class", "");
		classpath = target.getPath().replace("\\" + fileName,"");
		choosing = false;
	}
	
	//ˆÈ‰ºƒRƒsƒy
	 void generateTrace() {
		 try {
			rf = new ReadDAST(new FileInputStream("C:\\Users\\student\\Desktop\\prog\\BST\\BST.dust"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 PrintWriter writer = new PrintWriter(System.out);
	        VM.setDebugTraceMode(debugTraceMode);
	        EventThread eventThread = new EventThread(VM, excludes, writer, rf); 
	        eventThread.setEventRequests(watchFields);
	        eventThread.start();
	        redirectOutput();
	        VM.resume();

	        // Shutdown begins when event thread terminates
		try {
		    eventThread.join();
		    errThread.join(); // Make sure output is forwarded 
		    outThread.join(); // before we exit
		} catch (InterruptedException exc) {
		    // we don't interrupt
		}
		writer.close();
	 }
	 
	 void redirectOutput() {
	        Process process = VM.process();

	        // Copy target's output and error to our output and error.
	        errThread = new StreamRedirectThread("error reader",
	                                             process.getErrorStream(),
	                                             System.err);
	        outThread = new StreamRedirectThread("output reader",
	                                             process.getInputStream(),
	                                             System.out);
	        errThread.start();
	        outThread.start();
	    }
}
