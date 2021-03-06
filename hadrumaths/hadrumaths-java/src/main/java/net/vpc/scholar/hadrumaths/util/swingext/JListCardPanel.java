package net.vpc.scholar.hadrumaths.util.swingext;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.vpc.scholar.hadrumaths.util.StringShellFilter;

/**
 * @author Taha Ben Salah (taha.bensalah@gmail.com)
 * @creationtime 19 oct. 2006 00:16:49
 */
public class JListCardPanel extends JCardPanel {
    private JPanel main = new JPanel(new CardLayout());
    private JTextField filterText = new JTextField();
    private JList list = new JList(new FilteredListModel());
    private JSplitPane splitPane;

    public JListCardPanel() {
        super(new BorderLayout());
        JScrollPane spane = new JScrollPane(list);
        JPanel p = new JPanel(new BorderLayout());
        p.add(filterText, BorderLayout.PAGE_START);
        filterText.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                anyUpdate(e);
            }

            public void removeUpdate(DocumentEvent e) {
                anyUpdate(e);
            }

            public void changedUpdate(DocumentEvent e) {
                anyUpdate(e);
            }

            public void anyUpdate(DocumentEvent e) {
                ((FilteredListModel) (list.getModel())).setFilter(new TitleFiltredListModelFilter(filterText.getText()));
            }
        });
        p.add(spane, BorderLayout.CENTER);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, p, main);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(0.3);
        add(splitPane, BorderLayout.CENTER);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                PanelPage selectedValue = (PanelPage) list.getSelectedValue();
                if (selectedValue != null) {
                    ((CardLayout) main.getLayout()).show(main, selectedValue.id);
                }
            }
        });
        list.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                PanelPage i = (PanelPage) value;
                super.getListCellRendererComponent(list, i.title, index, isSelected, cellHasFocus);
                setIcon(i.icon);
                return this;
            }
        });
    }

    public void addPage(String id, String title, Icon icon, JComponent c) {
        ((FilteredListModel) list.getModel()).addElement(new PanelPage(c, id, title, icon));
        main.add(c, id);
        if (list.getModel().getSize() == 1) {
            list.setSelectedIndex(list.getModel().getSize() - 1);
        }
    }

    public JComponent[] getPageComponents() {
        FilteredListModel listModel = ((FilteredListModel) list.getModel());
        PanelPage[] pages = listModel.toArray();
        JComponent[] all = new JComponent[pages.length];
        for (int i = 0; i < pages.length; i++) {
            all[i] = pages[i].component;
        }
        return all;
    }

    public JComponent getPageComponent(String id) {
        PanelPage panelPage = getPage(id);
        return panelPage == null ? null : panelPage.component;
    }

    private PanelPage getPage(String id) {
        if(id==null){
            id="";
        }
        id=id.trim();
        if(id.isEmpty()){
            id="No Name";
        }
        FilteredListModel listModel = ((FilteredListModel) list.getModel());
        for (Object o : listModel.toArray()) {
            PanelPage p = (PanelPage) o;
            if (p.id.equals(id)) {
                return p;
            }
        }
        return null;
    }

    public JComponent getPageComponentAt(int i) {
        PanelPage o = ((FilteredListModel) list.getModel()).getElementAt(i);
        return o.component;
    }


    public JSplitPane getSplitPane() {
        return splitPane;
    }

    private static class PanelPage implements Serializable {
        String id;
        String title;
        Icon icon;
        JComponent component;

        public PanelPage(JComponent component, String id, String title, Icon icon) {
            this.id = id;
            this.title = title;
            this.component = component;
            this.icon = icon;
        }

    }

    private static class NoFiltredListModelFilter implements FiltredListModelFilter {

        public boolean accept(PanelPage o) {
            return true;
        }
    }

    private static class TitleFiltredListModelFilter implements FiltredListModelFilter {
        private String pattern;

        public TitleFiltredListModelFilter(String pattern) {
            pattern = pattern.toLowerCase();
            if (!pattern.startsWith("*")) {
                pattern = "*" + pattern;
            }
            if (!pattern.endsWith("*")) {
                pattern = pattern + "*";
            }
            this.pattern = StringShellFilter.shellToRegexpPattern(pattern);
        }

        public boolean accept(PanelPage o) {
            return o.title.toLowerCase().matches(pattern);
        }
    }

    private static interface FiltredListModelFilter {
        public boolean accept(PanelPage o);
    }

    private static class FilteredListModel extends AbstractListModel {
        private FiltredListModelFilter filter = new NoFiltredListModelFilter();
        private java.util.List<PanelPage> delegate = new ArrayList<PanelPage>();
        private java.util.List<PanelPage> realDelegate = new ArrayList<PanelPage>();


        public PanelPage getElementAt(int index) {
            return delegate.get(index);
        }

        public int getSize() {
            return delegate.size();
        }

        public FiltredListModelFilter getFilter() {
            return filter;
        }

        public void setFilter(FiltredListModelFilter filter) {
            if (filter == null) {
                filter = new NoFiltredListModelFilter();
            }
            this.filter = filter;
            rebuild();
        }

        private void rebuild() {
            if (filter != null) {
                int index = delegate.size();
                if (index > 0) {
                    delegate.clear();
                    fireIntervalRemoved(this, 0, index - 1);
                }
                for (PanelPage o : realDelegate) {
                    if (filter.accept(o)) {
                        delegate.add(o);
                    }
                }
                if (delegate.size() > 0) {
                    fireIntervalAdded(this, 0, delegate.size() - 1);
                }
            }
        }

        private PanelPage findPanelPageById(String id) {
            for (PanelPage panelPage : delegate) {
                if(panelPage.id.equals(id)){
                    return panelPage;
                }
            }
            return null;
        }

        public static void main(String[] args) {
            System.out.println(rename("hello [2]",6));
        }

        private static String rename(String name,int index) {
            Pattern p=Pattern.compile("^(.*)\\[([0-9]+)\\]$");
            Matcher m = p.matcher(name);
            if(m.find()){
                String prefix = m.group(1);
                String suffix = m.group(2);
                return prefix+"["+index+"]";
            }else{
                if(index==0){
                    return name;
                }
                return name+" ["+index+"]";
            }
        }

        private void preInsertPanelPage(PanelPage page) {
            if(page.id==null){
                page.id="";
            }
            if(page.title==null){
                page.title="";
            }
            if(page.title.isEmpty()){
                page.title="Default";
            }
            int loop=0;
            while(true){
                PanelPage p = findPanelPageById(page.id);
                if(p!=null){
                    page.id=rename(page.id,loop);
//                    page.title=page.title+"'";
                }else{
                    break;
                }
                loop++;
            }
        }

        private void addElement(PanelPage object) {
            preInsertPanelPage(object);
            for (PanelPage panelPage : delegate) {
                if(panelPage.id.equals(object.id)){
                    object.id=object.id+"'";
                    object.title=object.title+"'";
//                    throw new IllegalArgumentException(object.id+" Already exists");
                }
            }
            realDelegate.add(object);
            if (filter.accept(object)) {
                int index = delegate.size();
                delegate.add(object);
                fireIntervalAdded(this, index, index);
            }
        }

        public PanelPage[] toArray() {
            return realDelegate.toArray(new PanelPage[realDelegate.size()]);
        }

        public FilteredListModel() {
        }
    }

}
