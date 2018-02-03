package manifold.dbpedia;

import java.net.URL;
import java.util.List;
import javax.script.Bindings;
import manifold.json.extensions.java.net.URL.ManUrlExt;
import manifold.util.JsonUtil;

public class SuperclassFinder
{
  private static final String QUERY =
    "SELECT * WHERE { <http://dbpedia.org/ontology/${0}> rdfs:subClassOf ?x . }";

  public static String findSuperClass( String classUrl )
  {
    try
    {
      int iSlash = classUrl.lastIndexOf( '/' );
      if( iSlash < 0 )
      {
        throw new IllegalArgumentException( "'" + classUrl + "' is not a valid dbpedia class" );
      }

      String className = classUrl.substring( iSlash + 1 );
      String query = QUERY.replace( "${0}", className );
      // SELECT * WHERE { <class-url> rdfs:subClassOf ?x . }
      String superclassQuery =
        "http://dbpedia.org/sparql?default-graph-uri=http://dbpedia.org" +
        "&query=" + ManUrlExt.encode( query ) +
        "&output=json";

      URL classesQuery = new URL( superclassQuery );
      Bindings queryResults = ManUrlExt.getJsonContent( classesQuery );
      //noinspection unchecked
      List<Bindings> bindings = (List<Bindings>)((Bindings)queryResults.get( "results" )).get( "bindings" );
      return findSuperclassName( bindings );
    }
    catch( Exception e )
    {
      throw new RuntimeException( e );
    }
  }

  private static String findSuperclassName( List<Bindings> bindings )
  {
    if( bindings == null || bindings.isEmpty() )
    {
      return null;
    }
    String url = (String)((Bindings)bindings.get( 0 ).get( "x" )).get( "value" );
    if( url.contains( "/dbpedia.org/ontology/" ) )
    {
      // only accept dbpedia classes
      int iSlash = url.lastIndexOf( '/' );
      String simpleName = url.substring( iSlash + 1 );
      simpleName = JsonUtil.makeIdentifier( simpleName );
      return simpleName;
    }
    return null;
  }
}
