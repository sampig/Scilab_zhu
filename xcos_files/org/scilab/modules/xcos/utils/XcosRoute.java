/*
 * Scilab ( http://www.scilab.org/ ) - This file is part of Scilab
 * Copyright (C) 2015 - Chenfeng ZHU
 *
 * This file must be used under the terms of the CeCILL.
 * This source file is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.  The terms
 * are also available at
 * http://www.cecill.info/licences/Licence_CeCILL_V2.1-en.txt
 *
 */

package org.scilab.modules.xcos.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.scilab.modules.xcos.block.SplitBlock;
import org.scilab.modules.xcos.graph.XcosDiagram;
import org.scilab.modules.xcos.link.BasicLink;
import org.scilab.modules.xcos.port.BasicPort;
import org.scilab.modules.xcos.port.Orientation;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;

public class XcosRoute {

    private List<mxPoint> listRoute = new ArrayList<mxPoint>(0);

    /**
     * Update the Edge.
     * 
     * @param link
     * @param graph
     */
    public void updateRoute(BasicLink link, Object[] allCells, XcosDiagram graph) {
        mxICell sourceCell = link.getSource();
        mxICell targetCell = link.getTarget();
        Object[] allOtherCells = getAllOtherCells(allCells, link, sourceCell, targetCell);
        if (sourceCell != null && targetCell != null) {
            boolean isGetRoute = this.computeRoute(link, allOtherCells, graph);
            if (isGetRoute) {
                List<mxPoint> list = new ArrayList<mxPoint>(0);
                list.addAll(listRoute);
                mxGeometry geometry = new mxGeometry();
                geometry.setPoints(list);
                ((mxGraphModel) (graph.getModel())).setGeometry(link, geometry);
                listRoute.clear();
            } else {
                // if it cannot get the route, keep the same or change it to
                // straight or give a pop windows to inform user.
            }
        }
    }

    /**
     * Get the turning points for the optimal route. If the straight route is the optimal route,
     * return null.
     * 
     * @param link
     * @param allCells
     * @return list of turning points
     */
    private boolean computeRoute(BasicLink link, Object[] allCells, XcosDiagram graph) {
        listRoute.clear();
        mxICell sourceCell = link.getSource();
        mxICell targetCell = link.getTarget();
        // if the link is not connected with BasicPort.
        if (!(sourceCell instanceof BasicPort) || !(targetCell instanceof BasicPort)) {
            return false;
        }
        Orientation sourcePortOrien = null;
        Orientation targetPortOrien = null;
        double srcx = 0;
        double srcy = 0;
        double tgtx = 0;
        double tgty = 0;
        mxPoint sourcePoint = new mxPoint(srcx, srcy);
        mxPoint targetPoint = new mxPoint(tgtx, tgty);
        // if source is a port, get a new start point.
        if (sourceCell instanceof BasicPort) {
            mxCellState state = graph.getView().getState(sourceCell);
            if (state != null) {
                srcx = state.getCenterX();
                srcy = state.getCenterY();
                BasicPort sourcePort = (BasicPort) sourceCell;
                sourcePoint = this.getPointAwayPort(sourcePort, allCells, graph);
                sourcePortOrien = getPortRelativeOrientation(sourcePort, graph);
            }
        }
        // if source is a SplitBlock
        if (sourceCell.getParent() instanceof SplitBlock) {
            srcx = sourceCell.getParent().getGeometry().getCenterX();
            srcy = sourceCell.getParent().getGeometry().getCenterY();
            sourcePoint.setX(srcx);
            sourcePoint.setY(srcy);
        }
        // if target is a port, get a new end point.
        if (targetCell instanceof BasicPort) {
            mxCellState state = graph.getView().getState(targetCell);
            if (state != null) {
                tgtx = state.getCenterX();
                tgty = state.getCenterY();
                BasicPort targetPort = (BasicPort) targetCell;
                targetPoint = this.getPointAwayPort(targetPort, allCells, graph);
                targetPortOrien = getPortRelativeOrientation(targetPort, graph);
            }
        }
        // if target is a SplitBlock
        if (targetCell.getParent() instanceof SplitBlock) {
            tgtx = targetCell.getParent().getGeometry().getCenterX();
            tgty = targetCell.getParent().getGeometry().getCenterY();
            targetPoint.setX(tgtx);
            targetPoint.setY(tgty);
        }
        // if two ports are aligned and there are no blocks between them,
        // use straight route.
        if ((XcosRouteUtils.isStrictlyAligned(srcx, srcy, tgtx, tgty))
                && !XcosRouteUtils.checkObstacle(srcx, srcy, tgtx, tgty, allCells)) {
            return true;
        }
        List<mxPoint> list = XcosRouteUtils.getSimpleRoute(sourcePoint, sourcePortOrien, targetPoint,
                targetPortOrien, allCells);
        if (list != null && list.size() > 0) {
            listRoute.addAll(list);
            return true;
        } else {
            list = XcosRouteUtils.getComplexRoute(sourcePoint, sourcePortOrien, targetPoint, targetPortOrien,
                    allCells, XcosRouteUtils.TRY_TIMES);
            if (list != null && list.size() > 0) {
                listRoute.addAll(list);
                return true;
            }
        }
        // listRoute.add(sourcePoint);
        // listRoute.add(targetPoint);
        return false;
    }

