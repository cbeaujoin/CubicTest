/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.policies.StartPointNodeEditPolicy;
import org.cubictest.ui.gef.view.AbstractTransitionNodeFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Color;


/**
 * Contoller for the <code>SubTestStartPoint</code> model.
 * 
 * @author Christian Schwarz
 */
public class SubTestStartPointEditPart extends AbstractNodeEditPart {

	/**
	 * Constructor for <code>SubTestStartPoint</code>.
	 * @param point the model
	 */
	public SubTestStartPointEditPart(ConnectionPoint point) {
		setModel(point);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		
		AbstractTransitionNodeFigure startPointFigure = new AbstractTransitionNodeFigure();
		startPointFigure.setBackgroundColor(new Color(null, 255, 230, 164));
		startPointFigure.setLabelLength(200);
		Point p = ((TransitionNode)getModel()).getPosition();
		startPointFigure.setLocation(p);
		startPointFigure.setText(getName());
		startPointFigure.setToolTipText("This start point can only be reached when test is used as sub test in another test");
		return startPointFigure;
	}

	private String getName(){
		String name = ((ConnectionPoint)getModel()).getName();
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new StartPointNodeEditPolicy());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		ConnectionPoint connectionPoint = (ConnectionPoint)getModel();
		AbstractTransitionNodeFigure figure = (AbstractTransitionNodeFigure) getFigure();
		figure.setText(getName());
		Point position = connectionPoint.getPosition();
		Rectangle r = new Rectangle(position.x, position.y, -1, -1);
		((TestEditPart)getParent()).setLayoutConstraint(this, figure, r);
	}

	
	@Override
	public boolean isCopyable() {
		return false;
	}
	
	@Override
	public boolean isCuttable() {
		return false;
	}
	
}