/*
 *  ============================================================================================
 *  Nested shape class definition: A Rectangle shape that has shapes bouncing within it
 *  YOUR UPI: tlo227
 *  ============================================================================================
 */

import java.awt.Color;
import java.util.*;

import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class NestedShape extends RectangleShape{
	private ArrayList<Shape> nestedShapes=new ArrayList<Shape>();;
	private static ShapeType nextShapeType=ShapeType.RECTANGLE;
	
	public NestedShape() {
		super();
	}
	
	public NestedShape(int x, int y, int w, int h, int mw, int mh, Color bc, Color fc, PathType pt) {
		super(x ,y ,w, h ,mw ,mh, bc, fc, pt);
		ShapeType insert = nextShapeType;
		nextShapeType=nextShapeType.next();
		this.createAddInnerShape(insert);
	}
	public NestedShape(int x, int y, int w, int h, int mw, int mh, Color bc, Color fc, PathType pt,String message) {
		super(x ,y ,w, h ,mw ,mh, bc, fc, pt, message);
		ShapeType insert = nextShapeType;
		nextShapeType=nextShapeType.next();
		this.createAddInnerShape(insert);
	}
	public NestedShape(ArrayList<Shape> shapes, Color fc, Color bc) {
		super(0,0,Shape.DEFAULT_MARGIN_WIDTH, Shape.DEFAULT_MARGIN_HEIGHT, Shape.DEFAULT_MARGIN_WIDTH, Shape.DEFAULT_MARGIN_HEIGHT,fc,bc,PathType.BOUNCE,Shape.DEFAULT_TEXT);
		nestedShapes.addAll(shapes);
	}
	public void createAddInnerShape(ShapeType st) {
        switch (st) {
        case RECTANGLE: {
    		RectangleShape inner = new RectangleShape(0,0,super.width/2,super.height/2,super.width,super.height,super.getBorderColor(),super.getFillColor(),PathType.BOUNCE,super.text);
    		inner.setParent(this);
    		nestedShapes.add(inner);
            break;
		} case XRECTANGLE: {
			XRectangleShape inner = new XRectangleShape(0,0,super.width/2,super.height/2,super.width,super.height,super.getBorderColor(),super.getFillColor(),PathType.BOUNCE,super.text) ;
			inner.setParent(this);
			nestedShapes.add(inner);
		   	break;
	   } case OVAL: {
			OvalShape inner = new OvalShape(0,0,super.width/2,super.height/2,super.width,super.height,super.getBorderColor(),super.getFillColor(),PathType.BOUNCE,super.text);
			inner.setParent(this);
			nestedShapes.add(inner);
			break;
	   } case SQUARE: {
		   	int min = 0;
		   	if(width>height) {
		   		min=height;
		   	}
		   	else {
		   		min=width;
		   	}
			SquareShape inner = new SquareShape(0,0,min/2,super.width,super.height,super.getBorderColor(),super.getFillColor(),PathType.BOUNCE,super.text);
			inner.setParent(this);
			nestedShapes.add(inner);
			break;
		} case NESTED: {
			NestedShape inner = new NestedShape(0,0,super.width/2,super.height/2,super.width,super.height,super.getBorderColor(),super.getFillColor(),PathType.BOUNCE,super.text);
			inner.setParent(this);
			nestedShapes.add(inner);
			break;
		}
	}
	}
	public Shape getShapeAt(int index) {
		return nestedShapes.get(index);
	}
	public int getSize() {
		return nestedShapes.size();
	}
	public void draw(Painter painter) {
		painter.setPaint(Color.black);
		painter.drawRect(super.x, super.y, super.width, super.height);
		painter.translate(super.x, super.y);
		for (Shape s : nestedShapes){
		    s.draw(painter);
		}
		painter.translate(-super.x,-super.y);
	}
	public void move() {
		super.move();
		for(Shape s :nestedShapes) {
			s.move();
		}
	}
	public void add(Shape s) {
		s.setParent(this);
		nestedShapes.add(s);
	}
	public void remove(Shape s) {
		s.setParent(null);
		nestedShapes.remove(s);
	}
	public int indexOf(Shape s) {
		return nestedShapes.indexOf(s);
	}
	public Shape[] getChildren() {
		Shape[] array = new Shape[nestedShapes.size()];
		array = nestedShapes.toArray(array);
		return array;
	}
}
class TableModelAdapter extends AbstractTableModel{
	private Shape nestedShape;
	private static String[] columnNames= {"Type","X-pos","Y-pos","Width","Height"};
	public TableModelAdapter(Shape s){
	    nestedShape = s;
	}
	public int getColumnCount() {
		return columnNames.length;
	}
	public int getRowCount() {
		return ((NestedShape)nestedShape).getSize();
	}
	public String getColumnName(int index) {
		return columnNames[index];
	}
	public Object getValueAt(int row, int col) {
		if(col==0) {
			return ((NestedShape)nestedShape).getShapeAt(row).getClass().getName();
		} else if(col==1) {
			return ((NestedShape)nestedShape).getShapeAt(row).getX();
		} else if(col==2) {
			return ((NestedShape)nestedShape).getShapeAt(row).getY();
		} else if (col==3) {
			return ((NestedShape)nestedShape).getShapeAt(row).getWidth();
		} else if (col==4){
		    return ((NestedShape)nestedShape).getShapeAt(row).getHeight();
		} else {
			return null;
		}
	}
	public void setNestedShape(Shape s) {
		nestedShape=s;
	}
}
class TreeModelAdapter implements TreeModel{
	private Shape nestedShape;
	private ArrayList<TreeModelListener> treeModelListeners= new ArrayList<TreeModelListener>();
	
