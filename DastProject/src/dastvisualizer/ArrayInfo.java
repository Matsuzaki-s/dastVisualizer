package dastvisualizer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;



public class ArrayInfo extends ObjectInfo{
	private int size;
	private ArrayReference array;
	private boolean isPrimitive = false;
	private ObjectInfo[] arrayValue;
	private int directed;
	private String fieldName;
	private Type type;
	
	

	private String name;
	private int width = 0;
	private int length = 0;
	private int ownLength = 1;
	private int ownWidth = 1;
	private int up_half = 0;
	private int bottom_half = 0;
	private int left_half = 0;
	private int right_half = 0;
	private int with = -1;
	

	
	ArrayInfo(ObjectReference tar, Type type, ClassDefinition def,
			ObjectManager om, int directed, String fieldName) {
		//Å¶def = null
		super(tar, type, def, om);
		array = (ArrayReference)(this.object);
		this.directed = directed;
		arrayValue = new ObjectInfo[size];
		size = ((ArrayReference) tar).length();
		this.fieldName = fieldName;
		this.type = type;
		
	}

	ArrayInfo(ObjectInfo source){
		super(source);
		array = (ArrayReference)(this.object);
		isPrimitive = source.isPrimitive;
		this.directed = ((ArrayInfo)source).getDirection();
		size = ((ArrayInfo)source).getSize();
		arrayValue = new ObjectInfo[size];
		this.type = source.type;
		
		this.fieldName = ((ArrayInfo)source).getFieldName();
		this.px = source.getPx();
		this.py = source.getPy();
	}

