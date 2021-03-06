package manifold.dbpedia;

import java.lang.reflect.Modifier;
import java.util.Map;
import manifold.api.gen.SrcClass;
import manifold.api.gen.SrcGetProperty;

public class DbpediaClass
{
  public static String make( String fqn )
  {
    String url = ClassNamesCache.instance().getUrl( fqn );
    Map<String, String> nameToType = PropertiesFinder.findProperties( url );
    String superClass = SuperclassFinder.findSuperClass( url );
    return buildSrcClass( fqn, nameToType, superClass );
  }

  private static String buildSrcClass( String fqn, Map<String, String> nameToType, String superClass )
  {
    SrcClass srcClass = new SrcClass( fqn, SrcClass.Kind.Interface )
      .modifiers( Modifier.PUBLIC );
    if( superClass != null )
    {
      srcClass.superClass( superClass );
    }
    for( Map.Entry<String, String> entry: nameToType.entrySet() )
    {
      String name = entry.getKey();
      if( name == null )
      {
        continue;
      }

      String typeName = entry.getValue();
      if( typeName == null )
      {
        //## todo: skipping some properties (see todos elsewhere), eventually none will be skipped
        continue;
      }

      if( typeName.indexOf( '#' ) >= 0 || typeName.indexOf( '/' ) >= 0 )
      {
        Class javaType = DataTypeToJavaType.instance().getJavaType( typeName );
        if( javaType == null )
        {
          // skip properties with unsupported types
          continue;
        }
        typeName = javaType.getTypeName();
      }

      name = Character.toUpperCase( name.charAt( 0 ) ) + (name.length() > 1 ? name.substring( 1 ) : "");
      srcClass.addGetProperty( new SrcGetProperty( name, typeName ).modifiers( Modifier.PUBLIC | Modifier.ABSTRACT ) );
    }
    return srcClass.render( new StringBuilder(), 0 ).toString();
  }
}
