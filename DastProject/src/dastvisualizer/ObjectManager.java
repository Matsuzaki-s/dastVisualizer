package dastvisualizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;





import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;
import com.sun.jdi.event.ModificationWatchpointEvent;

public class ObjectManager {	
	private List<ReferenceType> preparedClass = new ArrayList<ReferenceType>();
	private List<ClassDefinition> targetClass = new ArrayList<ClassDefinition>();
	private List<ObjectReference> targetObject = new ArrayList<ObjectReference>();
	private List<ObjectInfo> objectInfo = new ArrayList<ObjectInfo>();
	private List<List<ObjectInfo>> objectInfoMemory = new ArrayList<List<ObjectInfo>>();
	private List<ObjectInfo> drawTarget;
	private ReadDAST rf;
	private Visualize visualize;

	/*public ObjectManager(List<ReferenceType> tar, ReadFile rf){
		this.rf = rf; 
		for(Iterator<ReferenceType> it = tar.iterator(); it.hasNext();){
			ReferenceType rtype = (ReferenceType)it.next();	
			
			for(Iterator<ObjectReference> ite = rtype.instances(0).iterator(); ite.hasNext();){
				ObjectReference OR = (ObjectReference) ite.next();
				System.out.println(OR.toString());
			}
				
			
			targetObject.addAll(rtype.instances(0));
			
	    	
		}
		
		for(Iterator<ObjectReference> it = targetObject.iterator();it.hasNext(); ){
    		System.out.println(it.next());
    	}
		
		insert();
		setLink();
		new Set(objectInfo);
		new Visualize(objectInfo);
	}*/
	
	public ObjectManager(List<ClassDefinition> cld){
		targetClass = cld;
	}
	
	/*private void insert(){
		for(Iterator<ObjectReference> it = targetObject.iterator(); it.hasNext();){
			ObjectReference oref = (ObjectReference)it.next();
			objectInfo.add(new ObjectInfo(oref, rf, this));

		}
	}*/
	
	public void setLink(){
		for(Iterator<ObjectInfo> it = (objectInfoMemory.get(objectInfoMemory.size() - 1)).iterator(); it.hasNext();){
			ObjectInfo oinfo = (ObjectInfo)it.next();
			try {
				oinfo.setLink();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ObjectInfo searchObjectInfo(ObjectReference tar){
		for(Iterator<ObjectInfo> it = objectInfoMemory.get(objectInfoMemory.size() - 1).iterator(); it.hasNext();){
			ObjectInfo oin = it.next();
			if(tar != null && oin.sameObject(tar)){
				return oin;
			}
		}
		return null;
	}
	
	public void draw(){
		List<ObjectInfo> tar = objectInfoMemory.get(objectInfoMemory.size() - 1); 
		if(visualize == null){
			set(tar);
			visualize = new Visualize(tar);
		}else{	
			set(tar);
			visualize.reDraw(tar);
		}
	}
	
	public void set(List<ObjectInfo> tar) {	
		for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
			ObjectInfo oin = it.next();
			if(oin.isLinked() == false && oin.hasLink() == true){
				oin.calculateSize();
				oin.setPosion();
				
				break;
			}
		}
	}
		
	public boolean classPrepare(ClassType tar){
		if(isDefinedClass(tar) != null){
			preparedClass.add(tar);
			return true;
		}
		return false;
	}
	
	public boolean classPrepare(ReferenceType tar) {
		if(isDefinedClass(tar) != null){
			preparedClass.add(tar);
			return true;
		}
		return false;
	}
	
	private ClassDefinition isDefinedClass(ReferenceType tar){
		for(Iterator<ClassDefinition> it = targetClass.iterator(); it.hasNext();){
			ClassDefinition cld = ((ClassDefinition) it.next());
			if(cld.getName().equals(tar.name())){
				return cld;
			}
		}
		return null;
	}
	
	public void renew(ModificationWatchpointEvent event){
		ObjectReference tar = event.object();
		ObjectInfo obInfo = isMadeObjectInfo(event.object());
		if(obInfo != null){
			
			obInfo.changeField(event);
			addObjectInfoMemory();
		}else{
			ClassDefinition cld = isDefinedClass((ClassType) tar.referenceType());
			if(cld != null){
				targetObject.add(tar);
				objectInfo.add(new ObjectInfo(tar, (ClassType)tar.referenceType(), cld, this));
				addObjectInfoMemory();
			}
			
		}
	}
	
	public void renew(ObjectReference object, Field field, Value value){
		ObjectInfo obInfo = isMadeObjectInfo(object);
		if(obInfo != null){
			
			obInfo.changeField(field, value);
			addObjectInfoMemory();
		}else{
			ClassDefinition cld = isDefinedClass((ClassType) object.referenceType());
			if(cld != null){
				targetObject.add(object);
				objectInfo.add(new ObjectInfo(object, (ClassType)object.referenceType(), cld, this));
				addObjectInfoMemory();
			}
			
		}
	}
	
	

	
	private ObjectInfo isMadeObjectInfo(ObjectReference tar){
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo obInfo = (ObjectInfo)it.next();
			if(obInfo.getobject() == tar){
				return obInfo;
			}
		}
		return null;
	}
	
	private void addObjectInfoMemory(){
		List<ObjectInfo> copy = new ArrayList<ObjectInfo>();
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo tar = (ObjectInfo) it.next();
			copy.add(tar.deepCopy());
		}
		objectInfoMemory.add(copy);
	}

	

}
