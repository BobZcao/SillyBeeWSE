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
    //load four _betaXYZ for combining signals
    float[] _betaArray = {_betaCosine,_betaQl,_betaPhrase,_betaNumviews};
    //load four scored document from four rankers each time when scoring a document
    ScoredDocument[] fourScoredDocument = new ScoredDocument[4];
    System.out.println("  with beta values" +
        ": cosine=" + Float.toString(_betaCosine) +
        ", ql=" + Float.toString(_betaQl) +
        ", phrase=" + Float.toString(_betaPhrase) +
        ", numviews=" + Float.toString(_betaNumviews));
    //create a vector and stores all the document that are scroed by the query.
    Vector<ScoredDocument> all = new Vector<ScoredDocument>();

    for (int i = 0; i < _indexer.numDocs(); ++i) {

      fourScoredDocument[0] = cos.runQuery_cos_sd(query,i);
      fourScoredDocument[1] = ql.runQuery_ql_sd(query,i);
      fourScoredDocument[2] = phrase.runQuery_phrase_sd(query,i);
      fourScoredDocument[3] = numviews.runQuery_numviews_sd(query,i);
      all.add(scoreDocument(query, i, fourScoredDocument, _betaArray));
    }
      //sort the documents.
      Collections.sort(all, Collections.reverseOrder());

      Vector<ScoredDocument> results = new Vector<ScoredDocument>();
      for (int i = 0; i < all.size() && i < numResults; ++i) {

        results.add(all.get(i));
      }

      return results;
  }

 private ScoredDocument scoreDocument(Query query, int did, ScoredDocument[] fourScoredDocument,float[] _betaArray ){
//      query.processQuery();
      Document doc = _indexer.getDoc(did);
      return new ScoredDocument(query._query, doc, fourScoredDocument, _betaArray);
 }


}
