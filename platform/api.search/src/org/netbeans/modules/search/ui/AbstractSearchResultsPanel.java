/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.modules.search.ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.text.DefaultEditorKit;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.search.SearchControl;
import org.netbeans.modules.search.ResultView;
import org.netbeans.spi.search.provider.SearchComposition;
import org.netbeans.spi.search.provider.SearchProvider;
import org.netbeans.spi.search.provider.SearchProvider.Presenter;
import org.netbeans.swing.outline.Outline;
import org.openide.awt.ToolbarWithOverflow;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.OutlineView;
import org.openide.explorer.view.Visualizer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;

/**
 *
 * @author jhavlin
 */
public abstract class AbstractSearchResultsPanel extends javax.swing.JPanel
        implements ExplorerManager.Provider, Lookup.Provider {

    @StaticResource
    private static final String REFRESH_ICON =
            "org/netbeans/modules/search/res/refresh.png";              //NOI18N
    @StaticResource
    private static final String STOP_ICON =
            "org/netbeans/modules/search/res/stop.png";                 //NOI18N
    @StaticResource
    private static final String NEXT_ICON =
            "org/netbeans/modules/search/res/next.png";                 //NOI18N
    @StaticResource
    private static final String PREV_ICON =
            "org/netbeans/modules/search/res/prev.png";                 //NOI18N
    @StaticResource
    private static final String EXPAND_ICON =
            "org/netbeans/modules/search/res/expandTree.png";           //NOI18N
    @StaticResource
    private static final String COLLAPSE_ICON =
            "org/netbeans/modules/search/res/collapseTree.png";         //NOI18N

    private ExplorerManager explorerManager;
    private SearchComposition<?> searchComposition;
    protected JButton btnStopRefresh = new JButton();
    protected JButton btnPrev = new JButton();
    protected JButton btnNext = new JButton();
    protected JToggleButton btnExpand = new JToggleButton();
    private final Presenter searchProviderPresenter;
    private Lookup lookup;
    private volatile boolean btnStopRefreshInRefreshMode = false;

    /**
     * Creates new form AbstractSearchResultsPanel
     */
    public AbstractSearchResultsPanel(SearchComposition<?> searchComposition,
            SearchProvider.Presenter searchProviderPresenter) {
        this.searchComposition = searchComposition;
        this.searchProviderPresenter = searchProviderPresenter;
        initComponents();
        explorerManager = new ExplorerManager();

        ActionMap map = this.getActionMap();
        // map delete key to delete action
        map.put("delete", //NOI18N
                ExplorerUtils.actionDelete(explorerManager, false));
        map.put(DefaultEditorKit.copyAction,
                ExplorerUtils.actionCopy(explorerManager));
        map.put(DefaultEditorKit.cutAction,
                ExplorerUtils.actionCut(explorerManager));

        lookup = ExplorerUtils.createLookup(explorerManager,
                ResultView.getInstance().getActionMap());
        initActions();
        initToolbar();
        initSelectionListeners();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new ToolbarWithOverflow();
        contentPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        toolBar.setOrientation(JToolBar.VERTICAL);
        toolBar.setRollover(true);
        toolBar.setPreferredSize(null);
        toolBar.setRequestFocusEnabled(false);
        add(toolBar, java.awt.BorderLayout.WEST);

        contentPanel.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("controlShadow")));
        contentPanel.setLayout(new javax.swing.BoxLayout(contentPanel, javax.swing.BoxLayout.LINE_AXIS));
        add(contentPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

    @Override
    public final ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private void initToolbar() {

        toolBar.setRollover(true);

        initStopRefreshButton();
        toolBar.add(btnStopRefresh);
        initPrevButton();
        toolBar.add(btnPrev);
        initNextButton();
        toolBar.add(btnNext);
        initExpandButton();
        toolBar.add(btnExpand);

        toolBar.setMinimumSize(new Dimension(
                (int) toolBar.getMinimumSize().getWidth(),
                (int) btnStopRefresh.getMinimumSize().getHeight()));
    }

    private void initStopRefreshButton() throws MissingResourceException {
        sizeButton(btnStopRefresh);
        btnStopRefresh.addActionListener((ActionEvent e) -> {
            if (btnStopRefreshInRefreshMode) {
                modifyCriteria();
            } else {
                getSearchComposition().terminate();
            }
        });
        btnStopRefresh.setToolTipText(
                UiUtils.getText("TEXT_BUTTON_STOP"));                   //NOI18N
        btnStopRefresh.setIcon(ImageUtilities.loadImageIcon(STOP_ICON, true));
        btnStopRefresh.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(ResultView.class,
                "ACS_TEXT_BUTTON_STOP"));                               //NOI18N
    }

    private void initExpandButton() {
        sizeButton(btnExpand);
        btnExpand.setIcon(ImageUtilities.loadImageIcon(EXPAND_ICON, true));
        btnExpand.setSelectedIcon(ImageUtilities.loadImageIcon(
                COLLAPSE_ICON, true));
        btnExpand.setToolTipText(UiUtils.getText(
                "TEXT_BUTTON_EXPAND"));                                 //NOI18N
        btnExpand.setEnabled(false);
        btnExpand.setSelected(false);
    }

    private void initNextButton() {
        sizeButton(btnNext);
        btnNext.setIcon(ImageUtilities.loadImageIcon(NEXT_ICON, true));
        btnNext.setToolTipText(UiUtils.getText(
                "TEXT_BUTTON_NEXT_MATCH"));                             //NOI18N
        btnNext.setEnabled(false);
        btnNext.addActionListener((ActionEvent e) -> shift(1));
    }

    private void initPrevButton() {
        sizeButton(btnPrev);
        btnPrev.setIcon(ImageUtilities.loadImageIcon(PREV_ICON, true));
        btnPrev.setToolTipText(UiUtils.getText(
                "TEXT_BUTTON_PREV_MATCH"));                             //NOI18N
        btnPrev.setEnabled(false);
        btnPrev.addActionListener((ActionEvent e) -> shift(-1));
    }

    protected void sizeButton(AbstractButton button) {
        Dimension dim = new Dimension(24, 24);
        button.setMinimumSize(dim);
        button.setMaximumSize(dim);
        button.setPreferredSize(dim);
    }

    protected JPanel getContentPanel() {
        return contentPanel;
    }

    protected SearchComposition<?> getSearchComposition() {
        return searchComposition;
    }

    public void searchStarted() {
    }

    public void searchFinished() {
        Mutex.EVENT.writeAccess(this::showRefreshButton);
    }

    /**
     * Set btnStopRefresh to show refresh icon.
     */
    protected void showRefreshButton() {
        btnStopRefresh.setToolTipText(UiUtils.getText(
                "TEXT_BUTTON_CUSTOMIZE"));                              //NOI18N
        btnStopRefresh.setIcon(
                ImageUtilities.loadImageIcon(REFRESH_ICON, true));
        btnStopRefresh.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(ResultView.class,
                "ACS_TEXT_BUTTON_CUSTOMIZE"));                          //NOI18N
        btnStopRefreshInRefreshMode = true;
    }

    protected void modifyCriteria() {
        if (searchProviderPresenter != null) {
            if (searchProviderPresenter.isReplacing()) {
                SearchControl.openReplaceDialog(searchProviderPresenter);
            } else {
                SearchControl.openFindDialog(searchProviderPresenter);
            }
        }
    }

    protected void addButton(AbstractButton button) {
        toolBar.add(button);
    }

    static class RootNode extends AbstractNode {

        Node resultsNode;
        Node infoNode;

        public RootNode(Node resultsNode, Node infoNode) {
            this(resultsNode, infoNode,
                    new RootNodeChildren(resultsNode, infoNode));
        }

        private RootNode(Node resultsNode, Node infoNode,
                final RootNodeChildren rootNodeChildren) {
            super(rootNodeChildren);
            this.infoNode = infoNode;
            this.resultsNode = resultsNode;
            if (infoNode != null) {
                setInfoNodeListener(rootNodeChildren);
            }
        }

        private void setInfoNodeListener(
                final RootNodeChildren rootNodeChildren) {

            assert infoNode != null;

            infoNode.getChildren().getNodes(true);
            infoNode.addNodeListener(new NodeAdapter() {
                private boolean added = false;

                @Override
                public synchronized void childrenAdded(NodeMemberEvent ev) {
                    if (!added) {
                        rootNodeChildren.showInfoNode();
                        infoNode.removeNodeListener(this);
                        added = true;
                    }
                }

                @Override
                public void propertyChange(PropertyChangeEvent ev) {
                    super.propertyChange(ev);
                }

                @Override
                public void childrenReordered(NodeReorderEvent ev) {
                    super.childrenReordered(ev);
                }
            });
        }
    }

    private static class RootNodeChildren extends Children.Keys<Node> {

        private Node[] standard;
        private Node[] withInfo;
        private boolean infoNodeShown = false;

        public RootNodeChildren(Node resultsNode, Node infoNode) {
            standard = new Node[] {resultsNode};
            withInfo = new Node[] {resultsNode, infoNode};
            setKeys(standard);
        }

        private synchronized void showInfoNode() {
            if (!infoNodeShown) {
                setKeys(withInfo);
                infoNodeShown = true;
            }
        }

        @Override
        protected Node[] createNodes(Node key) {
            return new Node[]{key};
        }
    }

    protected void toggleExpand(Node root, boolean expand) {
        if (expand) {
            getOutlineView().expandNode(root);
        }
        for (Node n : root.getChildren().getNodes()) {
            toggleExpand(n, expand);
        }
        if (!expand) {
            getOutlineView().collapseNode(root);
        }
    }

    protected abstract OutlineView getOutlineView();

    private void initActions() {
        ActionMap map = getActionMap();
        map.put("jumpNext", new PrevNextAction(1));                    // NOI18N
        map.put("jumpPrev", new PrevNextAction(-1));                   // NOI18N
    }

    private void initSelectionListeners() {
        getExplorerManager().addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (evt.getPropertyName().equals("selectedNodes")) { //NOI18N
                EventQueue.invokeLater(this::updateShiftButtons); //#218680
            }
        });
    }

    protected void updateShiftButtons() {
        if (btnPrev.isVisible() && btnNext.isVisible()) {
            btnPrev.setEnabled(
                    findShiftNode(-1, getOutlineView(), false) != null);
            btnNext.setEnabled(
                    findShiftNode(1, getOutlineView(), false) != null);
        }
    }

    private void shift(int direction) {

        Node next = findShiftNode(direction, getOutlineView(), true);
        if (next != null) {
            try {
                getExplorerManager().setSelectedNodes(new Node[]{next});
                onDetailShift(next);
            } catch (PropertyVetoException pve) {
                Exceptions.printStackTrace(pve);
            }
        }
    }

    /**
     * Should be called after a matching node was added to update state of
     * buttons.
     */
    protected void afterMatchingNodeAdded() {
        Mutex.EVENT.writeAccess(() -> {
            if (btnNext.isVisible() && !btnNext.isEnabled()) {
                updateShiftButtons();
            }
        });
    }

    /**
     * Called when a node is selected by clicking Next or Previous button.
     */
    protected void onDetailShift(Node n) {
    }

    private Node findShiftNode(int direction, OutlineView outlineView,
            boolean canExpand) {
        Node[] selected = getExplorerManager().getSelectedNodes();
        Node n = null;
        if ((selected == null || selected.length == 0)
                /* TODO && getExplorerManager().getRootContext() == resultsOutlineSupport.getRootNode() */) {
            n = getExplorerManager().getRootContext();
        } else if (selected.length == 1) {
            n = selected[0];
        }
        return n == null ? null : findDetailNode(n, direction, outlineView,
                canExpand);
    }

    Node findDetailNode(Node fromNode, int direction,
            OutlineView outlineView, boolean canExpand) {
        return findUp(fromNode, direction,
                isDetailNode(fromNode) || direction < 0 ? direction : 0,
                outlineView, canExpand);
    }

    /**
     * Start finding for next or previous occurance, from a node or its previous
     * or next sibling of node {@code node}
     *
     * @param node reference node
     * @param offset 0 to start from node {@code node}, 1 to start from its next
     * sibling, -1 to start from its previous sibling.
     * @param dir Direction: 1 for next, -1 for previous.
     */
    Node findUp(Node node, int dir, int offset, OutlineView outlineView,
            boolean canExpand) {
        if (node == null) {
            return null;
        }
        Node parent = node.getParentNode();
        Node[] siblings;
        if (parent == null) {
            siblings = new Node[]{node};
        } else {
            siblings = getChildren(parent, outlineView, canExpand);
        }
        int nodeIndex = findChildIndex(node, siblings);
        if (nodeIndex + offset < 0 || nodeIndex + offset >= siblings.length) {
            return findUp(parent, dir, dir, outlineView, canExpand);
        }
        for (int i = nodeIndex + offset;
                i >= 0 && i < siblings.length; i += dir) {
            Node found = findDown(siblings[i], siblings, i, dir, outlineView,
                    canExpand);
            return found;
        }
        return findUp(parent, dir, offset, outlineView, canExpand);
    }

    /**
     * Find Depth-first search to find a detail node in the subtree.
     */
    private Node findDown(Node node, Node[] siblings, int nodeIndex,
            int dir, OutlineView outlineView, boolean canExpand) {

        Node[] children = getChildren(node, outlineView, canExpand);
        for (int i = dir > 0 ? 0 : children.length - 1;
                i >= 0 && i < children.length; i += dir) {
            Node found = findDown(children[i], children, i, dir, outlineView,
                    canExpand);
            if (found != null) {
                return found;
            }
        }
        for (int i = nodeIndex; i >= 0 && i < siblings.length; i += dir) {
            if (isDetailNode(siblings[i])) {
                return siblings[i];
            }
        }
        return null;
    }

    protected abstract boolean isDetailNode(Node n);

    private static int findChildIndex(Node selectedNode, Node[] siblings) {
        int pos = -1;
        for (int i = 0; i < siblings.length; i++) {
            if (siblings[i] == selectedNode) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    private static Node[] getChildren(Node n, OutlineView outlineView,
            boolean canExpand) {
        if (outlineView != null) {
            if (!outlineView.isExpanded(n)) {
                if (canExpand) {
                    outlineView.expandNode(n);
                } else {
                    return n.getChildren().getNodes(true);
                }
            }
            return getChildrenInDisplayedOrder(n, outlineView);
        } else {
            return n.getChildren().getNodes(true);
        }
    }

    private static Node[] getChildrenInDisplayedOrder(Node parent,
            OutlineView outlineView) {

        Outline outline = outlineView.getOutline();
        Node[] unsortedChildren = parent.getChildren().getNodes(true);
        int rows = outlineView.getOutline().getRowCount();
        int start = findRowIndexInOutline(parent, outline, rows);
        if (start == -1) {
            return unsortedChildren;
        }
        List<Node> children = new LinkedList<>();
        for (int j = start + 1; j < rows; j++) {
            int childModelIndex = outline.convertRowIndexToModel(j);
            if (childModelIndex == -1) {
                continue;
            }
            Object childObject = outline.getModel().getValueAt(
                    childModelIndex, 0);
            Node childNode = Visualizer.findNode(childObject);
            if (childNode.getParentNode() == parent) {
                children.add(childNode);
            } else if (children.size() == unsortedChildren.length) {
                break;
            }
        }
        return children.toArray(new Node[0]);
    }

    private static int findRowIndexInOutline(Node node, Outline outline,
            int rows) {

        int startRow = Math.max(outline.getSelectedRow(), 0);
        int offset = 0;
        while (startRow + offset < rows || startRow - offset >= 0) {
            int up = startRow + offset + 1;
            int down = startRow - offset;

            if (up < rows && testNodeInRow(outline, node, up)) {
                return up;
            } else if (down >= 0 && testNodeInRow(outline, node, down)) {
                return down;
            } else {
                offset++;
            }
        }
        return -1;
    }

    private static boolean testNodeInRow(Outline outline, Node node, int i) {
        int modelIndex = outline.convertRowIndexToModel(i);
        if (modelIndex != -1) {
            Object o = outline.getModel().getValueAt(modelIndex, 0);
            Node n = Visualizer.findNode(o);
            if (n == node) {
                return true;
            }
        }
        return false;
    }

    private final class PrevNextAction extends javax.swing.AbstractAction {

        private int direction;

        public PrevNextAction(int direction) {
            this.direction = direction;
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            shift(direction);
        }
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
