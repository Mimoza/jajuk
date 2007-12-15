/*
 *  Jajuk
 *  Copyright (C) 2005 The Jajuk Team
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  $$Revision$$
 */
package org.jajuk.ui.action;

import org.jajuk.Main;
import org.jajuk.ui.wizard.SimpleDeviceWizard;
import org.jajuk.util.IconLoader;
import org.jajuk.util.Messages;

import java.awt.event.ActionEvent;

/**
 * Action for displaying the tip of the day.
 */
public class WizardAction extends ActionBase {

  private static final long serialVersionUID = 1L;

  WizardAction() {
    super(Messages.getString("JajukJMenuBar.18"), IconLoader.ICON_WIZARD, true);
  }

  /**
   * Invoked when an action occurs.
   * 
   * @param evt
   */
  public void perform(ActionEvent evt) {
    SimpleDeviceWizard wizard = new SimpleDeviceWizard();
    wizard.pack();
    wizard.setLocationRelativeTo(Main.getWindow());
    wizard.setVisible(true);
  }
}
