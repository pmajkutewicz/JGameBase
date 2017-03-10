/*
 * Copyright (C) 2006-2014 F. Gerbig (fgerbig@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jgamebase.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableCellRenderer;

import jgamebase.Const;
import jgamebase.JGameBase;
import jgamebase.db.Db;
import jgamebase.db.model.Game;
import jgamebase.db.model.Item;
import jgamebase.db.model.ItemView;
import jgamebase.db.model.ItemViewFilter;
import jgamebase.db.model.Music;
import jgamebase.db.model.Selection;
import jgamebase.model.Databases;
import jgamebase.model.Emulator;
import jgamebase.model.Preferences;

import org.jdesktop.swingx.table.TableColumnExt;

/**
 * The GameBase GUI.
 * 
 * @author F. Gerbig (fgerbig@users.sourceforge.net)
 */
public abstract class GuiObjects extends javax.swing.JFrame {

  private static final long serialVersionUID = -3626362724285446947L;

  protected static SwingWorker checkStopMusicButtonStatus;
  private static boolean itemTableIsSorting = false;

  // cell renderer for the item table
  static class ItemRenderer extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = -9095861952997699391L;

    Border unselectedBorder = null;

    Border selectedBorder = null;

    boolean isBordered = true;

    public ItemRenderer() {
      setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object object,
        final boolean isSelected, final boolean hasFocus, final int row, final int column) {

      table.setBackground(javax.swing.UIManager.getDefaults().getColor("Table.background"));

      final Item item = (Item) object;
      setText(item.getName());

      if (((item instanceof Game) && (((Game) item).getIsClassic() || ((Game) item)
          .getIsFavourite())) || ((item instanceof Music) && (((Music) item).getIsFavourite()))) {
        setFont(getFont().deriveFont(Font.BOLD));
      } else {
        setFont(getFont().deriveFont(Font.PLAIN));
      }

      if (isSelected) {
        setForeground(table.getSelectionForeground());
        setBackground(table.getSelectionBackground());
      } else {
        setForeground(table.getForeground());
        setBackground(table.getBackground());
      }

      setIcon(item.getIcon());

      if (isBordered) {
        if (isSelected) {
          if (selectedBorder == null) {
            selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                table.getSelectionBackground());
          }
          setBorder(selectedBorder);
        } else {
          if (unselectedBorder == null) {
            unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
          }
          setBorder(unselectedBorder);
        }
      }

