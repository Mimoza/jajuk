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
 * Revision 1.3  2003/10/23 22:07:40  bflorat
 * 23/10/2003
 *
 */
package org.jajuk.base;

import java.util.ArrayList;

/**
 *  A music style ( jazz, rock...)
 *<p> Logical item
 * @author     bflorat
 * @created    17 oct. 2003
 */
public class Style extends PropertyAdapter {

	/** Style ID. Ex:1,2,3...*/
	private String sId;
	/**Style name upper case. ex:ROCK, JAZZ */
	private String sName;
	/**Authors for this style*/
	private ArrayList alAuthors = new ArrayList(10);

	/**
	 * Style constructor
	 * @param id
	 * @param sName
	 */
	//TODO: see javadoc/arguments auto
	public Style(String sId, String sName) {
		this.sId = sId;
		this.sName = sName;
	}

	/**
	 * @return
	 */
	public String getName() {
		return sName;
	}

	/**
	 * toString method
	 */
	public String toString() {
		return "Style[IS="+sId+" Name=" + getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

		/**
	 * Return an XML representation of this item  
	 * @return
	 */
	public String toXml() {
		StringBuffer sb = new StringBuffer("\t\t<style id='" + sId);
		sb.append("' name='");
		sb.append(sName).append("'/>\n");
		return sb.toString();
	}

	/**
	* @return
	 */
	public String getId() {
		return sId;
	}
	
	/**
	 * Equal method to check two styles are identical
	 * @param otherStyle
	 * @return
	 */
	public boolean equals(Style otherStyle){
		return getName().equals(otherStyle.getName());
	}	
	
	/**
			 * @return
			 */
			public ArrayList getAuthors() {
				return alAuthors;
			}

			/**
			 * @param album
			 */
			public void addAuthor(Author author) {
				alAuthors.add(author);
			}


}
