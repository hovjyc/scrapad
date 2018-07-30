package org.hovjyc.scrapad.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.hovjyc.scrapad.business.ResourcesManager;
import org.hovjyc.scrapad.model.Ad;

/**
 * A custom table model to display information of a list of ads.
 */
public class AdTableModel extends AbstractTableModel {
	
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1745052002260651645L;

	/** Logger of the class. */
	private static final Logger LOG = Logger.getLogger(AdTableModel.class);
	
	/** The titles of the columns. */
	private String titles[] = new String[] { "Annonce", "Lien", "Supprimer" };
	
	/** The type of the cells content. */
	private Class<?> types[] = new Class[] { String.class, JButton.class, JButton.class };
	
	/** The data. */
	private Vector<Vector<Object>> data;

	/**
	 * Constructor
	 */
	public AdTableModel() {
		data = new Vector<Vector<Object>>();
	}

	// Implement the methods of the TableModel interface we're interested
	// in. Only getRowCount( ), getColumnCount( ), and getValueAt( ) are
	// required. The other methods tailor the look of the table.
	/**
	 * getRowCount
	 * 
	 * Get the number of rows
	 * 
	 * @return The number of rows
	 */
	public int getRowCount() {
		return data.size();
	}

	/**
	 * getColumnCount
	 * 
	 * Get the number of columns
	 * 
	 * @return The number of columns
	 */
	public int getColumnCount() {
		return titles.length;
	}

	/**
	 * getColumnName
	 * 
	 * Get the name of a specific column
	 * 
	 * @param pColumn
	 *            The column index
	 * @return The name of the column specified in arguments
	 */
	public String getColumnName(int pColumn) {
		return titles[pColumn];
	}

	/**
	 * getColumnClass
	 * 
	 * Get the class of a specific column
	 * 
	 * @param pColumn
	 *            The column index
	 * @return The class of the column specified in arguments
	 */
	public Class<?> getColumnClass(int pColumn) {
		return types[pColumn];
	}

	/**
	 * isCellEditable
	 * 
	 * Indicates if a cell is editable
	 * 
	 * @param pRow
	 *            The row index
	 * @param pColumn
	 *            The column index
	 * @return True if the cell is editable, false otherwise.
	 */
	public boolean isCellEditable(int pRow, int pColumn) {
		// All the table is editable. The textpane can be selected and a popup
		// menu is associated to it. The buttons can be clicked.
		return true;
	}

	/**
	 * getValueAt
	 * 
	 * Get the value of a cell
	 * 
	 * @param pRow
	 *            The row index
	 * @param pColumn
	 *            The column index
	 * @return The value of the specified cell.
	 */
	public Object getValueAt(int pRow, int pColumn) {
		return data.get(pRow).get(pColumn);
	}

	/**
	 * Resets the table.
	 */
	public void reset() {
		data = new Vector<Vector<Object>>();
	}

	/**
	 * setAdContent
	 * 
	 * Our own method for setting/changing the current ad being displayed. This
	 * method fills the data set with ad info. It also fires an
	 * update event, so this method could also be called after the table is on
	 * display.
	 * 
	 * @param pAdsToContact
	 *            The ads to display
	 */
	public void setAdContent(Ad pAd) {
		Vector<Object> lRowData = new Vector<Object>();
		String lText = pAd.getTitle() + "\n" + pAd.getPseudo() + " - " + pAd.getDate() + " - " + pAd.getLocation()
				+ "\n" + pAd.getDescription();
		lRowData.addElement(lText);
		JButton lLinkButton = new JButton("Lien");
		lLinkButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LOG.info("Open link: " + pAd.getUrl());
				OpenURL(pAd.getUrl());
				System.out.println(lRowData);
				System.out.println(lRowData.get(0));
			}
		});
		JButton lDeleteButton = new JButton("Supprimer");
		lDeleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LOG.info("Ajouter aux utilisateurs ignorés: " + pAd.getPseudo());
				data.remove(lRowData);
				ResourcesManager.getInstance().loadPseudos();
				ResourcesManager.getInstance().getPseudos().add(pAd.getPseudo());
				ResourcesManager.getInstance().savePseudos();
				fireTableDataChanged();
			}
		});
		lRowData.addElement(lLinkButton);
		lRowData.addElement(lDeleteButton);
		data.addElement(lRowData);
		fireTableDataChanged();
	}

	/**
	 * Launche the navigator at the given URL
	 * 
	 * @param pURL
	 *            The URL
	 */
	private void OpenURL(String pURL) {
		try {
			ProcessBuilder lProc = new ProcessBuilder("firefox", "-private", pURL);
			lProc.start();
		} catch (Exception e) {
			LOG.error("Impossible de lancer firefox");
		}
	}
}