package dastvisualizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IntegerType;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.event.ModificationWatchpointEvent;

public class ObjectManager {	
	private List<ReferenceType> preparedClass = new ArrayList<ReferenceType>();
	private List<ClassDefinition> targetClass = new ArrayList<ClassDefinition>();
	private List<ObjectReference> targetObject = new ArrayList<ObjectReference>();
	private List<ObjectInfo> objectInfo = new ArrayList<ObjectInfo>();
	//private List<List<ObjectInfo>> objectInfoMemory = new ArrayList<List<ObjectInfo>>();
	private List<ArrayInfo> arrayInfo = new ArrayList<ArrayInfo>();
	private List<ObjectInfo> drawTarget;
	private ReadDAST rf;
	private Visualize visualize;

	
	
	public ObjectManager(List<ClassDefinition> cld){
		targetClass = cld;
	}
	
	
	public void setLink(){
		if(objectInfo.size() > 0){
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
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
	}
	
	public ObjectInfo searchObjectInfo(ObjectReference tar){
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo oin = it.next();
			
			if(tar != null && oin.sameObject(tar)){
				return oin;
			}
		}
		return null;
	}
	
	public void draw(){
		paramReset();
		if(objectInfo.size() > 0){
		List<ObjectInfo> tar = objectInfo;
		if(visualize == null){
			setLink();
			set(tar);
			
			visualize = new Visualize(tar);
		}else{	
			setLink();
			set(tar);
			visualize.reDraw(tar);
		}
		}
		
		
	}
	
