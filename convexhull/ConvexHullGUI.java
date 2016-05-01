/**
 * ConvexHullGUI.java
 * 
 * Helen Hu and Greg Gagne
 *
 * September 2015
 * 
 * This is a truly UGLY class cobbled together from other classes to serve
 * as a GUI for the convex hull lab.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class ConvexHullGUI extends Frame implements ActionListener
{
  private static final long serialVersionUID = 1L;
  //These are static variables that modify program behavior.
  private static int WINDOW_SIZE = 1000; //value that determines windowsize
  private static int DOT_THICKNESS = 2; //size of dots
  private static int CANVAS_SIZE = 800;
  
  //GUI Objects
  private DrawingPanel drawerPanel;
  private JScrollPane vertexWindow;
  private JScrollPane drawerWindow;
  private JTextArea vertexWindowText;
  private JPanel buttonPanel;
  private JButton showConvexHull;
  private JButton showClosestPoints;
  private JButton clearPoints;
  private JButton quitButton;
  private boolean showHull;
  private boolean showClosest;
  
  /*
   * Creates an instance of itself.
   */
  public static void main(String[] args)
  {
    ConvexHullGUI gui = new ConvexHullGUI();
    gui.setResizable(true);
    gui.setFocusable(true);
  }
  
  
  /* 
   * Default constructor, builds the window, and sets up the drawing panel.
   */
  public ConvexHullGUI()
  {
    //Create the frame, set the size, location, and make it closeable.
    super();
    this.setLocation(10, 10);
    //drawerFrame
    this.setSize(WINDOW_SIZE, WINDOW_SIZE);
    //drawerFrame
    this.addWindowListener(new WindowAdapter()
                             {
      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
    });
    
    //set up drawing panel
    JLabel sizer = new JLabel();
    sizer.setSize(500,500);
    //sizer.setVisible(false);
    drawerPanel = new DrawingPanel();
    drawerPanel.setLayout(new GridLayout(1,1));
    drawerPanel.add(sizer);
    drawerPanel.addMouseListener(drawerPanel);
    drawerWindow = new JScrollPane(drawerPanel);
    drawerWindow.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    drawerWindow.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    drawerWindow.setBorder(new LineBorder(Color.blue, 2));
    
    //set up vertex showing frame
    vertexWindowText = new JTextArea(10,18);
    vertexWindowText.setEditable(false);
    vertexWindowText.setBorder(new LineBorder(Color.blue, 2));
    vertexWindow = new JScrollPane(vertexWindowText);
    vertexWindow.setViewportView(vertexWindowText);
    vertexWindow.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    vertexWindow.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    
    vertexWindow.add(vertexWindowText);
    this.add(vertexWindow, BorderLayout.EAST);
    this.add(vertexWindowText, BorderLayout.WEST);
    vertexWindow.setVisible(true);
    
    //add four buttons 
    showConvexHull = new JButton("Show Convex Hull");
    showConvexHull.addActionListener(this);
    showConvexHull.setActionCommand("hull");
    showHull = false;
    
    showClosestPoints = new JButton("Show Closest Points");
    showClosestPoints.addActionListener(this);
    showClosestPoints.setActionCommand("closest");
    showClosest = false;
    
    clearPoints = new JButton("Clear Points");
    clearPoints.addActionListener(this);
    clearPoints.setActionCommand("clear");
    
    quitButton = new JButton("Quit");
    quitButton.addActionListener(this);
    quitButton.setActionCommand("quit");
    
    // add buttons to panel
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout());
    buttonPanel.add(showConvexHull);
    buttonPanel.add(showClosestPoints);
    buttonPanel.add(clearPoints);
    buttonPanel.add(quitButton);
    this.add(buttonPanel, BorderLayout.NORTH);
    
    //finish configuring the frame
    this.setTitle("Convex Hull");
    this.add(drawerWindow, BorderLayout.CENTER);
    this.setVisible(true);
    
  }
  
  public void actionPerformed(ActionEvent evt)//event handling method of ActionListener interface
  {
    
    if("hull".equals(evt.getActionCommand())) // show or hide convex hull button
    {
      if (showHull) {
        showConvexHull.setText("Show Convex Hull");//The button will change the text display.
        showHull = false;
        drawerPanel.redrawVertexList = true;
      }
      else
      {
        showConvexHull.setText("Hide Convex Hull");
        showHull = true;
        drawerPanel.redrawVertexList = true;
      }
    }
    else if ("clear".equals(evt.getActionCommand())) // clear button
    {
      drawerPanel.resetList();
    }
    else if ("closest".equals(evt.getActionCommand()))
    {
      if (showClosest) {
        showClosestPoints.setText("Show Closest Points");//The button will change the text display.
        showClosest = false;
        drawerPanel.redrawVertexList = true;
      }
      else
      {
        showClosestPoints.setText("Hide Closest Points");
        showClosest = true;
        drawerPanel.redrawVertexList = true;
      }
    }
    else if ("quit".equals(evt.getActionCommand())) // quit button
    {
      System.exit(0);
    }
    this.repaint();
    this.validate();
  }
  
  /*
   * Performs the guts of the drawing tool. This
   * class contains its own event handler for mouse clicks.
   */
  private class DrawingPanel extends JPanel implements MouseListener
  {
    private static final long serialVersionUID = 1L;
    private ConvexHull points;
    private boolean graphicDrawn;
    private boolean clear;
    private boolean redrawVertexList;
    private JTextArea canvasVerticalSizer;
    private JTextArea canvasHorizontalSizer;
    
    /*
     * Create the panel, set the color, initialize the point list.
     */  
    public DrawingPanel()
    {
      clear = false;
      points = new ConvexHull();
      redrawVertexList = true;
      
      canvasHorizontalSizer = new JTextArea(1, CANVAS_SIZE);
      canvasVerticalSizer = new JTextArea(CANVAS_SIZE, 1);
      canvasHorizontalSizer.setSize(CANVAS_SIZE, 1);
      canvasVerticalSizer.setSize(1, CANVAS_SIZE);
      canvasHorizontalSizer.setBorder(new LineBorder(Color.red, 2));
      canvasVerticalSizer.setBorder(new LineBorder(Color.red, 2));
      canvasHorizontalSizer.setVisible(false);
      canvasVerticalSizer.setVisible(false);
      this.setLayout(new BorderLayout());
      this.add(canvasVerticalSizer, BorderLayout.EAST);
      this.add(canvasHorizontalSizer, BorderLayout.SOUTH);
      this.setSize(CANVAS_SIZE, CANVAS_SIZE);
    }
    
    
    public void mouseReleased(MouseEvent e)
    {
      
      if(graphicDrawn)
        clear = true;
      
      redrawVertexList = true;
      
      //create new click point and add it to the array list
      Point newPoint = new Point(e.getX(),e.getY());
      points.addPoint(newPoint);
      this.repaint();
    }  
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    
    //override panel paint method to draw the polygon
    public void paintComponent(Graphics pic)
    {
      
      pic.setColor(Color.black); //set the draw color
      connectTheDots(pic); //actual repaint method
    }
    
    /*
     * This is the actual paint method. It will draw all the points in the list.  
     */
    private void connectTheDots(Graphics pic)
    {
      Point point = new Point();
      int x,y;
      String vertexList = "Vertices\n", shortVertList;
      
      blankCanvas(pic);//clear it before redrawing
      
      //clear the image, won't get here unless the screen is clicked
      if(graphicDrawn  && clear)
      {
        graphicDrawn = false;
        resetList();
        clear = false;
        
      }    
      
      //iterate through the point list
      for(int i = 0; i < points.getNumber(); i++)
      {
        //current list point values
        point = points.getPoint(i);
        x = point.x;
        y = point.y;
        
        //draw the dot, visibly
        pic.setColor(Color.black);
        pic.fillOval(x,y,DOT_THICKNESS+2,DOT_THICKNESS+2);
        vertexList = vertexList + "  Point " + i + ": (" + point.x + " , " + point.y + ")\n";
        //MNEY - Put location next to the points.
        shortVertList = "(" + point.x + "," + point.y + ")\n";
        pic.drawString(shortVertList, point.x, point.y);
      }
      
      if (showHull)
      {
        pic.setColor(Color.orange);
        Polygon hull = points.getHull();
        pic.drawPolygon(hull);
        vertexList = vertexList + "\n" + points.convexHullToString();    
      }
      
      if (showClosest)
      {
        pic.setColor(Color.red);
        Point[] closest = points.getClosestPoints();
        pic.drawLine(closest[0].x, closest[0].y, 
                     closest[1].x, closest[1].y);
        
      }
      if (redrawVertexList)
      {
        vertexWindowText.setText(vertexList);
        redrawVertexList = false;
      }
    }
    
    /*
     * Clears the point list.
     */
    public void resetList()
    {
      showClosest = false;
      showClosestPoints.setText("Show Closest Points");
      points.clear();
      redrawVertexList = true;
    }
    
    /*
     * Draws a rectangle to clear the screen.
     */
    private void blankCanvas(Graphics pic)
    {
      pic.setColor(Color.white); //draw a white rectangle
      pic.fillRect(0, 0, canvasHorizontalSizer.getWidth(), canvasVerticalSizer.getHeight());
      pic.setColor(Color.black); //set color back 
    }
    
  }
}


