package edu.nyu.cs.cs2580;
import java.util.Collections;
/**
 * Document with score.
 *
 * @author fdiaz
 * @author congyu
 */
class ScoredDocument implements Comparable<ScoredDocument> {
  private String _query;
  private Document _doc;
  private double _score;

  public Integer getDocID(){
    return _doc._docid;
  }

  public ScoredDocument(String query, Document doc, double score) {
    _query = query;
    _doc = doc;
    _score = score;
  }
  //return the linear scoredDocument
  public ScoredDocument(String query, Document doc,
   ScoredDocument[] fourScoredDocument, float[] _betaArray ){
      _query = query;
      _doc = doc;
      _score = _betaArray[0] * fourScoredDocument[0]._score + _betaArray[1] * fourScoredDocument[1]._score+
      _betaArray[2] * fourScoredDocument[2]._score + _betaArray[3] * fourScoredDocument[3]._score;
  }
  public String asTextResult() {
    StringBuffer buf = new StringBuffer();
    buf.append(_query).append("\t");
    buf.append(_doc._docid).append("\t");
    buf.append(_doc.getTitle()).append("\t");
    buf.append(_score);
    return buf.toString();
  }

  /**
   * @CS2580: Student should implement {@code asHtmlResult} for final project.
   */
  public String asHtmlResult() {
    StringBuffer buf = new StringBuffer();
    buf.append("<tr><td>").append(_query).append("</td>");
    buf.append("<td>").append(_doc._docid).append("</td>");
    buf.append("<td>").append(_doc.getTitle()).append("</td>");
    buf.append("<td>").append(_score).append("</td></tr>");
    return buf.toString();
  }

  @Override
  public int compareTo(ScoredDocument o) {
    if (this._score == o._score) {
      return 0;
    }
    return (this._score > o._score) ? 1 : -1;
  }
}
