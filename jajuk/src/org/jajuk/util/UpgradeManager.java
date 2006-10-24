/*
 *  Jajuk
 *  Copyright (C) 2006 bflorat
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
 *  $Revision$
 */

package org.jajuk.util;

import java.io.File;

import org.jajuk.dj.AmbienceManager;
import org.jajuk.ui.perspectives.CatalogPerspective;
import org.jajuk.ui.perspectives.ConfigurationPerspective;
import org.jajuk.ui.perspectives.HelpPerspective;
import org.jajuk.ui.perspectives.InfoPerspective;
import org.jajuk.ui.perspectives.LogicalPerspective;
import org.jajuk.ui.perspectives.PhysicalPerspective;
import org.jajuk.ui.perspectives.PlayerPerspective;
import org.jajuk.ui.perspectives.StatPerspective;

/**
 *  Maintain all behavior needed upgrades from releases to releases
 *
 * @author     Bertrand Florat
 * @created    21 oct. 06
 */
public class UpgradeManager implements ITechnicalStrings{
    /**
     * Actions to migrate an existing installation
     * Step1 just at startup
     *
     */
    public static void upgradeStep1() throws Exception {
        //--For jajuk < 0.2 : remove backup file : collection~.xml
        File file = new File(FILE_COLLECTION+"~"); //$NON-NLS-1$
        file.delete();
        //upgrade code; if ugrade from <1.2, set default ambiences
        String sRelease = ConfigurationManager.getProperty(CONF_RELEASE);
        if (sRelease == null || sRelease.matches("0..*") //$NON-NLS-1$
                || sRelease.matches("1.0..*") //$NON-NLS-1$
                || sRelease.matches("1.1.*")){ //$NON-NLS-1$
            AmbienceManager.getInstance().createDefaultAmbiences();
        }
        //- For Jajuk < 1.3 : changed track pattern from %track to %title
        String sPattern = ConfigurationManager.getProperty(CONF_REFACTOR_PATTERN);
        if (sPattern.contains("track")){
            ConfigurationManager.setProperty(CONF_REFACTOR_PATTERN, 
                sPattern.replaceAll("track", "title"));
        }
        
    }
    
    /**
     * Actions to migrate an existing installation
     * Step 2 at the end of UI startup
     */
    public static void upgradeStep2() throws Exception {
        
    }
    
    
    /**
     * 
     * @return array of perspectives to be reseted
     */
    public static Class[] getPerspectivesToReset(){
        return new Class[]{PhysicalPerspective.class,LogicalPerspective.class,
             CatalogPerspective.class,ConfigurationPerspective.class,
             HelpPerspective.class,InfoPerspective.class,
             PlayerPerspective.class,StatPerspective.class};
    }
}
