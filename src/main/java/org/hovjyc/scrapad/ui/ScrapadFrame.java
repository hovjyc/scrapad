package org.hovjyc.scrapad.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.hovjyc.scrapad.business.IAdScrapListener;
import org.hovjyc.scrapad.business.ResourcesManager;
import org.hovjyc.scrapad.business.enums.GenderEnum;
import org.hovjyc.scrapad.business.enums.SiteEnum;
import org.hovjyc.scrapad.business.scrapers.AbstractScraper;
import org.hovjyc.scrapad.business.scrapers.GtrouveScraper;
import org.hovjyc.scrapad.business.scrapers.WannonceScraper;
import org.hovjyc.scrapad.common.IScrapadConstants;
import org.hovjyc.scrapad.common.ScrapadException;
import org.hovjyc.scrapad.model.Ad;
import org.jdesktop.swingx.JXDatePicker;

/**
 * The main frame
 */
public class ScrapadFrame extends JFrame implements IAdScrapListener {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -7737074797103614211L;

	/** The gap between components */
	private static final int GAP = 10;

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(ScrapadFrame.class);
	
	/** Max number of ads. */
	private static final int MAX_AD_COUNT = 200;

	/** The table width */
	private static final int TABLE_WIDTH = 1000;

	/** The action panel */
	private JPanel actionPanel;

	/** the table model */
	private AdTableModel adTableModel;

	/** The apply button */
	private JButton applyButton;

	/** The couple checkbox */
	private JCheckBox coupleCb;

	/** the glass panel */
	private JPanel glass;
	
	/** The radio button corresponding to the gtrouve scrap. */
	private JRadioButton gtrouveRadio;

	/** The padding label */
	JLabel padding;

	/** The man checkbox */
	private JCheckBox manCb;

	/** The menu bar. */
	private JMenuBar menuBar;

	/** The spinner model */
	private SpinnerNumberModel numberSpinnerModel;

	/** The date picker */
	private JXDatePicker picker;

	/** The table */
	private JTable table;
	
	/** The current frame. */
	private ScrapadFrame currentFrame;

	/** The ads website */
	private AbstractScraper scraper;

	/** The table panel. */
	private JPanel tablePanel;
	
	/** The radio button corresponding to the wannonce scrap. */
	private JRadioButton wannonceRadio;

	/** The woman checkox */
	private JCheckBox womanCb;

	/**
	 * Constructor
	 */
	public ScrapadFrame() {
		super();
		currentFrame = this;
		adTableModel = new AdTableModel();
		table = new JTable(adTableModel);
		setTitle("Scrapad");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buildFrame();
		loadOptions();
		setVisible(true);
		setExtendedState(MAXIMIZED_BOTH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleAdScraped(Ad pAd) {
		adTableModel.setAdContent(pAd);
	}

	/**
	 * Build the frame
	 */
	private void buildFrame() {
		createMenuBar();
		setJMenuBar(menuBar);
		glass = new JPanel();
		// add a label to help trap focus while the glass pane is active
		padding = new JLabel();
		glass.setOpaque(false);
		glass.add(padding);
		// trap both mouse and key events. Could provide a smarter
		// key handler if you wanted to allow things like a keystroke
		// that would cancel the long-running operation.
		glass.addMouseListener(new MouseAdapter() {
		});
		glass.addMouseMotionListener(new MouseMotionAdapter() {
		});
		glass.addKeyListener(new KeyAdapter() {
		});

		// make sure the focus won't leave the glass pane
		// glass.setFocusCycleRoot(true); // 1.4
		padding.setNextFocusableComponent(padding); // 1.3
		setGlassPane(glass);

		JPanel mainPanel = new JPanel();
		setContentPane(mainPanel);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		createActionPanel();
		mainPanel.add(actionPanel);
		createTablePanel();
		mainPanel.add(tablePanel);
		this.pack();
	}

	/**
	 * Create the table panel containing the ads
	 */
	private void createTablePanel() {
		tablePanel = new JPanel(new BorderLayout());

		table.getColumnModel().getColumn(0).setCellRenderer(new JTextPaneCellRenderer());
		table.getColumnModel().getColumn(0).setCellEditor(new JTextPaneCellEditor());
		table.getColumnModel().getColumn(0).setMinWidth(TABLE_WIDTH);
		table.getColumnModel().getColumn(1).setCellRenderer(new JButtonCellRenderer());
		table.getColumnModel().getColumn(1).setCellEditor(new JButtonCellEditor());
		table.getColumnModel().getColumn(2).setCellRenderer(new JButtonCellRenderer());
		table.getColumnModel().getColumn(2).setCellEditor(new JButtonCellEditor());
		tablePanel.add(table, BorderLayout.CENTER);

		JScrollPane lScrollPane = new JScrollPane(table);
		tablePanel.add(lScrollPane);
	}

	/**
	 * Create the panel with all the user actions
	 */
	private void createActionPanel() {
		actionPanel = new JPanel(new BorderLayout());
		JButton lLoadButton = new JButton("Recharger");
		lLoadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrap();
			}

		});
		actionPanel.add(lLoadButton, BorderLayout.LINE_END);

