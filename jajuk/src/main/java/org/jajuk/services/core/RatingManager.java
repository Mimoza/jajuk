/*
 *  Jajuk
 *  Copyright (C) 2007 The Jajuk Team
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

package org.jajuk.services.core;

import java.util.HashSet;
import java.util.Set;

import org.jajuk.base.FileManager;
import org.jajuk.base.Track;
import org.jajuk.base.TrackManager;
import org.jajuk.events.Event;
import org.jajuk.events.JajukEvents;
import org.jajuk.events.ObservationManager;
import org.jajuk.events.Observer;
import org.jajuk.util.Const;
import org.jajuk.util.error.JajukException;
import org.jajuk.util.log.Log;

/**
 * This thread is responsible for refreshing elements related to ratings (UI
 * refresh in tables, bestof files computations...)
 * <p>
 * It exists for performance reasons
 * </p>
 * <p>
 * Singleton
 * <p>
 */
public final class RatingManager extends Thread implements Const, Observer {

  private static RatingManager self;

  /**
   * Flag the fact a rate has change for a track, used by bestof view refresh
   * for perfs
   */
  private static boolean bRateHasChanged = true;

  /** Max rate */
  private static long lMaxPlaycount = 0l;

  private RatingManager() {
    // set thread name
    super("Rating Manager Thread");
    setPriority(Thread.MIN_PRIORITY);
      // Look for events
    ObservationManager.register(this);
  }

  public static RatingManager getInstance() {
    if (self == null) {
      self = new RatingManager();
    }
    return self;
  }

  @Override
  public void run() {
    while (!ExitService.isExiting()) {
      // Computes every 10 mins, until jajuk ends
      try {
        Thread.sleep(600000);
        // Computes bestof
        FileManager.getInstance().refreshBestOfFiles();
      } catch (InterruptedException e) {
        Log.error(e);
      }
      if (bRateHasChanged) {
        // refresh to update rates
        ObservationManager.notify(new Event(JajukEvents.EVENT_RATE_CHANGED));
        bRateHasChanged = false;
      }

    }
  }

  /**
   * @return maximum rating between all tracks
   */
  public static long getMaxPlaycount() {
    return lMaxPlaycount;
  }

  /**
   * Set max playcount
   * 
   * @param value
   *          the playcount value
   */
  public static void setMaxPlaycount(long value) {
    lMaxPlaycount = value;
    // Means that the playcount has been reset so recompute them
    if (lMaxPlaycount == 0) {
      // Computes bestof
      FileManager.getInstance().refreshBestOfFiles();
    }
  }

  /**
   * @return Returns the bRateHasChanged.
   */
  public static boolean hasRateChanged() {
    return bRateHasChanged;
  }

  /**
   * @param rateHasChanged
   *          The bRateHasChanged to set.
   */
  public static void setRateHasChanged(boolean rateHasChanged) {
    bRateHasChanged = rateHasChanged;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.events.Observer#getRegistrationKeys()
   */
  public Set<JajukEvents> getRegistrationKeys() {
    Set<JajukEvents> eventSubjectSet = new HashSet<JajukEvents>();
    eventSubjectSet.add(JajukEvents.EVENT_RATE_RESET);
    return eventSubjectSet;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.events.Observer#update(org.jajuk.events.Event)
   */
  public void update(Event event) {
    JajukEvents subject = event.getSubject();
    if (subject.equals(JajukEvents.EVENT_RATE_RESET)) {
      // Reset playcount
      setMaxPlaycount(0);
      // Reset rates
      for (final Track track : TrackManager.getInstance().getTracks()) {
        try {
          TrackManager.getInstance().changeTrackRate(track,0l);
        } catch (JajukException e) {
          Log.error(e);
          return;
        }
      }
      ObservationManager.notify(new Event(JajukEvents.EVENT_DEVICE_REFRESH));
      // Force suggestion view refresh. Not that the suggestion view doesn't
      // subscribe to EVENt_RATE_RESET event directly because we don't ensure
      // that the view will trap the event only after this class
      ObservationManager.notify(new Event(JajukEvents.EVENT_SUGGESTIONS_REFRESH));
      // Computes bestof
      FileManager.getInstance().refreshBestOfFiles();
    }
  }

}
