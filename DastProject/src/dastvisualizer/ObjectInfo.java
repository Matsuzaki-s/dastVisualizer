package dastvisualizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;






import java.util.Map.Entry;

import com.sun.jdi.ArrayType;
import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.event.ModificationWatchpointEvent;


public class ObjectInfo {
	
	static final int up_length = 0;
	static final int cent_length = 1;
	static final int down_length = 2;
	static final int up_left_width = 3;
	static final int up_cent_width = 4;
	static final int up_right_width = 5;
	static final int cent_left_width = 6;
	static final int cent_right_width = 7;
	static final int down_left_width = 8;
	static final int down_cent_width = 9;
	static final int down_right_width = 10;
	
	protected ObjectReference object;
	protected boolean isPrimitive = false;
	protected ReferenceType  type;
	private ClassDefinition def;
	protected int index;
	//private int num_linked = 0;
	//private int num_array = 0;
	
	protected boolean set = false;
	protected boolean calculated = false;
	private boolean linked = false;
	private boolean link = false;
	
	private ObjectInfo[] around = new ObjectInfo[8];
	
	public boolean copy;
	
	private ArrayInfo[] aroundArray = new ArrayInfo[8]; 
	private String[] aroundArrayName = new String[8];
	private int with = -1;
	private String[] aroundFieldName = new String[8];
	private Map<String, Object> anotherField = new HashMap<String,Object>();
	private int around_size[]  = { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private int width = 0;
	private int length = 0;
	protected int ownLength = 1;
	private int up_half = 0;
	private int bottom_half = 0;
	private int left_half= 0;
	private int right_half = 0;
 	
	protected int px;
	protected int py;

	//’Ç‰Á•ª
	protected ObjectManager om;
	private int i;
	private String primitiveType;
	
	ObjectInfo(ObjectReference tar,Type type, ClassDefinition def, ObjectManager om){
		object = tar;
		this.om = om;
		this.type = (ReferenceType)type;
		this.def = def;
		if(def != null){
		def.addObject();
		index = def.getNumObject();
		}
		this.copy = false;
		
	}
	
	ObjectInfo(ObjectInfo source){
		object = source.object;
		this.om = source.om;
		this.type = source.type;
		this.def = source.def;
		index = source.index;
		this.copy = true;
		this.aroundFieldName = source.aroundFieldName;
		this.anotherField = source.anotherField;

		
	}
	
	ObjectInfo(Value v){
		isPrimitive = true;
		if(v instanceof IntegerValue){
			i = ((IntegerValue)v).value();
			primitiveType = "int";
		}
		
	}
	
	public void setField(){
		Map<String, Integer> fieldDirection = def.getFields();
		List<Field> fields = type.fields();
		for(Iterator<Field> it = fields.iterator(); it.hasNext();){
			
			Field tar = (Field)it.next();
			Integer direction = fieldDirection.get(tar.name());
			if(direction == null){
				direction = fieldDirection.get(tar.name() + "[]");
			}
			if(direction != null && direction <= 7 && aroundFieldName[direction] == null){
				aroundFieldName[direction] = tar.name();
			}else if(direction != null && direction <= 7 && aroundFieldName[direction] != null && aroundArrayName[direction] == null){
				aroundArrayName[direction] = tar.name();
			}else{
				setAnotherField(tar.name(), object.getValue(tar));
			}
		}
	}
	
	public void changeField(ModificationWatchpointEvent event){
		ObjectReference object = event.object();
		Field field = event.field();
		Value value = event.valueToBe();
		Type type = null;
		if(value != null){
			type = value.type();
		}
			int direction = isAroundField(field);
			if (direction >= 0) {
				around[direction] = om.searchObjectInfo((ObjectReference) value);
			} else {
				setAnotherField(field.name(), value);
			}
			
		
	}
	
	public void changeField(Field field, Value value) {
		
		int direction = isAroundField(field); 
		if(direction >= 0){
			around[direction] = om.searchObjectInfo((ObjectReference)value);
		}else{
			
			setAnotherField(field.name(), value);
		}
	}

	public void setAnotherField(String name, Value value){
		if(value instanceof IntegerValue){
			anotherField.put("int " + name, ((IntegerValue)value).value());
		}else if(value instanceof StringReference){
			anotherField.put("String " + name, value);
		}else if(value instanceof ObjectReference){
			if(((ObjectReference) value).referenceType().name().equals("java.lang.Integer")){
				anotherField.put("Integer " + name,  (ObjectReference)value);
			}
		}
	}

	private int isAroundField(Field field){
		for(int i = 0; i <= 7; i++){
			if(aroundFieldName[i] == field.name()){
				return i;
			}
		}
		/*for(int i = 0; i <= 7; i++){
			if(aroundFieldName[i] != null && aroundFieldName[i].equals(field.name() + "[]")){
				return i;
			}
		}*/
		return -1;
	}
	
	public void setLink() throws IllegalArgumentException, IllegalAccessException{
		List<Field> fields = object.referenceType().fields();
		Map<String, Integer> fieldDirection = def.getFields();
		
		/*for(Iterator it = fieldDirection.entrySet().iterator(); it.hasNext();){
			Map.Entry<String, Integer> entry = (Entry<String, Integer>) it.next();
			System.out.println(entry.getKey() + " " + entry.getValue());
		}*/
		
		for(Iterator<Field> it = fields.iterator(); it.hasNext(); ){
			Field field = (Field)it.next();
			Integer direction = fieldDirection.get(field.name());
			if(direction == null){
				 direction = fieldDirection.get(field.name() + "[]");
			}
			if(direction != null && object.getValue(field) != null){
				if(around[direction] == null){
					around[direction] = om.searchObjectInfo((ObjectReference)object.getValue(field));
					if(field.name() != aroundFieldName[direction]){
						String copy = aroundArrayName[direction];
						aroundArrayName[direction] = aroundFieldName[direction];
						aroundFieldName[direction] = copy;							
					}
				}else{
					ObjectInfo obim = om.searchObjectInfo((ObjectReference)object.getValue(field));
					if(obim.isArray() && ((ArrayInfo)obim).isPrimitive()){
						aroundArray[direction] = (ArrayInfo)obim;
						if(field.name() != aroundArrayName[direction]){
							String copy = aroundArrayName[direction];
							aroundArrayName[direction] = aroundFieldName[direction];
							aroundFieldName[direction] = copy;							
						}
					}else if(around[direction].isArray() && ((ArrayInfo)around[direction]).isPrimitive()){
						aroundArray[direction] = (ArrayInfo)around[direction];
						around[direction] = obim;
						if(field.name() != aroundFieldName[direction]){
							String copy = aroundArrayName[direction];
							aroundArrayName[direction] = aroundFieldName[direction];
							aroundFieldName[direction] = copy;							
						}
					}
				}
				if(around[direction] != null){
					around[direction].Linked();
					this.Link();
				}
				if(aroundArray[direction] != null){
					aroundArray[direction].Linked();
					this.Link();
				}
			}
		}
	}
		
	
	
	
	boolean checkObject(Object obj){
		if(obj == object){
			return true;
		}
		return false;
	}
	
	public boolean sameObject(ObjectReference tar){
		System.out.println(tar +" "+object);
		return tar.equals(object);
	}
	
	/*void setArray(String name,Object[] a){
		ClassDefinition.Value v = type.SearchValue(name + "[]");
		if(v != null){
			if(around[v.direction]== null){
			around[v.direction] = new MyArray(this, a, v.direction, v.name);
			this.Link();
			}else{
				if(around[v.direction].isArray() && a.getClass().getSimpleName().equals("Integer[]") == false){
					aroundArray[v.direction] = (MyArray)around[v.direction];
					around[v.direction] = new MyArray(this, a, v.direction, v.name);
					aroundFieldName[v.direction] = v.name;
				}else{
					aroundArray[v.direction] = new MyArray(this, a, v.direction, v.name);
				}
				around[v.direction].setWith(v.direction);
			}
		}
		
	}
	
	void setArray(String name,int[] a){
		ClassDefinition.Value v = type.SearchValue(name + "[]");
		if(v != null){
			if(around[v.direction]== null){
			around[v.direction] = new MyPrimitiveArray(this, a, v.direction, v.name);
			this.Link();
			}else{
					aroundArray[v.direction] = new MyPrimitiveArray(this, a, v.direction, v.name);
			}
				around[v.direction].setWith(v.direction);
		}
	}
		
	*/
	
	/*public void reset(){
		set = false;
		calculated = false;
		linked = false;
		link = false;
		px = 0;
		py = 0;
		
		//resetAround();
		
	}*/
	
	public void calculateSize(){
		calculated = true;
		//ownLength += anotherField.size();
		around_size[cent_length] = ownLength; 
		for(int i = 5; i <= 7; i++){
			if(around[i] != null && around[i].calculated == false) {
				around[i].calculateSize();
				if(around_size[down_length] <= around[i].getLength()){
					around_size[down_length] = around[i].getLength();
					
				}
				around_size[i + 3] += around[i].getWidth();
			}
		}
		
		if(aroundArray[5] != null || aroundArray[6] != null || aroundArray[7] != null){
			around_size[down_length] ++;
		}
		
		if((around[5] != null || around[7] != null) &&around[6] == null){
			around_size[down_cent_width] += 1;
		}
			
		for(int i = 4; i >= 3; i--) {
			
			if (around[i] != null && around[i].calculated == false) {
				around[i].calculateSize();
				if (around_size[cent_length] < around[i].getLength()) {
					around_size[cent_length] = around[i].getLength();
					
				}
				around_size[i + 3] = around[i].getWidth();
			}
		}
		
		if(aroundArray[3] != null){
			around_size[cent_left_width]++;
		}
		if(aroundArray[4] != null){
			around_size[cent_right_width]++;
		}
		
		for (int i = 0; i <= 2; i++) {
			if (around[i] != null && around[i].calculated == false) {
				around[i].calculateSize();
				if (around_size[up_length] <= around[i].getLength()) {
					around_size[up_length] = around[i].getLength();
				}
				around_size[i + 3] += around[i].getWidth();

			}
		}
		
		if(aroundArray[0] != null || aroundArray[1] != null || aroundArray[2] != null){
			around_size[up_length] ++;
		}
		
		if ((around[0] != null || around[2] != null) && around[1] == null)  {
			around_size[up_cent_width] += 1;
		}

		
		setSize();
		
		return;
	}
	
	void setSize(){
		int left = Math.max(Math.max(around_size[up_left_width], around_size[down_left_width]), around_size[cent_left_width]);
		int right =  Math.max(Math.max(around_size[up_right_width], around_size[down_right_width]), around_size[cent_right_width]);
		width = left + right + Math.max(Math.max(around_size[up_cent_width], around_size[down_cent_width]), 1);
		
		
		int cent_left = 0;
		int cent_right = 0;
		if(around[1] != null){
			cent_left = around[1].getLeftHalf();
			cent_right = around[1].getRightHalf();
		}
		if(around[6] != null){
			if(around[6].getLeftHalf() > cent_left){
				cent_left = around[6].getLeftHalf();
			}
			if(around[6].getRightHalf() > cent_right){
				cent_right = around[6].getRightHalf();
			}
		}
		left_half = left + cent_left;
		right_half = right + cent_right;
		
		length = around_size[up_length] + around_size[cent_length] + around_size[down_length] ;

		
		int cent_up = 0;
		int cent_bottom = 0;
		if(around[3] != null){
			cent_up = around[3].getUpHalf();
			cent_bottom = around[3].getBottomHalf();
		}
		if(around[4] != null){
			if(around[4].getUpHalf() > cent_up){
				cent_up = around[4].getUpHalf();
			}
			if(around[4].getBottomHalf() > cent_bottom){
				cent_bottom = around[4].getBottomHalf();
			}
		}
		up_half = around_size[up_length] + cent_up;
		bottom_half = around_size[down_length] + cent_bottom;
		

}
	
	void setPosion(){
		setPosion(0, 0);
	}
	
	void setPosion(int ulx, int uly){
		if(set == true){
			return;
		}
		
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
		
		//ReadFile.setMap(this);
		}
		System.out.println(type.name() + index + ":(" + px + "," + py + ") ");
		System.out.println("l:" + getLeftHalf() + " r:"+ getRightHalf() + " u:" + getUpHalf() + " d:" + getBottomHalf());
		System.out.println("ulx:" + ulx + " uly:" + uly);
		System.out.println("width:" + getWidth() + " length:" + getLength());
		System.out.println();
		
		if(around[5] != null){
			int cw = 0;
			if(around[1] != null){
				cw = around[1].getLeftHalf();
			}
			if(around[6] != null && around[6].getLeftHalf() > cw){
				cw = around[6].getLeftHalf();
			}
			if(around[3] == null){
				around[5].setPosion(px - around[5].getWidth() - cw, py + ownLength);
			}else{
				around[5].setPosion(px - around[5].getWidth() - cw, py + around[3].getBottomHalf() + ownLength);
			}
			
			if(aroundArray[5] != null){
				aroundArray[5].setPxy(around[5].getPx(), around[5].getPy() -1);
			}
		}
		
		if(around[6] != null){
			around[6].setPosion(px - around[6].getLeftHalf() , py + ownLength);
			
			if(aroundArray[6] != null){
				aroundArray[6].setPxy(around[6].getPx(), around[6].getPy() - 1);
				
			}
			
		}
		
		if(around[7] != null){
			int cw = 0;
			
			if(around[1] != null){
				cw = around[1].getRightHalf();
			}
			if(around[6] != null && around[6].getRightHalf() > cw){
				cw = around[6].getRightHalf();
			}
			
			if(around[4] == null){
				around[7].setPosion( px + 1 + cw, py + ownLength);
			}else{
				around[7].setPosion(px + 1  + cw, py + around[4].getBottomHalf() + ownLength );
			}
			
			if(aroundArray[7] != null){
				aroundArray[7].setPxy(around[7].getPx(), around[7].getPy() - 1);
			
			}
		}
		
		
		if(around[4] != null){
			int cw = 0;
			
			if(around[1] != null && around[4].up_half > 0){
				cw = around[1].getRightHalf();
			}
			if(around[6] != null && around[6].getRightHalf() > cw && around[4].bottom_half > 0){
				cw = around[6].getRightHalf();
			}
			
			//cw = 0; //ˆêŽž—lŽqŒ©
			around[4].setPosion(px + 1 + cw, py - around[4].getUpHalf());
			
			if(aroundArray[4] != null){
				aroundArray[4].setPxy(around[4].getPx() - 1, around[4].getPy());
				
			}
		}
		if(around[3] != null){
			int cw = 0;
			if(around[1] != null && around[3].up_half > 0){
				cw = around[1].getLeftHalf();
			}
			if(around[6] != null && around[6].getLeftHalf() > cw && around[3].bottom_half > 0 ){
				cw = around[6].getLeftHalf();
			}
			
			//cw = 0; //ã‚Æ“¯‚¶‚­
			
			
			
			
		if(aroundArray[3] != null){
				around[3].setPosion(px - around[3].getWidth() - cw - 1, py - around[3].getUpHalf());
				
							
				aroundArray[3].setPxy(around[3].getPx() + 1, around[3].getPy());

			}else{	
				around[3].setPosion(px - around[3].getWidth() - cw, py - around[3].getUpHalf());	
			}
		}
		
		

		
		if(around[2] != null){
			int cw = 0;
			if(around[1] != null){
				cw = around[1].getRightHalf();
			}
			if(around[6] != null && around[6].getRightHalf() > cw){
				cw = around[6].getRightHalf();
			}
			if(around[4] == null){
				around[2].setPosion(px + 1 + cw, py - around[2].getLength());
			}else{
				around[2].setPosion(px + 1 + cw, py - around[4].getUpHalf() - around[2].getLength());
			}
			if(aroundArray[2] != null){
				aroundArray[2].setPxy(around[2].getPx(), around[2].getPy() -1);
			
			}
		}
		
		if(around[1] != null){
			around[1].setPosion(px - around[1].getLeftHalf(), py - around[1].getLength());
			if(aroundArray[1] != null){
				aroundArray[1].setPxy(around[1].getPx(), around[1].getPy() -1);
			
			}
		}
		
		
		if(around[0] != null){
			int cw = 0;
			if(around[1] != null){
				cw = around[1].getLeftHalf();
			}
			if(around[6] != null && around[6].getLeftHalf() > cw){
				cw = around[6].getLeftHalf();
			}
			if(around[3] == null){
				around[0].setPosion(px - around[0].getWidth() - cw, py - around[0].getLength());
			}else{
				around[0].setPosion(px - around[0].getWidth() - cw , py - around[0].getLength() - around[3].getUpHalf());
			}
			if(aroundArray[0] != null){
				aroundArray[0].setPxy(around[0].getPx(), around[0].getPy() - 1);
			
			}
		}
		
	}