	public TreeModelAdapter(Shape s) {
		nestedShape = s;
	}
	public Shape getRoot() {
		return nestedShape;
	}
	public boolean isLeaf(Object node) {
		if(((Shape)node).getClass()==NestedShape.class) {
			return false;
		} else {
			return true;
		}
	}
	public Shape getChild(Object parent, int index) {
		if (isLeaf(parent)==false) {
			return ((NestedShape)parent).getShapeAt(index);
		} else {
			return null;
		}
	}
	public int getChildCount(Object parent) {
		if(isLeaf(parent)==false) {
			return ((NestedShape)parent).getSize();
		} else {
			return -1;
		}
	}
	public int getIndexOfChild(Object parent, Object child) {
		if(isLeaf(parent)==false) {
			return ((NestedShape)parent).indexOf((Shape)child);
		} else {
			return -1;
		}
	}
	public void addTreeModelListener(final TreeModelListener modelListener) {
		treeModelListeners.add(modelListener);
	}
	public void removeTreeModelListener(final TreeModelListener modelListener) {
		treeModelListeners.remove(modelListener);
	}
	public void fireTreeNodesInserted(Object source, Object[] path,int[] childIndices,Object[] children) {
		final TreeModelEvent event = new TreeModelEvent(source,path,childIndices,children);
		for (TreeModelListener t : treeModelListeners) {
			t.treeNodesInserted(event);
		}
	}
	public void fireTreeNodesRemoved(Object source, Object[] path,int[] childIndices,Object[] children) {
		final TreeModelEvent event = new TreeModelEvent(source,path,childIndices,children);
		for(TreeModelListener t : treeModelListeners) {
			t.treeNodesRemoved(event);
		}
	}
	public void addToRoot(Shape s) {
		NestedShape parent = ((NestedShape) nestedShape);
		int[] num = {parent.getSize()};
		parent.add(s);
		Object[] root= {nestedShape};
		Object[] child= {s};
		fireTreeNodesInserted(this,root,num,child);
	}
	public boolean addNode(TreePath selectedPath, ShapeType currentShapeType) {
		Shape selected = (Shape) selectedPath.getLastPathComponent();
		if (!(selected instanceof NestedShape)){
			return false;
		}
		NestedShape select = (NestedShape)selected;
		int[] index = {select.getSize()};
		select.createAddInnerShape(currentShapeType);
		Object[] shape = {select.getShapeAt(index[0])};
		fireTreeNodesInserted(this,selectedPath.getPath(),index,shape);
		return true;
	}
	public boolean removeNodeFromParent(TreePath selectedPath) {
		if(selectedPath.getLastPathComponent() == nestedShape) {
			return false;
		}
		Object[] parent = selectedPath.getParentPath().getPath();
		NestedShape p = (NestedShape)selectedPath.getParentPath().getLastPathComponent();
		Object[] s =  {selectedPath.getLastPathComponent()};
		int[] index = {p.indexOf((Shape)s[0])};
		p.remove((Shape)s[0]);
		fireTreeNodesRemoved(this,parent,index,s);
		return true;
	}
	public void valueForPathChanged(TreePath path, Object newValue) {}
    public void fireTreeNodesChanged(TreeModelEvent e) {}
}



