/*
 *  Jajuk
 *  Copyright (C) 2003 The Jajuk Team
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
 *  $Revision:3950 $ 
 */

package org.jajuk.events;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import org.jajuk.util.Const;
import org.jajuk.util.log.Log;

/**
 * This is a mediator managing relationships between subjets and observers
 * <p>
 * All notification methods are synchronized to assure event order
 */
public final class ObservationManager implements Const {

  /** one event -> list of components */
  static ObserverRegistry observerRegistry = new ObserverRegistry();

  /**
   * Last event for a given subject (used for new objects that just registrated
   * to this subject)
   */
  static Map<JajukEvents, Properties> hLastEventBySubject = new HashMap<JajukEvents, Properties>(
      10);

  static volatile Queue<Event> vFIFO = new LinkedList<Event>();

  private static Thread t = new Thread("Observation Manager Thread") {
    @Override
    public void run() {
      while (true) {
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          Log.error(e);
        }

        final Event event = vFIFO.poll();
        if(event != null) {
          new Thread("Observation Manager Sync Notify Thread") { // launch
                                                                  // action
                                                                  // asynchronously
            @Override
            public void run() {
              notifySync(event);
            }
          }.start();
        }
      }
    }
  };

  /** 
   * Empty constructor to avoid instantiating utility class
   */
  private ObservationManager() {
  }
  
  /**
   * Register a component for a given subject
   * 
   * @param subject
   *          Subject ( event ) to observe
   * @param jc
   *          component to register
   */
  public static synchronized void register(Observer observer) {
    Set<JajukEvents> eventSubjectSet = observer.getRegistrationKeys();
    for (JajukEvents subject : eventSubjectSet) {
      Log.debug("Register: \"" + subject + "\" by: " + observer);
      observerRegistry.register(subject, observer);
    }
  }

  /**
   * Unregister a component for a given subject
   * 
   * @param subject
   *          Subject ( event ) to observe
   * @param jc
   *          component to deregister
   */
  public static boolean unregister(JajukEvents subject, Observer observer) {
    return observerRegistry.unregister(subject, observer);
  }

  /**
   * Notify all components having registered for the given subject
   * 
   * @param subject
   */
  public static void notify(Event event) {
    // asynchronous notification by default to avoid
    // exception throw in the register current thread
    notify(event, false);
  }

  /**
   * Notify synchronously all components having registered for the given subject
   * 
   * @param subject
   */
  public static void notifySync(Event event) {
    JajukEvents subject = event.getSubject();
    Log.debug("Notify: " + subject);
    // save last event
    hLastEventBySubject.put(subject, event.getDetails());
    observerRegistry.notifySync(event);
  }

  /**
   * Return whether the event already occurred at least once
   * 
   * @param subject
   * @return
   */
  public static boolean containsEvent(JajukEvents subject) {
    return hLastEventBySubject.containsKey(subject);
  }

  /**
   * Notify all components having registered for the given subject
   * asynchronously
   * 
   * @param subject
   * @param whether
   *          the notification is synchronous or not
   */
  public static void notify(final Event event, boolean bSync) {
    if (bSync) {
      ObservationManager.notifySync(event);
    } else { // do not launch it in a regular thread because AWT
      // event dispatcher waits thread end to display
      if (!t.isAlive()) {
        t.start();
      }
      vFIFO.add(event); // add event in FIFO fo future use
    }
  }

  /**
   * Return the details for last event of the given subject, or null if there is
   * no details
   * 
   * @param sEvent
   *          event name
   * @param sDetail
   *          Detail name
   * @return the detail as an object or null if the event or the detail doesn't
   *         exist
   */
  public static Object getDetailLastOccurence(JajukEvents subject, String sDetailName) {
    Properties pDetails = hLastEventBySubject.get(subject);
    if (pDetails != null) {
      return pDetails.get(sDetailName);
    }
    return null;
  }

  /**
   * Return the details for an event, or null if there is no details
   * 
   * @param sEvent
   *          event name
   * @param sDetail
   *          Detail name
   * @return the detail as an object or null if the event or the detail doesn't
   *         exist
   */
  public static Object getDetail(Event event, String sDetailName) {
    Properties pDetails = event.getDetails();
    if (pDetails != null) {
      return pDetails.get(sDetailName);
    }
    return null;
  }

  /**
   * Return the details for an event, or null if there is no details
   * 
   * @param sEvent
   *          event name
   * @return the detaisl or null there are not details
   */
  public static Properties getDetailsLastOccurence(JajukEvents subject) {
    return hLastEventBySubject.get(subject);
  }
}
