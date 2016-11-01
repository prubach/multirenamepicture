/*
 * Copyright 2008 the original author or authors.
 * Copyright 2005 Sun Microsystems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.waw.rubach;

import java.awt.*;
import java.io.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileMetadataDirectory;
import org.apache.commons.cli.*;

import javax.swing.*;

public class MultiRenamePicture {
	private static final String TEST = "y";
	private static final String UNDO = "u";
	private static final String FORMAT = "f";
	private static final String DATE_DIFF = "m";
	private static final String FOLDER = "d";
	private static final String HELP = "h";
	private static final String CAMERA = "c";
	
	private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd_HHmmss";

	private static Options buildOptions() {
		Options options = new Options();
		Option testOpt = new Option(TEST, "run", false, "Without this option the tool will only show how files will ne renamed");
		testOpt.setType(Boolean.class);
		testOpt.setArgName(TEST);
		options.addOption(testOpt);

		Option undo = new Option(UNDO, "undo", false, "Undo the renaming");
		undo.setType(Boolean.class);
		undo.setArgName(UNDO);
		options.addOption(undo);

		Option camera = new Option(CAMERA, "camera", false, "Add camera name to file name");
		camera.setType(Boolean.class);
		camera.setArgName(CAMERA);
		options.addOption(camera);

		Option dir = new Option(FOLDER, "dir", true,
				"Directory where images are located");
		dir.setType(String.class);
		dir.setArgs(1);
		dir.setArgName(FOLDER);
		options.addOption(dir);

		Option sysProps = new Option(FORMAT, "format", true,
				"Set the date format for renaming, for example:\n"
				+ "yyyyMMdd_HHmmss\n"
				+ "yyyy-MM-dd_HHmmss\n"
				+ "yyyyMMddHHmmss\n"
				+ "yyyyMMdd");
		sysProps.setType(String.class);
		sysProps.setArgs(1);
		sysProps.setArgName(FORMAT);
		options.addOption(sysProps);

		Option dateDiff = new Option(DATE_DIFF, "modify", true,
				"Modify the original date by given umber of seconds (can be a positive or negative number)");
		dateDiff.setType(Long.class);
		dateDiff.setArgs(1);
		dateDiff.setArgName(DATE_DIFF);
		options.addOption(dateDiff);
		
		Option help = new Option(HELP, "help", false,
				"Print help");
		help.setType(Boolean.class);
		help.setArgs(0);
		options.addOption(help);
			
		return options;
	}

	public static void runRename(File dir, String dateFormat, Long dateDiff, boolean isUndo, boolean isTest, boolean cameraName, JTextArea outTA) throws IOException {
			File children[] = dir.listFiles();
			FilenameFilter filter = new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return (name.endsWith(".jpg") || name.endsWith(".JPG"));
				}
			};
			children = dir.listFiles(filter);
			if (children==null || children.length==0)
				System.out.println("No files found to rename! For help run: \"renimages --help\"");
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					File picture = children[i];
					Date when = null;
					Long origTimeStamp = null;
					String prefixName = null;
					String newName = null;
					try {
						Metadata metadata = ImageMetadataReader.readMetadata(picture);
						// obtain the Exif directory
						ExifSubIFDDirectory directory
								= metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
						if (cameraName) {
							if (directory != null) {
								if ((directory.getString(ExifSubIFDDirectory.TAG_MAKE)!=null) && (directory.getString(ExifSubIFDDirectory.TAG_MODEL)!=null)) {
									prefixName = directory.getString(ExifSubIFDDirectory.TAG_MAKE).trim()
											+ "_" + directory.getString(ExifSubIFDDirectory.TAG_MODEL).trim()
											+ "_";
								} else {
									ExifIFD0Directory dir0
											= metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
									prefixName = (dir0.getString(ExifIFD0Directory.TAG_MODEL)!=null ? dir0.getString(ExifIFD0Directory.TAG_MODEL).trim() : "UNKNOWN") + "_";
								}
								newName = prefixName + children[i].getName();
							}
							else {
								continue;
							}
						}
						if (directory!=null && directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)!=null) {// query the tag's value
							origTimeStamp
									= directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL).getTime();
						} else {
							FileMetadataDirectory fileMetadataDirectory = metadata.getFirstDirectoryOfType(FileMetadataDirectory.class);
							if (fileMetadataDirectory!=null) origTimeStamp = fileMetadataDirectory.getDate(FileMetadataDirectory.TAG_FILE_MODIFIED_DATE).getTime();
							else throw new IOException();
						}
					} catch (IOException | ImageProcessingException ie) {
						if (outTA!=null) outTA.append("Problem reading EXIF for file: " + picture.getAbsolutePath() + "\n");
						origTimeStamp = picture.lastModified();
					}
					when = new Date(origTimeStamp + dateDiff*1000);
					SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
					sdf.setTimeZone(TimeZone.getDefault());
					String display = sdf.format(when);

					StringBuilder output = new StringBuilder();
					output
							.append(isTest ? "It would rename: " : "Renaming: ")
							.append(children[i].getName())
							.append(" to: ")
							.append(dir.getCanonicalPath())
							.append(File.separatorChar);

					if (newName==null)
						newName = new StringBuilder()
							.append(display)
							.append("_")
							.append(children[i].getName()).toString();
					if (isUndo && !cameraName)
						newName = children[i].getName().replace(display+"_", "");
					else if (isUndo && cameraName)
						newName = children[i].getName().replace(prefixName, "");

					output.append(newName);

					if (outTA!=null) outTA.append(output.toString()+"\n");
					else System.out.println(output.toString());
					if (!isTest) {
						children[i].renameTo(
								new File(
										(new StringBuilder(String.valueOf(dir.getCanonicalPath())))
												.append(File.separatorChar).append(newName).toString()));
					}
				}
			}
	}


	public static void main(String args[]) {
		try {
			Options options = buildOptions();
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);
			
			String dateFormat = DEFAULT_DATE_FORMAT;
			Boolean onlyTest = true;
			Boolean undo = false;
			Boolean cameraName = false;
			File dir = new File("");
			Long dateDiff = 0L;
			
			/*for (Option o : cmd.getOptions()) {
				System.out.println("Option: " + o.getArgName() 
					+ "\n" + o.getValue());
			}*/	

			if (cmd.hasOption(HELP)) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp(160,
                        "renimages [options]",
                        "Rename pictures using their creation date", options, null
                );
                System.exit(0);
			}
			if (cmd.hasOption(FORMAT)) {
				//System.out.println("got format: " + cmd.getOptionValue(FORMAT));
				dateFormat = cmd.getOptionValue(FORMAT);	          
			}			
			if (cmd.hasOption(TEST)) {
				onlyTest = false;
				//System.out.println("got run");
			}
			
			//System.out.println("run: " +!onlyTest);
			if (cmd.hasOption(UNDO)) {
				//System.out.println("got undo");
				undo = true;
			}
			if (cmd.hasOption(CAMERA)) {
				//System.out.println("got undo");
				cameraName = true;
			}
			if (cmd.hasOption(FOLDER)) {
				//System.out.println("got: " + cmd.getOptionValue(FOLDER));
				dir = new File(cmd.getOptionValue(FOLDER));	          
			}		
			if (cmd.hasOption(DATE_DIFF)) {
				dateDiff = Long.parseLong(cmd.getOptionValue(DATE_DIFF));
			}

			if (cmd.getOptions().length==0) {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (UnsupportedLookAndFeelException ue) {
				} catch (IllegalAccessException e) {
				} catch (InstantiationException e) {
				} catch (ClassNotFoundException e) {
				}
				JFrame frame = new JFrame("RenameImages");
				RenameImages renameImages = new RenameImages();
				renameImages.init();
				frame.setContentPane(renameImages.getPanel1());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			} else {

				try {
					runRename(dir, dateFormat, dateDiff, undo, onlyTest, cameraName, null);
				} catch (IOException e) {
					System.out.println("Caught error while trying to rename: " + e.getMessage());
				}
			}

		} catch (ParseException pe) {
			System.out.println("Command not parsed correctly");
		}

	}

}
