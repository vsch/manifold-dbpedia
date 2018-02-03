package manifold.dbpedia;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public class DataTypeToJavaType
{
  private static DataTypeToJavaType INSTANCE;

  public static DataTypeToJavaType instance()
  {
    if( INSTANCE == null )
    {
      INSTANCE = new DataTypeToJavaType();
    }
    return INSTANCE;
  }

  private Map<String, Class> _map;
  private DataTypeToJavaType()
  {
    _map = new HashMap<>();
    addXmlSchemaTypes();
    //## todo: handle dbpedia.org/datatype/Xxx, probably use the Gosu science units
  }

  public Class getJavaType( String dataTypeName )
  {
    Class type = _map.get( dataTypeName );
    if( type == null )
    {
      if( dataTypeName.toLowerCase().startsWith( "datatype/" ) )
      {
        //## todo: this is temporary until we handle units
        type = String.class;
      }
    }
    return type;
  }

  private void addXmlSchemaTypes()
  {
    _map.put( "XMLSchema#string", String.class );
    _map.put( "XMLSchema#boolean", boolean.class );
    _map.put( "XMLSchema#float", float.class );
    _map.put( "XMLSchema#double", double.class );
    _map.put( "XMLSchema#decimal", BigDecimal.class );
    _map.put( "XMLSchema#dateTime", LocalDate.class );
    _map.put( "XMLSchema#duration", Duration.class );
    _map.put( "XMLSchema#hexBinary", byte[].class );
    _map.put( "XMLSchema#base64Binary", byte[].class );
    _map.put( "XMLSchema#anyURI", String.class );
    _map.put( "XMLSchema#ID", String.class );
    _map.put( "XMLSchema#IDREF", String.class );
    _map.put( "XMLSchema#IDREFS", String.class );
    _map.put( "XMLSchema#ENTITY", String.class );
    _map.put( "XMLSchema#ENTITIES", String.class );
    //_map.put( "XMLSchema#NOTATION",  );
    _map.put( "XMLSchema#normalizedString", String.class );
    _map.put( "XMLSchema#token", String.class );
    _map.put( "XMLSchema#language", String.class );
    _map.put( "XMLSchema#NMTOKEN", String.class );
    _map.put( "XMLSchema#NMTOKENS", String.class );
    _map.put( "XMLSchema#Name", String.class );
    _map.put( "XMLSchema#QName", QName.class );
    _map.put( "XMLSchema#NCName", String.class );
    _map.put( "XMLSchema#integer", BigInteger.class );
    _map.put( "XMLSchema#nonNegativeInteger", BigInteger.class );
    _map.put( "XMLSchema#positiveInteger", BigInteger.class );
    _map.put( "XMLSchema#nonPositiveInteger", BigInteger.class );
    _map.put( "XMLSchema#negativeInteger", BigInteger.class );
    _map.put( "XMLSchema#byte", byte.class );
    _map.put( "XMLSchema#int", int.class );
    _map.put( "XMLSchema#long", long.class );
    _map.put( "XMLSchema#short", short.class );
    _map.put( "XMLSchema#unsignedByte", short.class );
    _map.put( "XMLSchema#unsignedInt", long.class );
    _map.put( "XMLSchema#unsignedLong", BigInteger.class );
    _map.put( "XMLSchema#unsignedShort", int.class );
    _map.put( "XMLSchema#date", LocalDate.class );
    _map.put( "XMLSchema#time", LocalTime.class );
    _map.put( "XMLSchema#gYearMonth", YearMonth.class );
    _map.put( "XMLSchema#gYear", Year.class );
    _map.put( "XMLSchema#gMonthDay", MonthDay.class );
    _map.put( "XMLSchema#gDay", DayOfWeek.class );
    _map.put( "XMLSchema#gMonth", Month.class );
  }
}
