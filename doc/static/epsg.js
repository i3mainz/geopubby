var epsgdefs={"EPSG:4326":"+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs","EPSG:3048":"proj=utm +zone=36 +ellps=GRS80 +units=m +no_defs","EPSG:3587":"+proj=lcc +lat_1=45.7 +lat_2=44.18333333333333 +lat_0=43.31666666666667 +lon_0=-84.36666666666666 +x_0=6000000 +y_0=0 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs","EPSG:3912":"proj=tmerc +lat_0=0 +lon_0=15 +k=0.9999 +x_0=500000 +y_0=-5000000 +ellps=bessel +towgs84=577.326,90.129,463.919,5.137,1.474,5.297,2.4232 +units=m +no_defs","EPSG:25832":"+proj=utm +zone=32 +ellps=GRS80 +units=m +no_defs","EPSG:25833":"+proj=utm +zone=33 +ellps=GRS80 +units=m +no_defs","EPSG:31467":"+proj=tmerc +lat_0=0 +lon_0=9 +k=1 +x_0=3500000 +y_0=0 +ellps=bessel +datum=potsdam +units=m +no_defs"}
var wgs84dest = new proj4.Proj('EPSG:4326'); 

function convertGeoJSON(geojson,from){
	if("features" in geojson){
		for(feature in geojson["features"]){
			coords=geojson["features"][feature]["geometry"]["coordinates"]
			geojson["features"][feature]["geometry"]=exportConvert(coords,from,geojson["features"][feature]["geometry"]["type"],false)
		}
	}else{
		coords=geojson["geometry"]["coordinates"]
		geojson["geometry"]=exportConvert(coords,from,geojson["geometry"]["type"],false)
	}
	console.log(geojson);
	return geojson;
}

function exportConvert(coordinates,from,geomtype,switchlatlong){
    console.log("ExportConvert")
    coords=convertit(coordinates,from,epsgdefs["EPSG:4326"],switchlatlong)
    console.log("Coords: "+coords)
    res=geometryToGeoJSON(geomtype,coords)
    console.log("Res: "+res)
    return res;
}

function geometryToGeoJSON(geomtype,coordinates){
    res={}
	res["geometry"]={}
    res["geometry"]["coordinates"]=""
    geomtype=geomtype.toLowerCase()
    console.log(geomtype.trim())
	switch(geomtype.trim()){
            case "linearring": 
            case "polygon": 
                res["geometry"]["type"]="Polygon"
		res["geometry"]["coordinates"]="[["
		break;
            case "envelope":
            res["geometry"]["type"]="Envelope"
		res["geometry"]["coordinates"]="["
		break;
            case "linestring":
                res["geometry"]["type"]="LineString"
		res["geometry"]["coordinates"]="["
		break;
            case "point":
                res["geometry"]["type"]="Point"
		res["geometry"]["coordinates"]=""
		break;
	}	
	splstr=coordinates.toString().split(",")
	console.log(res)
	i=0;
		while(i<splstr.length){
			res["geometry"]["coordinates"]+="["+splstr[i]+", "+splstr[i+1]+"], "
			i+=2;
		}
        res["geometry"]["coordinates"]=res["geometry"]["coordinates"].substring(0,res["geometry"]["coordinates"].length-2)
        if(geomtype=="linearring" || geomtype=="polygon"){
		res["geometry"]["coordinates"]+="]]"
	}else{
		res["geometry"]["coordinates"]+=""
	}
    console.log(res)
    res["geometry"]["coordinates"]=JSON.parse(res["geometry"]["coordinates"])
	return res["geometry"];
}

function convertit(coordinates,source,dest,switchlatlong){
	//console.log("Coordinates: "+coordinates)
	if(source==dest && !switchlatlong){
            return coordinates;
	}
	resultarray=[]
	//console.log(coordinates.length)
	i=0;
	splitted=coordinates.toString().split(",")
	while(i<splitted.length){
		//console.log(splitted[i]+" - "+splitted[i+1]+" - "+parseFloat(splitted[i])+" - "+parseFloat(splitted[i+1]))
		var p=new proj4.Point(splitted[i],splitted[i+1])
		//console.log("Point: "+p.x+" - "+p.y)
		//console.log(source)
		//console.log(dest)
		if(source!=dest)
			res=proj4(source, dest, p);
		else{
			res=p;
		}
		//console.log("Point: "+res.x+" - "+res.y)
		if(switchlatlong){
			resultarray.push(res.y)
			resultarray.push(res.x)
		}else{
			resultarray.push(res.x)
			resultarray.push(res.y)
		}
		i+=2;
	}
	//console.log("Resultarray: "+resultarray)
	return resultarray
}