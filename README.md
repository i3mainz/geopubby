This is a Linked Data server that adds an HTML interface and
dereferenceable URLs on top of RDF data that sits in a SPARQL
store.

![GeoPubby Architecture](geopubby.png)

See [the Pubby website](http://www4.wiwiss.fu-berlin.de/pubby/)
for details and instructions.

## Features

In addition to Pubby, GeoPubby supports the following features:

* Detection of geometries in the following geometry literals:
  * GeoSPARQL WKTLiteral
  * GeoSPARQL GMLLiteral
  * GeoJSON Literal
  * Well-Known-Binary WKBLiteral
* Supports reprojection of geometries to a selected amount of coordinate reference systems
* Implements a fuzzy search over concept labels 
* Implements the exports of geometries in one of the following formats:
  * RDF Serizalizations: [RDF/XML](https://www.w3.org/TR/rdf-syntax-grammar/), [RDF/JSON](https://www.w3.org/TR/rdf-json/), [TriG](https://www.w3.org/TR/trig/), TriX, [JSON-LD](https://json-ld.org/spec/latest/json-ld/), [TTL](https://www.w3.org/TR/turtle/), [N-Triples](https://www.w3.org/TR/n-triples/), [NQuads](https://www.w3.org/TR/n-quads/), [HexTuples](https://github.com/ontola/hextuples)
  * Vector geometry formats: [GeoJSON](https://geojson.org/), [KML](https://www.ogc.org/standards/kml/), [GML](https://www.ogc.org/standards/gml), [OSM/XML](https://wiki.openstreetmap.org/wiki/OSM_XML), [TopoJSON](https://github.com/topojson/topojson) [WKT](https://www.ogc.org/standards/sfa), HexWKB, [SVG](https://www.w3.org/Graphics/SVG/)
  * Coverage-centric formats:
     * [CoverageJSON](https://covjson.org) to display Points, LineStrings, Polygons and GridCoverages
     * XYZASCII to display coverage data or simply a list of points
  * Multiple purpose formats: [CSV](https://tools.ietf.org/html/rfc4180), [JSON](https://www.json.org/json-en.html), [ODS](http://www.openoffice.org/sc/excelfileformat.pdf), [XLS](http://www.openoffice.org/sc/excelfileformat.pdf), [XLSX](http://www.openoffice.org/sc/excelfileformat.pdf), [YAML](https://yaml.org)
  * Binary formats: [BSON](http://bsonspec.org/), [RDF/EXI](https://www.w3.org/TR/exi/), [RDF/Thrift](https://afs.github.io/rdf-thrift/)
  * Streaming formats: [JSON Sequential](https://tools.ietf.org/html/rfc7464), [GeoJSON Sequential](https://github.com/geojson/geojson-text-sequences)

## Sample Screen

![GeoPubby Architecture](pubbyexample.png)
