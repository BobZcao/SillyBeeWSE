package edu.nyu.cs.cs2580;

import java.util.Vector;
import java.util.Collections;
import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Use this template to implement the numviews ranker for HW1.
 *
 * @author congyu
 * @author fdiaz
 */
public class RankerNumviews extends Ranker {

  public RankerNumviews(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    //create a vector and stores all the document that are scroed by the query.
    Vector<ScoredDocument> all = new Vector<ScoredDocument>();

    for (int i = 0; i < _indexer.numDocs(); ++i) {
      //score and store each document.
      all.add(scoreDocument(query, i));
    }

    Collections.sort(all, Collections.reverseOrder());
    Vector<ScoredDocument> results = new Vector<ScoredDocument>();
    for (int i = 0; i < all.size() && i < numResults; ++i) {
      results.add(all.get(i));
    }
    return results;
  }
  //returns the number of times the document was viewed in the last hour procvided as part of corpus.
  private ScoredDocument scoreDocument(Query query, int did) {
    // Process the raw query into tokens.
    query.processQuery();

    // Get the document tokens.
    Document doc = _indexer.getDoc(did);

    double score = 0.0;

    score = doc.getNumViews();

    return new ScoredDocument(query._query, doc, score);
  }

  }

