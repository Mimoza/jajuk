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
 *  $Revision$
 */
package org.jajuk.ui.widgets;

import static org.jajuk.ui.actions.JajukActions.CONFIGURE_AMBIENCES;
import static org.jajuk.ui.actions.JajukActions.CONFIGURE_DJS;
import static org.jajuk.ui.actions.JajukActions.CONTINUE_MODE;
import static org.jajuk.ui.actions.JajukActions.CUSTOM_PROPERTIES_ADD;
import static org.jajuk.ui.actions.JajukActions.CUSTOM_PROPERTIES_REMOVE;
import static org.jajuk.ui.actions.JajukActions.HELP_REQUIRED;
import static org.jajuk.ui.actions.JajukActions.INTRO_MODE;
import static org.jajuk.ui.actions.JajukActions.OPTIONS;
import static org.jajuk.ui.actions.JajukActions.QUALITY;
import static org.jajuk.ui.actions.JajukActions.REPEAT_ALL_MODE;
import static org.jajuk.ui.actions.JajukActions.REPEAT_MODE;
import static org.jajuk.ui.actions.JajukActions.SHOW_ABOUT;
import static org.jajuk.ui.actions.JajukActions.SHOW_TRACES;
import static org.jajuk.ui.actions.JajukActions.SHUFFLE_MODE;
import static org.jajuk.ui.actions.JajukActions.SIMPLE_DEVICE_WIZARD;
import static org.jajuk.ui.actions.JajukActions.TIP_OF_THE_DAY;
import static org.jajuk.ui.actions.JajukActions.VIEW_RESTORE_DEFAULTS;
import ext.SwingWorker;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.jajuk.events.JajukEvent;
import org.jajuk.events.JajukEvents;
import org.jajuk.events.ObservationManager;
import org.jajuk.events.Observer;
import org.jajuk.ui.actions.ActionManager;
import org.jajuk.ui.actions.ActionUtil;
import org.jajuk.ui.actions.JajukActions;
import org.jajuk.ui.perspectives.PerspectiveAdapter;
import org.jajuk.ui.perspectives.PerspectiveManager;
import org.jajuk.ui.views.IView;
import org.jajuk.ui.views.ViewFactory;
import org.jajuk.ui.windows.JajukFullScreenWindow;
import org.jajuk.util.Conf;
import org.jajuk.util.Const;
import org.jajuk.util.IconLoader;
import org.jajuk.util.JajukIcons;
import org.jajuk.util.Messages;
import org.jajuk.util.UpgradeManager;
import org.jajuk.util.UtilSystem;
import org.jajuk.util.log.Log;

/**
 * Jajuk menu bar
 * <p>
 * Singleton
 */
public final class JajukJMenuBar extends JMenuBar implements Observer {

  private static final long serialVersionUID = 1L;

  static JajukJMenuBar jjmb;

  JMenu file;

  JMenuItem jmiFileOpen;

  JajukFileChooser jfchooser;

  JMenuItem jmiFileExit;

  JMenu views;

  JMenuItem jmiRestoreDefaultViews;

  JMenuItem jmiRestoreDefaultViewsAllPerpsectives;

  JMenu properties;

  JMenuItem jmiNewProperty;

  JMenuItem jmiRemoveProperty;

  JMenu mode;

  JCheckBoxMenuItem jcbShowPopups;

  JCheckBoxMenuItem jcbSyncTableTree;

  JCheckBoxMenuItem jcbNoneInternetAccess;

  private final JCheckBoxMenuItem jcbmiRepeat;

  private final JCheckBoxMenuItem jcbmiShuffle;

  private final JCheckBoxMenuItem jcbmiContinue;

  private final JCheckBoxMenuItem jcbmiIntro;

  private final JCheckBoxMenuItem jcbmiKaraoke;

  JMenu smart;

  JMenuItem jmiShuffle;

  JMenuItem jmiBestof;

