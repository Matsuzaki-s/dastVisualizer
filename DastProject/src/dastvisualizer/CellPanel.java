package dastvisualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.Value;

public class CellPanel extends JPanel {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	private List<CellParts> cp = new ArrayList<CellParts>();
	private int cp_num = 0;
	private ObjectReference tar;
	private int length = 0;

	Dimension maxDim = null;

	CellPanel(ObjectInfo cell) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setOpaque(true);
		setBackground(Color.WHITE);
		setVisible(true);
		LineBorder border = new LineBorder(Color.BLACK);
		setBorder(border);

		tar = cell.getobject();
		try {
			if (!(cell.isArray())) {
				makeObjectCell(cell);
				length = cp_num;
			} else if(((ArrayInfo)cell).isPrimitive() == false){
				makeArrayCell((ArrayInfo) cell);
			}/*else{
				makePrimitiveArray((MyPrimitiveArray) cell);
			}*/

		} catch (Exception e) {
			System.out.println(e + " CellPanel");
		}
		setVisible(true);

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	private void makeObjectCell(ObjectInfo cell) {
		try {
			String str = tar.referenceType().name() + " : "
					+ cell.getIndex();
			cp.add(new CellParts(str,Color.YELLOW));
			cp_num++;

			Map<String, Object> field = cell.getAnotherField();
			for (Iterator<Entry<String, Object>> it = field.entrySet().iterator(); it.hasNext();) {
				Entry<String, Object> target = it.next();
				str = target.getKey() + ": " + target.getValue(); 
				cp.add(new CellParts(str));
				cp_num++;
	
				/*if (f.get(i).type() instanceof PrimitiveType) {
					str = f.get(i).typeName() + " " + f.get(i).name()
							+ " : " + tar.getValue(f.get(i));
					cp[cp_num] = new CellParts(str);
					cp_num++;
				} else if (f.get(i).typeName().equals("String")
						|| f.get(i).typeName().equals("Integer")) {
					str = f.get(i).typeName() + " " + f.get(i).name()
							+ " : " + tar.getValue(f.get(i));
					cp[cp_num] = new CellParts(str);
					cp_num++;
				} else if(f.get(i).get(tar) instanceof Integer){
					str = "Integer" + " " + f.get(i).getName()
							+ " : " + f.get(i).get(tar);
					cp[cp_num] = new CellParts(str);
					cp_num++;
				}else if(f.get(i).get(tar) instanceof String){
					str = "String" + " " + f.get(i).getName()
							+ " : " + f.get(i).get(tar);
					cp[cp_num] = new CellParts(str);
					cp_num++;*/
				}
			
			for (int i = 0; i < 8; i++) {
				if (cell.getAroundFieldName()[i] != null) {
					if (cell.getAround()[i] != null && cell.getAround()[i].isArray() == false) {
						if (cell.getAround()[i] != null) {
							str = cell.getAroundFieldName()[i]
									+ " "
									+ cell.getAround()[i].getobject().referenceType().name() + ":"
									+ (cell.getAround()[i].getIndex());
						
						cp.add(new CellParts(str, Color.ORANGE));
						cp_num++;
						}
					} else if(cell.getAround()[i] != null && cell.getAround()[i].isArray() == true){
						str = cell.getAroundFieldName()[i]
								+ " "
								+ cell.getAround()[i].getobject().referenceType().name();
								//+ ((ArrayInfo) (cell.getAround()[i])).getIndex();
						cp.add(new CellParts(str, Color.ORANGE));
						
						cp_num++;
					} else {

						str = cell.getAroundFieldName()[i] + " " + "null";
						cp.add(new CellParts(str, Color.ORANGE));
						cp_num++;
					}
				}
			}
			

		} catch (Exception e) {
			System.out.println(e + " makeObjectCell");
		}

		adjustSize();
		for (int i = 0; i < cp.size(); i++) {
			add(cp.get(i));
		}

	}