		// Create layout
		JPanel lOptionsPanel = new JPanel();
		BoxLayout lBoxLayout = (new BoxLayout(lOptionsPanel, BoxLayout.X_AXIS));
		lOptionsPanel.setLayout(lBoxLayout);
		lOptionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
		JPanel lOptionsColumn1 = new JPanel();
		lOptionsColumn1.setLayout(new BoxLayout(lOptionsColumn1, BoxLayout.Y_AXIS));
		JPanel lOptionsColumn2 = new JPanel();
		lOptionsColumn2.setLayout(new BoxLayout(lOptionsColumn2, BoxLayout.Y_AXIS));
		lOptionsPanel.add(lOptionsColumn1);
		lOptionsPanel.add(Box.createRigidArea(new Dimension(2 * GAP, 0)));
		lOptionsPanel.add(lOptionsColumn2);

		applyButton = new JButton("Appliquer");
		applyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					ResourcesManager.getInstance().saveProperties();
				} catch (IOException e) {
					e.printStackTrace();
				}
				applyButton.setEnabled(false);
			}
		});

		// Create components
		
		final JPanel lCheckBoxesPanel = new JPanel();
		JLabel lGenreLabel = new JLabel("Genre:");
		manCb = new JCheckBox("Homme");
		manCb.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent lItemEvent) {
				applyButton.setEnabled(true);
				if (lItemEvent.getStateChange() == ItemEvent.SELECTED) {
					ResourcesManager.getInstance().getGenders().add(GenderEnum.HOMME);
				} else if (lItemEvent.getStateChange() == ItemEvent.DESELECTED) {
					ResourcesManager.getInstance().getGenders().remove(GenderEnum.HOMME);
				}
			}
		});
		womanCb = new JCheckBox("Femme");
		womanCb.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent lItemEvent) {
				applyButton.setEnabled(true);
				if (lItemEvent.getStateChange() == ItemEvent.SELECTED) {
					ResourcesManager.getInstance().getGenders().add(GenderEnum.FEMME);
				} else if (lItemEvent.getStateChange() == ItemEvent.DESELECTED) {
					ResourcesManager.getInstance().getGenders().remove(GenderEnum.FEMME);
				}
			}
		});
		coupleCb = new JCheckBox("Couple");
		coupleCb.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent lItemEvent) {
				applyButton.setEnabled(true);
				if (lItemEvent.getStateChange() == ItemEvent.SELECTED) {
					ResourcesManager.getInstance().getGenders().add(GenderEnum.COUPLE);
				} else if (lItemEvent.getStateChange() == ItemEvent.DESELECTED) {
					ResourcesManager.getInstance().getGenders().remove(GenderEnum.COUPLE);
				}
			}
		});
		lCheckBoxesPanel.add(manCb);
		lCheckBoxesPanel.add(womanCb);
		lCheckBoxesPanel.add(coupleCb);
		JPanel lGenderPanel = new JPanel();
		lGenderPanel.setLayout(new BoxLayout(lGenderPanel, BoxLayout.X_AXIS));
		lGenderPanel.setAlignmentX(LEFT_ALIGNMENT);
		lGenderPanel.add(lGenreLabel);
		lGenderPanel.add(Box.createRigidArea(new Dimension(GAP, 0)));
		lGenderPanel.add(lCheckBoxesPanel);
		
		JPanel lSitePanel = new JPanel();
		lSitePanel.setLayout(new BoxLayout(lSitePanel, BoxLayout.X_AXIS));
		lSitePanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		JLabel lSiteLabel = new JLabel("Site: ");
		wannonceRadio = new JRadioButton("Wannonce");
		wannonceRadio.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent lItemEvent) {
				applyButton.setEnabled(true);
				scraper = new WannonceScraper();
				scraper.addAdScrapListener(currentFrame);
				for(Component lComponent: lCheckBoxesPanel.getComponents()) {
					lComponent.setEnabled(true);
				}
				if (lItemEvent.getStateChange() == ItemEvent.SELECTED) {
					ResourcesManager.getInstance().setSite(SiteEnum.WANNONCE);
				}
			}
		});
		gtrouveRadio = new JRadioButton("GTrouve");
		gtrouveRadio.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent lItemEvent) {
				applyButton.setEnabled(true);
				scraper = new GtrouveScraper();
				scraper.addAdScrapListener(currentFrame);
				for(Component lComponent: lCheckBoxesPanel.getComponents()) {
					lComponent.setEnabled(false);
				}
				if (lItemEvent.getStateChange() == ItemEvent.SELECTED) {
					ResourcesManager.getInstance().setSite(SiteEnum.GTROUVE);
				}
			}
		});
		ButtonGroup lRadioGroup = new ButtonGroup();
		lRadioGroup.add(wannonceRadio);
		lRadioGroup.add(gtrouveRadio);
		lSitePanel.add(lSiteLabel);
		lSitePanel.add(wannonceRadio);
		lSitePanel.add(gtrouveRadio);
		
		lOptionsColumn1.add(lSitePanel);
		lOptionsColumn1.add(lGenderPanel);

		JPanel lDatePanel = new JPanel();
		lDatePanel.setLayout(new BoxLayout(lDatePanel, BoxLayout.X_AXIS));
		lDatePanel.setAlignmentX(LEFT_ALIGNMENT);
		JLabel dateMinLabel = new JLabel("Date de l'annonce la plus ancienne:");
		picker = new JXDatePicker();
		picker.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
		picker.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ResourcesManager.getInstance().setDate(picker.getDate());
				applyButton.setEnabled(true);
			}
		});
		lDatePanel.add(dateMinLabel);
		lDatePanel.add(Box.createRigidArea(new Dimension(GAP, 0)));
		lDatePanel.add(picker);
		lOptionsColumn1.add(lDatePanel);
		lOptionsColumn1.add(Box.createVerticalStrut(GAP));

		JPanel lNbPanel = new JPanel();
		lNbPanel.setLayout(new BoxLayout(lNbPanel, BoxLayout.X_AXIS));
		lNbPanel.setAlignmentX(LEFT_ALIGNMENT);
		JLabel nbMaxLabel = new JLabel("Nombre maximal d'annonces:");
		JSpinner lNumberSpinner;
		numberSpinnerModel = new SpinnerNumberModel(ResourcesManager.getInstance().getMaxNbAds(), 0, MAX_AD_COUNT, 1);
		numberSpinnerModel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				ResourcesManager.getInstance().setMaxNbAds(numberSpinnerModel.getNumber().intValue());
				applyButton.setEnabled(true);
			}
		});
		lNumberSpinner = new JSpinner(numberSpinnerModel);
		lNbPanel.add(nbMaxLabel);
		lNbPanel.add(Box.createRigidArea(new Dimension(GAP, 0)));
		lNbPanel.add(lNumberSpinner);
		lOptionsColumn1.add(lNbPanel);
		lOptionsColumn1.add(Box.createVerticalStrut(GAP));

		lOptionsColumn1.add(applyButton);

		JButton lBW = new JButton("Mauvais mots-clés");
		lBW.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openTextFile(IScrapadConstants.BAD_KEYWORDS);
			}
		});
		JButton lGW = new JButton("Bons mots-clés");
		lGW.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openTextFile(IScrapadConstants.GOOD_KEYWORDS);
			}
		});
		JButton lUI = new JButton("Utilisateurs ignorés");
		lUI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openTextFile(IScrapadConstants.PSEUDOS);
			}
		});
		lOptionsColumn2.add(lBW);
		lOptionsColumn2.add(Box.createVerticalStrut(GAP));
		lOptionsColumn2.add(lGW);
		lOptionsColumn2.add(Box.createVerticalStrut(GAP));
		lOptionsColumn2.add(lUI);

		actionPanel.add(lOptionsPanel, BorderLayout.CENTER);
		actionPanel.setMaximumSize(actionPanel.getMinimumSize());
	}

	/**
	 * Create the menu bar
	 */
	private void createMenuBar() {
		menuBar = new JMenuBar();
		// Contient qu'un menu fichier
		JMenu lMenu = new JMenu("Fichier");
		lMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem lItem;

		lItem = new JMenuItem("Bons mots-clés");
		lItem.setMnemonic(KeyEvent.VK_B);
		lItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK));
		lItem.setToolTipText("Liste des mots-clés rendant une annonce valide."
				+ " Une annonce contenant un de ces mots sera affichée,"
				+ " même si elle contient des mots-clés interdits");
		lItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openTextFile(IScrapadConstants.GOOD_KEYWORDS);
			}
		});
		lMenu.add(lItem);

		lItem = new JMenuItem("Mauvais mots-clés");
		lItem.setMnemonic(KeyEvent.VK_M);
		lItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
		lItem.setToolTipText(
				"Liste des mots-clés interdits." + " Une annonce contenant un de ces mots ne sera pas affichée");
		lItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openTextFile(IScrapadConstants.BAD_KEYWORDS);
			}
		});
		lMenu.add(lItem);

		lItem = new JMenuItem("Utilisateurs ignorés");
		lItem.setMnemonic(KeyEvent.VK_I);
		lItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
		lItem.setToolTipText("Liste des utilisateurs dont les annonces ne seront pas affichées");
		lItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openTextFile(IScrapadConstants.PSEUDOS);
			}
		});
		lMenu.add(lItem);
		lMenu.addSeparator();
		;

		lItem = new JMenuItem("Recharger");
		lItem.setMnemonic(KeyEvent.VK_R);
		lItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		lItem.setToolTipText("Parcours et affiche les annonces");
		lItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				scrap();
			}
		});
		lMenu.add(lItem);
		lMenu.addSeparator();

		lItem = new JMenuItem("Quitter");
		lItem.setMnemonic(KeyEvent.VK_Q);
		lItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		lItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		lMenu.add(lItem);

		menuBar.add(lMenu);
	}

	/**
	 * Lad the options
	 */
	private void loadOptions() {
		ResourcesManager.getInstance().load();
		if (ResourcesManager.getInstance().getSite().equals(SiteEnum.WANNONCE)) {
			wannonceRadio.setSelected(true);
		} else if (ResourcesManager.getInstance().getSite().equals(SiteEnum.GTROUVE)) {
			gtrouveRadio.setSelected(true);
		}
		if (ResourcesManager.getInstance().getGenders().contains(GenderEnum.HOMME)) {
			manCb.setSelected(true);
		} else {
			manCb.setSelected(false);
		}
		if (ResourcesManager.getInstance().getGenders().contains(GenderEnum.FEMME)) {
			womanCb.setSelected(true);
		} else {
			womanCb.setSelected(false);
		}
		if (ResourcesManager.getInstance().getGenders().contains(GenderEnum.COUPLE)) {
			coupleCb.setSelected(true);
		} else {
			coupleCb.setSelected(false);
		}
		picker.setDate(ResourcesManager.getInstance().getDate());
		numberSpinnerModel.setValue(ResourcesManager.getInstance().getMaxNbAds());
		applyButton.setEnabled(false);
	}

	/**
	 * Scraps the ads
	 */
	private void scrap() {

		adTableModel.reset();
		loadOptions();

		Thread thread = new Thread() {
			public void run() {
				try {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					glass.setVisible(true);
					// required to trap key events
					padding.requestFocus();
					scraper.scrap();
					// save the bad pseudos found during the scrap.
					ResourcesManager.getInstance().savePseudos();
				} catch (ScrapadException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
					LOG.error("Scraping interrompu" + e.getMessage());
				} finally {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					glass.setVisible(false);
				}
			}
		};

		thread.start();
	}

	/**
	 * Open the text file given in arguments
	 * 
	 * @param pTextFile
	 *            The text file
	 */
	private void openTextFile(String pTextFile) {
		try {
			ProcessBuilder lProc = new ProcessBuilder("gedit", pTextFile);
			lProc.start();
		} catch (Exception e) {
			try {
				ProcessBuilder lProc = new ProcessBuilder("notepad.exe", pTextFile);
				lProc.start();
			} catch (Exception e2) {
				System.out.println("Erreur lors de l'execution de gedit et bloc note.");
			}
		}
	}
}
