package manifold.dbpedia;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Bindings;
import manifold.json.extensions.java.net.URL.ManUrlExt;
import manifold.util.JsonUtil;

public class ClassNamesCache
{
  private static ClassNamesCache INSTANCE = null;

  // Sparql query:
  private static final String CLASSES_QUERY = "SELECT * { ?x a owl:Class }";
  private static final String CLASSES_URL =
    "https://dbpedia.org/sparql" +
    "?default-graph-uri=http%3A%2F%2Fdbpedia.org" +
    "&query=" + ManUrlExt.encode( CLASSES_QUERY ) +
    "&output=json";

  private static int initializing = 0;

  public static ClassNamesCache instance()
  {
    if( INSTANCE == null )
    {
      INSTANCE = new ClassNamesCache( initializing++ );
    }
    return INSTANCE;
  }

  private Map<String, String> _fqnToUrl;

  private ClassNamesCache( int initializing )
  {
    if( initializing > 0 )
    {
      _fqnToUrl = Collections.emptyMap();
      return;
    }

    try
    {
      URL classesQuery = new URL( CLASSES_URL );
      Bindings classes = ManUrlExt.getJsonContent( classesQuery );
      buildCache( classes );
    }
    catch( Throwable e )
    {
      throw new RuntimeException( e );
    }
  }

  public String getUrl( String fqn )
  {
    return _fqnToUrl.get( fqn );
  }

  public Collection<String> getAllTypeNames()
  {
    return _fqnToUrl.keySet();
  }

  private void buildCache( Bindings classes )
  {
    _fqnToUrl = new HashMap<>();

    // DBpedia's root class
    _fqnToUrl.put( DbpediaTypeManifold.CLASSES_PKG + ".Thing", "http://dbpedia.org/ontology/Thing" );

    for( Object obindings : (List)((Bindings)classes.get( "results" )).get( "bindings" ) )
    {
      Bindings bindings = (Bindings)obindings;
      String url = (String)((Bindings)bindings.get( "x" )).get( "value" );
      int iSlash = url.lastIndexOf( '/' );
      String simpleName = url.substring( iSlash + 1 );
      simpleName = JsonUtil.makeIdentifier( simpleName );
      String fqn = DbpediaTypeManifold.CLASSES_PKG + '.' + simpleName;
      _fqnToUrl.put( fqn, url );
    }
  }
}
