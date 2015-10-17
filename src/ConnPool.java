
  import java.lang.* ;
  import java.sql.* ;
  import java.util.* ;

  public class ConnPool {
    private static final int defaultMaxConnections=2;  

    private Vector  freeConnections ;
    private Hashtable  boundConnections ;
    private String  driverName ;
    private String  jdbcURL ;
    private String  username ;
    private String  password ;
    private int maxConnections ;
    
// ------------------------------------ Constructot -------------------------------------
   //設定連線數目
    public ConnPool( int numConnections ) {
      maxConnections=numConnections ;
      boundConnections=null ;
      freeConnections=null ;
      driverName="" ;
      jdbcURL="" ;
      username="" ;
      password="" ;
    }

// --------------------------------------------------------------------------------------
   
    public ConnPool() {
      this( defaultMaxConnections ) ;
    }


    public void closeDB() throws SQLException {
      if( boundConnections!=null ) {
        for( Enumeration e=boundConnections.elements() ; e.hasMoreElements() ; ) {
      	  Connection conn=(Connection)e.nextElement() ;
      	  conn.close() ;
        }
        boundConnections.clear() ;
        boundConnections=null ;
      }
      
      if( freeConnections!=null ) {
        for( Enumeration e=freeConnections.elements() ; e.hasMoreElements() ; ) {
      	  Connection conn=(Connection)e.nextElement() ;
      	  conn.close() ;
        }
        freeConnections.removeAllElements() ;
        freeConnections=null ;
      }
    }


    public synchronized Connection getConnection()
    throws SQLException {
      if( freeConnections==null )
        throw new SQLException( "The conection pool has not been established yet." ) ;
      if( boundConnections.get( Thread.currentThread() )!=null )
        throw new SQLException( "Cannot get connections over once for this current running thread." ) ;
      try {
        if( freeConnections.size()==0 ){
        System.out.println(Thread.currentThread().getName()+" is waiting..............................................");        
          wait() ;
        }  
      }
      catch( InterruptedException ex ) {
      	throw new SQLException( ex.toString() ) ;
      }
      Connection conn=(Connection)freeConnections.firstElement() ;
      freeConnections.removeElement( conn ) ;
      boundConnections.put( Thread.currentThread(), conn ) ;

      return conn ;
    }
    

    public void openDB( String drvName, String url,
			String uname, String passwd )
    throws SQLException {
      try {
        boundConnections=new Hashtable( maxConnections ) ;
        freeConnections=new Vector( maxConnections ) ;
        Class.forName( drvName ) ;
        for( int i=0 ; i<maxConnections ; i++ )
          freeConnections.addElement( DriverManager.getConnection( url ,uname ,passwd ) ) ;
      }
      catch( Exception ex ) {
      	boundConnections=null ;
      	freeConnections=null ;
       	throw new SQLException( ex.toString() ) ;
      }
    }


    public synchronized void returnConnection()
    throws SQLException {
      Connection conn=(Connection)boundConnections.remove( Thread.currentThread() ) ;
      if( conn==null )
        throw new SQLException( "The connection which this current running thread got is not found." ) ;
      freeConnections.addElement( conn ) ;
      notify() ;
    }
    

    public void setConnectionSwitch( String on_off ) throws SQLException {
      	if( on_off.equalsIgnoreCase( "ON" ) )
          openDB( driverName, jdbcURL, username, password ) ;
        else if( on_off.equalsIgnoreCase( "OFF" ) )
          closeDB() ;
    }


    public void setMaxConnections( int numConnections ) {
      maxConnections=numConnections ;
    }
    

    public void setDriverName( String drvName ) {
      driverName=drvName ;
    }


    public void setJdbcURL( String url ) {
      jdbcURL=url ;
    }


    public void setUserName( String uname ) {
      username=uname ;
    }


    public void setPassword( String passwd ) {
      password=passwd ;
    }
  }