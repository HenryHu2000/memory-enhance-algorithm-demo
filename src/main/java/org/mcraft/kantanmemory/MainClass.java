package org.mcraft.kantanmemory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import org.mcraft.kantanmemory.core.LearningListManager;
import org.mcraft.kantanmemory.core.LearningProcess;
import org.mcraft.kantanmemory.core.data.KnownType;
import org.mcraft.kantanmemory.core.data.LearningList;
import org.mcraft.kantanmemory.core.data.LearningWordData;
import org.mcraft.kantanmemory.file.DataInitializer;
import org.mcraft.kantanmemory.file.DataReader;
import org.mcraft.kantanmemory.file.DataWriter;
import org.mcraft.kantanmemory.file.data.UserConfig;
import org.mcraft.kantanmemory.graphics.AppFrame;
import org.mcraft.kantanmemory.graphics.AppPanel;
import org.mcraft.kantanmemory.graphics.PanelState;

/**
 * 
 * @author Henry Hu
 *
 */
public class MainClass {

	public static void main(String[] args) {
		graphicalUI();
	}

	public static boolean readYesOrNo() {
		while (true) {
			String readLine = System.console().readLine("(Y/N): ");
			if (!readLine.isEmpty()) {
				switch (readLine.toLowerCase().toCharArray()[0]) {
				case 'y':
					return true;
				case 'n':
					return false;
				default:
				}
			}
		}
	}

