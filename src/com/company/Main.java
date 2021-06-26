package com.company;

import javax.print.attribute.standard.Destination;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class Main {

    public static ArrayList<File> getRankedFiles(String searched) throws Exception{

        // Separating words from input String
        Set<String> searchSet = new TreeSet<>();
        String[] tokens = searched.split(" ");
        for (String t : tokens) {
            searchSet.add(t);
        }

        // Getting data from files
        final File folder = new File("data");
        final List<File> fileList = Arrays.asList(folder.listFiles());
        List<ArrayList<Integer>> relevantFilesLists = new ArrayList<>(); // list with indexes of files that contain each word
        Scanner myReader = null;
        try {
            for (String word : searchSet) {
                ArrayList<Integer> relevantFiles = new ArrayList<>(); // files that contain a certain word from the input
                for (int i = 0; i < fileList.size(); i++) {
                    myReader = new Scanner(fileList.get(i));
                    while (myReader.hasNext()) {
                        if (myReader.next().equals(word)) {
                            relevantFiles.add(i);
                            break;
                        }
                    }
                    myReader.close();
                }
                relevantFilesLists.add(relevantFiles);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


//        int maxScore = tokens.length;
        final Integer HIGH_PRIORITY_SCORE = 100;
        final Integer EXISTANCE_SCORE = 1;
        final Integer PROXIMITY_MULTIPLYER = 800;

//        int max_len = 0;
//        for (int i = 0; i < relevantFilesLists.size(); i++) {
//            if (relevantFilesLists.get(i).size() > max_len) {
//                max_len = relevantFilesLists.get(i).size();
//            }
//        }

        Map<Integer, Integer> scores = new LinkedHashMap<>(); // key = file index , value = score
        for (ArrayList<Integer> ar : relevantFilesLists) {
            for (Integer x : ar) {
                scores.put(x, EXISTANCE_SCORE);
            }
        }


        for (int i = 0; i < relevantFilesLists.size() - 1; i++)
            for (int j = i + 1; j < relevantFilesLists.size(); j++)
                for (int k = 0; k < relevantFilesLists.get(i).size(); k++)
                    if (relevantFilesLists.get(j).contains(relevantFilesLists.get(i).get(k)))
                        scores.put(relevantFilesLists.get(i).get(k), scores.get(relevantFilesLists.get(i).get(k)) + HIGH_PRIORITY_SCORE);


        scores.forEach((key, val) -> {
            if (val > EXISTANCE_SCORE) {
                try {

                    final Scanner myReader2 = new Scanner(fileList.get(key));
                    int nr = 0;
                    boolean flag = false;
                    while (myReader2.hasNext()) {
                        if (searchSet.contains(myReader2.next())) {
                            if (flag)
                                break;
                            flag = true;

                        }
                        if (flag)
                            nr++;
                    }
                    if (flag)
                        scores.put(key, scores.get(key) + PROXIMITY_MULTIPLYER / nr);
                    myReader2.close();

                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        });


        List<ArrayList<Integer>> scoreList = new ArrayList<>(); // list that contains file index and score, can be sorted by score

        scores.forEach((key, val) -> {
            ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(key, val));
            scoreList.add(arr);
        });

        List<ArrayList<Integer>> sortedScoreList;
        sortedScoreList = scoreList.stream().sorted((x1, x2) -> x2.get(1) - x1.get(1)).collect(Collectors.toList());


        int NUM_SEARCH_RES_DISPLAY = 20;
        Desktop desktop = Desktop.getDesktop();

        ArrayList<File> orderedFiles = new ArrayList<>();
        if(sortedScoreList.size() != 0){
            System.out.println("Best matching files : ");
            for (int i = 0; i < NUM_SEARCH_RES_DISPLAY && i < sortedScoreList.size() ; i++) {
                orderedFiles.add(fileList.get(sortedScoreList.get(i).get(0)));
                System.out.println(fileList.get(sortedScoreList.get(i).get(0)).getName());
//            desktop.open(fileList.get(sortedScoreList.get(i).get(0)));
            }
        }




        return orderedFiles;
    }

    public static void main(String[] args) throws Exception {
        Instant start = Instant.now();
        String searched = "cute puppy";

        SearchFolder searchFolder = new SearchFolder();



        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));
    }
}
