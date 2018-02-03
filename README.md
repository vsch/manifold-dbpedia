# manifold-dbpedia
Java support for DBpedia via Manifold

# Description
Core `manifold-dbpedia` provides a type manifold for the [DBpedia](http://wiki.dbpedia.org/) ontology via the [dbpedia.org/sparql](dbpedia.org/sparql) 
service.  With it you can reference DBpeidia ontology classes like this:
```java
org.dbpedia.ontology.Person person = null;
```
One Java interface maps to one ontology class.  The interface reflects the [rdfs:subClassOf](https://www.infowebml.ws/rdf-owl/subClassOf.htm) 
relationship using Java interface inheritance. Each property on the interface exists as a
"getter" method returning either another interface, a primitive, a String or other Java class
based on the defined schema type.

The `manifold-dbpedia-interface` module simply provides a static class library consisting of all the ontology
interfaces available from the core module.  This is primarily for performance as dynamically
 querying for and building a single interface and its graph is time consuming.  The resulting jar
 file is intended as a dependency for the next yet-to-be-developed `manifold-dbpedia-sparql` 
 component.
 
 The `manifold-dbpedia-sparql` module (TBD) provides a type manifold for [sparql](https://en.wikipedia.org/wiki/SPARQL)
 query resource files.  The idea is to write a partial parser for the sparql, enough to determine
 the result set types in terms of the ontology intefaces from core `manifold-dbpedia` and schema 
 data types.
  
# Contributing

Please do.

# Development

This project is defined with Maven.