	private void makeArrayCell(ArrayInfo cell) {
		String str = cell.getArray().referenceType().name();
		cp.add(new CellParts(str));
		cp.get(cp_num).setBackground(Color.YELLOW);
		cp_num++;

		CellParts[] idl = new CellParts[cell.getSize()];
		CellParts[] ll = new CellParts[cell.getSize()];

		JPanel IDL = new JPanel();
		JPanel LL = new JPanel();

		if (cell.getDirection() != 3 && cell.getDirection() != 4) {
			IDL.setLayout(new BoxLayout(IDL, BoxLayout.LINE_AXIS));
			LL.setLayout(new BoxLayout(LL, BoxLayout.LINE_AXIS));
		} else {
			IDL.setLayout(new BoxLayout(IDL, BoxLayout.PAGE_AXIS));
			LL.setLayout(new BoxLayout(LL, BoxLayout.PAGE_AXIS));
		}
		IDL.setOpaque(true);
		IDL.setBackground(Color.WHITE);
		for (int i = 0; i < cell.getSize(); i++) {
			idl[i] = new CellParts(Integer.toString(i));
			idl[i].setBackground(Color.ORANGE);
		}

		maxDim = idl[0].getPreferredSize();

		LL.setOpaque(true);
		LL.setBackground(Color.WHITE);
		for (int i = 0; i < cell.getSize(); i++) {
			if (cell.getArray().getValue(i) != null) {
				if (cell.getArrayValue(i) != null) {
					String str2 = cell.getArray().getValue(i).type().name()
							+ ":" + (cell.getArrayValue(i).index);
					ll[i] = new CellParts(str2);
				} else {
					String str2 = "null";
					ll[i] = new CellParts(str2);

				}

			} else {
				ll[i] = new CellParts("null");
			}

			if (ll[i].getPreferredSize().width > maxDim.width) {
				maxDim = ll[i].getPreferredSize();
			}
			ll[i].setBackground(Color.ORANGE);
		}

		for (int i = 0; i < idl.length; i++) {
			idl[i].setMaximumSize(maxDim);
			ll[i].setMaximumSize(maxDim);
			IDL.add(idl[i]);
			LL.add(ll[i]);
		}

		maxDim = IDL.getMaximumSize();
		if (maxDim.width < LL.getMaximumSize().width) {
			maxDim = LL.getMaximumSize();
		}

		IDL.setAlignmentX(0.0f);
		LL.setAlignmentX(0.0f);



		IDL.setVisible(true);
		LL.setVisible(true);

		if (cell.getDirection() >= 0 && cell.getDirection() <= 2) {
			add(IDL);
			add(LL);
			for (int i = 0; i < cp_num; i++) {
				cp.get(i).setAlignmentX(0.0f);
				cp.get(i).setMaximumSize(maxDim);
				add(cp.get(i));
			}
			length = 3;

		} else if (cell.getDirection() >= 6 && cell.getDirection() <= 7) {

			for (int i = 0; i < cp_num; i++) {
				cp.get(i).setAlignmentX(0.0f);
				cp.get(i).setMaximumSize(maxDim);
				add(cp.get(i));
			}
			add(IDL);
			add(LL);
			length = 3;
		} else {
			for (int i = 0; i < cp_num; i++) {
				cp.get(i).setAlignmentX(0.0f);
				cp.get(i).setMaximumSize(maxDim);
				add(cp.get(i));
			}

			JPanel subPanel = new JPanel();
			subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
			subPanel.setOpaque(true);
			subPanel.add(IDL);
			subPanel.add(LL);
			subPanel.setVisible(true);
			add(subPanel);

			cp.get(0).setAlignmentX(0.5f);
			cp.get(0).setMaximumSize(new Dimension(IDL.getMaximumSize().width
					+ LL.getMaximumSize().width,
					cp.get(0).getPreferredSize().height));
			subPanel.setMaximumSize(new Dimension(80, 18*(cell.getSize()+1)));
			this.setMaximumSize(new Dimension(80, 18*(cell.getSize()+1)+2));
			this.setPreferredSize(new Dimension(80, 18*(cell.getSize()+1)+2));
			
			length = cell.getSize();

			
		}

	}
	
