package fr.miage.m1.pa.explorateur.controleur;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextField;

import fr.miage.m1.pa.explorateur.controleur.plugin.ManageurPlugin;
import fr.miage.m1.pa.explorateur.controleur.plugin.ManageurPluginListener;
import fr.miage.m1.pa.explorateur.controleur.plugin.ManageurPluginVue;
import fr.miage.m1.pa.explorateur.controleur.save.SaveManager;
import fr.miage.m1.pa.explorateur.interfaces.Controleur;
import fr.miage.m1.pa.explorateur.interfaces.ControleurVueListener;
import fr.miage.m1.pa.explorateur.interfaces.Modele;
import fr.miage.m1.pa.explorateur.interfaces.Saving;
import fr.miage.m1.pa.explorateur.interfaces.Vue;
import fr.miage.m1.pa.explorateur.modele.ModeleImpl;
import fr.miage.m1.pa.explorateur.vue.VueImpl;

public class ControleurImpl implements Controleur, KeyListener, ActionListener, MouseListener, ControleurVueListener,
		ManageurPluginListener, Saving {

	private static final String SAVE_FILE = "Controleur";
	public static final String ACTION_PRECEDENT = "ACTION_PRECEDENT";

	private File currentFile;

	private ManageurPlugin managerPlugin;
	private SaveManager saveManager;
	private Modele modele;
	private Vue vue;

	public ControleurImpl() {
		managerPlugin = new ManageurPlugin(this);
		saveManager = new SaveManager();
		saveManager.retrieveState(this);
		
		if(currentFile == null) {
			currentFile = new File(System.getProperty("user.dir"));
		}
		modele = new ModeleImpl(currentFile);
		vue = new VueImpl(modele);

		saveManager.retrieveState(managerPlugin);
		
		vue.setPluginMenu(managerPlugin.getPlugins());
		vue.setMouseListener(this);
		vue.setControleurListener(this);
		vue.setActionListener(this);
		vue.setKeyListener(this);

	}

	@Override
	public Vue getVue() {
		return vue;
	}

	@Override
	public Modele getModele() {
		return modele;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource().equals(vue.getMainTable())) {
			if (e.getClickCount() == 2) {
				File f = modele.getFileAt(vue.getMainTable().getSelectedRow());
				if(f.isDirectory()) {
					setCurrentPath(f);
				} else {
					try {
						Desktop.getDesktop().open(f);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	private void setCurrentPath(File f) {
		if (f.isDirectory()) {
			if (modele.setCurrentPath(f)) {
				modele.populate();
				currentFile = f;
				JTextField text = vue.getLabelNavigateur();
				text.setText(modele.getCurrentPath().getAbsolutePath());
			}

		}
	}

	@Override
	public void onMenuClicked(String name) {

		if (name.equals("Plugins")) {
			new ManageurPluginVue(this, managerPlugin);
		} else {
			managerPlugin.showPluginView(name);
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void onPluginSelected(String plugin) {
		managerPlugin.onPluginClicked(plugin);
	}


	@Override
	public void onClose() {
		saveManager.saveState(managerPlugin);
		saveManager.saveState(this);
	}

	@Override
	public Object getObjectToSave() {
		return modele.getCurrentPath();
	}

	@Override
	public String getFileNameToSave() {
		return SAVE_FILE;
	}

	@Override
	public void retrieveSavedObject(Object obj) {
		currentFile = (File)obj;
		System.out.println("ControleurImpl.retrieveSavedObject() " + currentFile.getName());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_PRECEDENT.equals(e.getActionCommand())) {
			if (modele.setCurrentPath(modele.getCurrentPath().getParentFile())) {
				modele.populate();
				JTextField text = vue.getLabelNavigateur();
				text.setText(modele.getCurrentPath().getAbsolutePath());
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() == vue.getLabelNavigateur() && e.getKeyCode() == KeyEvent.VK_ENTER) {
			JTextField text = vue.getLabelNavigateur();

			if (modele.setCurrentPath(new File(text.getText()))) {
				modele.populate();
				text.setText(modele.getCurrentPath().getAbsolutePath());
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void setModele(Modele modele) {
		
		this.modele = modele;
		vue.setModele(modele);
		
	}

	@Override
	public void setVue(Vue vue) {
		
		this.vue = vue;
		
	}

}