	public static void graphicalUI() {
		// Initialize GUI
		final AppFrame frame = new AppFrame();

		new DataInitializer().initializeAll();
		UserConfig config = new DataReader().getConfig();

		final int newWordNum = 25;
		final int revisionWordNum = newWordNum * 5;
		final String wordlist = "wordlist.csv";
		config.setCurrentWordlist(wordlist);
		new DataWriter().saveConfig(config);
		if (new DataReader().getWordlist(wordlist).length <= config.getWordlistProgress(wordlist)) {
			// End of word list
			JOptionPane.showMessageDialog(frame,
					"<html><font size=+2>" + "Word list already finished!" + "</font></html>");
			System.exit(0);
		}

//		while (true) {
//			try {
//				String[] learningModes = new String[] { "New word mode", "Review mode" };
//				String learningMode = (String) JOptionPane.showInputDialog(frame,
//						"<html><font size=+2>" + "New word mode or review mode?" + "</font></html>",
//						AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null, learningModes, learningModes[0]);
//
//				if (learningMode == null) {
//					System.exit(0);
//				} else if (learningMode.equals(learningModes[0])) {
//					// New word mode
//
//					// Choose the word list to learn new words from
//					String wordlist = "";
//					String[] wordlists = config.getWordlists().toArray(new String[config.getWordlists().size()]);
//					wordlist = (String) JOptionPane.showInputDialog(frame,
//							"<html><font size=+2>" + "Which word list do you want to learn?" + "</font></html>",
//							AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null, wordlists,
//							config.getCurrentWordlist() != null ? config.getCurrentWordlist() : wordlists[0]);
//					if (wordlist == null) {
//						System.exit(0);
//					}
//					config.setCurrentWordlist(wordlist);
//					new DataWriter().saveConfig(config);
//
//					// Handle end of word list case
//					if (new DataReader().getWordlist(wordlist).length <= config.getWordlistProgress(wordlist)) {
//						// End of word list
//						JOptionPane.showMessageDialog(frame,
//								"<html><font size=+2>" + "Word list already finished!" + "</font></html>");
//						continue;
//					}
//
//					// Prompt to ask for number of new words
//					final int defaultNewWordNum = 10;
//					Object newWordNumObj = JOptionPane.showInputDialog(frame,
//							"<html><font size=+2>" + "How many new words do you want to learn?" + "</font></html>",
//							AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null,
//							new Integer[] { 5, 10, 15, 20, 25, 30, 40, 50, 60, 70, 80, 90, 100 }, defaultNewWordNum);
//					if (newWordNumObj == null) {
//						System.exit(0);
//					}
//					newWordNum = (int) newWordNumObj;
//					revisionWordNum = newWordNum * 5;
//
//				} else if (learningMode.equals(learningModes[1])) {
//					// Review mode
//
//					// Prompt to ask for number of revision words
//					final int defaultRevisionWordNum = 50;
//					Object revisionWordNumObj = JOptionPane.showInputDialog(frame,
//							"<html><font size=+2>" + "How many words do you want to review?" + "</font></html>",
//							AppFrame.FRAME_TITLE, JOptionPane.QUESTION_MESSAGE, null,
//							new Integer[] { 25, 50, 75, 100, 125, 150, 200, 250, 300, 350, 400, 450, 500 },
//							defaultRevisionWordNum);
//					if (revisionWordNumObj == null) {
//						System.exit(0);
//					}
//					revisionWordNum = (int) revisionWordNumObj;
//					newWordNum = 0;
//				}
//				break;
//			} catch (Exception e) {
//			}
//		}

		final LearningListManager learningListManager = new LearningListManager();
		LearningList learningList = learningListManager.generateLearningList(newWordNum, revisionWordNum);
		final LearningProcess learningProcess = new LearningProcess(learningList);

		// Set listeners for GUI buttons
		frame.getAppPanel().getKnownButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AppPanel appPanel = frame.getAppPanel();
				switch (appPanel.getState()) {
				case WORD_QUESTION:
					appPanel.setKnown(true);
					break;
				case HINT_QUESTION:
					if (learningProcess.getCurrentWordData().getKnownType() != KnownType.UNKNOWN) {
						// Known and half-known words are expected to be known at the first try
						appPanel.setKnown(false);
					} else {
						appPanel.setKnown(true);
					}
					break;
				default:
					break;
				}
				appPanel.setState(PanelState.ANSWER); // Continue to answer state
				appPanel.refreshPanel(learningProcess.getCurrentWordData().getWord());

			}
		});
		frame.getAppPanel().getUnknownButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AppPanel appPanel = frame.getAppPanel();
				switch (appPanel.getState()) {
				case WORD_QUESTION:
					appPanel.setKnown(false);
					appPanel.setState(PanelState.HINT_QUESTION);
					break;
				case HINT_QUESTION:
					appPanel.setKnown(false);
					appPanel.setState(PanelState.ANSWER);
					break;
				default:
					break;
				}

				appPanel.refreshPanel(learningProcess.getCurrentWordData().getWord());

			}
		});
		frame.getAppPanel().getContinueButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				AppPanel appPanel = frame.getAppPanel();
				if (appPanel.getState() == PanelState.ANSWER) {
					learningProcess.proceed(appPanel.isKnown());
					if (learningProcess.isTerminated()) {
						appPanel.initializePanel();
						appPanel.getWordLabel().setText("All words finished!");
						learningListManager.saveLearningList(learningProcess.getFinishedWordList());
						return;
					}
					if (learningProcess.getCurrentWordData().getKnownType() != KnownType.UNKNOWN) {
						appPanel.setState(PanelState.WORD_QUESTION);
					} else {
						appPanel.setState(PanelState.HINT_QUESTION);
					}
				}
				appPanel.refreshPanel(learningProcess.getCurrentWordData().getWord());
			}
		});

		// Put the first word into GUI
		AppPanel appPanel = frame.getAppPanel();
		appPanel.setState(PanelState.WORD_QUESTION);
		if (!learningProcess.isTerminated()) {
			appPanel.refreshPanel(learningProcess.getCurrentWordData().getWord());
		} else {
			appPanel.initializePanel();
			if (!learningProcess.getAllWords().isEmpty()) {
				appPanel.getWordLabel().setText("All words finished!");
			} else {
				appPanel.getWordLabel().setText("No word exists!");
			}
		}

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				learningListManager.saveLearningList(learningProcess.getAllWords());
				System.exit(0);
			}
		});

	}

}