	/*private void makePrimitiveArray(MyPrimitiveArray cell){
		String str = cell.getName();
		cp[cp_num] = new CellParts(str);
		cp[cp_num].setBackground(Color.YELLOW);
		cp_num++;

		CellParts[] idl = new CellParts[cell.array.length];
		CellParts[] ll = new CellParts[cell.array.length];

		JPanel IDL = new JPanel();
		JPanel LL = new JPanel();

		if (cell.getDirection() != 3 && cell.getDirection() != 4) {
			IDL.setLayout(new BoxLayout(IDL, BoxLayout.LINE_AXIS));
			LL.setLayout(new BoxLayout(LL, BoxLayout.LINE_AXIS));
		} else {
			IDL.setLayout(new BoxLayout(IDL, BoxLayout.PAGE_AXIS));
			LL.setLayout(new BoxLayout(LL, BoxLayout.PAGE_AXIS));
		}
		IDL.setOpaque(true);
		IDL.setBackground(Color.WHITE);
		for (int i = 0; i < cell.array.length; i++) {
			idl[i] = new CellParts(Integer.toString(i));
			idl[i].setBackground(Color.white);
		}


		LL.setOpaque(true);
		LL.setBackground(Color.WHITE);
		for (int i = 0; i < cell.array.length; i++) {
			ll[i] = new CellParts(Integer.toString(cell.array[i]));



			ll[i].setBackground(Color.white);
		}

		for (int i = 0; i < idl.length; i++) {
			idl[i].setMaximumSize(new Dimension(30,18));
			ll[i].setMaximumSize(new Dimension(30,18));
			IDL.add(idl[i]);
			LL.add(ll[i]);
		}



		IDL.setAlignmentX(0.0f);
		LL.setAlignmentX(0.0f);

	

		IDL.setVisible(true);
		LL.setVisible(true);

		if (cell.getDirection() >= 0 && cell.getDirection() <= 2) {
			IDL.setMaximumSize(new Dimension(30 * cell.array.length,18));
			LL.setMaximumSize(new Dimension(30*cell.array.length, 18));
			add(IDL);
			add(LL);
			for (int i = 0; i < cp_num; i++) {
				cp[i].setAlignmentX(0.0f);
				cp[i].setMaximumSize(new Dimension(cell.array.length * 30,18));
				add(cp[i]);
			}
			
			length = 3;

		} else if (cell.getDirection() >= 6 && cell.getDirection() <= 7) {
			IDL.setMaximumSize(new Dimension(30 * cell.array.length,18));
			LL.setMaximumSize(new Dimension(30*cell.array.length, 18));

			for (int i = 0; i < cp_num; i++) {
				cp[i].setAlignmentX(0.0f);
				cp[i].setMaximumSize(new Dimension(cell.array.length * 30,18));
				add(cp[i]);
			}
			add(IDL);
			add(LL);
			length = 3;
		} else {
			for (int i = 0; i < cp_num; i++) {
				cp[i].setAlignmentX(0.0f);
				cp[i].setMaximumSize(new Dimension(60,18));
				add(cp[i]);
			}

			JPanel subPanel = new JPanel();
			subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
			subPanel.setOpaque(true);
			subPanel.add(IDL);
			subPanel.add(LL);
			subPanel.setVisible(true);
			add(subPanel);

			cp[0].setAlignmentX(0.5f);
			IDL.setMaximumSize(new Dimension(30 ,18*cell.array.length));
			LL.setMaximumSize(new Dimension(30, 18*cell.array.length));
			subPanel.setMaximumSize(new Dimension(60, 18*cell.array.length));
			this.setMaximumSize(new Dimension(60, 18*(cell.array.length +1)+5));
			this.setPreferredSize(new Dimension(60, 18*(cell.array.length +1)+5));

			length = cell.array.length;
		}
	}*/

	private void adjustSize() {
		Dimension dim;
		for (int i = 0; i < cp_num; i++) {
			dim = cp.get(i).getPreferredSize();

			if (maxDim == null || maxDim.width < dim.width) {
				maxDim = dim;
			}
		}

		for (int i = 0; i < cp_num; i++) {
			cp.get(i).setMaximumSize(maxDim);
		}
	}

	public int getCellWidth() {
		return this.getPreferredSize().width;
	}

	public int getCellLength() {
		return this.length * 18 ;
	}

}
