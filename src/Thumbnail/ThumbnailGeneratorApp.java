package Thumbnail;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Swing program that allows users to generate thumbnail images from full-size 
 * images. The program presents a simple GUI. Using the GUI users can select a
 * directory on disk that contains jpeg (.jpg) files. The program creates a 
 * subdirectory named "thumbnails", and generates and stores the thumbnail 
 * images in the new subdirectory. 
 * 
 * @author Ian Warren
 * @Editor Mike Lee
 */
@SuppressWarnings("serial")
public class ThumbnailGeneratorApp extends JPanel {

	private JButton _startBtn;        // Button to start the thumbnail generation process.
	private JButton _cancelBtn;		  // Button to cancel thumbnail generation.
	private JTextArea _outputLog;  	  // Component to display in-progress messages.
	private JProgressBar _progressBar; 
	
	private List<File> _imageFiles;	  // List of image files for which thumbnails should be generated.
	private File _outputDirectory;	  // Output directory for storing thumbnails.
	private ThumbnailWorker tw;
	
	public ThumbnailGeneratorApp() {
		
		_startBtn = new JButton("Process");
		_cancelBtn = new JButton("Cancel");
		_progressBar = new JProgressBar();
		_progressBar.setVisible(true);        
        //_progressBar.setStringPainted(true);
        _progressBar.setValue(0);
		_outputLog = new JTextArea();
		_outputLog.setEditable(false);
		
		// Register a handler for Process buttons clicks.
		_startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				
				// Use a FileChooser Swing component to allow the user to 
				// select a directory where images are stored.
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showDialog(ThumbnailGeneratorApp.this, "Select");

				// Whenever the user selects a directory ...
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File directory = fc.getSelectedFile();
		            
		            // Created a subdirectory named "thumbnails" to store the
		            // generated thumbnails. If the subdirectory already exists
		            // no action is taken.
		            try {
		            	String pathname = directory.getCanonicalPath() + File.separator + "thumbnails";
		            	_outputDirectory = new File(pathname);
		            	_outputDirectory.mkdir();
		            } catch(IOException e) {
		            	e.printStackTrace();
		            }
		            	
		            // Scan the selected directory for all files with a "jpg" 
		            // extension. Store these files in a List.
		            _imageFiles = new ArrayList<File>();
		            File[] contents = directory.listFiles();
		            for(int i = 0; i < contents.length; i++) {
		            	File file = contents[i];
		            	String filename = file.getName();
		            	String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
		            	if(file.isFile() && extension.equals("jpg")) {
		            		_imageFiles.add(file);
		            	}
		            			
		            }
		            
		            // Set the enabled state for buttons.
		            _startBtn.setEnabled(false);
		            _cancelBtn.setEnabled(true);
		            
		            // clear the output log.
		            _outputLog.setText(null);
		            
		            // Set the cursor to busy.
		            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		            
		            tw = new ThumbnailWorker(_imageFiles, _outputLog, _outputDirectory, _progressBar);
		            tw.execute();
		            // Generate thumbnails.
		            
//		            for(File image : _imageFiles) {
//		            	try {
//		            		createThumbnail(image, _outputDirectory);
//		            		_outputLog.append("Processed " + image.getName() + "\n");
//		            	} catch(IOException e) {
//		            		e.printStackTrace();
//		            	}
//					}
		            
		            // Set the enabled state for buttons and restore the cursor.
		            _startBtn.setEnabled(true);
					_cancelBtn.setEnabled(true);
					setCursor(Cursor.getDefaultCursor());
		        } 	
			}
		});
		
		// Register a handler for Cancel button clicks.
		_cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If a SwingWorker was used, we could cancel it.
				tw.cancel(true);
				_outputLog.append("Cancelled\n");
				_cancelBtn.setEnabled(false);
			}
		});
		
		// Construct the GUI. 
		JPanel controlPanel = new JPanel();
		controlPanel.add(_startBtn);
		controlPanel.add(_cancelBtn);
		controlPanel.add(_progressBar);
		_cancelBtn.setEnabled(false);
		
		JScrollPane scrollPaneForOutput = new JScrollPane();
		scrollPaneForOutput.setViewportView(_outputLog);
		
		setLayout(new BorderLayout());
		add(controlPanel, BorderLayout.NORTH);
		add(scrollPaneForOutput, BorderLayout.CENTER);
		setPreferredSize(new Dimension(400,300));
	}

	/**
	 * Helper method to display the GUI.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("Thumbnail Image Creator");

		// Create and set up the content pane.
		JComponent newContentPane = new ThumbnailGeneratorApp();
		frame.add(newContentPane);

		// Display the window.
		frame.pack();
        frame.setLocationRelativeTo(null); 
		frame.setVisible(true);
	}

	/**
	 * Helper method to generate a thumbnail image for a particular image file.
	 * 
	 * @param imageFile the source image file.
	 * @param outputDirectory the directory in which to store the generated thumbnail.
	 * 
	 * @throws IOException if there is an error with loading images files or saving thumbnails.
	 * 
	 */
	private static void createThumbnail(File imageFile, File outputDirectory) throws IOException {
		BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		img.createGraphics().drawImage(ImageIO.read(imageFile).getScaledInstance(100, 100, Image.SCALE_SMOOTH),0,0,null);
		
		File thumbnailFile = new File(outputDirectory.getCanonicalPath() + File.separator + imageFile.getName());
		ImageIO.write(img, "jpg", thumbnailFile);
	}
	
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}