  JMenuItem jmiNovelties;

  JMenuItem jmiFinishAlbum;

  JMenu tools;

  JMenuItem jmiduplicateFinder;

  JMenuItem jmialarmClock;

  JMenu configuration;

  JMenuItem jmiDJ;

  JMenuItem jmiAmbience;

  JMenuItem jmiWebradios;

  JMenuItem jmiWizard;

  JMenuItem jmiOptions;

  JCheckBoxMenuItem jmiUnmounted;

  JMenu help;

  JMenuItem jmiHelp;

  JMenuItem jmiTipOfTheDay;

  JMenuItem jmiQualityAgent;

  JMenuItem jmiTraces;

  JMenuItem jmiCheckforUpdates;

  JMenuItem jmiAbout;

  JLabel jlUpdate;

  JButton jbSlim;

  private JajukButton jbFull = null;

  private final JCheckBoxMenuItem jcbmiRepeatAll;

  private JajukJMenuBar() {
    setAlignmentX(0.0f);
    // File menu
    file = new JMenu(Messages.getString("JajukJMenuBar.0"));
    /*
     * if(Messages.getString("JajukJMenuBarKey.0") != null) { char c =
     * Messages.getString("JajukJMenuBarKey.0").charAt(0); // TODO: this only
     * handles A-Z, a-z file.setMnemonic(c); }
     */

    jmiFileExit = new JMenuItem(ActionManager.getAction(JajukActions.EXIT));
    file.add(jmiFileExit);

    // Properties menu
    properties = new JMenu(Messages.getString("JajukJMenuBar.5"));
    jmiNewProperty = new JMenuItem(ActionManager.getAction(CUSTOM_PROPERTIES_ADD));
    jmiRemoveProperty = new JMenuItem(ActionManager.getAction(CUSTOM_PROPERTIES_REMOVE));
    properties.add(jmiNewProperty);
    properties.add(jmiRemoveProperty);

    // View menu
    views = new JMenu(Messages.getString("JajukJMenuBar.8"));
    jmiRestoreDefaultViews = new JMenuItem(ActionManager.getAction(VIEW_RESTORE_DEFAULTS));
    jmiRestoreDefaultViewsAllPerpsectives = new JMenuItem(ActionManager
        .getAction(JajukActions.ALL_VIEW_RESTORE_DEFAULTS));

    views.add(jmiRestoreDefaultViews);
    views.add(jmiRestoreDefaultViewsAllPerpsectives);
    views.addSeparator();
    // Add the list of available views parsed in XML files at startup
    JMenu jmViews = new JMenu(Messages.getString("JajukJMenuBar.25"));
    for (final Class<? extends IView> view : ViewFactory.getKnownViews()) {
      JMenuItem jmi = null;
      try {
        jmi = new JMenuItem(view.newInstance().getDesc(), IconLoader.getIcon(JajukIcons.LOGO_FRAME));
      } catch (Exception e1) {
        Log.error(e1);
        continue;
      }
      jmi.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // Simply add the new view in the current perspective
          PerspectiveAdapter current = (PerspectiveAdapter) PerspectiveManager
              .getCurrentPerspective();
          IView newView = ViewFactory.createView(view, current,
              (int) (Integer.MAX_VALUE * UtilSystem.getRandom().nextDouble()));
          newView.initUI();
          newView.setIsPopulated(true);
          current.addDockable(newView);
        }
      });
      jmViews.add(jmi);
    }
    views.add(jmViews);

    // Mode menu
    String modeText = Messages.getString("JajukJMenuBar.9");
    mode = new JMenu(ActionUtil.strip(modeText));
    mode.setMnemonic(ActionUtil.getMnemonic(modeText));

    jcbmiRepeat = new JCheckBoxMenuItem(ActionManager.getAction(REPEAT_MODE));
    jcbmiRepeat.setSelected(Conf.getBoolean(Const.CONF_STATE_REPEAT));
    jcbmiRepeatAll = new JCheckBoxMenuItem(ActionManager.getAction(REPEAT_ALL_MODE));
    jcbmiRepeatAll.setSelected(Conf.getBoolean(Const.CONF_STATE_REPEAT_ALL));
    jcbmiShuffle = new JCheckBoxMenuItem(ActionManager.getAction(SHUFFLE_MODE));
    jcbmiShuffle.setSelected(Conf.getBoolean(Const.CONF_STATE_SHUFFLE));
    jcbmiContinue = new JCheckBoxMenuItem(ActionManager.getAction(CONTINUE_MODE));
    jcbmiContinue.setSelected(Conf.getBoolean(Const.CONF_STATE_CONTINUE));
    jcbmiIntro = new JCheckBoxMenuItem(ActionManager.getAction(INTRO_MODE));
    jcbmiIntro.setSelected(Conf.getBoolean(Const.CONF_STATE_INTRO));
    jcbmiKaraoke = new JCheckBoxMenuItem(ActionManager.getAction(JajukActions.KARAOKE_MODE));
    jcbmiKaraoke.setSelected(Conf.getBoolean(Const.CONF_STATE_KARAOKE));

    mode.add(jcbmiRepeat);
    mode.add(jcbmiRepeatAll);
    mode.add(jcbmiShuffle);
    mode.add(jcbmiContinue);
    mode.add(jcbmiIntro);
    mode.add(jcbmiKaraoke);

    // Smart Menu
    smart = new JMenu(Messages.getString("JajukJMenuBar.29"));
    jmiShuffle = new SizedJMenuItem(ActionManager.getAction(JajukActions.SHUFFLE_GLOBAL));
    jmiBestof = new SizedJMenuItem(ActionManager.getAction(JajukActions.BEST_OF));
    jmiNovelties = new SizedJMenuItem(ActionManager.getAction(JajukActions.NOVELTIES));
    jmiFinishAlbum = new SizedJMenuItem(ActionManager.getAction(JajukActions.FINISH_ALBUM));

    smart.add(jmiShuffle);
    smart.add(jmiBestof);
    smart.add(jmiNovelties);
    smart.add(jmiFinishAlbum);

    // Tools Menu
    tools = new JMenu(Messages.getString("JajukJMenuBar.28"));
    jmiduplicateFinder = new JMenuItem(ActionManager.getAction(JajukActions.FIND_DUPLICATE_FILES));
    jmialarmClock = new JMenuItem(ActionManager.getAction(JajukActions.ALARM_CLOCK));
    tools.add(jmiduplicateFinder);
    tools.add(jmialarmClock);
    //tools.addSeparator();

    // Configuration menu
    configuration = new JMenu(Messages.getString("JajukJMenuBar.21"));
    jmiDJ = new JMenuItem(ActionManager.getAction(CONFIGURE_DJS));
    // Overwrite default icon
    jmiDJ.setIcon(IconLoader.getIcon(JajukIcons.DIGITAL_DJ_16X16));
    jmiAmbience = new JMenuItem(ActionManager.getAction(CONFIGURE_AMBIENCES));
    jmiWebradios = new JMenuItem(ActionManager.getAction(JajukActions.CONFIGURE_WEBRADIOS));
    jmiWebradios.setIcon(IconLoader.getIcon(JajukIcons.WEBRADIO_16X16));
    jmiWizard = new JMenuItem(ActionManager.getAction(SIMPLE_DEVICE_WIZARD));
    jmiOptions = new JMenuItem(ActionManager.getAction(OPTIONS));

    jmiUnmounted = new JCheckBoxMenuItem(ActionManager.getAction(JajukActions.UNMOUNTED));
    jmiUnmounted.setSelected(Conf.getBoolean(Const.CONF_OPTIONS_HIDE_UNMOUNTED));
    jmiUnmounted.putClientProperty(Const.DETAIL_ORIGIN, jmiUnmounted);

    jcbShowPopups = new JCheckBoxMenuItem(Messages.getString("ParameterView.228"));
    jcbShowPopups.setSelected(Conf.getBoolean(Const.CONF_SHOW_POPUPS));
    jcbShowPopups.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Conf.setProperty(Const.CONF_SHOW_POPUPS, Boolean.toString(jcbShowPopups.isSelected()));
        // force parameter view to take this into account
        ObservationManager.notify(new JajukEvent(JajukEvents.PARAMETERS_CHANGE));
      }
    });

    jcbSyncTableTree = new JCheckBoxMenuItem(Messages.getString("ParameterView.183"));
    jcbSyncTableTree.setToolTipText(Messages.getString("ParameterView.184"));
    jcbSyncTableTree.setSelected(Conf.getBoolean(Const.CONF_OPTIONS_SYNC_TABLE_TREE));
    jcbSyncTableTree.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Conf.setProperty(Const.CONF_OPTIONS_SYNC_TABLE_TREE, Boolean.toString(jcbSyncTableTree
            .isSelected()));
        // force parameter view to take this into account
        ObservationManager.notify(new JajukEvent(JajukEvents.PARAMETERS_CHANGE));
      }
    });

    jcbNoneInternetAccess = new JCheckBoxMenuItem(Messages.getString("ParameterView.264"));
    jcbNoneInternetAccess.setToolTipText(Messages.getString("ParameterView.265"));
    jcbNoneInternetAccess.setSelected(Conf.getBoolean(Const.CONF_NETWORK_NONE_INTERNET_ACCESS));
    jcbNoneInternetAccess.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Conf.setProperty(Const.CONF_NETWORK_NONE_INTERNET_ACCESS, Boolean
            .toString(jcbNoneInternetAccess.isSelected()));
        // force parameter view to take this into account
        ObservationManager.notify(new JajukEvent(JajukEvents.PARAMETERS_CHANGE));
      }
    });

    configuration.add(jmiUnmounted);
    configuration.add(jcbShowPopups);
    configuration.add(jcbSyncTableTree);
    configuration.add(jcbNoneInternetAccess);
    configuration.addSeparator();
    configuration.add(jmiDJ);
    configuration.add(jmiAmbience);
    configuration.add(jmiWebradios);
    configuration.add(jmiWizard);
    configuration.add(jmiOptions);

    // Help menu
    String helpText = Messages.getString("JajukJMenuBar.14");
    help = new JMenu(ActionUtil.strip(helpText));
    help.setMnemonic(ActionUtil.getMnemonic(helpText));
    jmiHelp = new JMenuItem(ActionManager.getAction(HELP_REQUIRED));
    jmiAbout = new JMenuItem(ActionManager.getAction(SHOW_ABOUT));
    jmiTraces = new JMenuItem(ActionManager.getAction(SHOW_TRACES));
    jmiTraces = new JMenuItem(ActionManager.getAction(SHOW_TRACES));
    jmiCheckforUpdates = new JMenuItem(ActionManager.getAction(JajukActions.CHECK_FOR_UPDATES));
    jmiTipOfTheDay = new JMenuItem(ActionManager.getAction(TIP_OF_THE_DAY));

    help.add(jmiHelp);
    help.add(jmiTipOfTheDay);
    // Install this action only if Desktop class is supported, it is used to
    // open default mail client
    if (Desktop.isDesktopSupported()) {
      jmiQualityAgent = new JMenuItem(ActionManager.getAction(QUALITY));
      help.add(jmiQualityAgent);
    }
    help.add(jmiTraces);
    help.add(jmiCheckforUpdates);
    help.add(jmiAbout);

    JMenuBar mainmenu = new JMenuBar();
    mainmenu.add(file);
    mainmenu.add(views);
    mainmenu.add(properties);
    mainmenu.add(mode);
    mainmenu.add(smart);
    mainmenu.add(tools);
    mainmenu.add(configuration);
    mainmenu.add(help);

    jbSlim = new JajukButton(ActionManager.getAction(JajukActions.SLIM_JAJUK));
    if (JajukFullScreenWindow.getInstance().isFullScreenSupported()) {
      jbFull = new JajukButton(ActionManager.getAction(JajukActions.FULLSCREEN_JAJUK));
    }

    JMenuBar eastmenu = new JMenuBar();
    eastmenu.add(jbSlim);
    if (JajukFullScreenWindow.getInstance().isFullScreenSupported()) {
      eastmenu.add(jbFull);
    }
    setLayout(new BorderLayout());
    add(mainmenu, BorderLayout.WEST);
    add(eastmenu, BorderLayout.EAST);

    // Check for new release and display the icon if a new release is available
    SwingWorker sw = new SwingWorker() {

      @Override
      public Object construct() {
        UpgradeManager.checkForUpdate();
        return null;
      }

      @Override
      public void finished() {
        // add the new release label if required
        if (UpgradeManager.getNewVersionName() != null) {
          jlUpdate = new JLabel(" ", IconLoader.getIcon(JajukIcons.UPDATE_MANAGER), JLabel.RIGHT);
          String newRelease = UpgradeManager.getNewVersionName();
          if (newRelease != null) {
            jlUpdate.setToolTipText(Messages.getString("UpdateManager.0") + newRelease
                + Messages.getString("UpdateManager.1"));
          }
          add(Box.createHorizontalGlue());
          add(jlUpdate);
        }
      }

    };
    // Search online for upgrade if the option is set and if the none Internet
    // access option is not set
    if (Conf.getBoolean(Const.CONF_CHECK_FOR_UPDATE)
        && !Conf.getBoolean(Const.CONF_NETWORK_NONE_INTERNET_ACCESS)) {
      sw.start();
    }
    ObservationManager.register(this);
  }

  public static JajukJMenuBar getInstance() {
    if (jjmb == null) {
      jjmb = new JajukJMenuBar();
    }
    return jjmb;
  }

  public Set<JajukEvents> getRegistrationKeys() {
    Set<JajukEvents> eventSubjectSet = new HashSet<JajukEvents>();
    eventSubjectSet.add(JajukEvents.PARAMETERS_CHANGE);
    return eventSubjectSet;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.ui.Observer#update(java.lang.String)
   */
  public void update(final JajukEvent event) {
    SwingUtilities.invokeLater(new Runnable() {

      public void run() {
        if (JajukEvents.PARAMETERS_CHANGE.equals(event.getSubject())) {
          jcbShowPopups.setSelected(Conf.getBoolean(Const.CONF_SHOW_POPUPS));
          jmiUnmounted.setSelected(Conf.getBoolean(Const.CONF_OPTIONS_HIDE_UNMOUNTED));
          jcbSyncTableTree.setSelected(Conf.getBoolean(Const.CONF_OPTIONS_SYNC_TABLE_TREE));
          jcbNoneInternetAccess.setSelected(Conf
              .getBoolean(Const.CONF_NETWORK_NONE_INTERNET_ACCESS));
        }
      }

    });
  }

  public void setRepeatSelected(final boolean b) {
    jcbmiRepeat.setSelected(b);
  }

  public void setRepeatAllSelected(final boolean b) {
    jcbmiRepeatAll.setSelected(b);
  }

  public void setShuffleSelected(final boolean b) {
    jcbmiShuffle.setSelected(b);
  }

  public void setContinueSelected(final boolean b) {
    jcbmiContinue.setSelected(b);
  }

  public void setIntroSelected(final boolean b) {
    jcbmiIntro.setSelected(b);
  }

  public void setKaraokeSelected(final boolean b) {
    jcbmiKaraoke.setSelected(b);
  }

}
