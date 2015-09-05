/*
	Vishnu S - http://vishnus.name
	Last updated: September 2013
	
	For more details about PorterStemmer Algorithm, search in Google
	Code sourced from http://stackoverflow.com/questions/9756653/porter-stemmer-code

	License: MIT License
	
*/

package com.olam;

public class Stemmer {

    //method to completely stem the words in an array list
    public String completeStem(String tokens){
    	
    	String tokensTrimmed = tokens.trim();
        PorterStemmerAlgo pa = new PorterStemmerAlgo();       
        String s1 = pa.step1(tokensTrimmed);
        String s2 = pa.step2(s1);
        String s3 = pa.step3(s2);
        String s4 = pa.step4(s3);
        String s5 = pa.step5(s4);
       
        return s5;
    }

}
