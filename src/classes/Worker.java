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


public class Worker {

	private static JFrame resultFrame;
	private static JTextArea resultText;
	private static JFileChooser fc;
	private static File resultFile;
	private static String resultString;
	
	private static Integer discCount;
	private static String artistName;
	private static String albumTitle;
	private static String releaseDate;
	private static String albumArworkURL;
	private static List<AlbumTrack> tracklist;

	public static void main(String[] args) {
		resultWindow();
		
		discCount = 1;
		artistName = "";
		albumTitle = "";
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
		
		resultString = artistName + "\n" + albumTitle + "\n" + releaseDate + "\n\n" + tracklistAsText() + "\nSource URL:\n" + albumURL
				+ "\n\nArtwork downloaded to Downloads/covers.";
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
				if (inputLine.contains("release-date")) {
					String s = inputLine.substring(inputLine.indexOf("Released:"));
					s = s.substring(s.indexOf("content=\"")+9);
					s = s.substring(0,s.indexOf("\">"));
					releaseDate = s;
				}
				if(inputLine.contains("itemtype=\"http://schema.org/MusicAlbum\">")){ // grab album title and artist name
					boolean loop = true;
					while(loop == true){
						if(inputLine.contains("<h1 itemprop=\"name\">")){
							String s = inputLine.substring(inputLine.indexOf("\">")+2,inputLine.indexOf("</h1>"));
							albumTitle = s;
							
							inputLine = in.readLine();
							String a = inputLine.substring(inputLine.indexOf("\"name\">")+7,inputLine.indexOf("</h2>"));
							artistName = a;
							loop = false;
						} else {
							inputLine = in.readLine();
						}
					}
				}
				if(inputLine.contains("class=\"track-list album music\"") && tracksParsed == false){ // MAIN TRACK TABLE
					Integer lastTrackNum = 0;
					boolean loop = true;
					while(loop == true){
						if(inputLine.contains("itemtype=\"http://schema.org/MusicRecording\"")){ // a track line in table
							Integer trackNum;
							String trackTitle;
							String trackDuration;
							inputLine = in.readLine();
							inputLine = in.readLine();
							inputLine = in.readLine();

							// track number
							trackNum = Integer.parseInt(inputLine.substring(inputLine.indexOf("<span>")+6,inputLine.indexOf("</span>")));
							inputLine = in.readLine();
							inputLine = in.readLine();
							inputLine = in.readLine();
							inputLine = in.readLine();
							
							// track title
							String s = inputLine.substring(inputLine.indexOf("\"text\">") + 7);
							trackTitle = s.substring(0, s.indexOf("</span>")).replace("&amp;", "&").replace("&quot;", "'");
							inputLine = in.readLine();
							inputLine = in.readLine();
							inputLine = in.readLine();
							inputLine = in.readLine();
							inputLine = in.readLine();
							inputLine = in.readLine();
							inputLine = in.readLine();
							inputLine = in.readLine();
							
							// track duration
							trackDuration = inputLine.substring(inputLine.indexOf("\"text\">")+7,inputLine.indexOf("</span>"));
							
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
				if (inputLine.contains("class=\"lockup product album music\">")) {
					inputLine = in.readLine();
					inputLine = in.readLine();
					String thumbnailURL = inputLine.substring(inputLine.indexOf("src-swap-high-dpi=\"") + 19,
							inputLine.indexOf("\" src-load-auto-after-dom-load"));
					String artworkID = thumbnailURL.substring(thumbnailURL.indexOf("Music"), thumbnailURL.indexOf("/cover"));
					albumArworkURL = "http://is5.mzstatic.com/image/thumb/" + artworkID + "/source/100000x100000-999.jpg";
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
