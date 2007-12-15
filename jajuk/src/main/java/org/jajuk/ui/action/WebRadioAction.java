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
 *  $$Revision: 2523 $$
 */

package org.jajuk.ui.action;

import org.jajuk.base.FIFO;
import org.jajuk.base.WebRadio;
import org.jajuk.util.ConfigurationManager;
import org.jajuk.util.IconLoader;
import org.jajuk.util.Messages;
import org.jajuk.util.error.JajukException;
import org.jajuk.webradio.WebRadioManager;

import java.awt.event.ActionEvent;

public class WebRadioAction extends ActionBase {

  private static final long serialVersionUID = 1L;

  WebRadioAction() {
    super(Messages.getString("CommandJPanel.25"), IconLoader.ICON_WEBRADIO, true);
    setShortDescription(WebRadioManager.getCurrentWebRadioTooltip());
  }

  public void perform(ActionEvent evt) throws JajukException {
    new Thread() {
      public void run() {
        WebRadio radio = WebRadioManager.getInstance().getWebRadioByName(
            ConfigurationManager.getProperty(CONF_DEFAULT_WEB_RADIO));
        FIFO.getInstance().launchRadio(radio);
      }
    }.start();
  }

}
