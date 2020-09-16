package rcomic.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import rcomic.control.RComic;

/** @author ray */
public class Home extends JFrame {
  private static final long serialVersionUID = 4239430715284526041L;
  /** 搜尋輸入框 */
  private JTextField searchComic;
  /** 放分頁 */
  private JTabbedPane tabbedPand;
  /** 漫畫列表 */
  private ComicList comicList;
  /** 漫畫集數列表 */
  private EpisodesList episodesList;

  public Home() {}

  /** 初始化，產生首頁所有使用到的物件 */
  public void initialize() {
    setupUI();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    double scale = 0.8;
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    setSize((int) (screen.getWidth() * scale), (int) (screen.getHeight() * scale));

    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void setupUI() {
    setTitle(RComic.get().getConfig().version);
    Container container = getContentPane();
    JPanel northPanel = new JPanel(new GridLayout(0, 5, 10, 0));
    JPanel centerPanel = new JPanel(new GridLayout(0, 2));
    getContentPane().add(northPanel, BorderLayout.NORTH);

    JPanel findPanel = new JPanel(new BorderLayout());
    searchComic = new JTextField();
    findPanel.add(new JLabel(RComic.get().getLang("Search")), BorderLayout.WEST);
    findPanel.add(searchComic, BorderLayout.CENTER);
    northPanel.add(findPanel);

    comicList = new ComicList();
    comicList.initialize();

    tabbedPand = new JTabbedPane();
    tabbedPand.add(RComic.get().getLang("ComicList"), comicList.getAll().toJScrollPane());
    tabbedPand.add(RComic.get().getLang("NewestComic"), comicList.getNew().toJScrollPane());
    centerPanel.add(tabbedPand);

    episodesList = new EpisodesList();

    comicList.setOpenClmicListener(
        comic -> {
          SwingUtilities.invokeLater(
              () -> {
                episodesList.setComic(comic);
              });
        });

    centerPanel.add(episodesList);
    container.add(centerPanel, BorderLayout.CENTER);

    searchComic
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {

              @Override
              public void insertUpdate(DocumentEvent e) {
                comicList.refreshSearchResult(searchComic.getText());
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                comicList.refreshSearchResult(searchComic.getText());
              }

              @Override
              public void changedUpdate(DocumentEvent e) {}
            });
  }

  /** 取得搜尋漫畫輸入框 要搜尋的漫畫名稱 */
  public String getFindFieldText() {
    return searchComic.getText();
  }

  /**
   * 漫畫下載程式 啟動點,啟動時會連向遠端檢查是本地端是否有漫畫編號文字檔，若沒有會重load並存檔
   *
   * @param args
   */
  public static void main(String[] args) {
    JDialog loading = RComic.get().newLoadingDialog();

    RComic.get()
        .preprogress(
            () -> {
              SwingUtilities.invokeLater(
                  () -> {
                    // 建立動畫程式首頁
                    Home download = new Home();
                    download.initialize();

                    loading.dispose();
                  });
            });
  }
}
