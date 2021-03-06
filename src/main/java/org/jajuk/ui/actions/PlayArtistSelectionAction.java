/*
 *  Jajuk
 *  Copyright (C) The Jajuk Team
 *  http://jajuk.info
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
 *  
 */
package org.jajuk.ui.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import org.jajuk.base.Artist;
import org.jajuk.base.File;
import org.jajuk.base.Track;
import org.jajuk.services.players.QueueModel;
import org.jajuk.util.Conf;
import org.jajuk.util.Const;
import org.jajuk.util.IconLoader;
import org.jajuk.util.JajukIcons;
import org.jajuk.util.Messages;
import org.jajuk.util.UtilFeatures;
import org.jajuk.util.log.Log;

/**
 * Play artists a selection. We expect the selection to be tracks and we play
 * only the first found artist
 * <p>
 * Action launcher is responsible to ensure all items provided share the same
 * type
 * </p>
 * <p>
 * Selection data is provided using the swing properties DETAIL_SELECTION
 * </p>
 */
public class PlayArtistSelectionAction extends SelectionAction {
  /** Generated serialVersionUID. */
  private static final long serialVersionUID = -8078402652430413821L;

  /**
   * Instantiates a new play artist selection action.
   */
  PlayArtistSelectionAction() {
    super(Messages.getString("TracksTableView.12"), IconLoader.getIcon(JajukIcons.ARTIST), true);
    setShortDescription(Messages.getString("TracksTableView.12"));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.ui.actions.JajukAction#perform(java.awt.event.ActionEvent)
   */
  @Override
  public void perform(final ActionEvent e) throws Exception {
    new Thread("PlayArtistSelectionAction") {
      @Override
      public void run() {
        try {
          PlayArtistSelectionAction.super.perform(e);
          if (selection.size() == 0 || !(selection.get(0) instanceof Track)) {
            return;
          }
          // Select all files from the first found album
          Artist artist = ((Track) selection.get(0)).getArtist();
          List<File> files = UtilFeatures.getPlayableFiles(artist);
          QueueModel.push(
              UtilFeatures.createStackItems(UtilFeatures.applyPlayOption(files),
                  Conf.getBoolean(Const.CONF_STATE_REPEAT), true), false);
        } catch (Exception e) {
          Log.error(e);
        }
      }
    }.start();
  }
}
