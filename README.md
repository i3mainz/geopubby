This is a Linked Data server that adds an HTML interface and
dereferenceable URLs on top of RDF data that sits in a SPARQL
store.

See [the Pubby website](http://www4.wiwiss.fu-berlin.de/pubby/)
for details and instructions.

In addition to Pubby, GeoPubby supports the following features:

* Detection of geometries in the following geometry literals:
  * GeoSPARQL WKTLiteral
  * GeoSPARQL GMLLiteral
  * GeoJSON Literal
  * Well-Known-Binary WKBLiteral
* Supports reprojection of geometries to a selected amount of coordinate reference systems
* Implements a fuzzy search over concept labels 
* Implements the exports of geometries in one of the following formats:
 * RDF Serizalizations: RDF/XML, RDF/JSON, [TriG](https://www.w3.org/TR/trig/), TriX, JSON-LD, TTL, N-Triples, NQuads, HexTuples
 * Vector geometry formats: GeoJSON, KML, GML, WKT, HexWKB, SVG
 * Coverage-centric formats:
    * [CoverageJSON](https://covjson.org):
    * ASCIIGrid:
* Multiple purpose formats: JSON, JSON-LD
 * Binary formats: BSON, RDF/Thrift
 * Streaming formats: GeoJSON sequential

