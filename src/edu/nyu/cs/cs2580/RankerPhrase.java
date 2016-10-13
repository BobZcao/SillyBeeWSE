package edu.nyu.cs.cs2580;

import java.util.Vector;
import java.util.Collections;
import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Use this template to implement the phrase ranker for HW1.
 *
 * @author congyu
 * @author fdiaz
 */
public class RankerPhrase extends Ranker {

  public RankerPhrase(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector<ScoredDocument> all = new Vector<ScoredDocument>();
    for (int i = 0; i < _indexer.numDocs(); ++i){
      all.add(scoreDocument(query, i));
    }
    Collections.sort(all, Collections.reverseOrder());
    Vector<ScoredDocument> results = new Vector<ScoredDocument>();
    for(int i = 0; i < all.size() && i < numResults; ++i){
      results.add(all.get(i));
    }
    return results;
  }

  //helper mthod for phrase ranker to do single document query
  public ScoredDocument runQuery_phrase_sd(Query query, int indexNumber){
    return scoreDocument(query, indexNumber);
  }

  private ScoredDocument scoreDocument(Query query, int did){
    // Process the raw query into tokens.
    query.processQuery();

    // Get the document tokens.
    Document doc = _indexer.getDoc(did);
    Vector<String> docTokens = ((DocumentFull) doc).getConvertedTitleTokens();


    double score = 0.0;

    //compare document tokens with query tokens
    for (String docToken : docTokens) {
      for (String queryToken : query._tokens) {
        if (docToken.equals(queryToken)) {
          score = score + 1.0;
        }
      }
    }
    return new ScoredDocument(query._query, doc, score);
  }

}
