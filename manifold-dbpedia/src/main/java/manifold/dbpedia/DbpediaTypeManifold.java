package manifold.dbpedia;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import manifold.api.type.TypeName;
import manifold.api.type.UrlTypeManifold;

public class DbpediaTypeManifold extends UrlTypeManifold
{
  public static final String ORG_PKG = "org";
  public static final String BASE_PKG = "org.dbpedia";
  public static final String CLASSES_PKG = BASE_PKG + '.' + "ontology";

  @Override
  public boolean isType( String fqn )
  {
    return ClassNamesCache.instance().getUrl( fqn ) != null;
  }

  @Override
  public boolean isPackage( String pkg )
  {
    switch( pkg )
    {
      case ORG_PKG:
      case BASE_PKG:
      case CLASSES_PKG:
        return true;
    }
    return false;
  }

  @Override
  public String getPackage( String fqn )
  {
    if( isType( fqn ) )
    {
      return CLASSES_PKG;
    }
    return null;
  }

  @Override
  public String produce( String fqn, String existing, DiagnosticListener<JavaFileObject> errorHandler )
  {
    return DbpediaClass.make( fqn );
  }

  @Override
  public Collection<String> getAllTypeNames()
  {
    return ClassNamesCache.instance().getAllTypeNames();
  }

  @Override
  public Collection<TypeName> getTypeNames( String pkg )
  {
    ClassNamesCache.instance();

    if( !pkg.equals( CLASSES_PKG ) )
    {
      return Collections.emptySet();
    }

    return getAllTypeNames().stream().map( e ->
      new TypeName( e, getTypeLoader(), TypeName.Kind.TYPE, TypeName.Visibility.PUBLIC ) )
      .collect( Collectors.toSet() );
  }
}