      return this;
    }
  }

  protected List<ItemView> views = new java.util.ArrayList<ItemView>();

  protected ItemView dummyView;

  protected ItemViewFilter[] categoryFilters = {
      new ItemViewFilter("Publisher", ItemViewFilter.SELECTOR_DB,
          ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_EQUAL, "Publishers", "PU_Id",
          "", ""),
      new ItemViewFilter("Year", ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Years", "YE_Id", "", ""),
      new ItemViewFilter("Programmer", ItemViewFilter.SELECTOR_DB,
          ItemViewFilter.CLAUSETYPE_DBFIELD, ItemViewFilter.OPERATOR_EQUAL, "Programmers", "PR_Id",
          "", ""),
      new ItemViewFilter("Musician", ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Musicians", "MU_Id", "Musicians", "MU_Id"),
      new ItemViewFilter("Language", ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Languages", "LA_Id", "", ""),
      new ItemViewFilter("Genre", ItemViewFilter.SELECTOR_OTHER, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Genres", "GE_Id", "", ""),
      new ItemViewFilter("Cracker", ItemViewFilter.SELECTOR_DB, ItemViewFilter.CLAUSETYPE_DBFIELD,
          ItemViewFilter.OPERATOR_EQUAL, "Crackers", "CR_Id", "", "") };

  protected void init() {
    initComponents();
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  protected javax.swing.JComboBox categoriesCategory;
  protected javax.swing.JDialog categoriesDialog;
  protected javax.swing.JList categoriesValues;
  protected javax.swing.JMenu downloadMenu;
  protected javax.swing.JMenuItem editMenu_AdditionalExtras;
  protected javax.swing.JMenuItem editMenu_ClearGameLink;
  protected javax.swing.JMenuItem editMenu_Extras;
  protected javax.swing.JMenuItem editMenu_GameFile;
  protected javax.swing.JMenuItem editMenu_KeyValuePairs;
  protected javax.swing.JMenuItem editMenu_MarkGameForLinking;
  protected javax.swing.JMenu editMenu_PersonalInfoMenu;
  protected javax.swing.JMenuItem editMenu_Screenshots;
  protected javax.swing.JMenuItem editMenu_SetGameLink;
  protected javax.swing.JMenu editMenu_VersionInfoMenu;
  protected javax.swing.JButton filterAdditionalExtrasButton;
  protected javax.swing.JButton filterExtrasButton;
  protected javax.swing.JButton filterPlayGameButton;
  protected javax.swing.JButton filterPlayMusicButton;
  protected javax.swing.JButton filterStopMusicButton;
  protected javax.swing.JButton filterViewButton;
  protected javax.swing.JComboBox filterViewCombobox;
  protected javax.swing.JButton gameInfoAdult;
  protected javax.swing.JLabel gameInfoCoding;
  protected javax.swing.JLabel gameInfoComment;
  protected javax.swing.JLabel gameInfoGenre;
  protected javax.swing.JLabel gameInfoLanguage;
  protected javax.swing.JLabel gameInfoMusic;
  protected javax.swing.JLabel gameInfoNoOfPlayers;
  protected javax.swing.JPanel gameInfoPanel;
  protected javax.swing.JButton gameInfoPrequel;
  protected javax.swing.JLabel gameInfoPublisher;
  protected javax.swing.JButton gameInfoRating;
  protected javax.swing.JButton gameInfoRelated;
  protected javax.swing.JButton gameInfoSequel;
  protected javax.swing.JLabel gameInfoTitle;
  protected javax.swing.JLabel gameInfoTitleLeft;
  protected javax.swing.JLabel gameInfoTitleRight;
  protected javax.swing.JLabel gameInfoYear;
  protected javax.swing.JPanel infoPanel;
  protected javax.swing.JTabbedPane infoTabbedPane;
  private javax.swing.JSplitPane itemPane;
  protected org.jdesktop.swingx.JXTable itemTable;
  private javax.swing.JLabel keyValuePairLabel;
  private javax.swing.JEditorPane keyValuePairPane;
  private javax.swing.JPanel keyValuePairsPanel;
  protected javax.swing.JSplitPane mainPane;
  protected javax.swing.JLabel musicInfoAuthor;
  protected javax.swing.JLabel musicInfoCopyright;
  protected javax.swing.JLabel musicInfoDefault;
  protected javax.swing.JLabel musicInfoGroup;
  protected javax.swing.JLabel musicInfoMusician;
  protected javax.swing.JLabel musicInfoName;
  protected javax.swing.JLabel musicInfoNickname;
  protected javax.swing.JPanel musicInfoPanel;
  protected javax.swing.JLabel musicInfoPhoto;
  protected javax.swing.JLabel musicInfoSongs;
  protected javax.swing.JLabel musicInfoTitle;
  protected javax.swing.JLabel musicInfoTitleLeft;
  protected javax.swing.JLabel musicInfoTitleRight;
  protected javax.swing.JEditorPane notesInfoNote;
  protected javax.swing.JPanel notesInfoPanel;
  protected javax.swing.JLabel notesInfoTitle;
  protected javax.swing.JLabel notesInfoTitleLeft;
  protected javax.swing.JLabel notesInfoTitleRight;
  protected javax.swing.JLabel personalInfoDifficulty;
  protected javax.swing.JLabel personalInfoHighScore;
  protected javax.swing.JLabel personalInfoLastPlayed;
  protected javax.swing.JPanel personalInfoPanel;
  protected javax.swing.JLabel personalInfoRating;
  protected javax.swing.JLabel personalInfoTimesPlayed;
  protected javax.swing.JLabel personalInfoTitle;
  protected javax.swing.JLabel personalInfoTitleLeft;
  protected javax.swing.JLabel personalInfoTitleRight;
  protected javax.swing.JDialog quickfilterDialog;
  protected javax.swing.JComboBox quickfilterField;
  protected javax.swing.JRadioButton quickfilterIncludeBoth;
  protected javax.swing.JRadioButton quickfilterIncludeGames;
  protected javax.swing.JRadioButton quickfilterIncludeMusic;
  protected javax.swing.JButton quickfilterOkButton;
  protected javax.swing.JTextField quickfilterText;
  protected javax.swing.JMenu recentMenu;
  protected javax.swing.JPanel sideButtonPanel;
  protected javax.swing.JScrollPane sidePane_AdditionalImage;
  protected javax.swing.JScrollPane sidePane_GameImage;
  protected javax.swing.JPanel sidePanel;
  protected javax.swing.JPanel sidePanel_AdditionalImage;
  protected javax.swing.JPanel sidePanel_GameImage;
  protected javax.swing.JSplitPane sidePanel_HSplitPane;
  protected javax.swing.JSplitPane sidePanel_VSplitPane;
  protected javax.swing.JLabel status0;
  protected javax.swing.JLabel status1;
  protected javax.swing.JLabel status2;
  protected javax.swing.JLabel status3;
  protected javax.swing.JLabel status4;
  protected javax.swing.JLabel status5;
  protected javax.swing.JLabel status6;
  protected javax.swing.JCheckBoxMenuItem toolsMenu_AdultFilter;
  protected javax.swing.JCheckBoxMenuItem toolsMenu_HardwareJoystick;
  protected javax.swing.JCheckBoxMenuItem toolsMenu_PlaySoundOnClassics;
  protected javax.swing.JLabel versionInfoComment;
  protected javax.swing.JLabel versionInfoCracked;
  protected javax.swing.JLabel versionInfoGamelength;
  protected javax.swing.JLabel versionInfoHighScoreSaver;
  protected javax.swing.JLabel versionInfoIncludedDocs;
  protected javax.swing.JLabel versionInfoLoadingScreen;
  protected javax.swing.JLabel versionInfoNoOfTrainers;
  protected javax.swing.JLabel versionInfoPalNTSC;
  protected javax.swing.JPanel versionInfoPanel;
  protected javax.swing.JLabel versionInfoTitle;
  protected javax.swing.JLabel versionInfoTitleLeft;
  protected javax.swing.JLabel versionInfoTitleRight;
  protected javax.swing.JLabel versionInfoTrueDriveEmu;
  protected javax.swing.JMenuItem viewMenu_EditView;
  protected javax.swing.JCheckBoxMenuItem viewMenu_GameDetails;
  protected javax.swing.JMenuItem viewMenu_RemoveView;
  javax.swing.JPopupMenu viewPopupMenu;
  protected javax.swing.JMenuItem viewPopupMenu_EditView;
  protected javax.swing.JMenuItem viewPopupMenu_RemoveView;

  // End of variables declaration//GEN-END:variables

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code
  // <editor-fold defaultstate="collapsed" desc=" Generated Code
  // <editor-fold defaultstate="collapsed"
  // <editor-fold defaultstate="collapsed"
  // desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    final javax.swing.ButtonGroup quickfilterIncludeButtonGroup = new javax.swing.ButtonGroup();
    quickfilterDialog = new javax.swing.JDialog();
    final javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    quickfilterText = new javax.swing.JTextField();
    final javax.swing.JLabel jLabel29 = new javax.swing.JLabel();
    quickfilterField = new javax.swing.JComboBox();
    final javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
    quickfilterIncludeGames = new javax.swing.JRadioButton();
    quickfilterIncludeMusic = new javax.swing.JRadioButton();
    quickfilterIncludeBoth = new javax.swing.JRadioButton();
    final javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
    quickfilterOkButton = new javax.swing.JButton();
    final javax.swing.JButton jButton2 = new javax.swing.JButton();
    categoriesDialog = new javax.swing.JDialog();
    categoriesCategory = new javax.swing.JComboBox();
    final javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    categoriesValues = new javax.swing.JList();
    keyValuePairsPanel = new javax.swing.JPanel();
    keyValuePairLabel = new javax.swing.JLabel();
    final javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
    keyValuePairPane = new javax.swing.JEditorPane();
    final javax.swing.ButtonGroup gameLink_ButtonGroup = new javax.swing.ButtonGroup();
    viewPopupMenu = new javax.swing.JPopupMenu();
    final javax.swing.JMenuItem viewPopupMenu_AddView = new javax.swing.JMenuItem();
    viewPopupMenu_EditView = new javax.swing.JMenuItem();
    viewPopupMenu_RemoveView = new javax.swing.JMenuItem();
    final javax.swing.JSeparator jSeparator2 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem viewPopupMenu_Quickfilter = new javax.swing.JMenuItem();
    mainPane = new javax.swing.JSplitPane();
    itemPane = new javax.swing.JSplitPane();
    final javax.swing.JScrollPane tablePane = new javax.swing.JScrollPane();
    itemTable = new org.jdesktop.swingx.JXTable();
    final javax.swing.JPanel filterAndInfoPanel = new javax.swing.JPanel();
    final javax.swing.JPanel filterPanel = new javax.swing.JPanel();
    filterViewButton = new javax.swing.JButton();
    filterViewCombobox = new javax.swing.JComboBox();
    final javax.swing.JButton filterDivButton = new javax.swing.JButton();
    filterPlayGameButton = new javax.swing.JButton();
    filterPlayMusicButton = new javax.swing.JButton();
    filterStopMusicButton = new javax.swing.JButton();
    filterExtrasButton = new javax.swing.JButton();
    filterAdditionalExtrasButton = new javax.swing.JButton();
    infoPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    gameInfoPrequel = new javax.swing.JButton();
    gameInfoSequel = new javax.swing.JButton();
    gameInfoRelated = new javax.swing.JButton();
    gameInfoRating = new javax.swing.JButton();
    gameInfoAdult = new javax.swing.JButton();
    infoTabbedPane = new javax.swing.JTabbedPane();
    gameInfoPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
    gameInfoTitleLeft = new javax.swing.JLabel();
    gameInfoTitle = new javax.swing.JLabel();
    gameInfoTitleRight = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
    final javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
    gameInfoYear = new javax.swing.JLabel();
    gameInfoPublisher = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
    gameInfoCoding = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
    gameInfoMusic = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
    gameInfoGenre = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
    gameInfoNoOfPlayers = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
    gameInfoLanguage = new javax.swing.JLabel();
    final javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
    gameInfoComment = new javax.swing.JLabel();
    versionInfoPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel7 = new javax.swing.JPanel();
    versionInfoTitleLeft = new javax.swing.JLabel();
    versionInfoTitle = new javax.swing.JLabel();
    versionInfoTitleRight = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
    versionInfoCracked = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
    versionInfoNoOfTrainers = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
    versionInfoHighScoreSaver = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
    versionInfoGamelength = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel12 = new javax.swing.JLabel();
    versionInfoPalNTSC = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel13 = new javax.swing.JLabel();
    versionInfoTrueDriveEmu = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel14 = new javax.swing.JLabel();
    versionInfoLoadingScreen = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel15 = new javax.swing.JLabel();
    versionInfoIncludedDocs = new javax.swing.JLabel();
    final javax.swing.JSeparator jSeparator5 = new javax.swing.JSeparator();
    versionInfoComment = new javax.swing.JLabel();
    personalInfoPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel8 = new javax.swing.JPanel();
    personalInfoTitleLeft = new javax.swing.JLabel();
    personalInfoTitle = new javax.swing.JLabel();
    personalInfoTitleRight = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel16 = new javax.swing.JLabel();
    personalInfoHighScore = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel17 = new javax.swing.JLabel();
    personalInfoDifficulty = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel18 = new javax.swing.JLabel();
    personalInfoTimesPlayed = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel19 = new javax.swing.JLabel();
    personalInfoLastPlayed = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel20 = new javax.swing.JLabel();
    personalInfoRating = new javax.swing.JLabel();
    notesInfoPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel9 = new javax.swing.JPanel();
    notesInfoTitleLeft = new javax.swing.JLabel();
    notesInfoTitle = new javax.swing.JLabel();
    notesInfoTitleRight = new javax.swing.JLabel();
    final javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
    notesInfoNote = new javax.swing.JEditorPane();
    final javax.swing.JButton notesInfoSave = new javax.swing.JButton();
    final javax.swing.JButton notesInfoClear = new javax.swing.JButton();
    musicInfoPanel = new javax.swing.JPanel();
    final javax.swing.JPanel jPanel10 = new javax.swing.JPanel();
    musicInfoTitleLeft = new javax.swing.JLabel();
    musicInfoTitle = new javax.swing.JLabel();
    musicInfoTitleRight = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel21 = new javax.swing.JLabel();
    musicInfoMusician = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel22 = new javax.swing.JLabel();
    musicInfoNickname = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel23 = new javax.swing.JLabel();
    musicInfoGroup = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel24 = new javax.swing.JLabel();
    musicInfoPhoto = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel25 = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel26 = new javax.swing.JLabel();
    musicInfoName = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel27 = new javax.swing.JLabel();
    musicInfoAuthor = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel28 = new javax.swing.JLabel();
    musicInfoCopyright = new javax.swing.JLabel();
    final javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
    final javax.swing.JLabel jLabel30 = new javax.swing.JLabel();
    musicInfoSongs = new javax.swing.JLabel();
    final javax.swing.JLabel jLabel32 = new javax.swing.JLabel();
    musicInfoDefault = new javax.swing.JLabel();
    sidePanel = new javax.swing.JPanel();
    sidePanel_HSplitPane = new javax.swing.JSplitPane();
    sidePanel_VSplitPane = new javax.swing.JSplitPane();
    sidePane_GameImage = new javax.swing.JScrollPane();
    sidePanel_GameImage = new javax.swing.JPanel();
    sidePane_AdditionalImage = new javax.swing.JScrollPane();
    sidePanel_AdditionalImage = new javax.swing.JPanel();
    final javax.swing.JScrollPane sideButtonPanelScrollPane = new javax.swing.JScrollPane();
    sideButtonPanel = new javax.swing.JPanel();
    final javax.swing.JPanel statusPanel = new javax.swing.JPanel();
    status0 = new javax.swing.JLabel();
    status1 = new javax.swing.JLabel();
    status2 = new javax.swing.JLabel();
    status3 = new javax.swing.JLabel();
    status4 = new javax.swing.JLabel();
    status5 = new javax.swing.JLabel();
    status6 = new javax.swing.JLabel();
    final javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
    final javax.swing.JMenu fileMenu = new javax.swing.JMenu();
    final javax.swing.JMenu fileMenu_addMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem addMenu_AddGame = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem addMenu_AddMusic = new javax.swing.JMenuItem();
    final javax.swing.JMenu fileMenu_removeMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem removeMenu_RemoveSelected = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem removeMenu_RemoveAll = new javax.swing.JMenuItem();
    final javax.swing.JSeparator fileMenu_Separator1 = new javax.swing.JSeparator();
    final javax.swing.JMenu fileMenu_importMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem importMenu_importFavourites = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem importMenu_importURLsFromCSV = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem importMenu_importDbFromCSV = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem importMenu_importGames = new javax.swing.JMenuItem();
    final javax.swing.JPopupMenu.Separator jSeparator7 = new javax.swing.JPopupMenu.Separator();
    final javax.swing.JMenuItem importMenu_matchURLsFromCSV = new javax.swing.JMenuItem();
    final javax.swing.JMenu fileMenu_exportMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem exportMenu_exportFavourites = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem exportMenu_exportUsedFiles = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem exportMenu_exportURLsToCSV = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem exportMenu_exportDbToCSV = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem exportMenu_exportDbToMdb = new javax.swing.JMenuItem();
    final javax.swing.JSeparator fileMenu_Separator2 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem fileMenu_Exit = new javax.swing.JMenuItem();
    final javax.swing.JMenu editMenu = new javax.swing.JMenu();
    final javax.swing.JMenu editMenu_GameMusicInfoMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem gameMusicInfoMenu_Current = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem gameMusicInfoMenu_All = new javax.swing.JMenuItem();
    editMenu_VersionInfoMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem versionInfoMenu_Current = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem versionInfoMenu_All = new javax.swing.JMenuItem();
    editMenu_PersonalInfoMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem personalInfoMenu_Current = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem personalInfoMenu_All = new javax.swing.JMenuItem();
    final javax.swing.JSeparator jSeparator15 = new javax.swing.JSeparator();
    editMenu_GameFile = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem editMenu_MusicFile = new javax.swing.JMenuItem();
    editMenu_Screenshots = new javax.swing.JMenuItem();
    editMenu_Extras = new javax.swing.JMenuItem();
    editMenu_AdditionalExtras = new javax.swing.JMenuItem();
    final javax.swing.JSeparator jSeparator21 = new javax.swing.JSeparator();
    editMenu_KeyValuePairs = new javax.swing.JMenuItem();
    final javax.swing.JSeparator jSeparator16 = new javax.swing.JSeparator();
    editMenu_MarkGameForLinking = new javax.swing.JMenuItem();
    editMenu_SetGameLink = new javax.swing.JMenuItem();
    editMenu_ClearGameLink = new javax.swing.JMenuItem();
    final javax.swing.JSeparator jSeparator17 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem editMenu_FindInList = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem editMenu_FindNext = new javax.swing.JMenuItem();
    final javax.swing.JSeparator jSeparator18 = new javax.swing.JSeparator();
    final javax.swing.JMenu editMenu_FindByIdMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem findByIdMenu_Game = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem findByIdMenu_Music = new javax.swing.JMenuItem();
    final javax.swing.JSeparator jSeparator19 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem editMenu_ToggleIsFavourite = new javax.swing.JMenuItem();
    final javax.swing.JMenu viewMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem viewMenu_ConfigureTableColumns = new javax.swing.JMenuItem();
    final javax.swing.JMenu viewMenu_SidebarMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem SideBarMenu_HideSidebar = new javax.swing.JMenuItem();
    final javax.swing.JPopupMenu.Separator jSeparator3 = new javax.swing.JPopupMenu.Separator();
    final javax.swing.JMenuItem SideBarMenu_SmallSidebar = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem SideBarMenu_MediumSidebar = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem SideBarMenu_LargeSidebar = new javax.swing.JMenuItem();
    viewMenu_GameDetails = new javax.swing.JCheckBoxMenuItem();
    final javax.swing.JSeparator jSeparator6 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem viewMenu_AddView = new javax.swing.JMenuItem();
    viewMenu_EditView = new javax.swing.JMenuItem();
    viewMenu_RemoveView = new javax.swing.JMenuItem();
    final javax.swing.JSeparator jSeparator10 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem viewMenu_QuickFilter = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem viewMenu_CategoryWindow = new javax.swing.JMenuItem();
    final javax.swing.JMenu emulatorMenu = new javax.swing.JMenu();
    final javax.swing.JSeparator jSeparator4 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem emulatorMenu_ManageGameEmulators = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem emulatorMenu_ManageMusicEmulators = new javax.swing.JMenuItem();
    final javax.swing.JMenu toolsMenu = new javax.swing.JMenu();
    toolsMenu_PlaySoundOnClassics = new javax.swing.JCheckBoxMenuItem();
    toolsMenu_HardwareJoystick = new javax.swing.JCheckBoxMenuItem();
    toolsMenu_AdultFilter = new javax.swing.JCheckBoxMenuItem();
    final javax.swing.JSeparator jSeparator12 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem toolsMenu_Options = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem toolsMenu_Paths = new javax.swing.JMenuItem();
    final javax.swing.JSeparator jSeparator14 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem toolsMenu_VerifyAvailableFiles = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem toolsMenu_SyncAdditionalExtras = new javax.swing.JMenuItem();
    recentMenu = new javax.swing.JMenu();
    downloadMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem downloadMenu_JDB = new javax.swing.JMenuItem();
    final javax.swing.JPopupMenu.Separator jSeparator9 = new javax.swing.JPopupMenu.Separator();
    final javax.swing.JMenuItem downloadMenu_DB = new javax.swing.JMenuItem();
    final javax.swing.JPopupMenu.Separator jSeparator11 = new javax.swing.JPopupMenu.Separator();
    final javax.swing.JMenu helpMenu = new javax.swing.JMenu();
    final javax.swing.JMenuItem helpMenu_Help = new javax.swing.JMenuItem();
    final javax.swing.JSeparator helpMenu_Separator1 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem helpMenu_Forum = new javax.swing.JMenuItem();
    final javax.swing.JMenuItem helpMenu_Bugtracker = new javax.swing.JMenuItem();
    final javax.swing.JSeparator helpMenu_Separator2 = new javax.swing.JSeparator();
    final javax.swing.JMenuItem helpMenu_About = new javax.swing.JMenuItem();

    quickfilterDialog.setTitle("Quick Filter");
    quickfilterDialog.setModal(true);
    quickfilterDialog.setResizable(false);
    quickfilterDialog.setSize(new Dimension(160, 250));
    quickfilterDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

    jLabel1.setDisplayedMnemonic('t');
    jLabel1.setLabelFor(quickfilterText);
    jLabel1.setText("Contains text:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 2, 0);
    quickfilterDialog.getContentPane().add(jLabel1, gridBagConstraints);

    quickfilterText.addKeyListener(new java.awt.event.KeyAdapter() {
      @Override
      public void keyPressed(final java.awt.event.KeyEvent evt) {
        quickfilterTextKeyHandler(evt);
      }

      @Override
      public void keyReleased(final java.awt.event.KeyEvent evt) {
        quickfilterTextKeyHandler(evt);
      }

      @Override
      public void keyTyped(final java.awt.event.KeyEvent evt) {
        quickfilterTextKeyHandler(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    quickfilterDialog.getContentPane().add(quickfilterText, gridBagConstraints);

    jLabel29.setDisplayedMnemonic('f');
    jLabel29.setLabelFor(quickfilterField);
    jLabel29.setText("Field:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 2, 0);
    quickfilterDialog.getContentPane().add(jLabel29, gridBagConstraints);

    quickfilterField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Name",
        "Publisher", "Programmer", "Musician", "Genre", "Personal Comment", "Version Comment" }));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    quickfilterDialog.getContentPane().add(quickfilterField, gridBagConstraints);

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Include Tables"));
    jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

    quickfilterIncludeButtonGroup.add(quickfilterIncludeGames);
    quickfilterIncludeGames.setMnemonic('g');
    quickfilterIncludeGames.setSelected(true);
    quickfilterIncludeGames.setText("Games Table Only");
    jPanel3.add(quickfilterIncludeGames);

    quickfilterIncludeButtonGroup.add(quickfilterIncludeMusic);
    quickfilterIncludeMusic.setMnemonic('m');
    quickfilterIncludeMusic.setText("Music Table Only");
    jPanel3.add(quickfilterIncludeMusic);

    quickfilterIncludeButtonGroup.add(quickfilterIncludeBoth);
    quickfilterIncludeBoth.setMnemonic('b');
    quickfilterIncludeBoth.setText("Both Tables");
    jPanel3.add(quickfilterIncludeBoth);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
    quickfilterDialog.getContentPane().add(jPanel3, gridBagConstraints);

    quickfilterOkButton.setMnemonic('o');
    quickfilterOkButton.setText("OK");
    quickfilterOkButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        quickfilterOkActionPerformed(evt);
      }
    });
    jPanel4.add(quickfilterOkButton);

    jButton2.setMnemonic('c');
    jButton2.setText("Cancel");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        quickfilterCancelActionPerformed(evt);
      }
    });
    jPanel4.add(jButton2);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
    quickfilterDialog.getContentPane().add(jPanel4, gridBagConstraints);

    categoriesDialog.setTitle("Categories");
    categoriesDialog.setSize(new Dimension(160, 250));

    categoriesCategory.setModel(new DefaultComboBoxModel(categoryFilters));
    categoriesCategory.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        categoriesCategoryActionPerformed(evt);
      }
    });
    categoriesDialog.getContentPane().add(categoriesCategory, java.awt.BorderLayout.NORTH);

    categoriesValues.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    categoriesValues.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      @Override
      public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
        categoriesValuesValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(categoriesValues);

    categoriesDialog.getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

    keyValuePairsPanel.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    keyValuePairsPanel.add(keyValuePairLabel, gridBagConstraints);

    keyValuePairPane
        .setToolTipText("Edit the key value pairs. Place each pair in a seaparate line.");
    keyValuePairPane.setMinimumSize(new java.awt.Dimension(320, 200));
    keyValuePairPane.setOpaque(false);
    keyValuePairPane.setPreferredSize(new java.awt.Dimension(320, 200));
    jScrollPane4.setViewportView(keyValuePairPane);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.9;
    keyValuePairsPanel.add(jScrollPane4, gridBagConstraints);

    viewPopupMenu_AddView.setText("Add View...");
    viewPopupMenu_AddView.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        addViewActionPerformed(evt);
      }
    });
    viewPopupMenu.add(viewPopupMenu_AddView);

    viewPopupMenu_EditView.setText("Edit View...");
    viewPopupMenu_EditView.setEnabled(false);
    viewPopupMenu_EditView.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editViewActionPerformed(evt);
      }
    });
    viewPopupMenu.add(viewPopupMenu_EditView);

    viewPopupMenu_RemoveView.setText("Remove View");
    viewPopupMenu_RemoveView.setEnabled(false);
    viewPopupMenu_RemoveView.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        removeViewActionPerformed(evt);
      }
    });
    viewPopupMenu.add(viewPopupMenu_RemoveView);
    viewPopupMenu.add(jSeparator2);

    viewPopupMenu_Quickfilter.setText("Quick Filter...");
    viewPopupMenu_Quickfilter.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        quickFilterActionPerformed(evt);
      }
    });
    viewPopupMenu.add(viewPopupMenu_Quickfilter);

    setTitle("[" + Databases.getCurrent().getName() + " powered by jGameBase V" + Const.VERSION
        + "]");
    setIconImage(Const.IMAGE_JGAMEBASE);
    addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(final java.awt.event.WindowEvent evt) {
        exitFormWindowClosing(evt);
      }
    });

    mainPane.setContinuousLayout(true);

    itemPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    itemPane.setResizeWeight(1.0);
    itemPane.setContinuousLayout(true);
    itemPane.setDoubleBuffered(true);

    itemTable.setModel(new ItemTableModel(dummyView));
    itemTable.setColumnControlVisible(true);
    itemTable.setEditable(false);
    itemTable.setHorizontalScrollEnabled(true);
    itemTable.setShowHorizontalLines(false);
    itemTable.setShowVerticalLines(false);
    itemTable.getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

    itemTable.getSelectionModel().addListSelectionListener(
        new javax.swing.event.ListSelectionListener() {
          @Override
          public void valueChanged(final javax.swing.event.ListSelectionEvent e) {
            // Ignore extra messages.
            if (e.getValueIsAdjusting()) {
              return;
            }
            displayItem(getSelectedItem());
          }
        });

    itemTable.setDefaultRenderer(Item.class, new ItemRenderer());
    itemTable.setDefaultRenderer(Game.class, new ItemRenderer());
    itemTable.setDefaultRenderer(Music.class, new ItemRenderer());

    // highlighting every second row
    itemTable.setHighlighters(org.jdesktop.swingx.decorator.HighlighterFactory
        .createSimpleStriping());

    // set wait cursor while sorting
    itemTable.getRowSorter().addRowSorterListener(new RowSorterListener() {
      @Override
      public void sorterChanged(final RowSorterEvent e) {
        if (e.getType() == javax.swing.event.RowSorterEvent.Type.SORT_ORDER_CHANGED) {
          setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        } else {
          setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
      }
    });
    itemTable.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        itemTable_MouseHandler(evt);
      }
    });
    itemTable.addKeyListener(new java.awt.event.KeyAdapter() {
      @Override
      public void keyPressed(final java.awt.event.KeyEvent evt) {
        itemTable_KeyHandler(evt);
      }
    });
    tablePane.setViewportView(itemTable);

    itemPane.setTopComponent(tablePane);

    filterAndInfoPanel.setLayout(new java.awt.BorderLayout());

    filterPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    filterPanel.setLayout(new java.awt.GridBagLayout());

    filterViewButton.setText("...");
    filterViewButton.setToolTipText("Custom Views");
    filterViewButton.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        filterViewButtonMouseHandler(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    filterPanel.add(filterViewButton, gridBagConstraints);

    filterViewCombobox.setModel(new DefaultComboBoxModel(views.toArray()));
    filterViewCombobox.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        filterViewActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    filterPanel.add(filterViewCombobox, gridBagConstraints);

    filterDivButton.setText("?");
    filterDivButton.setToolTipText("Random Select");
    filterDivButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        selectRandomActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    filterPanel.add(filterDivButton, gridBagConstraints);

    filterPlayGameButton.setText("Play Game");
    filterPlayGameButton.setToolTipText("Right-click to choose emulator.");
    filterPlayGameButton.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mousePressed(final java.awt.event.MouseEvent evt) {
        filterPlayGameButton_MouseHandler(evt);
      }
    });
    filterPlayGameButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        playGameActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    filterPanel.add(filterPlayGameButton, gridBagConstraints);

    filterPlayMusicButton.setToolTipText("Right-click to choose player.");
    filterPlayMusicButton.setIcon(Const.ICON_PLAY_MUSIC);
    filterPlayMusicButton.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mousePressed(final java.awt.event.MouseEvent evt) {
        filterPlayMusicButton_MouseHandler(evt);
      }
    });
    filterPlayMusicButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        playMusicActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    filterPanel.add(filterPlayMusicButton, gridBagConstraints);

    filterStopMusicButton.setToolTipText("Click to stop music.");
    filterStopMusicButton.setEnabled(false);
    filterStopMusicButton.setIcon(Const.ICON_STOP_MUSIC);
    filterStopMusicButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        stopMusicActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    filterPanel.add(filterStopMusicButton, gridBagConstraints);

    filterExtrasButton.setText("Extras");
    filterExtrasButton.setToolTipText("View/Run Selected Extra");
    filterExtrasButton.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mousePressed(final java.awt.event.MouseEvent evt) {
        filterExtrasButton_MouseHandler(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 6;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    filterPanel.add(filterExtrasButton, gridBagConstraints);

    filterAdditionalExtrasButton.setIcon(Const.ICON_ADDEXTRAS_BROWSER);
    filterAdditionalExtrasButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editAdditionalExtrasFile_ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 7;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    filterPanel.add(filterAdditionalExtrasButton, gridBagConstraints);

    filterAndInfoPanel.add(filterPanel, java.awt.BorderLayout.NORTH);

    infoPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 25, 5, 25));
    infoPanel.setMinimumSize(new java.awt.Dimension(320, 185));
    infoPanel.setPreferredSize(new java.awt.Dimension(320, 185));
    infoPanel.setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

    gameInfoPrequel.setToolTipText("Goto Prequel");
    gameInfoPrequel.setBorder(null);
    gameInfoPrequel.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        gotoPrequelActionPerformed(evt);
      }
    });
    jPanel1.add(gameInfoPrequel);

    gameInfoSequel.setToolTipText("Goto Sequel");
    gameInfoSequel.setBorder(null);
    gameInfoSequel.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        gotoSequelActionPerformed(evt);
      }
    });
    jPanel1.add(gameInfoSequel);

    gameInfoRelated.setToolTipText("Goto Related Game");
    gameInfoRelated.setBorder(null);
    gameInfoRelated.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        gotoRelatedActionPerformed(evt);
      }
    });
    jPanel1.add(gameInfoRelated);

    gameInfoRating.setToolTipText("Game Rating");
    gameInfoRating.setBorder(null);
    jPanel1.add(gameInfoRating);

    gameInfoAdult.setToolTipText("Adult");
    gameInfoAdult.setBorder(null);
    jPanel1.add(gameInfoAdult);

    infoPanel.add(jPanel1, java.awt.BorderLayout.NORTH);

    infoTabbedPane.setMinimumSize(new java.awt.Dimension(320, 120));

    gameInfoPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    gameInfoPanel.setLayout(new java.awt.GridBagLayout());

    jPanel6.add(gameInfoTitleLeft);

    gameInfoTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
    gameInfoTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    gameInfoTitle.setText("Title");
    jPanel6.add(gameInfoTitle);
    jPanel6.add(gameInfoTitleRight);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 4;
    gameInfoPanel.add(jPanel6, gridBagConstraints);

    jLabel2.setText("Published:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    gameInfoPanel.add(jLabel2, gridBagConstraints);

    gameInfoYear.setForeground(java.awt.Color.blue);
    gameInfoYear.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewYear(evt);
      }
    });
    jPanel5.add(gameInfoYear);

    gameInfoPublisher.setForeground(java.awt.Color.blue);
    gameInfoPublisher.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewPublisher(evt);
      }
    });
    jPanel5.add(gameInfoPublisher);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    gameInfoPanel.add(jPanel5, gridBagConstraints);

    jLabel3.setText("Coding:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    gameInfoPanel.add(jLabel3, gridBagConstraints);

    gameInfoCoding.setForeground(java.awt.Color.blue);
    gameInfoCoding.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewProgrammer(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    gameInfoPanel.add(gameInfoCoding, gridBagConstraints);

    jLabel4.setText("Music:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    gameInfoPanel.add(jLabel4, gridBagConstraints);

    gameInfoMusic.setForeground(java.awt.Color.blue);
    gameInfoMusic.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewMusician(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    gameInfoPanel.add(gameInfoMusic, gridBagConstraints);

    jLabel8.setText("Game Genre:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    gameInfoPanel.add(jLabel8, gridBagConstraints);

    gameInfoGenre.setForeground(java.awt.Color.blue);
    gameInfoGenre.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewGenre(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
    gameInfoPanel.add(gameInfoGenre, gridBagConstraints);

    jLabel9.setText("No. of Players:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    gameInfoPanel.add(jLabel9, gridBagConstraints);

    gameInfoNoOfPlayers.setForeground(java.awt.Color.blue);
    gameInfoNoOfPlayers.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewNoOfPlayers(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 3);
    gameInfoPanel.add(gameInfoNoOfPlayers, gridBagConstraints);

    jLabel10.setText("Language:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    gameInfoPanel.add(jLabel10, gridBagConstraints);

    gameInfoLanguage.setForeground(java.awt.Color.blue);
    gameInfoLanguage.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewLanguage(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 3);
    gameInfoPanel.add(gameInfoLanguage, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
    gameInfoPanel.add(jSeparator1, gridBagConstraints);

    gameInfoComment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    gameInfoComment.setText(" ");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
    gameInfoPanel.add(gameInfoComment, gridBagConstraints);

    infoTabbedPane.addTab("Game", gameInfoPanel);

    versionInfoPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    versionInfoPanel.setLayout(new java.awt.GridBagLayout());

    jPanel7.add(versionInfoTitleLeft);

    versionInfoTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
    versionInfoTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    versionInfoTitle.setText("Title");
    jPanel7.add(versionInfoTitle);
    jPanel7.add(versionInfoTitleRight);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 6;
    versionInfoPanel.add(jPanel7, gridBagConstraints);

    jLabel5.setText("Cracked/Crunched:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    versionInfoPanel.add(jLabel5, gridBagConstraints);

    versionInfoCracked.setForeground(java.awt.Color.blue);
    versionInfoCracked.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewCracker(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    versionInfoPanel.add(versionInfoCracked, gridBagConstraints);

    jLabel6.setText("No. of Trainers:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    versionInfoPanel.add(jLabel6, gridBagConstraints);

    versionInfoNoOfTrainers.setForeground(java.awt.Color.blue);
    versionInfoNoOfTrainers.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewNoOfTrainers(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    versionInfoPanel.add(versionInfoNoOfTrainers, gridBagConstraints);

    jLabel7.setText("High Score Saver:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    versionInfoPanel.add(jLabel7, gridBagConstraints);

    versionInfoHighScoreSaver.setForeground(java.awt.Color.blue);
    versionInfoHighScoreSaver.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewHighscoreSaver(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    versionInfoPanel.add(versionInfoHighScoreSaver, gridBagConstraints);

    jLabel11.setText("Game Length:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    versionInfoPanel.add(jLabel11, gridBagConstraints);

    versionInfoGamelength.setForeground(java.awt.Color.blue);
    versionInfoGamelength.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewGameLength(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
    versionInfoPanel.add(versionInfoGamelength, gridBagConstraints);

    jLabel12.setText("PAL/NTSC:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    versionInfoPanel.add(jLabel12, gridBagConstraints);

    versionInfoPalNTSC.setForeground(java.awt.Color.blue);
    versionInfoPalNTSC.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewPalNtsc(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 3);
    versionInfoPanel.add(versionInfoPalNTSC, gridBagConstraints);

    jLabel13.setText("True Drive Emul:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    versionInfoPanel.add(jLabel13, gridBagConstraints);

    versionInfoTrueDriveEmu.setForeground(java.awt.Color.blue);
    versionInfoTrueDriveEmu.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewTrueDriveEmu(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    versionInfoPanel.add(versionInfoTrueDriveEmu, gridBagConstraints);

    jLabel14.setText("Loading Screen:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    versionInfoPanel.add(jLabel14, gridBagConstraints);

    versionInfoLoadingScreen.setForeground(java.awt.Color.blue);
    versionInfoLoadingScreen.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewLoadingScreen(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    versionInfoPanel.add(versionInfoLoadingScreen, gridBagConstraints);

    jLabel15.setText("Included Docs:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    versionInfoPanel.add(jLabel15, gridBagConstraints);

    versionInfoIncludedDocs.setForeground(java.awt.Color.blue);
    versionInfoIncludedDocs.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewIncludedDocs(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    versionInfoPanel.add(versionInfoIncludedDocs, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
    versionInfoPanel.add(jSeparator5, gridBagConstraints);

    versionInfoComment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    versionInfoComment.setText(" ");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
    versionInfoPanel.add(versionInfoComment, gridBagConstraints);

    infoTabbedPane.addTab("Version", versionInfoPanel);

    personalInfoPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    personalInfoPanel.setLayout(new java.awt.GridBagLayout());

    jPanel8.add(personalInfoTitleLeft);

    personalInfoTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
    personalInfoTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    personalInfoTitle.setText("Title");
    jPanel8.add(personalInfoTitle);
    jPanel8.add(personalInfoTitleRight);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 4;
    personalInfoPanel.add(jPanel8, gridBagConstraints);

    jLabel16.setText("High Score:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    personalInfoPanel.add(jLabel16, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    personalInfoPanel.add(personalInfoHighScore, gridBagConstraints);

    jLabel17.setText("Difficulty Level:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    personalInfoPanel.add(jLabel17, gridBagConstraints);

    personalInfoDifficulty.setForeground(java.awt.Color.blue);
    personalInfoDifficulty.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewDifficulty(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    personalInfoPanel.add(personalInfoDifficulty, gridBagConstraints);

    jLabel18.setText("Times Played:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    personalInfoPanel.add(jLabel18, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    personalInfoPanel.add(personalInfoTimesPlayed, gridBagConstraints);

    jLabel19.setText("Last Played:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
    personalInfoPanel.add(jLabel19, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
    personalInfoPanel.add(personalInfoLastPlayed, gridBagConstraints);

    jLabel20.setText("Rating:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 3, 0, 0);
    personalInfoPanel.add(jLabel20, gridBagConstraints);

    personalInfoRating.setForeground(java.awt.Color.blue);
    personalInfoRating.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    personalInfoRating.setText(" ");
    personalInfoRating.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewRating(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.ipady = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
    personalInfoPanel.add(personalInfoRating, gridBagConstraints);

    infoTabbedPane.addTab("Personal", personalInfoPanel);

    notesInfoPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 10, 10));
    notesInfoPanel.setLayout(new java.awt.GridBagLayout());

    jPanel9.add(notesInfoTitleLeft);

    notesInfoTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
    notesInfoTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    notesInfoTitle.setText("Title");
    jPanel9.add(notesInfoTitle);
    jPanel9.add(notesInfoTitleRight);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    notesInfoPanel.add(jPanel9, gridBagConstraints);

    jScrollPane2
        .setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane2
        .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    notesInfoNote.setOpaque(false);
    jScrollPane2.setViewportView(notesInfoNote);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.1;
    gridBagConstraints.weighty = 0.1;
    notesInfoPanel.add(jScrollPane2, gridBagConstraints);

    notesInfoSave.setText("Save");
    notesInfoSave.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        gameInfoNotes_Save_ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 0);
    notesInfoPanel.add(notesInfoSave, gridBagConstraints);

    notesInfoClear.setText("Clear");
    notesInfoClear.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        gameInfoNotes_Clear_ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 6, 3, 0);
    notesInfoPanel.add(notesInfoClear, gridBagConstraints);

    infoTabbedPane.addTab("Notes", notesInfoPanel);

    musicInfoPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    musicInfoPanel.setLayout(new java.awt.GridBagLayout());

    jPanel10.add(musicInfoTitleLeft);

    musicInfoTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
    musicInfoTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    musicInfoTitle.setText("Title");
    jPanel10.add(musicInfoTitle);
    jPanel10.add(musicInfoTitleRight);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
    musicInfoPanel.add(jPanel10, gridBagConstraints);

    jLabel21.setText("Musician:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
    musicInfoPanel.add(jLabel21, gridBagConstraints);

    musicInfoMusician.setForeground(java.awt.Color.blue);
    musicInfoMusician.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewMusician(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
    musicInfoPanel.add(musicInfoMusician, gridBagConstraints);

    jLabel22.setText("Nick:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
    musicInfoPanel.add(jLabel22, gridBagConstraints);

    musicInfoNickname.setForeground(java.awt.Color.blue);
    musicInfoNickname.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewMusicianNickname(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
    musicInfoPanel.add(musicInfoNickname, gridBagConstraints);

    jLabel23.setText("Group:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
    musicInfoPanel.add(jLabel23, gridBagConstraints);

    musicInfoGroup.setForeground(java.awt.Color.blue);
    musicInfoGroup.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        quickviewMusicianGroup(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
    musicInfoPanel.add(musicInfoGroup, gridBagConstraints);

    jLabel24.setText("Photo:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
    musicInfoPanel.add(jLabel24, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
    musicInfoPanel.add(musicInfoPhoto, gridBagConstraints);

    jLabel25.setText("File Info");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
    musicInfoPanel.add(jLabel25, gridBagConstraints);

    jLabel26.setText("Name:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
    musicInfoPanel.add(jLabel26, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 5);
    musicInfoPanel.add(musicInfoName, gridBagConstraints);

    jLabel27.setText("Author:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
    musicInfoPanel.add(jLabel27, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 5);
    musicInfoPanel.add(musicInfoAuthor, gridBagConstraints);

    jLabel28.setText("Copyright:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
    musicInfoPanel.add(jLabel28, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 5);
    musicInfoPanel.add(musicInfoCopyright, gridBagConstraints);

    jPanel2.setLayout(new java.awt.GridLayout(1, 4));

    jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel30.setText("Songs:");
    jPanel2.add(jLabel30);

    musicInfoSongs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jPanel2.add(musicInfoSongs);

    jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel32.setText("Default:");
    jPanel2.add(jLabel32);

    musicInfoDefault.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jPanel2.add(musicInfoDefault);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 5);
    musicInfoPanel.add(jPanel2, gridBagConstraints);

    infoTabbedPane.addTab("Music", musicInfoPanel);

    infoPanel.add(infoTabbedPane, java.awt.BorderLayout.CENTER);

    filterAndInfoPanel.add(infoPanel, java.awt.BorderLayout.CENTER);

    itemPane.setBottomComponent(filterAndInfoPanel);

    mainPane.setLeftComponent(itemPane);

    sidePanel.setLayout(new java.awt.BorderLayout());

    sidePanel_HSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    sidePanel_HSplitPane.setResizeWeight(1.0);
    sidePanel_HSplitPane.setToolTipText("");

    sidePanel_VSplitPane.setResizeWeight(0.75);

    sidePane_GameImage
        .setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    sidePane_GameImage
        .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    sidePanel_GameImage.setMinimumSize(new java.awt.Dimension(100, 10));
    sidePanel_GameImage.setLayout(new javax.swing.BoxLayout(sidePanel_GameImage,
        javax.swing.BoxLayout.Y_AXIS));
    sidePane_GameImage.setViewportView(sidePanel_GameImage);

    sidePanel_VSplitPane.setLeftComponent(sidePane_GameImage);

    sidePane_AdditionalImage
        .setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    sidePane_AdditionalImage
        .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    sidePanel_AdditionalImage.setMinimumSize(new java.awt.Dimension(100, 10));
    sidePanel_AdditionalImage.setLayout(new javax.swing.BoxLayout(sidePanel_AdditionalImage,
        javax.swing.BoxLayout.Y_AXIS));
    sidePane_AdditionalImage.setViewportView(sidePanel_AdditionalImage);

    sidePanel_VSplitPane.setRightComponent(sidePane_AdditionalImage);

    sidePanel_HSplitPane.setLeftComponent(sidePanel_VSplitPane);

    sideButtonPanelScrollPane
        .setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    sideButtonPanelScrollPane
        .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    sideButtonPanelScrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentResized(final java.awt.event.ComponentEvent evt) {
        sideButtonPanelResized(evt);
      }
    });

    sideButtonPanel.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mousePressed(final java.awt.event.MouseEvent evt) {
        sideButtonPanel_MouseHandler(evt);
      }
    });
    sideButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
    sideButtonPanelScrollPane.setViewportView(sideButtonPanel);

    sidePanel_HSplitPane.setRightComponent(sideButtonPanelScrollPane);

    sidePanel.add(sidePanel_HSplitPane, java.awt.BorderLayout.CENTER);

    mainPane.setRightComponent(sidePanel);

    getContentPane().add(mainPane, java.awt.BorderLayout.CENTER);

    statusPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 3, 2, 3));
    statusPanel.setLayout(new java.awt.GridBagLayout());

    status0.setToolTipText("The ID of the current Item.");
    status0.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
    statusPanel.add(status0, gridBagConstraints);

    status1.setToolTipText("Game File");
    status1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
    statusPanel.add(status1, gridBagConstraints);

    status2.setToolTipText("Music File");
    status2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
    statusPanel.add(status2, gridBagConstraints);

    status3.setToolTipText("Game marked for Editing or Linking");
    status3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
    statusPanel.add(status3, gridBagConstraints);

    status4.setToolTipText("Extra to Paste");
    status4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
    statusPanel.add(status4, gridBagConstraints);

    status5.setToolTipText("Sounds toggle");
    status5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    status5.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        playSoundOnClassics_MouseClicked(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
    statusPanel.add(status5, gridBagConstraints);

    status6.setToolTipText("Joystick toggle");
    status6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    status6.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(final java.awt.event.MouseEvent evt) {
        hardwareJoystick_MouseClicked(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    statusPanel.add(status6, gridBagConstraints);

    getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);

    fileMenu.setText("File");

    fileMenu_addMenu.setText("Add");

    addMenu_AddGame.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_INSERT, java.awt.event.InputEvent.SHIFT_MASK));
    addMenu_AddGame.setText("Game...");
    addMenu_AddGame.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        addGameFile_ActionPerformed(evt);
      }
    });
    fileMenu_addMenu.add(addMenu_AddGame);

    addMenu_AddMusic.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_INSERT, java.awt.event.InputEvent.CTRL_MASK));
    addMenu_AddMusic.setText("Music...");
    addMenu_AddMusic.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        addMusicFile_ActionPerformed(evt);
      }
    });
    fileMenu_addMenu.add(addMenu_AddMusic);

    fileMenu.add(fileMenu_addMenu);

    fileMenu_removeMenu.setText("Remove");

    removeMenu_RemoveSelected.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_DELETE, 0));
    removeMenu_RemoveSelected.setText("Selected Game/Music...");
    removeMenu_RemoveSelected.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        removeSelectedItem_ActionPerformed(evt);
      }
    });
    fileMenu_removeMenu.add(removeMenu_RemoveSelected);

    removeMenu_RemoveAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.SHIFT_MASK));
    removeMenu_RemoveAll.setText("All Games/Music in Current View...");
    removeMenu_RemoveAll.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        removeAllItems_ActionPerformed(evt);
      }
    });
    fileMenu_removeMenu.add(removeMenu_RemoveAll);

    fileMenu.add(fileMenu_removeMenu);

    fileMenu_Separator1.setPreferredSize(new java.awt.Dimension(2, 2));
    fileMenu.add(fileMenu_Separator1);

    fileMenu_importMenu.setText("Import");

    importMenu_importFavourites.setMnemonic('I');
    importMenu_importFavourites.setText("Import Favourites...");
    importMenu_importFavourites.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importFavourites_ActionPerformed(evt);
      }
    });
    fileMenu_importMenu.add(importMenu_importFavourites);

    importMenu_importURLsFromCSV.setText("Import URLs from CSV");
    importMenu_importURLsFromCSV
        .setToolTipText("Adds URLs from the databases 'url.csv' file to the database.");
    importMenu_importURLsFromCSV.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importURLsFromCSV_ActionPerformed(evt);
      }
    });
    fileMenu_importMenu.add(importMenu_importURLsFromCSV);

    importMenu_importDbFromCSV.setText("Import Database from CSV");
    importMenu_importDbFromCSV.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importDbFromCSV_ActionPerformed(evt);
      }
    });
    fileMenu_importMenu.add(importMenu_importDbFromCSV);

    importMenu_importGames.setText("Import Games...");
    importMenu_importGames.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        importGames_ActionPerformed(evt);
      }
    });
    fileMenu_importMenu.add(importMenu_importGames);
    fileMenu_importMenu.add(jSeparator7);

    importMenu_matchURLsFromCSV.setText("Match URLs from CSV...");
    importMenu_matchURLsFromCSV
        .setToolTipText("<html>\nTries to match URLs by game name.<br><br>\nSpecify a CSV file in the following format: &quot;Name of the Game&quot;,&quot;Category&quot;,&quot;URL&quot;.<br>\nFor example &quot;Archon&quot;,&quot;Manual&quot;,&quot;http://somewhere.org/archon.pdf&quot;.<br><br>\nThe matched URLs will be added to the databases 'url.csv' file,<br>\nthe not matched URLs will be added to the databases 'url_notfound.csv' file.\"\n</html>");
    importMenu_matchURLsFromCSV.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        matchURLsFromCSV_ActionPerformed(evt);
      }
    });
    fileMenu_importMenu.add(importMenu_matchURLsFromCSV);

    fileMenu.add(fileMenu_importMenu);

    fileMenu_exportMenu.setText("Export");

    exportMenu_exportFavourites.setMnemonic('E');
    exportMenu_exportFavourites.setText("Export Favourites");
    exportMenu_exportFavourites.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        exportFavourites_ActionPerformed(evt);
      }
    });
    fileMenu_exportMenu.add(exportMenu_exportFavourites);

    exportMenu_exportUsedFiles.setText("Export List of used Files");
    exportMenu_exportUsedFiles.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        exportUsedFiles_ActionPerformed(evt);
      }
    });
    fileMenu_exportMenu.add(exportMenu_exportUsedFiles);

    exportMenu_exportURLsToCSV.setText("Export URLs to CSV");
    exportMenu_exportURLsToCSV
        .setToolTipText("Exports URLs from the database to the databases 'url.csv' file.");
    exportMenu_exportURLsToCSV.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        exportURLsToCSV_ActionPerformed(evt);
      }
    });
    fileMenu_exportMenu.add(exportMenu_exportURLsToCSV);

    exportMenu_exportDbToCSV.setText("Export Database to CSV");
    exportMenu_exportDbToCSV.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        exportDbToCSV_ActionPerformed(evt);
      }
    });
    fileMenu_exportMenu.add(exportMenu_exportDbToCSV);

    exportMenu_exportDbToMdb.setText("Export Database to MS Access (*.mdb)");
    exportMenu_exportDbToMdb.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        exportDbToMdb_ActionPerformed(evt);
      }
    });
    fileMenu_exportMenu.add(exportMenu_exportDbToMdb);

    fileMenu.add(fileMenu_exportMenu);

    fileMenu_Separator2.setPreferredSize(new java.awt.Dimension(2, 2));
    fileMenu.add(fileMenu_Separator2);

    fileMenu_Exit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4,
        java.awt.event.InputEvent.ALT_MASK));
    fileMenu_Exit.setText("Exit");
    fileMenu_Exit.setToolTipText("Exits the program.");
    fileMenu_Exit.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        quitActionPerformed(evt);
      }
    });
    fileMenu.add(fileMenu_Exit);

    menuBar.add(fileMenu);

    editMenu.setText("Edit");

    editMenu_GameMusicInfoMenu.setText("Game/Music Info");

    gameMusicInfoMenu_Current.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.CTRL_MASK));
    gameMusicInfoMenu_Current.setText("Current Game/Music...");
    gameMusicInfoMenu_Current.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editGameMusicInfo_Current_ActionPerformed(evt);
      }
    });
    editMenu_GameMusicInfoMenu.add(gameMusicInfoMenu_Current);

    gameMusicInfoMenu_All.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.SHIFT_MASK
            | java.awt.event.InputEvent.CTRL_MASK));
    gameMusicInfoMenu_All.setText("All Games/Music in Current View...");
    gameMusicInfoMenu_All.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editGameMusicInfo_All_ActionPerformed(evt);
      }
    });
    editMenu_GameMusicInfoMenu.add(gameMusicInfoMenu_All);

    editMenu.add(editMenu_GameMusicInfoMenu);

    editMenu_VersionInfoMenu.setText("Version Info");

    versionInfoMenu_Current.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_MASK));
    versionInfoMenu_Current.setText("Current Game...");
    versionInfoMenu_Current.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editVersionInfo_Current_ActionPerformed(evt);
      }
    });
    editMenu_VersionInfoMenu.add(versionInfoMenu_Current);

    versionInfoMenu_All.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.SHIFT_MASK
            | java.awt.event.InputEvent.CTRL_MASK));
    versionInfoMenu_All.setText("All Games in Current View...");
    versionInfoMenu_All.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editVersionInfo_All_ActionPerformed(evt);
      }
    });
    editMenu_VersionInfoMenu.add(versionInfoMenu_All);

    editMenu.add(editMenu_VersionInfoMenu);

    editMenu_PersonalInfoMenu.setText("Personal Info");

    personalInfoMenu_Current.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_MASK));
    personalInfoMenu_Current.setText("Current Game...");
    personalInfoMenu_Current.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editPersonalInfo_Current_ActionPerformed(evt);
      }
    });
    editMenu_PersonalInfoMenu.add(personalInfoMenu_Current);

    personalInfoMenu_All.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.SHIFT_MASK
            | java.awt.event.InputEvent.CTRL_MASK));
    personalInfoMenu_All.setText("All Games in Current View...");
    personalInfoMenu_All.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editPersonalInfo_All_ActionPerformed(evt);
      }
    });
    editMenu_PersonalInfoMenu.add(personalInfoMenu_All);

    editMenu.add(editMenu_PersonalInfoMenu);

    jSeparator15.setPreferredSize(new java.awt.Dimension(2, 2));
    editMenu.add(jSeparator15);

    editMenu_GameFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
    editMenu_GameFile.setText("Game File...");
    editMenu_GameFile.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editGameFile_ActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_GameFile);

    editMenu_MusicFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
    editMenu_MusicFile.setText("Music File...");
    editMenu_MusicFile.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editMusicFile_ActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_MusicFile);

    editMenu_Screenshots.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
    editMenu_Screenshots.setText("Screenshots...");
    editMenu_Screenshots.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editScreenshotsFile_ActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_Screenshots);

    editMenu_Extras.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T,
        java.awt.event.InputEvent.CTRL_MASK));
    editMenu_Extras.setText("Extras...");
    editMenu_Extras.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editExtrasFile_ActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_Extras);

    editMenu_AdditionalExtras.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
    editMenu_AdditionalExtras.setText("Additional Extras...");
    editMenu_AdditionalExtras.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editAdditionalExtrasFile_ActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_AdditionalExtras);

    jSeparator21.setPreferredSize(new java.awt.Dimension(2, 2));
    editMenu.add(jSeparator21);

    editMenu_KeyValuePairs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F2, 0));
    editMenu_KeyValuePairs.setText("\"KEY=VALUE\" pairs...");
    editMenu_KeyValuePairs.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editKeyValuePairsActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_KeyValuePairs);

    jSeparator16.setPreferredSize(new java.awt.Dimension(2, 2));
    editMenu.add(jSeparator16);

    editMenu_MarkGameForLinking.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
    editMenu_MarkGameForLinking.setText("Mark Game");
    editMenu_MarkGameForLinking.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        markGameForLinking_ActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_MarkGameForLinking);

    editMenu_SetGameLink.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
    editMenu_SetGameLink.setText("Set Game Link...");
    editMenu_SetGameLink.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        setGameLink_ActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_SetGameLink);

    editMenu_ClearGameLink.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
    editMenu_ClearGameLink.setText("Clear Game Link...");
    editMenu_ClearGameLink.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        clearGameLink_ActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_ClearGameLink);

    jSeparator17.setPreferredSize(new java.awt.Dimension(2, 2));
    editMenu.add(jSeparator17);

    editMenu_FindInList.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
    editMenu_FindInList.setText("Find in List...");
    editMenu_FindInList.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        findInList_ActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_FindInList);

    editMenu_FindNext.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F3, 0));
    editMenu_FindNext.setText("Find Next");
    editMenu_FindNext.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        findNext_ActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_FindNext);

    jSeparator18.setPreferredSize(new java.awt.Dimension(2, 2));
    editMenu.add(jSeparator18);

    editMenu_FindByIdMenu.setText("Find by ID");

    findByIdMenu_Game.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
    findByIdMenu_Game.setText("Game...");
    findByIdMenu_Game.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        findByGameIdActionPerformed(evt);
      }
    });
    editMenu_FindByIdMenu.add(findByIdMenu_Game);

    findByIdMenu_Music.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
    findByIdMenu_Music.setText("Music...");
    findByIdMenu_Music.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        findByMusicIdActionPerformed(evt);
      }
    });
    editMenu_FindByIdMenu.add(findByIdMenu_Music);

    editMenu.add(editMenu_FindByIdMenu);

    jSeparator19.setPreferredSize(new java.awt.Dimension(2, 2));
    editMenu.add(jSeparator19);

    editMenu_ToggleIsFavourite.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F12, java.awt.event.InputEvent.CTRL_MASK));
    editMenu_ToggleIsFavourite.setText("Toggle \"Is Favourite\"");
    editMenu_ToggleIsFavourite.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        toggleIsFavourite_ActionPerformed(evt);
      }
    });
    editMenu.add(editMenu_ToggleIsFavourite);

    menuBar.add(editMenu);

    viewMenu.setText("View");

    viewMenu_ConfigureTableColumns.setText("Configure Table Columns");
    viewMenu_ConfigureTableColumns.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        ItemTableConfigureColumns_ActionPerformed(evt);
      }
    });
    viewMenu.add(viewMenu_ConfigureTableColumns);

    viewMenu_SidebarMenu.setText("Sidebar");

    SideBarMenu_HideSidebar.setText("Hide Sidebar");
    SideBarMenu_HideSidebar.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        sidebarHide_ActionPerformed(evt);
      }
    });
    viewMenu_SidebarMenu.add(SideBarMenu_HideSidebar);

    jSeparator3.setPreferredSize(new java.awt.Dimension(2, 2));
    viewMenu_SidebarMenu.add(jSeparator3);

    SideBarMenu_SmallSidebar.setText("Small Sidebar");
    SideBarMenu_SmallSidebar.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        sidebarSmall_ActionPerformed(evt);
      }
    });
    viewMenu_SidebarMenu.add(SideBarMenu_SmallSidebar);

    SideBarMenu_MediumSidebar.setText("Medium Sidebar");
    SideBarMenu_MediumSidebar.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        sidebarMedium_ActionPerformed(evt);
      }
    });
    viewMenu_SidebarMenu.add(SideBarMenu_MediumSidebar);

    SideBarMenu_LargeSidebar.setText("Large Sidebar");
    SideBarMenu_LargeSidebar.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        sidebarLarge_ActionPerformed(evt);
      }
    });
    viewMenu_SidebarMenu.add(SideBarMenu_LargeSidebar);

    viewMenu.add(viewMenu_SidebarMenu);

    viewMenu_GameDetails.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
    viewMenu_GameDetails.setSelected(true);
    viewMenu_GameDetails.setText("Game Details");
    viewMenu_GameDetails.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        viewGameDetailsActionPerformed(evt);
      }
    });
    viewMenu.add(viewMenu_GameDetails);

    jSeparator6.setPreferredSize(new java.awt.Dimension(2, 2));
    viewMenu.add(jSeparator6);

    viewMenu_AddView.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
    viewMenu_AddView.setText("Add View...");
    viewMenu_AddView.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        addViewActionPerformed(evt);
      }
    });
    viewMenu.add(viewMenu_AddView);

    viewMenu_EditView.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
    viewMenu_EditView.setText("Edit View...");
    viewMenu_EditView.setEnabled(false);
    viewMenu_EditView.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        editViewActionPerformed(evt);
      }
    });
    viewMenu.add(viewMenu_EditView);

    viewMenu_RemoveView.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
    viewMenu_RemoveView.setText("Remove View");
    viewMenu_RemoveView.setEnabled(false);
    viewMenu_RemoveView.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        removeViewActionPerformed(evt);
      }
    });
    viewMenu.add(viewMenu_RemoveView);

    jSeparator10.setPreferredSize(new java.awt.Dimension(2, 2));
    viewMenu.add(jSeparator10);

    viewMenu_QuickFilter.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
    viewMenu_QuickFilter.setText("Quick Filter...");
    viewMenu_QuickFilter.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        quickFilterActionPerformed(evt);
      }
    });
    viewMenu.add(viewMenu_QuickFilter);

    viewMenu_CategoryWindow.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F4, 0));
    viewMenu_CategoryWindow.setText("Category Window");
    viewMenu_CategoryWindow.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        categoryWindowActionPerformed(evt);
      }
    });
    viewMenu.add(viewMenu_CategoryWindow);

    menuBar.add(viewMenu);

    emulatorMenu.setText("Emulators");

    jSeparator4.setPreferredSize(new java.awt.Dimension(2, 2));
    emulatorMenu.add(jSeparator4);

    emulatorMenu_ManageGameEmulators.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F11, java.awt.event.InputEvent.SHIFT_MASK));
    emulatorMenu_ManageGameEmulators.setText("Manage Game Emulators...");
    emulatorMenu_ManageGameEmulators.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        ManageGameEmulatorsActionPerformed(evt);
      }
    });
    emulatorMenu.add(emulatorMenu_ManageGameEmulators);

    emulatorMenu_ManageMusicEmulators.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F12, java.awt.event.InputEvent.SHIFT_MASK));
    emulatorMenu_ManageMusicEmulators.setText("Manage Music Emulators...");
    emulatorMenu_ManageMusicEmulators.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        ManageMusicEmulatorsActionPerformed(evt);
      }
    });
    emulatorMenu.add(emulatorMenu_ManageMusicEmulators);

    menuBar.add(emulatorMenu);

    toolsMenu.setText("Tools");

    toolsMenu_PlaySoundOnClassics.setMnemonic('S');
    toolsMenu_PlaySoundOnClassics.setText("Play Sound on Classics");
    toolsMenu_PlaySoundOnClassics.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        playSoundOnClassics_ActionPerformed(evt);
      }
    });
    toolsMenu.add(toolsMenu_PlaySoundOnClassics);

    toolsMenu_HardwareJoystick.setMnemonic('J');
    toolsMenu_HardwareJoystick.setText("Use Hardware Joystick");
    toolsMenu_HardwareJoystick.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        hardwareJoystick_ActionPerformed(evt);
      }
    });
    toolsMenu.add(toolsMenu_HardwareJoystick);

    toolsMenu_AdultFilter.setMnemonic('A');
    toolsMenu_AdultFilter.setText("Adult Filter...");
    toolsMenu_AdultFilter.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        adultFilter_ActionPerformed(evt);
      }
    });
    toolsMenu.add(toolsMenu_AdultFilter);

    jSeparator12.setPreferredSize(new java.awt.Dimension(2, 2));
    toolsMenu.add(jSeparator12);

    toolsMenu_Options.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
    toolsMenu_Options.setMnemonic('O');
    toolsMenu_Options.setText("Options...");
    toolsMenu_Options.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        options_ActionPerformed(evt);
      }
    });
    toolsMenu.add(toolsMenu_Options);

    toolsMenu_Paths.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L,
        java.awt.event.InputEvent.CTRL_MASK));
    toolsMenu_Paths.setMnemonic('P');
    toolsMenu_Paths.setText("Paths...");
    toolsMenu_Paths.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        pathsActionPerformed(evt);
      }
    });
    toolsMenu.add(toolsMenu_Paths);

    jSeparator14.setPreferredSize(new java.awt.Dimension(2, 2));
    toolsMenu.add(jSeparator14);

    toolsMenu_VerifyAvailableFiles.setMnemonic('V');
    toolsMenu_VerifyAvailableFiles.setText("Verify Available Files...");
    toolsMenu_VerifyAvailableFiles.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        verifyAvailableFilesActionPerformed(evt);
      }
    });
    toolsMenu.add(toolsMenu_VerifyAvailableFiles);

    toolsMenu_SyncAdditionalExtras.setText("Sync Additional Extras...");
    toolsMenu_SyncAdditionalExtras.setActionCommand("Synchronize Additional Extras...");
    toolsMenu_SyncAdditionalExtras.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        syncAdditionalExtrasActionPerformed(evt);
      }
    });
    toolsMenu.add(toolsMenu_SyncAdditionalExtras);

    menuBar.add(toolsMenu);

    recentMenu.setText("Recent");
    menuBar.add(recentMenu);

    downloadMenu.setText("Download");

    downloadMenu_JDB.setText("jGameBase Databases");
    downloadMenu_JDB
        .setToolTipText("Opens the download page for jGameBase databases in your web browser.");
    downloadMenu_JDB.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        downloadJDB_ActionPerformed(evt);
      }
    });
    downloadMenu.add(downloadMenu_JDB);
    downloadMenu.add(jSeparator9);

    downloadMenu_DB.setText("GameBase Databases");
    downloadMenu_DB.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        downloadDB_ActionPerformed(evt);
      }
    });
    downloadMenu.add(downloadMenu_DB);
    downloadMenu.add(jSeparator11);

    menuBar.add(downloadMenu);

    helpMenu.setText("Help");

    helpMenu_Help.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1,
        0));
    helpMenu_Help.setText("jGameBase Help...");
    helpMenu_Help.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        help_ActionPerformed(evt);
      }
    });
    helpMenu.add(helpMenu_Help);

    helpMenu_Separator1.setPreferredSize(new java.awt.Dimension(2, 2));
    helpMenu.add(helpMenu_Separator1);

    helpMenu_Forum.setText("Help forum...");
    helpMenu_Forum.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        forum_ActionPerformed(evt);
      }
    });
    helpMenu.add(helpMenu_Forum);

    helpMenu_Bugtracker.setText("File bug report...");
    helpMenu_Bugtracker.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        bugtracker_ActionPerformed(evt);
      }
    });
    helpMenu.add(helpMenu_Bugtracker);

    helpMenu_Separator2.setPreferredSize(new java.awt.Dimension(2, 2));
    helpMenu.add(helpMenu_Separator2);

    helpMenu_About.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
        java.awt.event.InputEvent.CTRL_MASK));
    helpMenu_About.setText("About jGameBase");
    helpMenu_About.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final java.awt.event.ActionEvent evt) {
        about_ActionPerformed(evt);
      }
    });
    helpMenu.add(helpMenu_About);

    menuBar.add(helpMenu);

    setJMenuBar(menuBar);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void itemTable_KeyHandler(final java.awt.event.KeyEvent evt) {// GEN-FIRST:event_itemTable_KeyHandler
    // "enter pressed" => play
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
      final Item item = getSelectedItem();
      addToRecent(item);
      item.play();
      displayItem(item); // times played might have changed
      // set key to space (enter selects next line)
      evt.setKeyCode(KeyEvent.VK_SPACE);
    }

    // quicksearch
    final long time = System.currentTimeMillis();

    // last key press to long ago? => start new search
    if ((time - timeLastKeyPressed) > 1000) {
      searchString = "";
    }
    // this key is the last key pressed
    timeLastKeyPressed = time;
    // add key character to search
    searchString += evt.getKeyChar();
    searchString = searchString.toLowerCase();

    // search in all rows
    final ItemTableModel data = (ItemTableModel) itemTable.getModel();
    final Iterator<Item> iter = data.iterator();
    boolean found = false;

    while (!found && iter.hasNext()) {
      final Item item = iter.next();
      // match found?
      if (item.getName().toLowerCase().startsWith(searchString)) {
        found = true;
        // get row of found object
        final int row = data.getIndexOf(item);
        // select found row
        data.setSelectedRow(row);
      }
    }

  }// GEN-LAST:event_itemTable_KeyHandler

  private void itemTable_MouseHandler(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_itemTable_MouseHandler

    // show
    if (Gui.singleClick(evt)) {
      displaySelectedItem(false);
    }

    // play
    if (Gui.doubleClick(evt)) {
      addToRecent(getSelectedItem());
      getSelectedItem().play();
      displaySelectedItem(false); // times played might have changed
    }

    // popup menu
    if (Gui.rightClick(evt)) {
      // select clicked row
      final int row = itemTable.rowAtPoint(evt.getPoint());
      itemTable.getSelectionModel().setSelectionInterval(row, row);

      final JPopupMenu popupMenu = createItemTablePopupMenu();

      if (popupMenu != null) {
        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
      }
    }
  }

  private void exportDbToMdb_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exportDbToMdb_ActionPerformed
    exportDbToMdb();
  }// GEN-LAST:event_exportDbToMdb_ActionPerformed

  private void filterPlayMusicButton_MouseHandler(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_filterPlayMusicButton_MouseHandler
    if (Gui.rightClick(evt)) {
      final JPopupMenu popupMenu = createPlayMusicPopupMenu();
      if (popupMenu != null) {
        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
      }
    }
  }// GEN-LAST:event_filterPlayMusicButton_MouseHandler

  private void filterPlayGameButton_MouseHandler(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_filterPlayGameButton_MouseHandler
    if (Gui.rightClick(evt)) {
      final JPopupMenu popupMenu = createPlayGamePopupMenu();
      if (popupMenu != null) {
        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
      }
    }
  }// GEN-LAST:event_filterPlayGameButton_MouseHandler

  private void stopMusicActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_stopMusicActionPerformed
    stopMusic();
  }// GEN-LAST:event_stopMusicActionPerformed

  private void ItemTableConfigureColumns_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_ItemTableConfigureColumns_ActionPerformed
    final ColumnsDialog dialog = new ColumnsDialog(itemTable.getColumns());
    if (dialog.getCloseAction() == Const.CloseAction.OK) {
      final ItemTableModel data = (ItemTableModel) itemTable.getModel();
      data.setColumnVisibility(dialog.getVisibleColumns());
    }
  }// GEN-LAST:event_ItemTableConfigureColumns_ActionPerformed

  public List<String> getColumnVisibility() {
    final List<String> visibleColumns = new ArrayList<String>();

    // get visible columns
    for (int i = 0; i < itemTable.getColumnCount(false); i++) {
      final TableColumnExt column = itemTable.getColumnExt(i);
      visibleColumns.add((String) column.getHeaderValue());
    }

    return visibleColumns;
  }

  public List<Integer> getColumnWidth() {
    final List<Integer> columnWidth = new ArrayList<Integer>();

    for (final String name : getColumnVisibility()) {
      final TableColumnExt column = itemTable.getColumnExt(name);
      columnWidth.add(column.getWidth());
    }

    return columnWidth;
  }

  /**
   * Creates the root window and the views.
   */
  private void exportUsedFiles_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exportUsedFiles_ActionPerformed
    exportUsedFiles();
  }// GEN-LAST:event_exportUsedFiles_ActionPerformed

  private void removeAllItems_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_removeAllItems_ActionPerformed
    removeAllItems();
  }// GEN-LAST:event_removeAllItems_ActionPerformed

  private void bugtracker_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_bugtracker_ActionPerformed
    bugtracker();
  }// GEN-LAST:event_bugtracker_ActionPerformed

  private void forum_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_forum_ActionPerformed
    forum();
  }// GEN-LAST:event_forum_ActionPerformed

  private void exportDbToCSV_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exportDbToCSV_ActionPerformed
    exportDbToCSV();
  }// GEN-LAST:event_exportDbToCSV_ActionPerformed

  private void importDbFromCSV_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importDbFromCSV_ActionPerformed
    importDbFromCSV();
  }// GEN-LAST:event_importDbFromCSV_ActionPerformed

  private void sidebarHide_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sidebarHide_ActionPerformed
    setSidebarWidth(0);
  }// GEN-LAST:event_sidebarHide_ActionPerformed

  private void sidebarSmall_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sidebarSmall_ActionPerformed
    setSidebarWidth(320);
  }// GEN-LAST:event_sidebarSmall_ActionPerformed

  private void sidebarMedium_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sidebarMedium_ActionPerformed
    setSidebarWidth(640);
  }// GEN-LAST:event_sidebarMedium_ActionPerformed

  private void sidebarLarge_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sidebarLarge_ActionPerformed
    setSidebarWidth(960);
  }// GEN-LAST:event_sidebarLarge_ActionPerformed

  private void downloadJDB_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_downloadJDB_ActionPerformed
    downloadJDB();
  }// GEN-LAST:event_downloadJDB_ActionPerformed

  private void downloadDB_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_downloadDB_ActionPerformed
    downloadDB();
  }// GEN-LAST:event_downloadDB_ActionPerformed

  private void importGames_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importGames_ActionPerformed
    importGames();
  }// GEN-LAST:event_importGames_ActionPerformed

  private void sideButtonPanelResized(final java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_sideButtonPanelResized
    sideButtonPanelResized();
  }// GEN-LAST:event_sideButtonPanelResized

  private void importURLsFromCSV_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importURLsFromCSV_ActionPerformed
    importURLsFromCSV();
  }// GEN-LAST:event_importURLsFromCSV_ActionPerformed

  private void exportURLsToCSV_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exportURLsToCSV_ActionPerformed
    exportURLsToCSV();
  }// GEN-LAST:event_exportURLsToCSV_ActionPerformed

  private void matchURLsFromCSV_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_matchURLsFromCSV_ActionPerformed
    matchURLsFromCSV();
  }// GEN-LAST:event_matchURLsFromCSV_ActionPerformed

  private void sideButtonPanel_MouseHandler(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_sideButtonPanel_MouseHandler
    if (Gui.rightClick(evt)) {
      final JPopupMenu popupMenu = createSideButtonPanelPopupMenu();
      if (popupMenu != null) {
        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
      }
    }
  }// GEN-LAST:event_sideButtonPanel_MouseHandler

  private void help_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_help_ActionPerformed
    help();
  }// GEN-LAST:event_help_ActionPerformed

  private void findNext_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_findNext_ActionPerformed
    findNext();
  }// GEN-LAST:event_findNext_ActionPerformed

  private void findInList_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_findInList_ActionPerformed
    findInList();
  }// GEN-LAST:event_findInList_ActionPerformed

  private void clearGameLink_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_clearGameLink_ActionPerformed
    clearGameLink();
  }// GEN-LAST:event_clearGameLink_ActionPerformed

  private void setGameLink_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_setGameLink_ActionPerformed
    setGameLink();
  }// GEN-LAST:event_setGameLink_ActionPerformed

  private void markGameForLinking_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_markGameForLinking_ActionPerformed
    markGameForLinking();
  }// GEN-LAST:event_markGameForLinking_ActionPerformed

  private void editExtrasFile_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editExtrasFile_ActionPerformed
    editExtrasFile();
  }// GEN-LAST:event_editExtrasFile_ActionPerformed

  private void editScreenshotsFile_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editScreenshotsFile_ActionPerformed
    editScreenshotsFile();
  }// GEN-LAST:event_editScreenshotsFile_ActionPerformed

  private void editMusicFile_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editMusicFile_ActionPerformed
    editMusicFile();
  }// GEN-LAST:event_editMusicFile_ActionPerformed

  private void removeSelectedItem_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_removeSelectedItem_ActionPerformed
    removeSelectedItem();
  }// GEN-LAST:event_removeSelectedItem_ActionPerformed

  private void filterExtrasButton_MouseHandler(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_filterExtrasButton_MouseHandler
    if (Gui.singleClick(evt) || Gui.rightClick(evt)) {
      final JPopupMenu popupMenu = createExtrasButtonPopupMenu();
      if (popupMenu != null) {
        popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
      }
    }
  }// GEN-LAST:event_filterExtrasButton_MouseHandler

  private void exportFavourites_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exportFavourites_ActionPerformed
    exportFavourites();
  }// GEN-LAST:event_exportFavourites_ActionPerformed

  private void importFavourites_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importFavourites_ActionPerformed
    importFavourites();
  }// GEN-LAST:event_importFavourites_ActionPerformed

  private void hardwareJoystick_MouseClicked(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_hardwareJoystick_MouseClicked
    if (Gui.doubleClick(evt)) {
      toggleHardwareJoystick();
    }
  }// GEN-LAST:event_hardwareJoystick_MouseClicked

  private void playSoundOnClassics_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_playSoundOnClassics_ActionPerformed
    togglePlaySoundOnClassics();
  }// GEN-LAST:event_playSoundOnClassics_ActionPerformed

  private void adultFilter_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_adultFilter_ActionPerformed
    toggleAdultFilter();
  }// GEN-LAST:event_adultFilter_ActionPerformed

  private void addGameFile_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addGameFile_ActionPerformed
    addGameFile();
  }// GEN-LAST:event_addGameFile_ActionPerformed

  private void addMusicFile_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addMusicFile_ActionPerformed
    addMusicFile();
  }// GEN-LAST:event_addMusicFile_ActionPerformed

  private void editGameFile_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editGameFile_ActionPerformed
    editGameFile();
  }// GEN-LAST:event_editGameFile_ActionPerformed

  private void hardwareJoystick_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_hardwareJoystick_ActionPerformed
    toggleHardwareJoystick();
  }// GEN-LAST:event_hardwareJoystick_ActionPerformed

  private void playSoundOnClassics_MouseClicked(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_playSoundOnClassics_MouseClicked
    if (Gui.doubleClick(evt)) {
      togglePlaySoundOnClassics();
    }
  }// GEN-LAST:event_playSoundOnClassics_MouseClicked

  private void toggleIsFavourite_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addToFavourites_ActionPerformed
    toggleIsFavourite();
  }// GEN-LAST:event_addToFavourites_ActionPerformed

  private void gameInfoNotes_Save_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gameInfoNotes_Save_ActionPerformed
    final Game game = getSelectedGame();
    game.setNote(notesInfoNote.getText());
    Db.saveOrUpdate(game);
  }// GEN-LAST:event_gameInfoNotes_Save_ActionPerformed

  private void gameInfoNotes_Clear_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gameInfoNotes_Clear_ActionPerformed
    notesInfoNote.setText("");
  }// GEN-LAST:event_gameInfoNotes_Clear_ActionPerformed

  private void editPersonalInfo_All_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editPersonalInfo_All_ActionPerformed
    editPersonalInfo_All();
  }// GEN-LAST:event_editPersonalInfo_All_ActionPerformed

  private void editPersonalInfo_Current_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editPersonalInfo_Current_ActionPerformed
    editPersonalInfo_Current();
  }// GEN-LAST:event_editPersonalInfo_Current_ActionPerformed

  private void editVersionInfo_All_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editVersionInfo_All_ActionPerformed
    editVersionInfo_All();
  }// GEN-LAST:event_editVersionInfo_All_ActionPerformed

  private void editVersionInfo_Current_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editVersionInfo_Current_ActionPerformed
    editVersionInfo_Current();
  }// GEN-LAST:event_editVersionInfo_Current_ActionPerformed

  private void editGameMusicInfo_All_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editGameMusicInfo_All_ActionPerformed
    editGameMusicInfo_All();
  }// GEN-LAST:event_editGameMusicInfo_All_ActionPerformed

  private void editGameMusicInfo_Current_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editGameMusicInfo_Current_ActionPerformed
    editGameMusicInfo_Current();
  }// GEN-LAST:event_editGameMusicInfo_Current_ActionPerformed

  private void editKeyValuePairsActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editKeyValuePairsActionPerformed
    final Game game = getSelectedGame();
    keyValuePairLabel.setText(game.getName());
    keyValuePairPane.setText(game.getKeyValuePairs().toUpperCase());

    if (JOptionPane.showOptionDialog(JGameBase.getGui(), keyValuePairsPanel,
        "Edit \"key=value\" pairs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
        null, null) == JOptionPane.OK_OPTION) {

      game.setKeyValuePairs(keyValuePairPane.getText());
      Db.saveOrUpdate(game);
    }

  }// GEN-LAST:event_editKeyValuePairsActionPerformed

  private void ManageMusicEmulatorsActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_ManageMusicEmulatorsActionPerformed
    manageMusicEmulators();
  }// GEN-LAST:event_ManageMusicEmulatorsActionPerformed

  private void ManageGameEmulatorsActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_ManageGameEmulatorsActionPerformed
    manageGameEmulators();
  }// GEN-LAST:event_ManageGameEmulatorsActionPerformed

  private void verifyAvailableFilesActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_verifyAvailableFilesActionPerformed
    verifyAvailableFiles();
  }// GEN-LAST:event_verifyAvailableFilesActionPerformed

  private void pathsActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pathsActionPerformed
    new PathsDialog(); // create and display dialog
  }// GEN-LAST:event_pathsActionPerformed

  private void options_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_options_ActionPerformed
    options();
  }// GEN-LAST:event_options_ActionPerformed

  private void categoriesValuesValueChanged(final javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_categoriesValuesValueChanged
    final ItemViewFilter filter = (ItemViewFilter) categoriesCategory.getSelectedItem();

    // no selection => select first element
    if (categoriesValues.getSelectedValue() == null) {
      categoriesValues.setSelectedIndex(0);
      categoriesValues.scrollRectToVisible(categoriesValues.getBounds());
    }

    final Selection selection = (Selection) categoriesValues.getSelectedValue();
    filter.setClauseData(selection.getValue());

    int include = ItemView.INCLUDE_GAMES;
    if (filter.getName().equals("Musician")) {
      include = ItemView.INCLUDE_BOTH;
    }

    final ItemView view = createQuickview(filter.getName() + " = " + selection.getName(), include);
    view.addFilter(filter);
    setQuickview(view);
  }// GEN-LAST:event_categoriesValuesValueChanged

  private void categoriesCategoryActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_categoriesCategoryActionPerformed
    final ItemViewFilter filter = (ItemViewFilter) categoriesCategory.getSelectedItem();
    categoriesValues.setListData(filter.getSelections().toArray());
  }// GEN-LAST:event_categoriesCategoryActionPerformed

  private void categoryWindowActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_categoryWindowActionPerformed
    categoriesCategory.setSelectedIndex(0);
    categoriesValues.setSelectedIndex(-1);
    categoriesDialog.setVisible(true);
  }// GEN-LAST:event_categoryWindowActionPerformed

  private void quickfilterTextKeyHandler(final java.awt.event.KeyEvent evt) {// GEN-FIRST:event_quickfilterTextKeyHandler
    if (quickfilterText.getText().isEmpty()) {
      quickfilterOkButton.setEnabled(false);
    } else {
      quickfilterOkButton.setEnabled(true);
    }

    // enter is the same as clicking "OK"
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
      quickfilterOkActionPerformed(null);
    }
  }// GEN-LAST:event_quickfilterTextKeyHandler

  private void quickviewYear(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewYear
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("Year = " + game.getYear().getName(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Years", "YE_Id", "", "", game.getYear().getId()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewYear

  private void quickfilterCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_quickfilterCancelActionPerformed
    quickfilterDialog.setVisible(false);
  }// GEN-LAST:event_quickfilterCancelActionPerformed

  private void quickfilterOkActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_quickfilterOkActionPerformed
    quickfilterDialog.setVisible(false);

    final String text = quickfilterText.getText();
    final String field = (String) quickfilterField.getSelectedItem();
    int include;

    if (quickfilterIncludeGames.isSelected()) {
      include = ItemView.INCLUDE_GAMES;
    } else if (quickfilterIncludeMusic.isSelected()) {
      include = ItemView.INCLUDE_MUSIC;
    } else {
      include = ItemView.INCLUDE_BOTH;
    }

    final ItemView view = createQuickview(quickfilterField.getSelectedItem() + " = "
        + quickfilterText.getText(), include);

    switch (field) {
      case "Name":
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_CONTAINSTEXT,
          ItemViewFilter.OPERATOR_EQUAL, "Games", "Name", "", "", text));
        break;
      case "Publisher":
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_CONTAINSTEXT,
          ItemViewFilter.OPERATOR_EQUAL, "Publishers", "Publisher", "", "", text));
        break;
      case "Programmer":
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_CONTAINSTEXT,
          ItemViewFilter.OPERATOR_EQUAL, "Programmers", "Programmer", "", "", text));
        break;
      case "Musician":
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_CONTAINSTEXT,
          ItemViewFilter.OPERATOR_EQUAL, "Musicians", "Musician", "Musicians", "Musician", text));
        break;
      case "Genre":
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_CONTAINSTEXT,
          ItemViewFilter.OPERATOR_EQUAL, "Genres", "Genre", "", "", text));
        break;
      case "Personal Comment":
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_CONTAINSTEXT,
          ItemViewFilter.OPERATOR_EQUAL, "Games", "Comment", "", "", text));
        break;
      case "Version Comment":
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_CONTAINSTEXT,
          ItemViewFilter.OPERATOR_EQUAL, "Games", "V_Comment", "", "", text));
        break;
    }

    setQuickview(view);
  }// GEN-LAST:event_quickfilterOkActionPerformed

  private void quickviewMusicianGroup(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewMusicianGroup
    if (clicked(evt)) {
      final Item item = getSelectedItem();
      if (item != null) {
        final ItemView view = createQuickview("Group = " + item.getMusician().getGroupForDisplay(),
            ItemView.INCLUDE_BOTH);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_CONTAINSTEXT,
            ItemViewFilter.OPERATOR_EQUAL, "Musicians", "Grp", "Musicians", "Grp", item
                .getMusician().getGroup()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewMusicianGroup

  private void quickviewMusicianNickname(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewMusicianNickname
    if (clicked(evt)) {
      final Item item = getSelectedItem();
      if (item != null) {
        final ItemView view = createQuickview("Nickname = "
            + item.getMusician().getNicknameForDisplay(), ItemView.INCLUDE_BOTH);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_CONTAINSTEXT,
            ItemViewFilter.OPERATOR_EQUAL, "Musicians", "Nick", "Musicians", "Nick", item
                .getMusician().getNickname()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewMusicianNickname

  private void quickviewRating(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewRating
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("Rating = " + game.getRatingForDisplay(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Games", "Rating", "", "", game.getRating()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewRating

  private void quickviewDifficulty(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewDifficulty
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("Difficulty = " + game.getDifficulty().getName(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Difficulty", "DI_Id", "", "", game.getDifficulty()
                .getId()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewDifficulty

  private void quickviewIncludedDocs(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewIncludedDocs
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview(
            "Included Docs = " + game.getHasIncludedDocsForDisplay(), ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Games", "V_IncludedDocs", "", "", game
                .getHasIncludedDocs() ? "-1" : ""));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewIncludedDocs

  private void quickviewLoadingScreen(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewLoadingScreen
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview(
            "Loading Screen = " + game.getHasLoadingScreenForDisplay(), ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Games", "V_LoadingScreen", "", "", game
                .getHasLoadingScreen() ? "-1" : ""));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewLoadingScreen

  private void quickviewTrueDriveEmu(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewTrueDriveEmu
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview(
            "True Drive Emu = " + game.getNeedsTruedriveEmuForDisplay(), ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Games", "V_TrueDriveEmu", "", "", game
                .getNeedsTruedriveEmu() ? "-1" : ""));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewTrueDriveEmu

  private void quickviewPalNtsc(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewPalNtsc
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("PAL/NTSC = " + game.getPalNtscForDisplay(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Games", "V_PalNTSC", "", "", game.getPalNtsc()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewPalNtsc

  private void quickviewGameLength(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewGameLength
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("Game Length = " + game.getLengthForDisplay(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Games", "V_Length", "", "", game.getLength()));
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Games", "V_LengthType", "", "", game.getLengthType()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewGameLength

  private void quickviewHighscoreSaver(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewHighscoreSaver
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview(
            "High Score Saver = " + game.getHasHighscoreSaverForDisplay(), ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Games", "V_HighScoreSaver", "", "", game
                .getHasHighscoreSaver() ? "-1" : ""));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewHighscoreSaver

  private void quickviewNoOfTrainers(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewNoOfTrainers
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("No. of Trainers = " + game.getTrainerForDisplay(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Games", "V_Trainers", "", "", game.getTrainer()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewNoOfTrainers

  private void quickviewCracker(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewCracker
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("Cracker = " + game.getCracker().getName(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Crackers", "CR_Id", "", "", game.getCracker().getId()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewCracker

  private void quickviewLanguage(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewLanguage
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("Language = " + game.getLanguage().getName(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Languages", "LA_Id", "", "", game.getLanguage().getId()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewLanguage

  private void quickviewNoOfPlayers(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewNoOfPlayers
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("No. of Players = " + game.getPlayersForDisplay(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_AFTER, "Games", "PlayersFrom", "", "", game.getPlayersMin() - 1));
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_BEFORE, "Games", "PlayersFrom", "", "",
            game.getPlayersMin() + 1));
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Games", "PlayersTo", "", "", game.getPlayersMax()));
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Games", "PlayersSim", "", "", game
                .getIsSimultaneouslyPlayable() ? "-1" : ""));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewNoOfPlayers

  private void quickviewGenre(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewGenre
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("Genre = " + game.getGenre().getName(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Genres", "GE_Id", "", "", game.getGenre().getId()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewGenre

  private void quickviewMusician(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewMusician
    if (clicked(evt)) {
      final Item item = getSelectedItem();
      if (item != null) {
        final ItemView view = createQuickview("Musician = " + item.getMusician().getName(),
            ItemView.INCLUDE_BOTH);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Musicians", "MU_Id", "Musicians", "MU_Id", item
                .getMusician().getId()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewMusician

  private void quickviewProgrammer(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewProgrammer
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("Programmer = " + game.getProgrammer().getName(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Programmers", "PR_Id", "", "", game.getProgrammer()
                .getId()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewProgrammer

  private void quickviewPublisher(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_quickviewPublisher
    if (clicked(evt)) {
      final Game game = getSelectedGame();
      if (game != null) {
        final ItemView view = createQuickview("Publisher = " + game.getPublisher().getName(),
            ItemView.INCLUDE_GAMES);
        view.addFilter(new ItemViewFilter(ItemViewFilter.CLAUSETYPE_DBFIELD,
            ItemViewFilter.OPERATOR_EQUAL, "Publishers", "PU_Id", "", "", game.getPublisher()
                .getId()));
        setQuickview(view);
      }
    }
  }// GEN-LAST:event_quickviewPublisher

  private void quickFilterActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_quickFilterActionPerformed
    // init fields
    quickfilterText.setText("");
    quickfilterField.setSelectedIndex(0);
    quickfilterIncludeGames.setSelected(true);
    quickfilterOkButton.setEnabled(false);

    quickfilterDialog.setVisible(true);

  }// GEN-LAST:event_quickFilterActionPerformed

  private void removeViewActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_removeViewActionPerformed
    removeView();
  }// GEN-LAST:event_removeViewActionPerformed

  private void editViewActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editViewActionPerformed
    editView();
  }// GEN-LAST:event_editViewActionPerformed

  private void addViewActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addViewActionPerformed
    addView();
  }// GEN-LAST:event_addViewActionPerformed

  private void filterViewButtonMouseHandler(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_filterViewButtonMouseHandler
    if ((evt.getButton() == MouseEvent.BUTTON1) && (evt.getClickCount() == 1)) {
      viewPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }
  }// GEN-LAST:event_filterViewButtonMouseHandler

  private void selectRandomActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_selectRandomActionPerformed
    final ItemTableModel data = (ItemTableModel) itemTable.getModel();

    // only one item => select that
    if (itemTable.getRowCount() == 1) {
      data.setSelectedRow(0);
      displayItem(data.getItem(0));
    }

    // more items => get a random one, but not the currently selected
    if (itemTable.getRowCount() > 1) {
      final int oldRow = data.getSelectedRow();
      int row = oldRow;

      while (row == oldRow) {
        row = (int) Math.round(Math.random() * (itemTable.getRowCount() - 1));
      }

      data.setSelectedRow(row);
      displayItem(data.getItem(row));
    }
  }// GEN-LAST:event_selectRandomActionPerformed

  private void gotoRelatedActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gotoRelatedActionPerformed
    final Item item = getSelectedItem();
    if (item instanceof Game) {
      final Game game = (Game) item;
      if (game.hasRelated()) {
        selectItem("G" + game.getRelatedId(), true);
      }
    }
  }// GEN-LAST:event_gotoRelatedActionPerformed

  private void gotoSequelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gotoSequelActionPerformed
    final Item item = getSelectedItem();
    if (item instanceof Game) {
      final Game game = (Game) item;
      if (game.hasSequel()) {
        selectItem("G" + game.getSequelId(), true);
      }
    }
  }// GEN-LAST:event_gotoSequelActionPerformed

  private void gotoPrequelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gotoPrequelActionPerformed
    final Item item = getSelectedItem();
    if (item instanceof Game) {
      final Game game = (Game) item;
      if (game.hasPrequel()) {
        selectItem("G" + game.getPrequelId(), true);
      }
    }
  }// GEN-LAST:event_gotoPrequelActionPerformed

  private void findByMusicIdActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_findByMusicIdActionPerformed
    final String idString = JOptionPane.showInputDialog(this, "Music ID to find:",
        "Find Music by ID", JOptionPane.PLAIN_MESSAGE);
    selectItem("M" + idString, true);
  }// GEN-LAST:event_findByMusicIdActionPerformed

  private void findByGameIdActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_findByGameIdActionPerformed
    final String idString = JOptionPane.showInputDialog(this, "Game ID to find:",
        "Find Game by ID", JOptionPane.PLAIN_MESSAGE);
    selectItem("G" + idString, true);
  }// GEN-LAST:event_findByGameIdActionPerformed

  private void playMusicActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_playMusicActionPerformed
    playSelectedItemAsMusic(null);
  }// GEN-LAST:event_playMusicActionPerformed

  private void playGameActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_playGameActionPerformed
    final Item item = getSelectedItem();

    if (item instanceof Game) {
      addToRecent(item);
      item.play();

      try {
        ((Game) item).createScreenshots();
      } catch (final IOException e) {
        e.printStackTrace();
      }
      Db.saveOrUpdate(item);
    }
  }// GEN-LAST:event_playGameActionPerformed

  private void filterViewActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_filterViewActionPerformed
    final ItemView view = (ItemView) filterViewCombobox.getSelectedItem();
    ((ItemTableModel) itemTable.getModel()).setView(view);
  }// GEN-LAST:event_filterViewActionPerformed

  private void viewGameDetailsActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_viewGameDetailsActionPerformed
    infoPanel.setVisible(viewMenu_GameDetails.isSelected());
    itemPane.resetToPreferredSizes();
    mainPane.revalidate();
    Preferences.set(Preferences.DISPLAY_DETAILS, viewMenu_GameDetails.isSelected());
  }// GEN-LAST:event_viewGameDetailsActionPerformed

  private void about_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_about_ActionPerformed
    about();
  }// GEN-LAST:event_about_ActionPerformed

  private void quitActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_quitActionPerformed
    JGameBase.quit();
  }// GEN-LAST:event_quitActionPerformed

  private void exitFormWindowClosing(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitFormWindowClosing
    JGameBase.quit();
  }// GEN-LAST:event_exitFormWindowClosing

  private void editAdditionalExtrasFile_ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editAdditionalExtrasFile_ActionPerformed
    editAdditionalExtrasFile();
  }// GEN-LAST:event_editAdditionalExtrasFile_ActionPerformed

  private void syncAdditionalExtrasActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_syncAdditionalExtrasActionPerformed
    syncAdditionalExtras();
  }// GEN-LAST:event_syncAdditionalExtrasActionPerformed

  private long timeLastKeyPressed;

  private String searchString = "";

  abstract protected void about();

  abstract protected void help();

  abstract protected void forum();

  abstract protected void bugtracker();

  abstract protected void options();

  abstract protected void quit();

  abstract protected void displayItem(Item item);

  abstract protected void displaySelectedItem(boolean forceUpdate);

  abstract protected void selectItem(String idString, boolean displayWarnings);

  abstract protected void addView();

  abstract protected void editView();

  abstract protected void removeView();

  abstract protected ItemView createQuickview(String name, int include);

  abstract protected void setQuickview(ItemView view);

  abstract protected boolean clicked(java.awt.event.MouseEvent evt);

  abstract protected Item getSelectedItem();

  abstract protected Game getSelectedGame();

  abstract protected Music getSelectedMusic();

  abstract protected void addToRecent(Item item);

  abstract protected void editGameMusicInfo_Current();

  abstract protected void editGameMusicInfo_All();

  abstract protected void editVersionInfo_Current();

  abstract protected void editVersionInfo_All();

  abstract protected void editPersonalInfo_Current();

  abstract protected void editPersonalInfo_All();

  abstract protected void toggleIsFavourite();

  abstract protected void updateItemTable();

  abstract protected void togglePlaySoundOnClassics();

  abstract protected void toggleHardwareJoystick();

  abstract protected void toggleAdultFilter();

  abstract protected void importFavourites();

  abstract protected void importDbFromCSV();

  abstract protected void exportFavourites();

  abstract protected void exportUsedFiles();

  abstract protected void exportDbToCSV();

  abstract protected void exportDbToMdb();

  abstract protected JPopupMenu createPlayGamePopupMenu();

  abstract protected JPopupMenu createPlayMusicPopupMenu();

  abstract protected JPopupMenu createExtrasButtonPopupMenu();

  abstract protected JPopupMenu createSideButtonPanelPopupMenu();

  abstract protected JPopupMenu createItemTablePopupMenu();

  abstract protected void addGameFile();

  abstract protected void addMusicFile();

  abstract protected void editGameFile();

  abstract protected void editMusicFile();

  abstract protected void editScreenshotsFile();

  abstract protected void editExtrasFile();

  abstract protected void editAdditionalExtrasFile();

  abstract protected void verifyAvailableFiles();

  abstract protected void syncAdditionalExtras();

  abstract protected void removeSelectedItem();

  abstract protected void removeAllItems();

  abstract protected void findNext();

  abstract protected void findInList();

  abstract protected void markGameForLinking();

  abstract protected void setGameLink();

  abstract protected void clearGameLink();

  abstract protected void setSidebarWidth(int size);

  abstract protected void playSelectedItemAsMusic(Emulator emulator);

  abstract protected void stopMusic();

  abstract protected void unzipDatabase(File file);

  abstract protected void downloadJDB();

  abstract protected void downloadDB();

  abstract protected void importGames();

  abstract protected void manageMusicEmulators();

  abstract protected void manageGameEmulators();

  abstract protected void sideButtonPanelResized();

  abstract protected void importURLsFromCSV();

  abstract protected void exportURLsToCSV();

  abstract protected void matchURLsFromCSV();

}
