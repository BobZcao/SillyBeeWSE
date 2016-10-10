package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Evaluator for HW1.
 * 
 * @author fdiaz
 * @author congyu
 */
class Evaluator {
  public static class DocumentRelevances {
    private Map<Integer, Double> relevances = new HashMap<Integer, Double>();
    private double _relevenceDocNum = 0.0;
    
    public DocumentRelevances() { }
    
    public void addDocument(int docid, String grade) {
      relevances.put(docid, convertToBinaryRelevance(grade));
      _relevenceDocNum += convertToBinaryRelevance(grade);
    }

    public boolean hasRelevanceForDoc(int docid) {
      return relevances.containsKey(docid);
    }
    
    public double getRelevanceForDoc(int docid) {
      return relevances.get(docid);
    }
    
    private static double convertToBinaryRelevance(String grade) {
      if (grade.equalsIgnoreCase("Perfect") ||
          grade.equalsIgnoreCase("Excellent") ||
          grade.equalsIgnoreCase("Good")) {
        return 1.0;
      }
      return 0.0;
    }

    public double getRelevanceDocNum() {
      return _relevenceDocNum;
    }
  }
  
  /**
   * Usage: java -cp src edu.nyu.cs.cs2580.Evaluator [labels] [metric_id]
   */
  public static void main(String[] args) throws IOException {
    Map<String, DocumentRelevances> judgments =
        new HashMap<String, DocumentRelevances>();
    SearchEngine.Check(args.length == 2, "Must provide labels and metric_id!");
    readRelevanceJudgments(args[0], judgments);
    evaluateStdin(Integer.parseInt(args[1]), judgments);
  }

  public static void readRelevanceJudgments(
      String judgeFile, Map<String, DocumentRelevances> judgements)
      throws IOException {
    String line = null;
    BufferedReader reader = new BufferedReader(new FileReader(judgeFile));
    while ((line = reader.readLine()) != null) {
      // Line format: query \t docid \t grade
      Scanner s = new Scanner(line).useDelimiter("\t");
      String query = s.next();
      DocumentRelevances relevances = judgements.get(query);
      if (relevances == null) {
        relevances = new DocumentRelevances();
        judgements.put(query, relevances);
      }
      relevances.addDocument(Integer.parseInt(s.next()), s.next());
      s.close();
    }
    reader.close();
  }

  // @CS2580: implement various metrics inside this function
  public static void evaluateStdin(
      int metric, Map<String, DocumentRelevances> judgments)
          throws IOException {
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(System.in));
    List<Integer> results = new ArrayList<Integer>();
    String line = null;
    String currentQuery = "";
    while ((line = reader.readLine()) != null) {
      Scanner s = new Scanner(line).useDelimiter("\t");
      final String query = s.next();
      if (!query.equals(currentQuery)) {
        // if (results.size() > 0) {

        //   switch (metric) {
        //   case -1:
        //     evaluateQueryInstructor(currentQuery, results, judgments);
        //     break;
        //   case 0:
        //     evaluateQueryMetric0(currentQuery, results, judgments);
        //     break;
        //   case 1:
        //   case 2:
        //   case 3:
        //   case 4:
        //   case 5:
        //   case 6:
        //   default:
        //     // @CS2580: add your own metric evaluations above, using function
        //     // names like evaluateQueryMetric0.
        //     System.err.println("Requested metric not implemented!");
        //   }
        //   results.clear();
        // }
        currentQuery = query;
      }
      results.add(Integer.parseInt(s.next()));
      s.close();
    }
    reader.close();
    if (results.size() > 0) {
      // evaluateQueryInstructor(currentQuery, results, judgments);
      switch (metric) {
          case -1:
            evaluateQueryInstructor(currentQuery, results, judgments);
            break;
          case 0:
            evaluateQueryMetric0(currentQuery, results, judgments);
            break;
          case 1:
            evaluateQueryMetric1(currentQuery, results, judgments);
            break;
          case 2:
          case 3:
          case 4:
          case 5:
          case 6:
          default:
            // @CS2580: add your own metric evaluations above, using function
            // names like evaluateQueryMetric0.
            System.err.println("Requested metric not implemented!");
    }
  }
}
  
  public static void evaluateQueryMetric1(
      String query, List<Integer> docids,
      Map<String, DocumentRelevances> judgments) {

    // Recall at 1, 5, 10
    List<Integer> K = new ArrayList<Integer>();
    K.add(1);
    K.add(5);
    K.add(10);

    int k = 0;
    double R = 0.0;

    DocumentRelevances relevances = judgments.get(query);
    double releventDocNum = relevances.getRelevanceDocNum();

    for (int docid : docids) {
      
      if (relevances == null) {
        System.out.println("Query [" + query + "] not found!");
      } else {
        if (relevances.hasRelevanceForDoc(docid)) {
          R += relevances.getRelevanceForDoc(docid);
        }
      }
      k++;
      if(K.contains(k)) {
        if(releventDocNum == 0.0){
          System.err.println(query + "\tRecall@" + Integer.toString(k)+ "\tThere is no relevant document.");
        }
        else {
          System.out.println(query + "\tRecall@" + Integer.toString(k)+ "\t" + Double.toString(R / releventDocNum));
        }
      }
    }
   
  }
  
  public static void evaluateQueryMetric0(
      String query, List<Integer> docids,
      Map<String, DocumentRelevances> judgments) {

    // Precision at 1, 5, 10
    List<Integer> K = new ArrayList<Integer>();
    K.add(1);
    K.add(5);
    K.add(10);

    double R = 0.0;
    int k = 0;
    
    DocumentRelevances relevances = judgments.get(query);
    
    for (int docid : docids) {
      
      if (relevances == null) {
        System.out.println("Query [" + query + "] not found!");
      } else {
        if (relevances.hasRelevanceForDoc(docid)) {
          R += relevances.getRelevanceForDoc(docid);
        }
      }
      k++;
      if(K.contains(k)) {
        System.out.println(query + "\tPrecision@" + Integer.toString(k)+ "\t" + Double.toString(R / (double)k));
      }
    }
  }

  public static void evaluateQueryInstructor(
      String query, List<Integer> docids,
      Map<String, DocumentRelevances> judgments) {
    double R = 0.0;
    double N = 0.0;
    for (int docid : docids) {
      DocumentRelevances relevances = judgments.get(query);
      if (relevances == null) {
        System.out.println("Query [" + query + "] not found!");
      } else {
        if (relevances.hasRelevanceForDoc(docid)) {
          System.out.println(docid);
          R += relevances.getRelevanceForDoc(docid);
        }
        ++N;
      }
    }
    System.out.println(query + "\t" + Double.toString(R / N));
  }
}
