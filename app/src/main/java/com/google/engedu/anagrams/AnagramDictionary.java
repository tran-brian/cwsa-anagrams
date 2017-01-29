/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private ArrayList<String> wordList = new ArrayList<String>();
    private HashSet<String> wordSet= new HashSet();
    private HashMap<String, ArrayList<String>> lettersToWord = new HashMap<>();
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<>();
    private HashSet<String> pastWordSet = new HashSet<>();
    private int wordLength = 4;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);

            String sorted = sortLetters(word);

            if(lettersToWord.containsKey(sorted)) {
                lettersToWord.get(sorted).add(word);
            } else {
                lettersToWord.put(sorted, new ArrayList<String>(Arrays.asList(word)));
            }

            if(sizeToWords.containsKey(word.length())) {
                sizeToWords.get(word.length()).add(word);
            } else {
                sizeToWords.put(word.length(), new ArrayList<String>(Arrays.asList(word)));
            }
        }
    }

    public String sortLetters(String word) {
        char[] letters = word.toCharArray();

        Arrays.sort(letters);

        return new String(letters);
    }

    public boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !word.contains(base);
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();
        String sorted = sortLetters(targetWord);

        if(lettersToWord.containsKey(sortLetters(sorted))) {
            result.addAll(lettersToWord.get(sorted));
        }

        return result;
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();

        pastWordSet.add(word);

        for(int i = 97; i <= 122; i++) {
            String s = sortLetters(word + Character.toString((char)i));

            if(lettersToWord.containsKey(s)) {
                result.addAll(lettersToWord.get(s));
            }
        }

        return result;
    }

    public String pickGoodStarterWord() {
        String word = "";
        ArrayList<String> words = sizeToWords.get(wordLength);
        int rand = random.nextInt(words.size());
        int i = 0;

        while(word.length() == 0 && ((rand + i) - words.size()) != rand) {
            String s;

            if(rand + i < words.size()) {
                s = words.get(rand + i);
            } else {
                s = words.get((rand + i) - words.size());
            }

            if (lettersToWord.get(sortLetters(s)).size() >= MIN_NUM_ANAGRAMS && !pastWordSet.contains(s)) {
                word = s;
                wordLength++;
            } else {
                i++;
            }
        }

        if(word.length() == 0) {
            wordLength -= 1;
            word = pickGoodStarterWord();
        }

        return word;
    }
}
