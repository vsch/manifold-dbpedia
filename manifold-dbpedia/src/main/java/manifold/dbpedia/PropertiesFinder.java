package manifold.dbpedia;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Bindings;
import manifold.json.extensions.java.net.URL.ManUrlExt;

public class PropertiesFinder
{
  private static final String QUERY =
    "select distinct ?property ?range " +
      "where { ?property <http://www.w3.org/2000/01/rdf-schema#domain> " +
       "<http://dbpedia.org/ontology/${0}> . ?property rdfs:range ?range . }";

  public static Map<String, String> findProperties( String classUrl )
  {
    try
    {
      String className = deriveContentName( classUrl );

      String query = QUERY.replace( "${0}", className );

      String superclassQuery =
        "https://dbpedia.org/sparql" +
        "?default-graph-uri=http%3A%2F%2Fdbpedia.org" +
        "&query=" + ManUrlExt.encode( query ) +
        "&output=json";

      URL classesQuery = new URL( superclassQuery );
      Bindings queryResults =  ManUrlExt.getJsonContent( classesQuery );
      //noinspection unchecked
      List<Bindings> bindings = (List<Bindings>)((Bindings)queryResults.get( "results" )).get( "bindings" );
      return makeProperties( bindings );
    }
    catch( Exception e )
    {
      throw new RuntimeException( e );
    }
  }

  private static Map<String, String> makeProperties( List<Bindings> results )
  {
    if( results == null || results.isEmpty() )
    {
      return Collections.emptyMap();
    }

    HashMap<String, String> nameToType = new HashMap<>( results.size() );
    for( Bindings bindings: results )
    {
      String url = (String)((Bindings)bindings.get( "property" )).get( "value" );
      String propertyName = deriveContentName( url );

      String range = (String)((Bindings)bindings.get( "range" )).get( "value" );
      String typeName = deriveTypeName( range );
      nameToType.put( propertyName, typeName );
    }

    return nameToType;
  }

  private static String deriveTypeName( String url )
  {
    String dataTypeName = getDbpediaDataTypeName( url );
    if( dataTypeName != null )
    {
      // dbpedia.org/datatype/Xxx

      return dataTypeName;
    }

    String name = deriveContentName( url );
    if( name != null )
    {
      int iQual = name.indexOf( '#' );
      if( iQual < 0 )
      {
        // class name
        return name;
      }
      String dataType = getXmlSchemaDataTypeName( name, iQual );
      if( dataType != null )
      {
        return dataType;
      }
    }

    return handleMisc( url );
  }

  private static String handleMisc( String url )
  {
    if( url.toLowerCase().endsWith( "rdf-syntax-ns#langString" ) )
    {
      //## todo: make a special class for langString where the string and the attribute are paired
      return "XMLSchema#string";
    }
    return null;
  }

  private static String getDbpediaDataTypeName( String url )
  {
    int iDataType = url.toLowerCase().indexOf( "/datatype/" );
    return iDataType >= 0 ? url.substring( iDataType + 1 ) : null;
  }

  private static String getXmlSchemaDataTypeName( String name, int iQual )
  {
    String qualifier = name.substring( 0, iQual );
    if( qualifier.equalsIgnoreCase( "XMLSchema" ) )
    {
      return name;
    }

    // only consider XMLSchema data types for now
    return null;
  }

  private static String deriveContentName( String url )
  {
    int iSlash = url.lastIndexOf( '/' );
    if( iSlash < 0 )
    {
      throw new IllegalArgumentException( "'" + url + "' is not a valid dbpedia ontology name" );
    }

    String path = url.substring( 0, iSlash );
    String name = url.substring( iSlash + 1 );

    if( path.toLowerCase().endsWith( "ontology" ) )
    {
      return name;
    }

    if( name.toLowerCase().startsWith( "xmlschema#" ) )
    {
      return name;
    }

    //## todo: some ontology names are nested e.g., ontology/PopulatedPlace/areaTotal, where areaTotal is defined in PopulatedPlace
    //## todo: we are skipping these for now

    return null;
  }
}