	public int getLength() {
		return length;
	}
	
	public int getOwnLength(){
		return ownLength;
	}

	public Map<String, Object> getAnotherField() {
		return anotherField;
	}

	public void setAnotherField() {
		
	}

	public ObjectInfo[] getAround() {
		return around;
	}

	public void setAround(ObjectInfo[] around) {
		this.around = around;
	}

	public String[] getAroundFieldName() {
		return aroundFieldName;
	}

	public void setAroundFieldName(String[] aroundFieldName) {
		this.aroundFieldName = aroundFieldName;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getUpHalf(){
		return this.up_half;
	}
	
	public int getBottomHalf(){
		return this.bottom_half;
	}
	
	public int getLeftHalf(){
		return this.left_half;
	}
	
	public int getRightHalf(){
		return this.right_half;
	}
	
	public boolean isCalculated(){
		return this.calculated;
	}
	
	public boolean isArray(){
		return false;
	}
	

	
	public int getPx(){
		return px;
	}
	public int getPy(){
		return py;
	}
	
	public ObjectReference getobject(){
		return object;
	}

	public void slideUp(){
		this.py--;
	}
	
	public void slideLeft(){
		this.px--;
	}

	public void setWith(int d){
		with = d;
	}

	/*private void setAroundFieldName(){
		ClassDefinition.Value[] value = type.getValue();
		for(int i =0; i< value.length; i++){
			if(value[i] != null){
				aroundFieldName[value[i].direction] = value[i].name;
			}
		}

	}*/
	
	public void Linked(){
		this.linked = true;
	}
	
	public void Link() {
		this.link = true;
	}
	
	public boolean isLinked(){
		return this.linked;
	}
	
	public boolean hasLink(){
		return this.link;
	}
	
	/*public void resetAround(){
		for(int i = 0 ; i <= 7;i++){
			around[i] = null;
			aroundArray[i] = null;
		}
	}*/

	public int getIndex(){
		return index;
	}
	
	public ObjectInfo deepCopy(){
		ObjectInfo tar = new ObjectInfo(this);
		return tar;
		
	}

	
}
