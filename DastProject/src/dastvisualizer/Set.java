package dastvisualizer;

import java.util.Iterator;
import java.util.List;

public class Set {

	static final int up_length = 0;
	static final int cent_length = 1;
	static final int down_length = 2;
	static final int up_left_width = 3;
	static final int up_cent_width = 4;
	static final int up_right_width = 5;
	static final int left_width = 6;
	static final int right_width = 7;
	static final int down_left_width = 8;
	static final int down_cent_width = 9;
	static final int down_right_width = 10;



	Set(List<ObjectInfo> tar) {
		
		
		for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
			ObjectInfo oin = it.next();
			if(oin.isLinked() == false && oin.hasLink() == true){
				oin.calculateSize();
				oin.setPosion();
				
				break;
			}
		}

		/*try{
		for(int i= 0; i < cell.length; i++){
			if(cell[i] != null && cell[i].isLinked() == false && cell[i].hasLink() == true && cell[i].isCalculated() == false){
				cell[i].calculateSize();
				ReadFile.makeMap(cell[i]);
				cell[i].setPosion();
				ReadFile.checkMap();
			}
		}
		
		}catch(Exception e){
			System.out.println(e);
		}*/
		
	}


	

}