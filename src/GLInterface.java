import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;

public class GLInterface extends JPanel{
	GLDisplay glDisplay;
	JFrame frame;
	SQLiteHandler db;
	private static final int FIELD_WIDTH = 250;
	private static final int FIELD_HEIGHT = 25;
	private static final int SPINNER_WIDTH = 70;
	private static final int LIST_WIDTH = 250;
	private static final int LIST_HEIGHT = 200;
	ArrayList<SakugawaParabolicRecipe> sakugawaParametersList = new ArrayList<SakugawaParabolicRecipe>();
	public static CalcThread calcThread;
	
	GLInterface(JFrame frame, GLDisplay glDisplay, SQLiteHandler db){
		this.frame = frame;
		this.glDisplay = glDisplay;
		this.db = db;
		this.setSize(glDisplay.INTERFACE_WIDTH, frame.getHeight());
		this.setMinimumSize(new Dimension(glDisplay.INTERFACE_WIDTH, frame.getHeight()));
		this.setMaximumSize(new Dimension(glDisplay.INTERFACE_WIDTH, frame.getHeight()));
		setAlignmentX(JPanel.CENTER_ALIGNMENT);
		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setUI();
		try{
			getParametersWithoutPoints();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void paintComponent(Graphics g){
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	JList<String> parametersList;
	DefaultListModel<String> parametersListModel;
	private JButton loadButton, saveButton, calcButton, cancelButton;
	private JSpinner z0Spinner, theta_aSpinner, theta_bSpinner, maxLevelSpinner, stepSpinner,
					 epsilonSpinner;
	private JTextField followTagsField;
	private JCheckBox autoCalcCheck, drawAllCheck, draw_aCheck, draw_bCheck, draw_ACheck, draw_BCheck;
	public static JCheckBox ellipticCheck;
	double stepSize = 0.01;
	public void setUI(){
		parametersListModel = new DefaultListModel<String>();
		parametersList = new JList<String>(parametersListModel);
		parametersList.setMaximumSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
		JScrollPane listScrollPane = new JScrollPane();
		listScrollPane.setViewportView(parametersList);
		listScrollPane.setMaximumSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
		parametersList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int index = parametersList.getSelectedIndex();
				if(index == -1) return;
				SakugawaParabolicRecipe param = sakugawaParametersList.get(index);

				z0Spinner.setValue(param.z0.re());
				theta_aSpinner.setValue(param.thetaA);
				theta_bSpinner.setValue(param.thetaB);
				maxLevelSpinner.setValue(param.maxLevel);
			}
		});
		
		
		loadButton = new JButton("load");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = parametersList.getSelectedIndex();
				if(index == -1) return;
				glDisplay.l3DList = sakugawaParametersList.get(index).getLinesList();
			}
		});
		
		SpinnerNumberModel z0Model = new SpinnerNumberModel(0.0, null, null, stepSize);
		z0Spinner = new JSpinner(z0Model);
		z0Spinner.setMaximumSize(new Dimension(SPINNER_WIDTH, FIELD_HEIGHT));
		HorizontalPanel z0Panel = new HorizontalPanel(FIELD_WIDTH, FIELD_HEIGHT);
		z0Panel.add(new JLabel("z0"));
		z0Panel.add(z0Spinner);
		z0Spinner.addChangeListener(new SpinnerChangeListener());
		
		SpinnerNumberModel theta_aModel = new SpinnerNumberModel(0.0, null, null, stepSize);
		theta_aSpinner = new JSpinner(theta_aModel);
		theta_aSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, FIELD_HEIGHT));
		HorizontalPanel theta_aPanel = new HorizontalPanel(FIELD_WIDTH, FIELD_HEIGHT);
		theta_aPanel.add(new JLabel("theta a"));
		theta_aPanel.add(theta_aSpinner);
		theta_aSpinner.addChangeListener(new SpinnerChangeListener());
		
		SpinnerNumberModel theta_bModel = new SpinnerNumberModel(0.0, null, null, stepSize);
		theta_bSpinner = new JSpinner(theta_bModel);
		theta_bSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, FIELD_HEIGHT));
		HorizontalPanel theta_bPanel = new HorizontalPanel(FIELD_WIDTH, FIELD_HEIGHT);
		theta_bPanel.add(new JLabel("theta b"));
		theta_bPanel.add(theta_bSpinner);
		theta_bSpinner.addChangeListener(new SpinnerChangeListener());
		
		SpinnerNumberModel epsilonModel = new SpinnerNumberModel(0.001, 0, 1000, 0.0001);
		epsilonSpinner = new JSpinner(epsilonModel);
		DecimalFormat format = new DecimalFormat();
		format.setMinimumFractionDigits(6);
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(epsilonSpinner, "#.##########");
		epsilonSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, FIELD_HEIGHT));
		HorizontalPanel epsilonPanel = new HorizontalPanel(FIELD_WIDTH, FIELD_HEIGHT);
		epsilonPanel.add(new JLabel("epsilon"));
		epsilonPanel.add(epsilonSpinner);
		epsilonSpinner.setEditor(editor);
		
		SpinnerNumberModel maxLevelModel = new SpinnerNumberModel(15, null, null, 1);
		maxLevelSpinner = new JSpinner(maxLevelModel);
		maxLevelSpinner.setMaximumSize(new Dimension(SPINNER_WIDTH, FIELD_HEIGHT));
		HorizontalPanel maxLevelPanel = new HorizontalPanel(FIELD_WIDTH, FIELD_HEIGHT);
		maxLevelPanel.add(new JLabel("max level"));
		maxLevelPanel.add(maxLevelSpinner);
		maxLevelSpinner.addChangeListener(new SpinnerChangeListener());

		JTextField fromTags = new JTextField();
		JTextField toTags = new JTextField();
		HorizontalPanel fromPanel = new HorizontalPanel(FIELD_WIDTH, FIELD_HEIGHT);
		HorizontalPanel toPanel = new HorizontalPanel(FIELD_WIDTH, FIELD_HEIGHT);
		fromPanel.add(new JLabel("開始タグ"));
		fromPanel.add(fromTags);
		toPanel.add(new JLabel("終了タグ"));
		toPanel.add(toTags);
		
		drawAllCheck = new JCheckBox("draw all");
		drawAllCheck.setOpaque(false);
		draw_aCheck = new JCheckBox("a");
		draw_aCheck.setOpaque(false);
		draw_bCheck = new JCheckBox("b");
		draw_bCheck.setOpaque(false);
		draw_ACheck = new JCheckBox("A");
		draw_ACheck.setOpaque(false);
		draw_BCheck = new JCheckBox("B");
		draw_BCheck.setOpaque(false);
		HorizontalPanel drawPanel = new HorizontalPanel(FIELD_WIDTH, FIELD_HEIGHT);
		drawPanel.add(drawAllCheck);
		drawPanel.add(draw_aCheck);
		drawPanel.add(draw_bCheck);
		drawPanel.add(draw_ACheck);
		drawPanel.add(draw_BCheck);
		drawAllCheck.setSelected(true);
		
		HorizontalPanel optionPanel = new HorizontalPanel(FIELD_WIDTH, FIELD_HEIGHT);
		autoCalcCheck = new JCheckBox("auto calc");
		autoCalcCheck.setOpaque(false);
		optionPanel.add(autoCalcCheck);
		SpinnerNumberModel stepModel = new SpinnerNumberModel(0.01, null, null, 0.01);
		stepSpinner = new JSpinner(stepModel);
		optionPanel.add(new JLabel(" 変化幅"));
		optionPanel.add(stepSpinner);
		stepSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				stepSize = (double) stepSpinner.getValue();
				((SpinnerNumberModel) z0Spinner.getModel()).setStepSize(stepSize);
				((SpinnerNumberModel) theta_aSpinner.getModel()).setStepSize(stepSize);
				((SpinnerNumberModel) theta_bSpinner.getModel()).setStepSize(stepSize);
				//((SpinnerNumberModel) epsilonSpinner.getModel()).setStepSize(stepSize);
			}
		});
		
		HorizontalPanel optionPanel2 = new HorizontalPanel(FIELD_WIDTH, FIELD_HEIGHT);
		ellipticCheck = new JCheckBox("楕円型有限語を調べる");
		ellipticCheck.setOpaque(false);
		optionPanel2.add(ellipticCheck);
		
		calcButton = new JButton("calculate");
		calcButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				calcThread = new CalcThread();
				calcThread.start();
			}
		});
		
		
		cancelButton = new JButton("キャンセル");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(calcThread != null && calcThread.isAlive()){
					calcThread.interrupt();
				}
			}
		}); 
		
		HorizontalPanel followTagsPanel = new HorizontalPanel(FIELD_WIDTH, FIELD_HEIGHT);
		followTagsField = new JTextField();
		followTagsPanel.add(new JLabel("追跡語"));
		followTagsPanel.add(followTagsField);
		
		add(new JLabel("パラメータリスト"));
		add(listScrollPane);
		add(z0Panel);
		add(theta_aPanel);
		add(theta_bPanel);
		add(maxLevelPanel);
		add(epsilonPanel);
		add(followTagsPanel);
		//add(drawAllCheck);
		add(drawPanel);
		add(optionPanel);
		add(optionPanel2);
		add(Box.createRigidArea(new Dimension(10, 5)));
		add(calcButton);
		add(Box.createRigidArea(new Dimension(10, 5)));
		add(cancelButton);
		add(Box.createRigidArea(new Dimension(10, 5)));
	}
	
	int[] begin_aTags = {-1, 1, 2, 3, 4}; 
	int[] end_aTags   = {-1, 1, 4, 3, 2};
	int[] begin_bTags = {-1, 2, 3, 4, 1}; 
	int[] end_bTags   = {-1, 2, 1, 4, 3};
	int[] begin_ATags = {-1, 3, 4, 1, 2}; 
	int[] end_ATags   = {-1, 3, 2, 1, 4};
	int[] begin_BTags = {-1, 4, 1, 2, 3}; 
	//int[] end_BTags   = {-1, 4, 3, 2, 1}; 
	int[] end_BTags   = {-1, 4, 1, 4, 4}; 
	private void calculate() throws InterruptedException{
		Quaternion z0 = Quaternion.valueOf((double) z0Spinner.getValue(), 0);
		double theta_a = (double) (theta_aSpinner.getValue());
		double theta_b = (double) theta_bSpinner.getValue();
		int maxLevel = (int) maxLevelSpinner.getValue();
		double epsilon = (double) epsilonSpinner.getValue();
		glDisplay.maxLevel = maxLevel;
		ThreeDimensionalKlein k = new ThreeDimensionalKlein(z0, theta_a, theta_b, maxLevel, epsilon);
		
		MaskitExtensionRecipe m = new MaskitExtensionRecipe(-0.2, 0.8, 0.3, maxLevel, epsilon);
		
		String strRepeatTags = followTagsField.getText();
		glDisplay.followTags = null;
		if(strRepeatTags.length() != 0){
			int[] tags = new int[maxLevel + 1];
			int[] repeatTags = new int[strRepeatTags.length()];
			for(int i = 0 ; i < repeatTags.length ; i++){
				repeatTags[i] = Integer.parseInt(String.valueOf(strRepeatTags.charAt(i)));
			}
			int repeatTagIndex = 0;
			for(int i = 1 ; i < maxLevel + 1 ; i++){
				tags[i] = repeatTags[repeatTagIndex % repeatTags.length];
				repeatTagIndex++;
			}
			glDisplay.followTags = tags;
		}
		ArrayList<Line3D> lines;
		if(drawAllCheck.isSelected()){
			//lines = k.runLine();
			lines = m.runLine();
			synchronized (glDisplay.l3DList) {
				glDisplay.l3DList.clear();
				glDisplay.l3DList = lines;
			}
		}else{
			if(draw_aCheck.isSelected()){
				//lines = k.runPartOf(begin_aTags, end_aTags);
				m = new MaskitExtensionRecipe(-0.2, 0.8, 0.3, maxLevel, epsilon);
				lines = m.runPartOf(begin_aTags, end_aTags);
				synchronized (glDisplay.l3DList) {
					for(Line3D l : lines){
						glDisplay.l3DList.add(l);
					}
				}
			}
			if(draw_ACheck.isSelected()){
//				k = new ThreeDimensionalKlein(z0, theta_a, theta_b, maxLevel, epsilon);
//				lines = k.runPartOf(begin_ATags, end_ATags);
				m = new MaskitExtensionRecipe(-0.2, 0.8, 0.3, maxLevel, epsilon);
				lines = m.runPartOf(begin_ATags, end_ATags);
				synchronized (glDisplay.l3DList) {
					for(Line3D l : lines){
						glDisplay.l3DList.add(l);
					}
				}
			}
			if(draw_bCheck.isSelected()){
//				k = new ThreeDimensionalKlein(z0, theta_a, theta_b, maxLevel, epsilon);
//				lines = k.runPartOf(begin_bTags, end_bTags);
				m = new MaskitExtensionRecipe(-0.2, 0.8, 0.3, maxLevel, epsilon);
				lines = m.runPartOf(begin_bTags, end_bTags);
				synchronized (glDisplay.l3DList) {
					for(Line3D l : lines){
						glDisplay.l3DList.add(l);
					}
				}
			}
			if(draw_BCheck.isSelected()){
//				k = new ThreeDimensionalKlein(z0, theta_a, theta_b, maxLevel, epsilon);
//				lines = k.runPartOf(begin_BTags, end_BTags);
				m = new MaskitExtensionRecipe(-0.2, 0.8, 0.3, maxLevel, epsilon);
				lines = m.runPartOf(begin_BTags, end_BTags);
				synchronized (glDisplay.l3DList) {
					for(Line3D l : lines){
						glDisplay.l3DList.add(l);
					}
				}
			}
		}
		System.gc();
	}
	
	class SpinnerChangeListener implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e) {
			if(autoCalcCheck.isSelected()){
				calcThread = new CalcThread();
				calcThread.start();
			}
		}
	}
	
	class CalcThread extends Thread{
		public void run(){
			try{
				calculate();
			}catch(InterruptedException ex){
				
			}
		}
	}
	
	public void getParameters() throws SQLException{
		ResultSet rs = db.query("select id, z0, theta_a, theta_b, max_level, points from sakugawa_parabolic_table");
		while(rs.next()){
			int id = rs.getInt(1);
			double z0 = rs.getDouble(2);
			double theta_a = rs.getDouble(3);
			double theta_b = rs.getDouble(4);
			int maxLevel = rs.getInt(5);
			String pointsStr = rs.getString(6);
			Quaternion z0q = Quaternion.valueOf(z0,  0);
			if(!pointsStr.equals("")){
				sakugawaParametersList.add(new SakugawaParabolicRecipe(id, z0q, theta_a, theta_b, maxLevel, pointsStr));
			}else{
				sakugawaParametersList.add(new SakugawaParabolicRecipe(id, z0q, theta_a, theta_b, maxLevel));
			}
			parametersListModel.addElement(id+": z0 "+ z0 +" theta_a "+ theta_a +" theta_b "+ theta_b +" maxLevel "+ maxLevel);
		}
	}
	
	public void getParametersWithoutPoints() throws SQLException{
		ResultSet rs = db.query("select id, z0, theta_a, theta_b, max_level from sakugawa_parabolic_table");
		while(rs.next()){
			int id = rs.getInt(1);
			double z0 = rs.getDouble(2);
			double theta_a = rs.getDouble(3);
			double theta_b = rs.getDouble(4);
			int maxLevel = rs.getInt(5);
			Quaternion z0q = Quaternion.valueOf(z0,  0);
			sakugawaParametersList.add(new SakugawaParabolicRecipe(id, z0q, theta_a, theta_b, maxLevel));
			parametersListModel.addElement(id+": z0 "+ z0 +" theta_a "+ theta_a +"theta_b "+ theta_b +" maxLevel "+ maxLevel);
		}
	}
}
