import java.awt.*;
import javax.swing.*;

public class Client
  extends JApplet
  implements Runnable
{
  private static int WIDTH = 800;
  private static int HEIGHT = 800;
  private static int TICK = 200;

  private static class Panel
    extends JPanel
  {
    private Client client;

    public Panel( Client client )
    {
      this.client = client;
    }

    @Override
    public void paint( Graphics g )
    {
      for( int i = 0; i < 8; ++i ) {
        for( int j = 0; j < 8; ++j ) {
          g.setColor( client.tiles[i][j] ? Color.WHITE : Color.BLACK );
          g.fillRect( j * 100, i * 100, 100, 100 );
        }
      }
    }

    @Override
    public void update( Graphics g )
    {
      paint( g );
    }
  }

  private Panel panel = new Panel( this );
  private volatile boolean isAlive = true;

  private long number = 0;
  private boolean[][] tiles = new boolean[][] {
    new boolean[8], new boolean[8], new boolean[8], new boolean[8],
    new boolean[8], new boolean[8], new boolean[8], new boolean[8]
  };

  private void step()
  {
    ++number;

    for( int i = 0; i < 8; ++i ) {
      for( int j = 0; j < 8; ++j ) {
        long shift = i * 8 + j;

        tiles[i][j] = ( ( 1l << shift ) & number ) != 0;
      }
    }
  }

  private static void sleep( long millis )
  {
    try {
      Thread.sleep( millis );
    }
    catch( InterruptedException e )
    {}
  }

  @Override
  public void run()
  {
    long timeNext = System.currentTimeMillis() + TICK;
    long timeLeft;

    while( isAlive ) {
      step();

      timeLeft = timeNext - System.currentTimeMillis();
      if( timeLeft > 0 ) {
        panel.repaint();
        repaint();

        timeLeft = timeNext - System.currentTimeMillis();
        if( timeLeft > 0 ) {
          sleep( TICK );
        }
      }
      else if( timeLeft < -TICK ) {
        timeNext -= timeLeft;
      }
      timeNext += TICK;
    }
  }

  @Override
  public void paint( Graphics g )
  {
    panel.paint( g );
  }

  @Override
  public void update( Graphics g )
  {
    panel.paint( g );
  }

  @Override
  public void init()
  {
    Client client = new Client();
    add( client );
    client.setSize( getWidth(), getHeight() );

    Thread thread = new Thread( client );
    thread.start();
  }

  public static void main( String[] args )
  {
    Client client = new Client();
    JFrame frame = new JFrame( "Ura" );

    frame.setResizable( false );
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.add( client.panel );
    client.panel.setPreferredSize( new Dimension( WIDTH, HEIGHT ) );
    frame.pack();
    frame.setVisible( true );

    if( args.length != 0 ) {
      try {
        client.number = Long.parseLong( args[0] );
      }
      catch( Exception e )
      {}
    }

    Thread thread = new Thread( client );
    thread.start();
  }
}
