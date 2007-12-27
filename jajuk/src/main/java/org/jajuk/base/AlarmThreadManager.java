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
 *  $Revision: 3156 $
 *
 */

package org.jajuk.base;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmThreadManager{
  private static AlarmThreadManager singleton;
  
  private List<AlarmThread> allAlarms = new ArrayList<AlarmThread>(20);
  
  public static AlarmThreadManager getInstance() {
    if (singleton == null) {
      singleton = new AlarmThreadManager();
    }
    return singleton;
  }
  
  public void run(){
    new Thread() {
      public void run(){
        boolean bstop = false;
        while(!bstop){
          try{
            Thread.sleep(1000);
          }catch(InterruptedException e){}
          if(allAlarms.size() == 0)
            bstop = true;
          else{
            Calendar cal = Calendar.getInstance();
            String currentTime = cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
            for(AlarmThread alarm : allAlarms){
              long timediff= Time.valueOf(currentTime).getTime() - alarm.getAlarmMilliSeconds();
              // wake up only if within 5 minutes of alarm Time
              if(timediff > 0 && timediff < 300000)
                alarm.wakeUpSleeper();
            }
          }
        }
      }
    }.start();
    
  }
  public void addAlarm(AlarmThread aAlarm){
    if (allAlarms.size() == 0){
      allAlarms.add(aAlarm);
      run();
    } 
    else
      allAlarms.add(aAlarm);
  }
  
  public void stopAlarm(AlarmThread aAlarm){
    allAlarms.remove(aAlarm);
  }
  
  public List<AlarmThread> getAllAlarms(){
    return allAlarms;
  }
  
  public void removeAlarm(AlarmThread aAlarm){
    allAlarms.remove(aAlarm);
  }
}