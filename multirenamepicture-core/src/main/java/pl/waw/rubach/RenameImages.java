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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

/**
 * Created by pol on 08.10.16.
 */
public class RenameImages {
    private JButton run;
    private JTextField directory;
    private JCheckBox undo;
    private JTextField offset;
    private JTextField format;
    private JTextArea result;
    private JButton chooseDir;
    private JPanel panel1;
    private JLabel formatLabel;
    private JCheckBox cameraName;

    private File propFile = new File(System.getProperty("user.home") + File.separator + ".renimages.properties");

    private File dirFile;


    public void init() {
        String tooltip = "<html>For example:<br>" +
                "yyyyMMdd_HHmmss<br>" +
                "yyyy-MM-dd_HHmmss<br>" +
                "yyyyMMddHHmmss<br>" +
                "yyyyMMdd</html>";

        format.setToolTipText(tooltip);
        formatLabel.setToolTipText(tooltip);

        chooseDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser(readLastDirectory());
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fc.showOpenDialog(directory);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    dirFile = fc.getSelectedFile();
                    directory.setText(dirFile.getAbsolutePath());
                    saveLastDirectory(dirFile);
                }
            }
        });

        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Long offsetSec = Long.parseLong(offset.getText());
                    MultiRenamePicture.runRename(new File(directory.getText()), format.getText(), offsetSec, undo.isSelected(), false, cameraName.isSelected(), result);
                } catch (NumberFormatException pe) {
                    JOptionPane.showMessageDialog(null, "Offset must be a number");
                } catch (IOException io) {
                    JOptionPane.showMessageDialog(null, "Problem running rename: " + io.getMessage());
                }
            }
        });


    }

    public File readLastDirectory() {
        if (propFile.exists() && propFile.canRead()) {
            Properties prop = new Properties();
            InputStream input = null;
            try {
                input = new FileInputStream(propFile);
                // load a properties file
                prop.load(input);
                // get the property value and print it out
                String lastDirS = prop.getProperty("lastdir");
                if ((lastDirS!=null) && (new File(lastDirS).exists() && new File(lastDirS).isDirectory()))
                    return new File(lastDirS);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new File(System.getProperty("user.home"));
    }

    public void saveLastDirectory(File lastDir) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            output = new FileOutputStream(propFile);
            // set the properties value
            prop.setProperty("lastdir", lastDir.getAbsolutePath());
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public JPanel getPanel1() {
        return panel1;
    }

}
