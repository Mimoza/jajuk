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
 *  $$Revision:3308 $$
 */
package org.jajuk.ui.actions;

import java.awt.event.ActionEvent;

import org.jajuk.events.JajukEvent;
import org.jajuk.events.JajukEvents;
import org.jajuk.events.ObservationManager;
import org.jajuk.services.players.QueueModel;
import org.jajuk.services.players.StackItem;
import org.jajuk.ui.widgets.CommandJPanel;
import org.jajuk.ui.widgets.JajukJMenuBar;
import org.jajuk.util.Conf;
import org.jajuk.util.Const;
import org.jajuk.util.IconLoader;
import org.jajuk.util.JajukIcons;
import org.jajuk.util.Messages;

public class RepeatModeAction extends JajukAction {
  private static final long serialVersionUID = 1L;

  RepeatModeAction() {
    super(Messages.getString("JajukJMenuBar.10"), IconLoader.getIcon(JajukIcons.REPEAT), "ctrl T",
        true, false);
    setShortDescription(Messages.getString("CommandJPanel.1"));
  }

  /**
   * Invoked when an action occurs.
   * 
   * @param evt
   */
  @Override
  public void perform(ActionEvent evt) {

    boolean b = Conf.getBoolean(Const.CONF_STATE_REPEAT);
    Conf.setProperty(Const.CONF_STATE_REPEAT, Boolean.toString(!b));

    JajukJMenuBar.getInstance().setRepeatSelected(!b);
    CommandJPanel.getInstance().setRepeatSelected(!b);

    if (!b) { // enabled button
      // if FIFO is not void, repeat over current item
      StackItem item = QueueModel.getCurrentItem();
      if (item != null) {
        item.setRepeat(true);
      }
    } else {// disable repeat mode
      if (!Conf.getBoolean(Const.CONF_STATE_REPEAT_ALL)) {
        // remove repeat mode to all items
        QueueModel.setRepeatModeToAll(false);
        if (Conf.getBoolean(Const.CONF_DROP_PLAYED_TRACKS_FROM_QUEUE)) {
          // remove tracks before current position
          QueueModel.remove(0, QueueModel.getIndex() - 1);
          QueueModel.setIndex(0); // select first track
        }
      }
    }
    // computes planned tracks
    QueueModel.computesPlanned(false);
    // Refresh Queue View
    ObservationManager.notify(new JajukEvent(JajukEvents.QUEUE_NEED_REFRESH));
  }
}
