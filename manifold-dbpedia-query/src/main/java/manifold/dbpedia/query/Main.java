package manifold.dbpedia.query;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.algebra.QueryModelNode;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.Var;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.QueryParserUtil;

public class Main
{
  public static void main( String[] args )
  {
    ParsedQuery query = QueryParserUtil.parseQuery( QueryLanguage.SPARQL,
      "PREFIX dbo: <http://www.dbpedia.org/ontology/>\n" +
      "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
      "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
      "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
      "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
      "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
      "PREFIX : <http://dbpedia.org/resource/>\n" +
      "PREFIX dbpedia2: <http://dbpedia.org/property/>\n" +
      "PREFIX dbpedia: <http://dbpedia.org/>\n" +
      "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
      "\n" +
      "SELECT ?name ?birth ?description ?person WHERE {\n" +
      "      ?person a dbo:MusicalArtist.\n" +
      "      ?person dbo:birthPlace :Berlin.\n" +
      "      ?person dbo:birthDate ?birth.\n" +
      "      ?person foaf:name ?name.\n" +
      "      ?person rdfs:comment ?description.\n" +
      "      FILTER (LANG(?description) = 'en') . \n" +
      "} ORDER BY ?name",
      null );
    try
    {
      Map<String, String> nameToType = makeNameBindings( query.getTupleExpr().getAssuredBindingNames() );
      query.getTupleExpr().visit( new MyVisitor( nameToType ) );
      System.out.println( nameToType );
    }
    catch( Exception e )
    {
      throw new RuntimeException( e );
    }
  }

  private static Map<String, String> makeNameBindings( Set<String> assuredBindingNames )
  {
    Map<String, String> nameToType = new LinkedHashMap<>();
    for( String name: assuredBindingNames )
    {
      nameToType.put( name, null );
    }
    return nameToType;
  }

  private static class MyVisitor extends AbstractQueryModelVisitor<Exception>
  {
    private Map<String, String> _nameToType;

    public MyVisitor( Map<String, String> nameToType )
    {
      _nameToType = nameToType;
    }

    @Override
    public void meet( Var node ) throws Exception
    {
      System.out.println( node );
      String name = node.getName();
      if( _nameToType.get( name ) == null &&
          _nameToType.containsKey( name ) )
      {
        QueryModelNode parent = node.getParentNode();
        if( parent instanceof StatementPattern )
        {
          StatementPattern tuple = (StatementPattern)parent;
          if( !maybeAssignFromObject( node, name, tuple ) )
          {
            maybeAssignFromSubject( node, name, tuple );
          }
        }
      }
      super.meet( node );
    }

    private boolean maybeAssignFromObject( Var node, String name, StatementPattern tuple )
    {
      if( tuple.getObjectVar().equals( node ) &&
          tuple.getPredicateVar().hasValue() )
      {
        Value type = tuple.getPredicateVar().getValue();
        _nameToType.put( name, type.stringValue() );
        return true;
      }
      return false;
    }

    private void maybeAssignFromSubject( Var node, String name, StatementPattern tuple )
    {
      if( tuple.getSubjectVar().equals( node ) &&
          tuple.getPredicateVar().hasValue() && tuple.getPredicateVar().getValue().stringValue().equals( "http://www.w3.org/1999/02/22-rdf-syntax-ns#type" ) &&
          tuple.getObjectVar().hasValue() )
      {
        Value type = tuple.getObjectVar().getValue();
        _nameToType.put( name, type.stringValue() );
      }
    }
  }
}
