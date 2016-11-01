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
import java.io.File;
import java.io.IOException;

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
                final JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fc.showOpenDialog(directory);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    dirFile = fc.getSelectedFile();
                    directory.setText(dirFile.getAbsolutePath());
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

    public JPanel getPanel1() {
        return panel1;
    }

}
