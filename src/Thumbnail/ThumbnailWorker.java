package Thumbnail;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

//@Editor Mike Lee
public class ThumbnailWorker extends SwingWorker<Void, String> {
	private List<File> _imageFiles;
	private File _outputDirectory;
	private JTextArea _outputLog;
	private JProgressBar _progressBar;
	private ThumbnailWorker _worker;
	
	public ThumbnailWorker(List<File> imageFiles, JTextArea outputLog, File outputDirectory, JProgressBar progressBar) {
		_imageFiles = new ArrayList<File>();
		_imageFiles = imageFiles;
		_outputDirectory = outputDirectory;
		_outputLog = outputLog;
		_progressBar = progressBar;
		
	}
	
	@Override
	protected Void doInBackground() throws Exception {
        // Generate thumbnails.
		double size = _imageFiles.size();
		double count = 1;
        for(File image : _imageFiles) {
        	try {
        		publish("Currently Processsing " + image.getName());

        		createThumbnail(image, _outputDirectory);
        		publish("Processed " + image.getName());
        		Double percentage = new Double((count/size) * 100);
        		setProgress(percentage.intValue());

        		count++;
        		//_outputLog.append("Processed " + image.getName() + "\n");
        		
        	} catch(IOException e) {
        		e.printStackTrace();
        	}
		}
		return null;

	}
	@Override
	protected void process(final List<String> chunks) {
		// Updates the messages text area
		for (final String string : chunks) {
			_outputLog.append(string + "\n");
			Integer prog = new Integer(getProgress());
			_outputLog.append(prog.toString() + "%\n");
			_progressBar.setValue(prog);
		}
	}

	private static void createThumbnail(File imageFile, File outputDirectory) throws IOException {
		BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		img.createGraphics().drawImage(ImageIO.read(imageFile).getScaledInstance(100, 100, Image.SCALE_SMOOTH),0,0,null);
		
		File thumbnailFile = new File(outputDirectory.getCanonicalPath() + File.separator + imageFile.getName());
		ImageIO.write(img, "jpg", thumbnailFile);
	}



}
