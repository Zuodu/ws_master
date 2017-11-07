import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.rdf.model.*;

public class Aggregator {
	private ArrayList<Model> dataset;
	
	// CONSTRUCTEUR
	public Aggregator() {
		dataset = new ArrayList<Model>();
	}
	
	public boolean loadModelFromFile(String filename) throws IOException {
		if(filename == null) return false;
		
		InputStream in = new FileInputStream(new File(filename));
		
		Model graph = ModelFactory.createMemModelMaker().createDefaultModel();
		graph.read(in,"");
		in.close();
		
		dataset.add(graph);
		
		return true;
	}
	
	// TODO : REWRITE USING WRITE METHOD FOR A MODEL OBJECT (EASY) =>faire la méthode suivante mais qui prend un model
	public boolean writeOutputFile(String content, String filename) throws IOException {
		FileOutputStream fop = null;
		File file;

		try {
			file = new File(filename);
			fop = new FileOutputStream(file);
			
			if (!file.exists()) {file.createNewFile();}

			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	// TODO : REWRITE USING WRITE METHOD FOR A MODEL OBJECT (EASY) =>faire la méthode suivante mais qui prend un model
	public void writeOutputFileFromModel(Model modelContent, String filename) throws IOException {
		PrintWriter out= new PrintWriter(filename);
		modelContent.write(out,"RDF/XML");
		out.close();
	}
	
	public Model strictAggregation(int aggregationType) {
		int weightLimit = 0;
		Model union = ModelFactory.createMemModelMaker().createDefaultModel();
		Model intersection = ModelFactory.createMemModelMaker().createDefaultModel();
		
		// UNION AND WEIGHT VALUES COMPUTING
		for(Model g : dataset) {
			/*for(int i = 0; i < g.size(); i++) {
				union.addTripleFromArray(g.getTriple(i));
			}*/
			union.add(g.difference(union));
			//union.add(union.difference(g));
		}
		
		HashMap<Statement, Integer> weightMap = new HashMap<Statement, Integer>();
		
		StmtIterator iUnionStat1 = union.listStatements();
		while(iUnionStat1.hasNext()) {
		    Statement sUnion = iUnionStat1.nextStatement();
		    
		    weightMap.put(sUnion, 0);
		    for(Model g : dataset) {
		    	StmtIterator iStat = g.listStatements();
				while(iStat.hasNext()) {
				    Statement s = iStat.nextStatement();
				    
				    if(sUnion.equals(s))
				    	weightMap.replace(sUnion, weightMap.get(sUnion), weightMap.get(sUnion) + 1);
				}
			}
		}
		
		// CHECKING TYPE OF AGGREGATION
		switch(aggregationType) {
			case 1:	 	weightLimit = 1;
						break;
			case 2: 	weightLimit = (int) (dataset.size()/2);
						break;
			default : 	weightLimit = dataset.size();
						break;
		}
		
		// INTERSECTION AND AGGREGATE COMPUTING
		StmtIterator iUnionStat2 = union.listStatements();
		while(iUnionStat2.hasNext()) {
		    Statement s = iUnionStat2.nextStatement();
		    
		    if(weightMap.get(s) >= weightLimit)
		    	intersection.add(s);
		}
		
		return intersection;
	}
	
	// TODO : DO THIS AFTER THE kSNAP (MEDIUM)
	public Model semanticAggregation() {
		Model union = ModelFactory.createMemModelMaker().createDefaultModel();
		Model intersection = ModelFactory.createMemModelMaker().createDefaultModel();
		
		return intersection;
	}
	
	// TODO : DO THIS FIRST (HARD)
	public Model kSNAPAggregation() {
		Model union = ModelFactory.createMemModelMaker().createDefaultModel();
		Model intersection = ModelFactory.createMemModelMaker().createDefaultModel();
		
		return intersection;
	}
	
	// DO NOT USE YET
	public boolean addGraph(Model graph) {
		if(graph == null)
			return false;
		
		for(Model g : dataset) {
			if(g.equals(graph))
				return false;
		}
		
		dataset.add(graph);
		return true;
	}
	
	// DO NOT USE YET
	/*public boolean setQuery(Model graph) {
		if(ResultGraph.equals(graph))
			return false;
		
		ResultGraph = graph;
		return true;
	}*/
}