    /**
     * According to the relative position (orientation) of the port, get a point which is
     * XcosRoute.BEAUTY_DISTANCE away from the port and out of block.
     * 
     * @param port
     * @param graph
     * @return
     */
    private mxPoint getPointAwayPort(BasicPort port, Object[] allCells, XcosDiagram graph) {
        double portX = graph.getView().getState(port).getCenterX();
        double portY = graph.getView().getState(port).getCenterY();
        mxPoint point = new mxPoint(portX, portY);
        double distance = XcosRouteUtils.BEAUTY_AWAY_DISTANCE;
        switch (getPortRelativeOrientation(port, graph)) {
        // switch (port.getOrientation()) {
        case EAST:
            point.setX(point.getX() + distance);
            while (Math.abs(point.getX() - portX) > XcosRouteUtils.BEAUTY_AWAY_REVISION
                    && XcosRouteUtils.checkObstacle(portX, portY, point.getX(), point.getY(), allCells)) {
                point.setX(point.getX() - XcosRouteUtils.BEAUTY_AWAY_REVISION);
            }
            break;
        case SOUTH:
            point.setY(point.getY() + distance);
            while (Math.abs(point.getY() - portY) > XcosRouteUtils.BEAUTY_AWAY_REVISION
                    && XcosRouteUtils.checkObstacle(portX, portY, point.getX(), point.getY(), allCells)) {
                point.setY(point.getY() - XcosRouteUtils.BEAUTY_AWAY_REVISION);
            }
            break;
        case WEST:
            point.setX(point.getX() - distance);
            while (Math.abs(point.getX() - portX) > XcosRouteUtils.BEAUTY_AWAY_REVISION
                    && XcosRouteUtils.checkObstacle(portX, portY, point.getX(), point.getY(), allCells)) {
                point.setX(point.getX() + XcosRouteUtils.BEAUTY_AWAY_REVISION);
            }
            break;
        case NORTH:
            point.setY(point.getY() - distance);
            while (Math.abs(point.getY() - portY) > XcosRouteUtils.BEAUTY_AWAY_REVISION
                    && XcosRouteUtils.checkObstacle(portX, portY, point.getX(), point.getY(), allCells)) {
                point.setY(point.getY() + XcosRouteUtils.BEAUTY_AWAY_REVISION);
            }
            break;
        }
        return point;
    }

    /**
     * As BasicPort.getOrientation is the default orientation, the Orientation is not correct when
     * the block is mirrored or flipped. This method could get the current Orientation of the port.
     * 
     * @param port
     * @return
     */
    private Orientation getPortRelativeOrientation(BasicPort port, XcosDiagram graph) {
        // the coordinate (x,y) for the port.
        double portx = graph.getView().getState(port).getCenterX();
        double porty = graph.getView().getState(port).getCenterY();
        // the coordinate (x,y) and the width-height for the parent block
        mxICell parent = port.getParent();
        double blockx = graph.getView().getState(parent).getCenterX();
        double blocky = graph.getView().getState(parent).getCenterY();
        double blockw = parent.getGeometry().getWidth();
        double blockh = parent.getGeometry().getHeight();
        // calculate relative coordinate based on the center of parent block.
        portx -= blockx;
        porty -= blocky;
        Orientation orientation = port.getOrientation();
        if ((portx) >= blockw * Math.abs(porty) / blockh) { // x>=w*|y|/h
            orientation = Orientation.EAST;
        } else if (porty >= blockh * Math.abs(portx) / blockw) { // y>=h*|x|/w
            orientation = Orientation.SOUTH;
        } else if (portx <= -blockw * Math.abs(porty) / blockh) { // x<=-w*|y|/h
            orientation = Orientation.WEST;
        } else if (porty <= -blockh * Math.abs(portx) / blockw) { // y<=-h*|x|/w
            orientation = Orientation.NORTH;
        }
        return orientation;
    }

    /**
     * Remove the selves from the array of all. Remove all SplitBlock. Add the Ports.
     * 
     * @param all
     * @param self
     * @return a new array of all objects excluding selves
     */
    private Object[] getAllOtherCells(Object[] all, Object... self) {
        List<Object> listme = Arrays.asList(self);
        List<Object> listnew = new ArrayList<Object>(0);
        for (Object o : all) {
            // if it belongs to self or it is SplitBlock.
            if (!listme.contains(o) && !(o instanceof SplitBlock)) {
                listnew.add(o);
                // if it is a Link, add its Ports.
                if (o instanceof BasicLink) {
                    BasicLink link = (BasicLink) o;
                    if (!listnew.contains(link.getSource())
                            && !(link.getSource().getParent() instanceof SplitBlock)) {
                        listnew.add(link.getSource());
                    }
                    if (!listnew.contains(link.getTarget())
                            && !(link.getTarget().getParent() instanceof SplitBlock)) {
                        listnew.add(link.getTarget());
                    }
                }
            }
        }
        Object[] newAll = listnew.toArray();
        return newAll;
    }

}
