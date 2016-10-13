package edu.nyu.cs.cs2580;

import java.util.Vector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Use this template to implement the cosine ranker for HW1.
 *
 * @author congyu
 * @author fdiaz
 */
public class RankerCosine extends Ranker {

  private Vector<Map<String, Double>> _docVectorSet = new Vector<Map<String, Double>>();

  public RankerCosine(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    loadDocumentVector();
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  void loadDocumentVector() {
    for (int i = 0; i < _indexer.numDocs(); ++i) {
      // Get l2-normalized tf-idf vector of document
      Map<String, Double> docVector = new HashMap<String, Double>();
      getDocumentVector(i, docVector);
      _docVectorSet.add(docVector);
    }
  }

  void getDocumentVector(int docid, Map<String, Double> docVector) {
    Document doc = _indexer.getDoc(docid);
    Vector<String> docTokens = ((DocumentFull) doc).getConvertedBodyTokens();

    // Record tokens frequency in document body
    for (String token : docTokens) {
      if (docVector.containsKey(token)) {
        docVector.put(token, docVector.get(token) + 1.0);
      } else {
        docVector.put(token, 1.0);
      }
    }

    // Compute tf-idf vector
    double normalizeFactor = 0.0;
    double totalWordNum = docTokens.size();
    for (Map.Entry<String, Double> entry : docVector.entrySet()) {
      double IDF = getTermIDF(entry.getKey());
      entry.setValue(entry.getValue() / totalWordNum * IDF);
      normalizeFactor += Math.pow(entry.getValue(), 2);
    }
    normalizeFactor = Math.sqrt(normalizeFactor);

    //L2-Normalize
    for (Map.Entry<String, Double> entry : docVector.entrySet()) {
      entry.setValue(entry.getValue() / normalizeFactor);
    }
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {

    Vector<ScoredDocument> all = new Vector<ScoredDocument>();

    for (int i = 0; i < _indexer.numDocs(); ++i) {
      // Get l2-normalized tf-idf vector of document
      Map<String, Double> docVector = _docVectorSet.get(i);

      // Compute score for document
      // Present query vector as tf-vector
      double score = 0.0;
      for(String queryToken : query._tokens) {
        if (docVector.containsKey(queryToken)) {
          score += docVector.get(queryToken) * 1.0;
        }
      }
      all.add(new ScoredDocument(query._query, _indexer.getDoc(i), score));
    }

    Collections.sort(all, Collections.reverseOrder());
    Vector<ScoredDocument> results = new Vector<ScoredDocument>();
    for (int i = 0; i < all.size() && i < numResults; ++i) {
      results.add(all.get(i));
    }
    return results;
  }

  //helper mthod for cos ranker to do single document query
  public ScoredDocument runQuery_cos_sd(Query query, int indexNumber){
    Map<String, Double> docVector = _docVectorSet.get(indexNumber);
    double score = 0.0;
    for(String queryToken : query._tokens){
      if (docVector.containsKey(queryToken)){
        score += docVector.get(queryToken) * 1.0;
      }
    }
    return new ScoredDocument(query._query, _indexer.getDoc(indexNumber), score);
  }

  private double getTermIDF(String term) {
    int totalNum = _indexer.numDocs();
    int containTermNum = _indexer.corpusDocFrequencyByTerm(term);
    return Math.log((double)(totalNum) / (double)(containTermNum));
  }

}
