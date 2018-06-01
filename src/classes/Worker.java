package classes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * EXAMPLES
 * 
 * EP:
 * https://itunes.apple.com/us/album/aromatic-ep/1325214121
 * 
 * Features: 
 * https://itunes.apple.com/us/album/to-pimp-a-butterfly/974187289
 * 
 * Foreign Characters:
 * https://itunes.apple.com/br/album/pedro-alt%C3%A9rio-bruno-piazza/577195766
 * 
 * Two Disc:
 * https://itunes.apple.com/us/album/why-mountains-are-black-primeval-greek-village-music/1069921584
 */


public class Worker {

	private static JFrame resultFrame;
	private static JTextArea resultText;
	private static JFileChooser fc;
	private static File resultFile;
	private static String resultString;
	
	private static Integer discCount;
	private static String artistName;
	private static String albumTitle;
	private static String albumType;
	private static String releaseDate;
	private static String albumArworkURL;
	private static List<AlbumTrack> tracklist;

	public static void main(String[] args) {
		resultWindow();
		
		discCount = 1;
		artistName = "";
		albumTitle = "";
		albumType = "Album";
		releaseDate = "";
		albumArworkURL = "";
		tracklist = new ArrayList<AlbumTrack>();

		String albumURL = null;
		
		System.out.println();
		System.out.println();
		
		albumURL = (String) JOptionPane.showInputDialog(null,
				"Enter Apple Music album url: \n(Example: https://itunes.apple.com/us/album/abbey-road/id401186200)", "Enter Apple Music album url",
				JOptionPane.PLAIN_MESSAGE, null, null, null);

		if (albumURL == null || albumURL.equals("")) {
			System.exit(0);
		}

		parseHTML(albumURL);
		downloadArtwork();
		
		resultString = artistName + "\n" + albumTitle + "\n" + releaseDate + "\n\nType: " + albumType + "\n\n" +tracklistAsText() + "\nSource URL:\n" + albumURL
				+ "\n\nArtwork automatically downloaded to Downloads/covers.";
		resultText.setText(resultString);
		resultFrame.setVisible(true);
		resultFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		System.out.println();

	}
	
