package dastvisualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

public class Draw extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2426900381722536632L;
	private int CellWidth = 0;
	private int CellLength = 0;
	
	private Map<ObjectInfo, CellPanel> cellpanel = new HashMap<ObjectInfo, CellPanel>();
	private List<ObjectInfo> targetObject;
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		connect(g2);
	}
	
	Draw(List<ObjectInfo> tar){
		
		
		
		super();
		System.out.println();
		setLayout(null);
		setBackground(Color.WHITE);
				
		setPanel(tar);
	
		this.setPreferredSize(new Dimension(2000,2000));
		revalidate();
		this.setVisible(true);
	}
	
	private void setPanel(List<ObjectInfo> tar){
		this.targetObject = tar;
		cellpanel.clear();
		
		for(Iterator<ObjectInfo> it = tar.iterator(); it.hasNext();){
			ObjectInfo oin = (ObjectInfo)it.next();
			//System.out.println(oin.object.toString());
			//System.out.println(oin.isLinked() + " "+ oin.hasLink());
			if(oin.isLinked() == true || oin.hasLink() == true){
				cellpanel.put(oin ,new CellPanel(oin));
				
				if(cellpanel.get(oin).getPreferredSize().width > CellWidth && oin.isArray() == false){
					CellWidth = cellpanel.get(oin).getPreferredSize().width;
				}
				if(cellpanel.get(oin).getPreferredSize().height > CellLength && oin.isArray() == false){
					CellLength = cellpanel.get(oin).getPreferredSize().height;

				}
			}
		}
		
		for(Iterator<Entry<ObjectInfo, CellPanel>> it = cellpanel.entrySet().iterator(); it.hasNext();){
			Entry<ObjectInfo, CellPanel> entry = it.next();
			ObjectInfo object = entry.getKey();
			CellPanel panel = entry.getValue();
			try{
			if(panel != null ){
				if(panel.getCellLength() > CellLength /2){
					panel.setBounds(object.getPx() * (CellWidth+ 20) + 20  , object.getPy() * (CellLength+ 40) + 20, panel.getMaximumSize().width,panel.getMaximumSize().height );
				}else{
					panel.setBounds(object.getPx() * (CellWidth+ 20) + 20  , object.getPy() * (CellLength+ 40) +(CellLength /2) - panel.getCellLength()/2, panel.getMaximumSize().width,panel.getMaximumSize().height );
				}
				add(panel);
			}
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}
	
	void connect(Graphics2D g){
		g.setColor(Color.BLUE);
		for(Iterator<Entry<ObjectInfo, CellPanel>> it = cellpanel.entrySet().iterator(); it.hasNext();){
			Entry<ObjectInfo, CellPanel> ent = it.next();
			
			 //|| if((MyArray)targetObject.get(i)).isPrimitive() ==false)
				connectCell(ent, g);
			
		}
	}
	
	void connectCell(Entry<ObjectInfo, CellPanel> ent,Graphics2D g){
		ObjectInfo oin = ent.getKey();
		CellPanel cell = ent.getValue();
		ObjectInfo around[] = oin.getAround();
		CellPanel tar;
		if(!(oin instanceof ArrayInfo)){
		for(int i = 0;i <= 7;i++ ){ 
			if(around[i] != null && ((tar = searchAroundPanel(around[i]) )!= null)){
				
				if(i == 0 || i == 2){
					if(oin.getPy() > around[i].getPy()){
						drawArrow(g, cell.getX() + cell.getCellWidth() / 2, cell.getY(), tar.getX() + tar.getCellWidth() / 2, 
								tar.getY() + tar.getCellLength() + 3, 5);
				
					}else{
						//
						//
					}
				}else if(i == 1){
					if(oin.getPy() > around[i].getPy()){
							drawArrow(g, cell.getX() + cell.getCellWidth() / 2, cell.getY(), cell.getX() + cell.getCellWidth() / 2,tar.getY() + tar.getCellLength(), 5);
					}else{
						//
						//
					}
						
				}else if(i == 3){
					if(oin.getPx() > around[i].getPx()){
						drawArrow(g, cell.getX(), cell.getY() + cell.getCellLength() / 2,  tar.getX() + tar.getCellWidth(), cell.getY() +  cell.getCellLength() / 2, 5);
					}else{
						g.drawLine(cell.getX(), cell.getY() + cell.getCellLength() / 2,cell.getX()-10 , cell.getY() + cell.getCellLength() / 2);
						if(oin.getBottomHalf() > around[i].getBottomHalf()){
							g.drawLine(cell.getX()-10 , cell.getY() + cell.getCellLength() / 2,cell.getX()-10 ,cell.getY() + cell.getCellLength() + 10);
							g.drawLine(cell.getX()-10 ,cell.getY() + cell.getCellLength() + 10, tar.getX() + tar.getCellWidth() + 10, cell.getY() + cell.getCellLength() + 10);
							g.drawLine( tar.getX() + tar.getCellWidth() + 10, cell.getY() + cell.getCellLength() + 10 ,tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2 );
							drawArrow(g,tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2 , tar.getX() + tar.getCellWidth(),tar.getY() + tar.getCellLength() / 2 , 5);
						}else{
							g.drawLine(cell.getX()-10 , cell.getY() + cell.getCellLength() / 2,cell.getX()-10 ,cell.getY() + tar.getCellLength() + 10);
							g.drawLine(cell.getX()-10 ,cell.getY() + tar.getCellLength() + 10, tar.getX() + tar.getCellWidth() + 10,cell.getY() + tar.getCellLength() + 10);
							g.drawLine( tar.getX() + tar.getCellWidth() + 10,cell.getY() + tar.getCellLength() + 10,tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2 );
							drawArrow(g, tar.getX() + tar.getCellWidth() + 10, tar.getY() + tar.getCellLength() / 2 , tar.getX() + tar.getCellWidth(),tar.getY() + tar.getCellLength() / 2, 5);
						}
															
					}
				}else if(i == 4){
					if(oin.getPx() < around[i].getPx()){
						drawArrow(g, cell.getX() + cell.getCellWidth(), cell.getY() + cell.getCellLength() / 2, tar.getX(), cell.getY() + cell.getCellLength() / 2, 5);
					}else{
						g.drawLine(cell.getX() + cell.getCellWidth(), cell.getY() + cell.getCellLength() / 2,cell.getX()+cell.getCellWidth() + 10 , cell.getY() + cell.getCellLength() / 2);
						if(oin.getBottomHalf() >= around[i].getBottomHalf()){
							g.drawLine(cell.getX()+cell.getCellWidth() + 10 , cell.getY() + cell.getCellLength() / 2,cell.getX()+cell.getCellWidth() + 10 ,cell.getY() + cell.getCellLength() + 10);
							g.drawLine(cell.getX()-10 ,cell.getY() + cell.getCellLength() + 10, tar.getX()  - 10, cell.getY() + cell.getCellLength() + 10);
							g.drawLine( tar.getX() - 10, cell.getY() + cell.getCellLength() + 10,tar.getX() - 10, tar.getY() + tar.getCellLength() / 2 );
							drawArrow(g,tar.getX() - 10, tar.getY() + tar.getCellLength() / 2, tar.getX(),tar.getY() + tar.getCellLength() / 2 , 5);
						}else{
							g.drawLine(cell.getX()+cell.getCellWidth() + 10 , cell.getY() + cell.getCellLength() / 2,cell.getX()+cell.getCellWidth() + 10,cell.getY() + tar.getCellLength() + 10);
							g.drawLine(cell.getX()+10 ,cell.getY() + tar.getCellLength() + 10, tar.getX() - 10,cell.getY() + tar.getCellLength() + 10);
							g.drawLine(tar.getX() - 10,cell.getY() + tar.getCellLength() + 10,tar.getX()-10 , tar.getY() + tar.getCellLength() / 2 );
							drawArrow(g,tar.getX()-10 , tar.getY() + tar.getCellLength() / 2 , tar.getX(),tar.getY() + tar.getCellLength() / 2, 5);
						}
					}
				}else if (i == 5 || i == 7){
					if(oin.getPy() < around[i].getPy()){
						drawArrow(g, cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength(), tar.getX() + tar.getCellWidth() / 2, tar.getY() -3, 5);
					}else{
						
					}
				}else{
					if(oin.getPy() < around[i].getPy()){
						if(oin.getPx() == around[i].getPx()){
						drawArrow(g, cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength(), cell.getX() + cell.getCellWidth() / 2, tar.getY()-3, 5);
						}else{
							g.drawLine(cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength(), cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10);
							g.drawLine(cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10, tar.getX() + tar.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10);
							drawArrow(g, tar.getX() + tar.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10, tar.getX() + tar.getCellWidth() / 2 ,tar.getY(), 5);
						}
						
					}else{
						g.drawLine(cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength(), cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10);
						g.drawLine(cell.getX() + cell.getCellWidth() / 2, cell.getY() + cell.getCellLength() + 10, tar.getX() - 10, cell.getY() + cell.getCellLength() + 10);
						g.drawLine(tar.getX() - 10, cell.getY() + cell.getCellLength() + 10, tar.getX() - 10, tar.getY() - 10);
						g.drawLine(tar.getX() - 10, tar.getY() - 10, tar.getX() + tar.getCellWidth() / 2, tar.getY() - 10);
						drawArrow(g, tar.getX() + tar.getCellWidth() / 2, tar.getY() - 10, tar.getX() + tar.getCellWidth() / 2, tar.getY(), 5);
					}
				}
			}
		}
		}else{
			ArrayInfo array = (ArrayInfo)(oin);
			ObjectInfo[] arrayValue = array.getArrayValue();
			for(int i = 0; i < arrayValue.length; i++){
				if(arrayValue[i] != null && (tar = searchAroundPanel(arrayValue[i]) )!= null){
					if(array.getDirection() >= 5 && array.getDirection() <= 7){
						drawArrow(g, cell.getX() +(cell.getWidth() / arrayValue.length /2) +(cell.getWidth() / arrayValue.length * i), cell.getY() + cell.getCellLength(), tar.getX() + tar.getCellWidth() /2, tar.getY(), 5);
					}else if(array.getDirection() >= 0 && array.getDirection() <= 2){
						drawArrow(g, cell.getX() +(cell.getWidth() / arrayValue.length /2) +(cell.getWidth() / arrayValue.length * i), cell.getY(), tar.getX() + tar.getCellWidth() /2, tar.getY() + tar.getCellLength(), 5);
					}else if(array.getDirection() == 4){
						if(oin.getPx() < arrayValue[i].getPx()){
							drawArrow(g, cell.getX() +cell.getWidth(), cell.getY() + 27 + 18 *i , tar.getX(), tar.getY() + tar.getCellLength()/2, 5);
						}else{
							g.drawLine(cell.getX() +cell.getWidth(), cell.getY() + 27 + 18 *i , cell.getX() +cell.getWidth() + 10, cell.getY() + 27 + 18 *i );
							g.drawLine(cell.getX() +cell.getWidth() + 10, cell.getY() + 27 + 18 *i , cell.getX() +cell.getWidth() + 10, cell.getY() + 27 + 18 *i + tar.getY() + tar.getCellLength() + 5);
							g.drawLine(cell.getX() +cell.getWidth() + 10, cell.getY() + 27 + 18 *i + tar.getY() + tar.getCellLength() + 5, tar.getX() + tar.getCellWidth()/2, cell.getY() + 27 + 18 *i + tar.getY() + tar.getCellLength() + 5);
							drawArrow(g, tar.getX() + tar.getCellWidth()/2, cell.getY() + 27 + 18 *i + tar.getY() + tar.getCellLength() + 5, tar.getX() + tar.getCellWidth()/2, tar.getY() + tar.getCellLength(), 5);
						}
					}else{
						drawArrow(g, cell.getX(), cell.getY() + 27 + 18 *i , tar.getX() + tar.getCellWidth(), tar.getY() + tar.getCellLength()/2, 5);
					}
				}
					
			}
		}

	}
	
	CellPanel searchAroundPanel(ObjectInfo tar){
		for(int i = 0; i < targetObject.size(); i++){
			if(targetObject.get(i) == tar){
				return cellpanel.get(tar);
			}
		}
		return null;
	}
	
	/*http://www.asahi-net.or.jp/~QC8K-STU/java/ExGraphics.java*/
	public void drawArrow(Graphics2D g,int x0,int y0,int x1,int y1,int l){
	    double theta;
	    int x,y;
	    double dt = Math.PI / 6.0;
	   
	    
	   
	    theta = Math.atan2((double)(y1-y0),(double)(x1-x0));
	    g.drawLine(x0,y0,x1,y1);
	    x = x1-(int)(l*Math.cos(theta-dt));
	    y = y1-(int)(l*Math.sin(theta-dt));
	    g.drawLine(x1,y1,x,y);
	    x = x1-(int)(l*Math.cos(theta+dt));
	    y = y1-(int)(l*Math.sin(theta+dt));
	    g.drawLine(x1,y1,x,y);
	  }

	public void reDraw(List<ObjectInfo> cell){
		
	}

}
