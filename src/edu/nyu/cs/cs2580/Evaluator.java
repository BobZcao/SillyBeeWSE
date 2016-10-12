package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeMap;
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
            evaluateQueryMetric2(currentQuery, results, judgments);
            break;
          case 3:
            evaluateQueryMetric3(currentQuery, results, judgments);
            break;
          case 4:
            evaluateQueryMetric4(currentQuery, results, judgments);
            break;
          case 5:
          case 6:
          default:
            // @CS2580: add your own metric evaluations above, using function
            // names like evaluateQueryMetric0.
            System.err.println("Requested metric not implemented!");
    }
  }
}
  
  public static void evaluateQueryMetric4(
      String query, List<Integer> docids,
      Map<String, DocumentRelevances> judgments) {

    // Average Precision, iterate through all document
    DocumentRelevances relevances = judgments.get(query);
    double averagePrecision = 0.0;
    double R = 0.0; 
    int i = 1;

    if (relevances == null) {
      System.out.println("Query [" + query + "] not found!");
    } else {
      
      for (int docid : docids) {
        if (relevances.hasRelevanceForDoc(docid) 
          && relevances.getRelevanceForDoc(docid) != 0.0) {
            R += relevances.getRelevanceForDoc(docid);
            averagePrecision += R / (double)(i);
          }
        i++;
      }

      if (R == 0.0) {
        System.out.println(query + "\tAverage precision\t there is no relevant document in labels.");
        averagePrecision = 0.0;
      } else {
        averagePrecision /= R;
        System.out.println(query + "\tAverage precision\t" + Double.toString(averagePrecision));
      }
    }

}
    

  public static void evaluateQueryMetric3(
      String query, List<Integer> docids,
      Map<String, DocumentRelevances> judgments) {
    
    DocumentRelevances relevances = judgments.get(query);
    TreeMap<Double, Double> recall_precision = new TreeMap<Double, Double>();
    double R = 0.0;
    double precision = 0.0;
    double recall = 0.0;
    int i = 1;

    if (relevances == null) {
      System.out.println("Query [" + query + "] not found!");
    } else {
      // Calculate precision and recall value
      double releventDocNum = relevances.getRelevanceDocNum();
      if (releventDocNum == 0.0) {
        System.out.println("There is no relevant documents in label");
      } else {
        for (int docid : docids) {
          if (relevances.hasRelevanceForDoc(docid)) {
              R += relevances.getRelevanceForDoc(docid);
            }
          recall = R / releventDocNum;
          precision = R / i;
          if(recall_precision.containsKey(recall)){
            if(precision > recall_precision.get(recall)) {
              recall_precision.put(recall, precision);
            }
          } else {
            recall_precision.put(recall, precision);
          }
          i++;
        }

        // Interpolation of recall value 
        double precision_stdcell = 0.0;
        
        for (int j = 0; j <= 10; j++) {
          double r = j / 10.0;
          if(recall_precision.containsKey(r)) {
            precision_stdcell = recall_precision.get(r);
          } else {
            double rightSlotPrecision = 0.0;
            double leftSlotPrecision = 0.0;
            if (recall_precision.ceilingKey(r) != null) {
              rightSlotPrecision = recall_precision.get(recall_precision.ceilingKey(r));
            }
            if (recall_precision.floorKey(r) != null) {
              leftSlotPrecision = recall_precision.get(recall_precision.floorKey(r));
            }
            precision_stdcell = leftSlotPrecision > rightSlotPrecision ? leftSlotPrecision : rightSlotPrecision;
          } 
          
          System.out.println(query + "\tPrecision@Recall@" + Double.toString(r) + "\t" + precision_stdcell);
          
        }
      }
    }

  }

  public static void evaluateQueryMetric2(
      String query, List<Integer> docids,
      Map<String, DocumentRelevances> judgments) {

    // F-0.5 Measure 
    double alpha = 0.5;
    int[] K = {1, 5, 10};

    DocumentRelevances relevances = judgments.get(query);
    if(relevances == null){
      System.out.println("Query [" + query + "] not found!");
    } else {
      double releventDocNum = relevances.getRelevanceDocNum();

      for( int k : K) {
        double R = getKRelevanceDocInResult(query, docids, judgments, k);
        if(releventDocNum == 0.0){
            System.err.println(query + "\tF0.5@" + Integer.toString(k) 
              + "\tThere is no relevant document in labels.");
        }
        else {
            double FMeasure = evaluateFMeasure(R / (double)k, R / releventDocNum, alpha);
            System.out.println(query + "\tF0.5@" + Integer.toString(k)+ "\t" + Double.toString(FMeasure));
        }
      }
    }

  }

  private static double evaluateFMeasure(double P, double R, double alpha) {
    return 1.0 / (alpha * (1.0 / P) + (1.0 - alpha) * (1.0 / R));
  }

  private static double getKRelevanceDocInResult(
    String query, List<Integer> docids,
    Map<String, DocumentRelevances> judgments, int k) {

    DocumentRelevances relevances = judgments.get(query);
    List<Integer> _docids = docids.subList(0, k);
    double R = 0.0;

    for (int docid : _docids) {
      if (relevances == null) {
        System.out.println("Query [" + query + "] not found!");
      } else {
        if (relevances.hasRelevanceForDoc(docid)) {
          R += relevances.getRelevanceForDoc(docid);
        }
      }
    }
    return R;
  }

  public static void evaluateQueryMetric1(
      String query, List<Integer> docids,
      Map<String, DocumentRelevances> judgments) {

    // Recall at 1, 5, 10
    int[] K = {1, 5, 10};
    DocumentRelevances relevances = judgments.get(query);

    if(relevances == null){
      System.out.println("Query [" + query + "] not found!");
    } else {
      double releventDocNum = relevances.getRelevanceDocNum();
      for (int k : K){
        double R = getKRelevanceDocInResult(query, docids, judgments, k);
        if(releventDocNum == 0.0){
          System.err.println(query + "\tRecall@" + Integer.toString(k) 
            + "\tThere is no relevant document in labels.");
        }
        else {
          System.out.println(query + "\tRecall@" + Integer.toString(k)
            + "\t" + Double.toString(R / releventDocNum));
        }
      }
    }
  }
  
  public static void evaluateQueryMetric0(
      String query, List<Integer> docids,
      Map<String, DocumentRelevances> judgments) {

    // Precision at 1, 5, 10
    int[] K = {1, 5, 10};

    for (int k : K) {
      double R = getKRelevanceDocInResult(query, docids, judgments, k);
      System.out.println(query + "\tPrecision@" + Integer.toString(k)
        + "\t" + Double.toString(R / (double)k));
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