	public static void downloadArtwork() {
		String downloadLoc = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "covers";
		String filename = albumTitle.replaceAll("[\\/|:*?<>\"]", "") + ".jpg";
		File path = new File(downloadLoc);
		if (!path.exists())
			path.mkdirs();

		try {
			URL url = new URL(albumArworkURL);
			InputStream in = url.openStream();
			File dest = new File(downloadLoc + File.separator + filename);
			if (!dest.exists()) {
				Files.copy(in, Paths.get(downloadLoc + File.separator + filename));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String tracklistAsText(){
		StringBuilder sb = new StringBuilder();
		for(AlbumTrack at : tracklist){
			String discPrefix = (discCount == 1) ? "" : (at.discNum + ".");
			sb.append(discPrefix + at.trackNum + "|" + at.trackTitle + "|" + at.trackDuration + "\n");
		}
		return sb.toString();
	}

	public static void parseHTML(String input) {
		JFrame frame = new JFrame("Parsing webpages...");
		frame.setPreferredSize(new Dimension(300, 120));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel label1 = new JLabel();
		label1.setPreferredSize(new Dimension(200, 40));
		label1.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label1);

		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		String url = input;

		try {
			URL source = null;
			boolean valid = true;
			try {
				source = new URL(url);
			} catch (MalformedURLException e) {
				valid = false;
			}
			while (valid == false) {
				valid = true;
				url = (String) JOptionPane.showInputDialog(null, "Malformed URL format. Are you sure you copied the entire URL?\n" + "Try again:",
						"Provide URL", JOptionPane.PLAIN_MESSAGE, null, null, null);
				try {
					source = new URL(url);
				} catch (MalformedURLException e) {
					valid = false;
				}
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(source.openStream()));
			boolean tracksParsed = false;

			String inputLine = in.readLine();
			while (inputLine != null) {
				// PARSING STARTS HERE

				// grab release date
				if (inputLine.contains("link-list__item--released")) {
					String s = inputLine.substring(inputLine.indexOf("Released:"), inputLine.indexOf("</span>"));
					s = s.substring(s.indexOf(">") + 1);
					releaseDate = s;
				}

				// grab album title and artist name
				if (inputLine.contains("product-header ")) {
					boolean loop = true;
					while(loop == true){
						if(inputLine.contains("<h1 ")){
							String s = inputLine.substring(inputLine.indexOf("\">")+2,inputLine.indexOf("</h1>"));
							if(s.contains("- EP")) {
								albumType = "EP";
								s = s.replace("- EP", "").trim();
							}
							albumTitle = s.replace("&amp;", "&").replace("&quot;", "'").replace("â€™", "'").replace("Ã©", "é");
							
							inputLine = in.readLine();
							inputLine = in.readLine();
							String a;
							if(inputLine.contains("<a ")) {
								a = inputLine.substring(inputLine.indexOf("LinkToArtist&quot;}\">")+21,inputLine.indexOf("</a>"));
							} else {
								a = inputLine.trim();
							}
							artistName = a.replace("&amp;", "&").replace("&quot;", "'").replace("â€™", "'").replace("Ã©", "é");
							loop = false;
						} else {
							inputLine = in.readLine();
						}
					}
				}
				if(inputLine.contains("product-hero__tracks") && tracksParsed == false){ // MAIN TRACK TABLE
					Integer lastTrackNum = 0;
					boolean loop = true;
					while(loop == true){
						if(inputLine.contains("table__row__track")){ // a track line in table
							Integer trackNum;
							String trackTitle;
							String trackDuration;
							inputLine = in.readLine();
							if(inputLine.contains("table__row__star-rating")) {
								inputLine = in.readLine();
							}
							inputLine = in.readLine();

							// track number
							trackNum = Integer.parseInt(inputLine.trim());

							while(!inputLine.contains("table__row__headline ")) {
								inputLine = in.readLine();
							}
							
							// track title
							String s = inputLine.substring(inputLine.indexOf("\">") + 2);
							trackTitle = s.trim().replace("&amp;", "&").replace("&quot;", "'").replace("â€™", "'").replace("Ã©", "é").replace("Ã", "á")
									.replace("Ã£", "ã").replace("Ã³", "ó").replace("Ãº", "ú").replace("Ã§", "ç").replace("á¼", "ü").replace("á¯", "ï").replace("á¨", "è");
							
							while(!inputLine.contains("table__row__duration ")) {
								inputLine = in.readLine();
							}
							
							
							// track duration
							s = inputLine.substring(inputLine.indexOf("\">") + 2);
							trackDuration = s.substring(0,s.indexOf("</td>"));
							
							if(trackNum < lastTrackNum){
								discCount++;
							}
							lastTrackNum = trackNum;
							AlbumTrack at = new AlbumTrack(discCount, trackNum, trackTitle, trackDuration);
							tracklist.add(at);

							inputLine = in.readLine();
						} else {
							inputLine = in.readLine();
						}
						if(inputLine.contains("</table>")){ // end of track table
							loop = false;
							tracksParsed = true;
							Collections.sort(tracklist);
						}
					}
				}
				if (inputLine.contains("product-artwork") && albumArworkURL.equals("")) {
					String artworkID = inputLine.substring(inputLine.indexOf("<source srcset=\"") + 16, inputLine.indexOf(".jpg/") + 4);
					albumArworkURL = artworkID + "/9999x0w.jpg";
				}

				inputLine = in.readLine();
			}

			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static void resultWindow() {
		resultFrame = new JFrame("Results");
		File resultPath = new File(System.getProperty("user.dir"));
		fc = new JFileChooser(resultPath);

		FileFilter filter = new FileNameExtensionFilter("Text file (*.txt)", "txt");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
		int x = (int) (frameSize.width / 2);
		int y = (int) (frameSize.height / 2);
		resultFrame.setBounds(x, y, 600, frameSize.height);

		resultText = new JTextArea();
		resultText.setText("");
		resultText.setEditable(false);

		JButton saveFile = new JButton("Save Results");
		saveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					String filepath = f.getAbsolutePath();
					String filename = f.getName();

					if (!filename.contains(".txt")) {
						resultFile = new File(filepath + ".txt");
					} else {
						resultFile = f;
					}

					try {
						Files.write(Paths.get(resultFile.getAbsolutePath()), resultString.getBytes());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JPanel controls = new JPanel();
		controls.setLayout(new FlowLayout());
		controls.add(saveFile);

		resultFrame.getContentPane().add(new JScrollPane(resultText), BorderLayout.CENTER);
		resultFrame.getContentPane().add(controls, BorderLayout.SOUTH);
	}

}
