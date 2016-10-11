package edu.nyu.cs.cs2580;

import java.util.Collections;
import java.util.Vector;

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
    // @CS2580: fill in your code here.
    for (int i=0; i<_indexer.numDocs(); i++){
      all.add(scoreDocument(query, i));
    }
    Collections.sort(all, Collections.reverseOrder());
    Vector<ScoredDocument> results = new Vector<>();
    for (int i=0; i<all.size() && i< numResults; i++){
      results.add(all.get(i));
    }
    return results;
  }

  // score each document
  private ScoredDocument scoreDocument(Query query, int did){
    query.processQuery();

    // get the document tokens
    Document doc = _indexer.getDoc(did);
    Vector<String> docTokens = ((DocumentFull) doc).getConvertedBodyTokens();

    // |D| number of words in the document D
    int docTotalTokenNum = docTokens.size();
    // |C| total number of word occurrences in the corpus C
    long corpusTotalTokenNum = _indexer.totalTermFrequency();

    double score = 0.0;
    for (String queryToken : query._tokens){
      int docTokenNum = countDocToken(doc, queryToken);
      int corpusTokenNum = _indexer.corpusTermFrequency(queryToken);
      double temp = 0.5*(Double.valueOf(docTokenNum)/Double.valueOf(docTotalTokenNum)) + 0.5*(Double.valueOf(corpusTokenNum)/Double.valueOf(corpusTotalTokenNum));
      score += Math.log(temp);
    }

    return new ScoredDocument(query._query, doc, score);
  }

  // count the number of a query token in a document
  private Integer countDocToken(Document doc, String queryToken){
    int count = 0;
    Vector<String> docTokens = ((DocumentFull) doc).getConvertedBodyTokens();
    for (String token : docTokens){
      if(token.equals(queryToken)){
        count++;
      }
    }
    return count;
  }
}
