/*
 *  Jajuk
 *  Copyright (C) 2003-2008 The Jajuk Team
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
 *  $Revision: 3132 $
 */
package org.jajuk.ui.widgets;

import javax.swing.Action;
import javax.swing.Icon;

import org.jajuk.util.IconLoader;
import org.jajuk.util.JajukIcons;

/**
 * 
 */
public class JajukButtonSetIconAdapter extends JajukButton {

  private static final long serialVersionUID = 8827520017323338775L;

  public JajukButtonSetIconAdapter(Action a) {
    super(a);
  }

  @Override
  public void setIcon(Icon icon) {
    Icon destIcon = null;
    if (icon.equals(IconLoader.getIcon(JajukIcons.PAUSE))) {
      destIcon = IconLoader.getIcon(JajukIcons.PLAYER_PAUSE_BIG);
    } else if (icon.equals(IconLoader.getIcon(JajukIcons.PLAY))) {
      destIcon = IconLoader.getIcon(JajukIcons.PLAYER_PLAY_BIG);
    }

    if (destIcon == null) {
      super.setIcon(icon);
    } else {
      super.setIcon(destIcon);
    }
  }

}