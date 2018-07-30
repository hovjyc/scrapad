package org.hovjyc.scrapad.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;
import org.hovjyc.scrapad.business.ResourcesManager;

/**
 * The text pane editor permitting to resize according to the content, select and
 * do some actions on the text.
 */
public class JTextPaneCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener {
	
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -4809346295777489103L;

	/** Logger of the class. */
	private static final Logger LOG = Logger.getLogger(JTextPaneCellEditor.class);
	
	/** The viewport */
	private JViewport viewport;
	
	/** The ads table containing the current cell */
	private JTable table;
	
	/** The current row */
	private int row;
	
	/** THe text pane */
	private JTextPane pane;

	/**
	 * Constructor
	 */
	public JTextPaneCellEditor() {
		viewport = new JViewport();
		pane = new JTextPane();
		viewport.add(pane);
		pane.addKeyListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getCellEditorValue() {
		return pane.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.table = table;
		this.row = row;
		pane.setText(value.toString());
		pane.setEditable(false);
		int newHeight = (int) pane.getPreferredSize().getHeight();
		if (column == 0 && table.getRowHeight(row) < newHeight) {
			table.setRowHeight(row, newHeight);
		}
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem addToGW = new JMenuItem("Ajouter aux bons mots-clés");
		JMenuItem addToBW = new JMenuItem("Ajouter aux mauvais mots-clés");
		addToGW.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String lGoodKeyword = pane.getSelectedText();
				LOG.info("Ajouter aux bons mots-clés: " + lGoodKeyword);
				ResourcesManager.getInstance().loadGoodKeywords();
				ResourcesManager.getInstance().getGoodKeywords().add(lGoodKeyword);
				ResourcesManager.getInstance().saveGoodKeywords();
			}
		});

		addToBW.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String lBadKeyword = pane.getSelectedText();
				LOG.info("Ajouter aux mauvais mots-clés: " + lBadKeyword);
				ResourcesManager.getInstance().loadBadKeywords();
				ResourcesManager.getInstance().getBadKeywords().add(lBadKeyword);
				ResourcesManager.getInstance().saveBadKeywords();
			}
		});
		popupMenu.add(addToGW);
		popupMenu.add(addToBW);
		MouseListener mouseListener = new JPopupMenuShower(popupMenu, pane);
		pane.addMouseListener(mouseListener);
		return pane;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCellEditable(EventObject e) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		table.setRowHeight(row, (int) pane.getPreferredSize().getHeight());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			stopCellEditing();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyReleased(KeyEvent e) {
	}
}

/**
 * The class defining the popup menu appearing on right click on a selected
 * text.
 */
class JPopupMenuShower extends MouseAdapter {

	/** The popup menu */
	private JPopupMenu popup;
	/** The text pane */
	private JTextPane pane;

	/**
	 * Constructor
	 * 
	 * @param popup
	 *            The popup menu
	 * @param pane
	 *            The pane
	 */
	public JPopupMenuShower(JPopupMenu popup, JTextPane pane) {
		this.popup = popup;
		this.pane = pane;
	}

	/**
	 * Show the popup menu according to the mouse action
	 * 
	 * @param mouseEvent
	 *            The mouse event listened to show the popup menu or not
	 */
	private void showIfPopupTrigger(MouseEvent mouseEvent) {
		if (popup.isPopupTrigger(mouseEvent)) {
			popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
			if (pane == null || pane.getSelectedText() == null || pane.getSelectedText().isEmpty()) {
				for (Component lComponent : popup.getComponents()) {
					lComponent.setEnabled(false);
				}
			} else {
				for (Component lComponent : popup.getComponents()) {
					lComponent.setEnabled(true);
				}
			}
		}
	}
	
	/**
	 * Called when mouse pressed
	 */
	public void mousePressed(MouseEvent mouseEvent) {
		showIfPopupTrigger(mouseEvent);
	}

	/**
	 * Called when mouse released
	 */
	public void mouseReleased(MouseEvent mouseEvent) {
		showIfPopupTrigger(mouseEvent);
	}
}