package com.company;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SearchFolder implements ActionListener {

    private JTextField main_search;
    private JButton search_btn_first;
    private JFrame mainFrame;
    private JFrame secondFrame;
    private final int NR_RESULTS_PPAGE = 4;
    private final int RESULT_HEIGHT = 100;
    private final int RESULT_WIDTH = 600;
    ArrayList<JTextArea> results;
    ArrayList<JButton> buttonsResults;


    public JTextField getMain_search() {
        return main_search;
    }

    public void setMain_search(JTextField main_search) {
        this.main_search = main_search;
    }

    public JButton getSearch_btn_first() {
        return search_btn_first;
    }

    public void setSearch_btn_first(JButton search_btn_first) {
        this.search_btn_first = search_btn_first;
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public JFrame getsecondFrame() {
        return secondFrame;
    }

    public void setsecondFrame(JFrame secondFrame) {
        this.secondFrame = secondFrame;
    }

    public SearchFolder() {
        mainFrame = new JFrame();
        main_search = new JTextField();
        main_search.setBounds(300, 50, 200, 100);

        search_btn_first = new JButton("Search");
        search_btn_first.setBounds(300, 200, 100, 50);
        search_btn_first.addActionListener(this);

        // second frame
        secondFrame = new JFrame();
        results = new ArrayList<>();
        buttonsResults = new ArrayList<>();

        for (int i = 0; i < NR_RESULTS_PPAGE; i++) {
            JTextArea tf = new JTextArea();
            tf.setBounds(100, 100 + i * 10 + i * RESULT_HEIGHT, 600, RESULT_HEIGHT);
            tf.setLineWrap(true);
            results.add(tf);

            JButton btn = new JButton("More...");
            btn.setBounds(725, 100 + i * 10 + i * RESULT_HEIGHT, 100, 20);
            btn.addActionListener(this);
            buttonsResults.add(btn);
        }

        for (JTextArea tf : results)
            secondFrame.add(tf);

        for(JButton btn : buttonsResults)
            secondFrame.add(btn);


        secondFrame.setSize(1024, 600);
        secondFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        secondFrame.setLayout(null);


        mainFrame.add(search_btn_first);
        mainFrame.add(main_search);
        mainFrame.setSize(1024, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
    }

    public void highlightWords(JTextArea ta, String text, String searchedWords) {
        Set<String> searchSet = new TreeSet<>();
        String[] tokens = searchedWords.split(" ");
        for (String t : tokens) {
            searchSet.add(t);
        }

        String[] words = text.split(" ");
        for (String t : tokens) {
            int p0 = 0;
            for (String w : words)
                if (t.equals(w)) {
                    try {
                        Highlighter highlighter = ta.getHighlighter();
                        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
                        p0 = text.indexOf(" " + w + " ", p0++);
                        int p1 = p0 + w.length();
                        highlighter.addHighlight(1 + p0, 1 + p1, painter);
                    } catch (BadLocationException b) {
                        b.printStackTrace();
                    }
                }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e)  {
        System.out.println("something happened");
        if (e.getSource() == search_btn_first) {


            main_search.setBounds(100, 20, 600, 30);
            search_btn_first.setBounds(725, 20, 100, 20);
            secondFrame.add(main_search);
            secondFrame.add(search_btn_first);


            ArrayList<File> files = null;
            try {
                files = Main.getRankedFiles(main_search.getText());

            } catch (Exception error) {
                error.printStackTrace();
            }
            int i;
            for ( i = 0; i < results.size() && i < files.size(); i++) {
                try {
                    String str = "";
                    Scanner myReader = new Scanner(files.get(i));

                    while (myReader.hasNextLine()) {
                        str += myReader.nextLine();
                    }
//                        JScrollPane scrollPane = new JScrollPane(results.get(i));
//                        secondFrame.add(scrollPane, BorderLayout.CENTER);
                    results.get(i).setText(str);
                    highlightWords(results.get(i), str, main_search.getText());


                } catch (FileNotFoundException err) {
                    System.out.println("error");
                    err.printStackTrace();
                }
            }

            for( i = i; i < results.size();i++)
                results.get(i).setText("");


            mainFrame.setVisible(false);
            secondFrame.setVisible(true);
        }
        for(int i =0 ; i < buttonsResults.size(); i++ )
            if(e.getSource() == buttonsResults.get(i)){
                ArrayList<File> files = null;
                try {
                    files = Main.getRankedFiles(main_search.getText());
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(files.get(i));
                } catch (Exception error) {
                    error.printStackTrace();
                }

            }
    }
}
