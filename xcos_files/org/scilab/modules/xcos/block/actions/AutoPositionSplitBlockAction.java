/*
 * Scilab ( http://www.scilab.org/ ) - This file is part of Scilab
 * Copyright (C) 2016 - Chenfeng ZHU
 *
 * This file must be used under the terms of the CeCILL.
 * This source file is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.  The terms
 * are also available at
 * http://www.cecill.info/licences/Licence_CeCILL_V2.1-en.txt
 *
 */
package org.scilab.modules.xcos.block.actions;

import java.awt.event.ActionEvent;

import org.scilab.modules.graph.ScilabComponent;
import org.scilab.modules.graph.ScilabGraph;
import org.scilab.modules.graph.actions.base.VertexSelectionDependantAction;
import org.scilab.modules.gui.menuitem.MenuItem;
import org.scilab.modules.xcos.graph.XcosDiagram;
import org.scilab.modules.xcos.utils.BlockAutoPositionUtils;
import org.scilab.modules.xcos.utils.XcosMessages;

/**
 * SplitBlock auto Position.
 */
@SuppressWarnings(value = { "serial" })
public class AutoPositionSplitBlockAction extends VertexSelectionDependantAction {

    /** Name of the action */
    public static final String NAME = XcosMessages.BLOCK_AUTO_POSITION_SPLIT_BLOCK;
    /** Icon name of the action */
    public static final String SMALL_ICON = "";
    /** Mnemonic key of the action */
    public static final int MNEMONIC_KEY = 0;
    /** Accelerator key for the action */
    public static final int ACCELERATOR_KEY = 0;

    /**
     * Default constructor the associated graph
     *
     * @param scilabGraph
     *            the graph to associate
     */
    public AutoPositionSplitBlockAction(ScilabGraph scilabGraph) {
        super(scilabGraph);
    }

    /**
     * @param scilabGraph
     * @return menu item
     */
    public static MenuItem createMenu(ScilabGraph scilabGraph) {
        return createMenu(scilabGraph, AutoPositionSplitBlockAction.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        XcosDiagram graph = (XcosDiagram) getGraph(e);
        if (graph.getSelectionCells().length == 0) {
            return;
        }

        // action disabled when the cell is edited
        final ScilabComponent comp = ((ScilabComponent) graph.getAsComponent());
        if (comp.isEditing()) {
            return;
        }

        Object[] cells = graph.getSelectionCells();

        graph.getModel().beginUpdate();
        try {
            double scale = graph.getView().getScale();
            graph.getView().setScale(1.0);
            BlockAutoPositionUtils.changeSplitBlocksPosition((XcosDiagram) getGraph(null), cells);
            graph.getView().setScale(scale);
        } finally {
            graph.getModel().endUpdate();
        }
    }

}