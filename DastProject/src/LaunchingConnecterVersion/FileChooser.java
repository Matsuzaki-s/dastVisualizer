package LaunchingConnecterVersion;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class FileChooser extends JFrame implements ActionListener{
	
	File file;
	JTextField jtf;
	Connect from;
	
	FileChooser(final Connect from){
		this.from = from;
		JButton button = new JButton("file select");
		button.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(button);
		
		jtf = new JTextField();
		JPanel jtfPanel = new JPanel();
		jtf.setColumns(25);
		jtfPanel.add(jtf);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent event){
	        	if(!(jtf.getText().equals("")) && !(file.getPath().equals(jtf.getText()))){
	        		file = new File(jtf.getText());
	        	}
	        	from.setTarget(file);
	        	dispose();
	          }
	        });
		
		JPanel okPanel = new JPanel();
		okPanel.add(ok);
		
		
		
		
		
		getContentPane().add(jtfPanel, BorderLayout.LINE_START);
		getContentPane().add(buttonPanel, BorderLayout.LINE_END);
		getContentPane().add(okPanel, BorderLayout.PAGE_END);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10, 10, 400, 150);
		setTitle("実行ファイルを選択");
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		 JFileChooser filechooser = new JFileChooser("C:\\Users\\student\\Desktop");

		    int selected = filechooser.showOpenDialog(this);
		    if (selected == JFileChooser.APPROVE_OPTION){
		      file = filechooser.getSelectedFile();
		      jtf.setText(file.getPath());
		      
		    }else if (selected == JFileChooser.CANCEL_OPTION){
		    }else if (selected == JFileChooser.ERROR_OPTION){
		    	
		    }	    
	}
	
	public File getSelectedFile(){
		return file;
	}

}