	public void setLink(){
		
		//try {
			/*if(((ReferenceType) at.componentType()).isVerified() ||at.componentType() instanceof PrimitiveType){
				isPrimitive = true;
				for(int i = 0; i < size; i++){
					arrayValue[i] = new ObjectInfo(ar.getValue(i));
				}
			}else{*/
				isPrimitive = false;
				for(int i = 0; i < size; i++){
					System.out.println(array.getValue(i));
					arrayValue[i] = om.searchObjectInfo((ObjectReference)array.getValue(i));
					if(arrayValue[i] != null){
						arrayValue[i].Linked();
						this.Link();
						//System.out.println("array[" + i + "] "+ arrayValue[i].object.referenceType() );
					}
					
				}
				
				
			/*}
		} catch (ClassNotLoadedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		
	}
	
	public void calculateSize(){
		calculated =true;
		if(directed == 3 || directed == 4){
			ownLength = size + 1;
			ownWidth = 1;
			
			for(int i =0; i < size; i++){
				if(arrayValue[i] != null && arrayValue[i].isCalculated() == false){
					arrayValue[i].calculateSize();
					length += arrayValue[i].getLength();
					if(arrayValue[i].getWidth() > width){
						width = arrayValue[i].getWidth();
					}
				}else{
					length ++;
				}
			}
			width += ownWidth;
		}else{

			ownWidth = size;
			
			for(int i =0; i < size; i++){
				if(arrayValue[i] != null && arrayValue[i].isCalculated() == false){

					arrayValue[i].calculateSize();

					width += arrayValue[i].getWidth();
					if(arrayValue[i].getLength() > length){
						length = arrayValue[i].getLength();
					}
				}else{
					width++;
				}
				
			}
			length += ownLength;
		}

		setSize();
	
	}
	
	
	void setSize(){
		int cent = size / 2;
		//System.out.println(cent);
		if(directed ==3 || directed == 4){
			int child_width = 0;
			for(int i = 0; i < cent; i++){
				if(arrayValue[i] != null){
					up_half += arrayValue[i].getLength();
					if(arrayValue[i].getWidth() > child_width){
						child_width = arrayValue[i].getWidth();
					}
				}else{
					up_half++;
				}
			}
			if(arrayValue[cent] != null){
			up_half += arrayValue[cent].getUpHalf();
			if(arrayValue[cent].getWidth() > child_width){
				child_width = arrayValue[cent].getWidth();
			}
			}
			for(int i = cent + 1; i < size; i++){
				if(arrayValue[i] != null){
					bottom_half += arrayValue[i].getLength();
					if(arrayValue[i].getWidth() > child_width){
						child_width = arrayValue[i].getWidth();
					}
				}else{
					bottom_half++;
				}
			}
			if(arrayValue[cent] != null){
			bottom_half += arrayValue[cent].getBottomHalf();
			}
			if(directed == 3){
				left_half = child_width;
			}else if(directed == 4){
				right_half = child_width;
			}
			
		}else{
			int child_length = 0;
			for(int i = 0; i < cent; i++){
				if(arrayValue[i] != null){
					left_half += arrayValue[i].getWidth();
					if(child_length  > arrayValue[i].getLength()){
						child_length = arrayValue[i].getLength();
					}
				}else{
					//left_half++;
				}
			}
			if(arrayValue[cent] != null){
			left_half += arrayValue[cent].getLeftHalf();
			if(child_length  > arrayValue[cent].getLength()){
				child_length = arrayValue[cent].getLength();
			}
			}
			for(int i = cent + 1; i < size; i++){
				if(arrayValue[i] != null){
					right_half += arrayValue[i].getWidth();
					if(child_length  < arrayValue[i].getLength()){
						child_length = arrayValue[i].getLength();
					}
				}else{
					//right_half++;
				}
			}
			if(arrayValue[cent] != null){
			right_half += arrayValue[cent].getRightHalf();
			}
			
			if(directed >= 0 && directed <= 2){
				up_half = child_length;
			}else{
				bottom_half = child_length;
			}
		}
	}
	
	void setPosion(){
		setPosion(0,0);
	}
	
	void setPosion(int ulx, int uly){
		/*if(set == true){
			return;
		}*/
		
		set = true;
		
		if(with >= 0){
			if(0 <= with && with <= 2){
				px = ulx + getLeftHalf();
				py = uly + getUpHalf() - 1;
			}else if(with == 3){
				px = ulx + getLeftHalf();
				py = uly + getUpHalf();
			}else if(with == 4){
				px = ulx + getLeftHalf() + 1;
				py = uly + getUpHalf();
			}else{
				px = ulx + getLeftHalf();
				py = uly + getUpHalf() + 1;
			}
		}else{

		px = ulx + getLeftHalf();
		py = uly + getUpHalf();
		}
		
		System.out.println(type.name() + index + ":(" + px + "," + py + ") ");
		System.out.println("l:" + getLeftHalf() + " r:"+ getRightHalf() + " u:" + getUpHalf() + " d:" + getBottomHalf());
		System.out.println("ulx:" + ulx + " uly:" + uly);
		System.out.println("width:" + getWidth() + " length:" + getLength());
		System.out.println();
		
		if(directed >= 0 && directed <= 2){
			int nx = 0;
			for(int i = 0; i < size; i++){
				if(arrayValue[i] != null){
					arrayValue[i].setPosion(ulx + nx, py - arrayValue[i].getLength());
					nx += arrayValue[i].getWidth();
				}else{
					//nx++;
				}
			}
		}else if(directed == 3){
			int ny = 0;

			for(int i = 0; i < size; i++){
				if(arrayValue[i] != null){
					arrayValue[i].setPosion(ulx  , uly + ny);
					ny += arrayValue[i].getLength();
				}else{
					ny ++;
				}
			}
			
		}else if(directed == 4){
			int ny = 0;
			for(int i = 0; i < size; i++){
				if(arrayValue[i] != null){
					arrayValue[i].setPosion(px + 1, uly + ny);
					ny += arrayValue[i].getLength();
				}else{
					ny ++;
				}
			}
			
		}else{
			int nx = 0;
			for(int i = 0; i < size; i++){
				if(arrayValue[i] != null){
					arrayValue[i].setPosion(ulx + nx, py + ownLength);
					nx += arrayValue[i].getWidth();
				}else{
					//nx++;
				}
			}
		}
	}
	
	boolean checkObject(Object obj){
		return checkArray(obj);
	}
	
	boolean checkArray(Object obj){
		if(obj.equals(array)){
			return true;
		}
		return false;
	}

	
	public int getSize(){
		return size;
	}

	public boolean isPrimitive() {
		// TODO Auto-generated method stub
		return isPrimitive;
	}
	
	public ArrayReference getArray(){
		return array;
	}

	public int getDirection() {		
		return directed;
	}
	
	public ObjectInfo[] getArrayValue(){
		return arrayValue;
	}
	
	public ObjectInfo getArrayValue(int i){
		return arrayValue[i];
		
	}
	
	public String getFieldName(){
		return fieldName;
	}
	
	public ObjectInfo deepCopy(){
		ObjectInfo tar = new ArrayInfo(this);
		return tar;
		
	}
	
	public boolean isArray(){
		return true;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getLength(){
		return length;
	}
}
