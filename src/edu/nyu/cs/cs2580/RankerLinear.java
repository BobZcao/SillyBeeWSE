package edu.nyu.cs.cs2580;

import java.util.Collections;
import java.util.Vector;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Use this template to implement the linear ranker for HW1. You must
 * use the provided _betaXYZ for combining the signals.
 *
 * @author congyu
 * @author fdiaz
 */
public class RankerLinear extends Ranker {
  private float _betaCosine = 1.0f;
  private float _betaQl = 1.0f;
  private float _betaPhrase = 1.0f;
  private float _betaNumviews = 1.0f;
  private RankerCosine cos;
  private RankerQl ql;
  private RankerPhrase phrase;
  private RankerNumviews numviews;

  public RankerLinear(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
    _betaCosine = options._betaValues.get("beta_cosine");
    _betaQl = options._betaValues.get("beta_ql");
    _betaPhrase = options._betaValues.get("beta_phrase");
    _betaNumviews = options._betaValues.get("beta_numviews");
    //create four rankers and work locally
    cos = new RankerCosine(options,arguments,indexer);
    ql = new RankerQl(options,arguments,indexer);
    phrase = new RankerPhrase(options,arguments,indexer);
    numviews = new RankerNumviews(options,arguments,indexer);
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    System.out.println("  with beta values" +
        ": cosine=" + Float.toString(_betaCosine) +
        ", ql=" + Float.toString(_betaQl) +
        ", phrase=" + Float.toString(_betaPhrase) +
        ", numviews=" + Float.toString(_betaNumviews));
        //create a vector and stores all the document that are scroed by the query.
    Vector<ScoredDocument> all = new Vector<ScoredDocument>();
    //run and get Vector<ScoredDocument> for each ranker.runQuery;

    for (int i = 0; i < _indexer.numDocs(); ++i) {
          //score and store each document.
      //all.add(scoreDocument(query, i));
    }
      Collections.sort(all, Collections.reverseOrder());
      Vector<ScoredDocument> results = new Vector<ScoredDocument>();
      for (int i = 0; i < all.size() && i < numResults; ++i) {
        results.add(all.get(i));
      }

      return results;
  }

//  private ScoredDocument scoreDocument(Query query, int did){
//    score =
//  }


}