	private void paramReset(){
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo oin = it.next();
			oin.reset();
		}
			
	}
	public void set(List<ObjectInfo> tar) {	
		for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
			ObjectInfo oin = it.next();
			if(oin.isLinked() == false && oin.hasLink() == true){
				oin.calculateSize();
				oin.setPosion();
				
				for(int i = 0; i < oin.getWidth() || i < oin.getLength(); i++){
					boolean slideLeft = true;
					boolean slideUp = true;
					for(Iterator<ObjectInfo> iit = tar.iterator(); iit.hasNext();){
						ObjectInfo oi = iit.next();
						if(oi.getPx() == i){
							slideLeft = false;
						}
						if(oi.getPy() == i){
							slideUp = false;
						}
						if(slideUp == false && slideLeft == false){
							break;
						}
					}
					if(slideLeft && i <= oin.getWidth()){
						for(Iterator<ObjectInfo> iit = tar.iterator(); iit.hasNext();){
							ObjectInfo oi = iit.next();
							if(oi.getPx() > i){
								oi.slideLeft();
							}
						}
					}
					if(slideUp && i <= oin.getLength()){
						for(Iterator<ObjectInfo> iit = tar.iterator(); iit.hasNext();){
							ObjectInfo oi = iit.next();
							if(oi.getPy() > i){
								oi.slideUp();
							}
						}
					}
					
				}
				break;
			}
		}
		boolean sp = false;
		List<ObjectInfo> remove = new ArrayList<ObjectInfo>(); 
		/*for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
			ObjectInfo oin = it.next();
			if(sp == false && oin.isLinked() == false && oin.hasLink() == true){
				sp = true;
			}else if(sp == true && oin.isLinked() == false && oin.hasLink() == true){
				remove.add(oin);
			}
		}
		
		for(Iterator<ObjectInfo> it = remove.iterator();it.hasNext();){
			tar.remove(it.next());
		}*/
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
	
	public ClassDefinition isDefinedClass(ReferenceType tar){
		
		for(Iterator<ClassDefinition> it = targetClass.iterator(); it.hasNext();){
			ClassDefinition cld = ((ClassDefinition) it.next());
			//System.out.println(tar.name() + " "+cld.getName());
			if(tar.name().matches(".*\\.*" + cld.getName())){
				//System.out.println(cld.getName() + " "+ tar.name() );
				return cld;
			}else if(tar.name().equals(cld.getName())){
				//System.out.println(cld.getName() + " "+ tar.name() );
				return cld;
			}
		}
		return null;
	}
	
	public void renew(ModificationWatchpointEvent event){
		ObjectReference tar = event.object();
		ObjectInfo obInfo = isMadeObjectInfo(event.object());
		Value value = event.valueToBe();
		Type type = null;
		if(value != null){
			type = value.type();

		}
		
		if(value != null && value.type() instanceof ReferenceType){
			ClassDefinition cld =  isDefinedClass((ReferenceType)value.type());
			if(cld != null){
				ObjectInfo tobe = isMadeObjectInfo((ObjectReference)value);
				if(tobe == null){
					ObjectReference toBe = (ObjectReference)tobe;
					getTargetObject().add(toBe);
					ObjectInfo object = new ObjectInfo(toBe, toBe.referenceType(), cld, this);
					objectInfo.add(object);
					//System.out.println("add " + toBe);
					object.setField();
					try {
						object.setLink();
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		if(obInfo != null){
			if(type != null && type instanceof ArrayType){
				Field field = event.field();
				addArray(tar, field, value);
				obInfo.Link();
			}
			try {
				obInfo.setLink();
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//obInfo.changeField(event);
			//obInfo.setField();
			//addObjectInfoMemory();
		}else{
			ClassDefinition cld = isDefinedClass((ReferenceType) tar.referenceType());
			if(cld != null){
				getTargetObject().add(tar);
				ObjectInfo object = new ObjectInfo(tar, (ReferenceType)tar.referenceType(), cld, this);
				object.setField();
				objectInfo.add(object);
				//System.out.println("add " + tar);
				try {
					object.setLink();
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//addObjectInfoMemory();
				if(type != null && type instanceof ArrayType){
					Field field = event.field();
					addArray(tar, field, value);
					object.Link();
					//addObjectInfoMemory();
				}
				
			}
			
		}
		
	}
	
	public void addArray(ObjectReference from,Field field, Value value){
		if(searchObjectInfo((ObjectReference)value) == null){
			ClassDefinition cld = isDefinedClass((ClassType) from.referenceType());
			int directed = cld.getDirectionbyName(field.name());
			ArrayInfo array = new ArrayInfo((ObjectReference)value, value.type(), null, this, directed, field.name());
			objectInfo.add(array);
			arrayInfo.add(array);
		}
	}


	public void renew(ObjectReference object, Field field, Value value){
		
		//System.out.println();
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo obInfo = (ObjectInfo)it.next();
			//System.out.println(obInfo.getobject());
		}
		
 		ObjectInfo obInfo = isMadeObjectInfo(object);
		Type type = null;
		if(value != null){
			type = value.type();
		}
		if(obInfo != null){		
			if(type != null && type instanceof ArrayType){
				addArray(object, field, value);
			}
			try {
				obInfo.setLink();
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//obInfo.changeField(field, value);
			//obInfo.setField();
			//addObjectInfoMemory();
		}else{
			if(object != null){
			ClassDefinition cld = isDefinedClass((ClassType) object.referenceType());
			if(cld != null){
				getTargetObject().add(object);
				//System.out.println("!!");
				ObjectInfo obj = new ObjectInfo(object, (ClassType)object.referenceType(), cld, this);
				obj.setField();
				objectInfo.add(obj);
				//System.out.println("add" + object);
				try {
					obj.setLink();
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//addObjectInfoMemory();
				if(type != null && type instanceof ArrayType){
					addArray(object, field, value);
					obj.Link();
					//addObjectInfoMemory();
				}
			}
			}
		}
		 
	}	
	

	
	ObjectInfo isMadeObjectInfo(ObjectReference tar){
		//System.out.println();
		//System.out.println("target:"+tar);
		for(Iterator<ObjectInfo> it = objectInfo.iterator(); it.hasNext();){
			ObjectInfo obInfo = (ObjectInfo)it.next();
			//System.out.println("next:"+obInfo.getobject());
			if(obInfo.getobject().equals(tar)){
				return obInfo;
			}
		}
		//System.out.println("false:" +  tar);
		return null;
	}
	
	/*private void addObjectInfoMemory(){
		List<ObjectInfo> copy = new ArrayList<ObjectInfo>();
		for(Iterator<ObjectInfo> it = objectInfo().iterator(); it.hasNext();){
			ObjectInfo tar = (ObjectInfo) it.next();
			copy.add(tar.deepCopy());
		}
		objectInfoMemory.add(copy);
		for(Iterator<ObjectInfo> it = copy.iterator(); it.hasNext();){
			ObjectInfo tar = it.next();
			try {
				tar.setLink();
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/

	/*public void updateArray(){
		boolean updated = false;
		if(objectInfo.size() <= 0 || arrayInfo.size() <= 0){
			return;
		}
		for(Iterator<ObjectInfo> it = objectInfo.iterator();it.hasNext();){
			ObjectInfo tar = it.next();
			if(tar.isArray()){
				if(((ArrayInfo)tar).arrayUpdated()){
					updated = true;
				}
			}
		}
		if(updated){
			//addObjectInfoMemory();
			draw();
		}
	}*/

	public List<ObjectReference> getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(List<ObjectReference> targetObject) {
		this.targetObject = targetObject;
	}

	public List<ObjectInfo> getObjectInfo() {
		return objectInfo;
	}



}
