
function queryResultToMap(results){
	relToVal={}
	for(res in results){
		if("rel" in results[res]["rel"] && !(results[res]["rel"] in relToVal)){
			relToVal[results[res]["rel"]]=Set()
		} 
		if("val" in results[res]){
			relToVal[results[res]["rel"]].add(results[res]["val"])
		}
	}	
	return relToVal
}

function exportToTTL(concept,results){
	ttlres=""
	relToVal=queryResultToMap(results)
	for(rel in relToVal){
		for(val in relToVal[rel]){
			ttlres+="<"+concept+"> <"+rel+"> "
			if(val.includes("http")){
				ttlres+="<"+val+"> .\n"
			}else{
				ttlres+="\""+val+"\" .\n"
			}	
		}	
	}
	return ttlres
}

function exportToCSV(concept,results){
	csvres=""
	
}