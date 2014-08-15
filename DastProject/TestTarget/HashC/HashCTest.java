
import java.io.FileInputStream;
import java.io.FileNotFoundException;



public class HashCTest {

	HashC HC = new HashC(5);
	
	public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub


		new HashCTest();


	}
	
	HashCTest(){

		HC.insert(new MyKey("A"), null);
		HC.insert(new MyKey("fa3"), null);
		HC.insert(new MyKey("TOA"), null);
		HC.insert(new MyKey("Fwr"), null);
		HC.insert(new MyKey("POA"), null);
		
	}

}
