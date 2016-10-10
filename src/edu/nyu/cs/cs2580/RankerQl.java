package edu.nyu.cs.cs2580;

import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Use this template to implement the query likelihood ranker for HW1.
 * 
 * @author congyu
 * @author fdiaz
 */
public class RankerQl extends Ranker {

  public RankerQl(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector<ScoredDocument> all = new Vector<ScoredDocument>();
    for (int i = 0; i < _indexer.numDocs(); ++i) {
      all.add(scoreDocument(query, i));
    }
    Collections.sort(all, Collections.reverseOrder());
    Vector<ScoredDocument> results = new Vector<ScoredDocument>();
    for (int i = 0; i < all.size() && i < numResults; ++i) {
      results.add(all.get(i));
    }
    return results;
  }

  private ScoredDocument scoreDocument(Query query, int did) {
    // Process the raw query into tokens.
    query.processQuery();

    // Use Jelinek-Mercer smoothing with lamda 0.5
    double lamda = 0.5;
    double score = 0.0;
    
    Document doc = _indexer.getDoc(did);
    Vector<String> docTokens = ((DocumentFull) doc).getConvertedBodyTokens();

    long totalTermFrequency = _indexer.totalTermFrequency();
    long documentLength = docTokens.size();

    // Store term and corresponding frequency in hashmap
    Map<String, Integer> termFrequencyMap = new HashMap<String, Integer>();
    for (String queryToken : query._tokens) {
      termFrequencyMap.put(queryToken, 0);
    }

    getDocTermFrequency(did, termFrequencyMap);
    System.out.println("id" + did + "totalfreq" + totalTermFrequency + "\tlength" + documentLength);
    for (Map.Entry<String, Integer> entry : termFrequencyMap.entrySet()) {
      int corpusTermFrequency = _indexer.corpusTermFrequency(entry.getKey());
      int documentTermFrequency = entry.getValue();
      System.out.println("id" + did + entry.getKey() + "\t" + documentTermFrequency + "\t" + corpusTermFrequency);
      score += Math.log(((1.0 - lamda) * ((double)documentTermFrequency / (double)documentLength) / (lamda * ((double)corpusTermFrequency / (double)totalTermFrequency))) + 1.0);
    }
  
    return new ScoredDocument(query._query, doc, score);
  }

  private void getDocTermFrequency(int did, Map<String, Integer> termFrequencyMap) {
 
    Document doc = _indexer.getDoc(did);
    Vector<String> docTokens = ((DocumentFull) doc).getConvertedBodyTokens();

    // Iterate through body tokens in document and count term frequency 
    for (String docToken : docTokens) {
      if(termFrequencyMap.containsKey(docToken)){
        termFrequencyMap.put(docToken, termFrequencyMap.get(docToken) + 1);
      }
    }
  }


}
