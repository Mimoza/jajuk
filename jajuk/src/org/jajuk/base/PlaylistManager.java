/*
 *  Jajuk
 *  Copyright (C) 2003 bflorat
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
 * $Log$
 * Revision 1.3  2003/10/26 21:28:49  bflorat
 * 26/10/2003
 *
 * Revision 1.2  2003/10/23 22:07:40  bflorat
 * 23/10/2003
 *
 * Revision 1.1  2003/10/21 17:51:43  bflorat
 * 21/10/2003
 *
 */

package org.jajuk.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.jajuk.util.MD5Processor;

/**
 *  Convenient class to manage playlists
 * @Author    bflorat
 * @created    17 oct. 2003
 */
public class PlaylistManager {
	/**Playlists collection**/
	static HashMap hmPlaylists = new HashMap(100);

	/**
	 * No constructor available, only static access
	 */
	private PlaylistManager() {
		super();
	}

	/**
	 * Register an Playlist
	 *@param files : playlist files
	 */	
	public static Playlist registerPlaylist(ArrayList alPlaylistFiles) {
		Iterator it = alPlaylistFiles.iterator();
		String sName ="";
		while (it.hasNext()){
			PlaylistFile pf = (PlaylistFile)it.next();
			sName += pf.getId();
		}
		String sId = MD5Processor.hash(sName);
		Playlist playlist = new Playlist(sId,alPlaylistFiles);
		hmPlaylists.put(sId,playlist);
		return playlist;
	}


	/**Return all registred Playlists*/
	public static ArrayList getPlaylists() {
		return new ArrayList(hmPlaylists.values());
	}

	/**
	 * Return Playlist by id
	 * @param sId
	 * @return
	 */
	public static Playlist getPlaylist(String sId) {
		return (Playlist) hmPlaylists.get(sId);
	}

	
}
