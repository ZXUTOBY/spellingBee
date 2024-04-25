import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.io.*;

//Handles the logic for the JavaBee game
public class GameLogic{
   
   
   //Name of dictionary file (containing English words to validate guesses against)
   private static final String DICTIONARY_FILENAME = "dictionary.txt";   
    
   //Total number of hives in the game
   public static final int HIVE_COUNT = 7;
      
   //Required Min/Max length for a valid player guess
   public static final int MIN_WORD_LENGTH = 4;
   public static final int MAX_WORD_LENGTH = 19;    
   
   
   //Required Min/Max number of formable words for a randomized hive
   public static final int MIN_FORMABLE = 30;
   public static final int MAX_FORMABLE = 110;    
   
   //Collection of various letters (vowels only, consonants only, all letters)
   public static final String VOWEL_CHARS = "AEIOU";  
   public static final String CONSONANT_CHARS = "BCDFGHJKLMNPQRSTVWXYZ";
   public static final String ALL_CHARS = VOWEL_CHARS + CONSONANT_CHARS;
   
   //The various score rank thresholds and their respective titles
   public static final double[] RANK_PERCENTS = {0, 0.02, 0.05, 0.08, 0.15, 0.25, 0.4, 0.5, 0.7};
   public static final String[] RANK_TITLES = {"Beginner", "Good Start", "Moving Up", 
      "Good", "Solid", "Nice", "Great", 
      "Amazing", "Genius"};
   
   //Text for different error messages that occur for various invalid inputs
   private static final String ERROR_TOO_LONG = "Too long...";
   private static final String ERROR_TOO_SHORT = "Too short...";
   private static final String ERROR_MISSING_CENTER = "Missing yellow letter...";
   private static final String ERROR_INVALID_LETTER = "Contains non-hive letter...";   
   private static final String ERROR_ALREADY_FOUND = "Already in word list...";  
   private static final String ERROR_NOT_WORD = "Not in dictionary...";
   
   //Character codes for the enter and backspace key press
   public static final char ENTER_KEY = KeyEvent.VK_ENTER;
   public static final char BACKSPACE_KEY = KeyEvent.VK_BACK_SPACE;  
   
   //A collection of letters to be used for the hives when the "Hardcoded hives" debug is enabled
   private static final char[] DEBUG_HARDCODED_HIVES = {'C', 'A', 'T', 'E', 'L', 'O', 'G'};      
   
   //Use me for generating random numbers (see https://docs.oracle.com/javase/8/docs/api/java/util/Random.html)!
   private static final Random rand = new Random(); 

      

   //...Feel free to add more **FINAL** variables of your own!
   
   
   
   



   
   
   //******************   NON-FINAL GLOBAL VARIABLES   ******************
   //********  YOU CANNOT ADD ANY ADDITIONAL NON-FINAL GLOBALS!  ******** 
   //********     YOU WILL ONLY NEED THESE FOR MILESTONE #2       ********
   
   
   //Array storing all formable words given the chosen hives
   public static String[] validWords = new String[MAX_FORMABLE];   
   
   //The maximum number of points possible given the game's chosen hive letters
   public static int maxPoints = 0;
   
   
   
   //*******************************************************************
   
   
   //This function gets called ONCE when the game is very first launched
   //before the user has the opportunity to do anything.
   //
   //Should perform any initialization that needs to happen at the start of the game,
   //and return the randomly chosen hive letters as a char array.  Whichever letter
   //is at index 0 of the array will be the center (yellow) hive letter, the remainder
   //will be the outer (gray) hive letters.
   //
   //The returned char array:
   //  -must be seven letters long
   //  -cannot have duplicate letters
   //  -cannot have an 'S' as one of its letters
   //  -must contain AT LEAST one vowel character (AEIOU) 
   //   (additionally: if the array only contains one vowel, it should be 
   //    possible for the vowel to be in any hive, including the center)
   public static char[] random_characters()
   {
      int vowel_place = rand.nextInt(7);
      char[] answer = new char[7];

      // put vowel in array
      for (int i = 0; i < answer.length; i++)
      {
         if (i == vowel_place) 
         {
            boolean still_run = true;
            while (still_run)
            {
               char vowel = VOWEL_CHARS.charAt(rand.nextInt(5));
               boolean vowel_existed = false;
               for (int j = 0; j < i; j++) {if (vowel == answer[j]) {vowel_existed = true;}}
               if (!(vowel_existed)) 
               {
                  answer[i] = vowel;
                  still_run = false;
               }
            }
         }
         // put other letters in array
         else
         {
            boolean still_run = true;
            while (still_run)
            {
               char random_letter = ALL_CHARS.charAt(rand.nextInt(ALL_CHARS.length()));
               boolean letter_existed = false;
               for (int j = 0; j < i; j++) {if (random_letter == answer[j]) {letter_existed = true;}}
               if (!(letter_existed) && random_letter != 'S') 
               {
                  answer[i] = random_letter;
                  still_run = false;
               }
            }
         }
      }
      return answer;
   }

   public static int formable_words_count(String[] word_dict, char[] answer)
   {
      int formable_words = 0;
      for (String word : word_dict)
      {
         boolean all_letter_valid = true;
         for (int i = 0; i < word.length(); i++)
         {
            boolean letter_valid = false;
            for (char j : answer)
            {
               if (word.charAt(i) == j) {letter_valid = true; break;}
            }
            if (!letter_valid) {all_letter_valid = false; break;}
         }
         if (all_letter_valid) {formable_words++;}
      }
      return formable_words;
   }



   public static char[] initializeGame(){
      Scanner dict_scan_line;
      Scanner dict_scan_word;
      int word_num = 0;
      File dictionary = new File(DICTIONARY_FILENAME);
      
      // check how many lines in the file, and catch error if file connot be found
      try
      {
         dict_scan_line = new Scanner(dictionary);
         while (dict_scan_line.hasNextLine()) {dict_scan_line.nextLine(); word_num++;}
      }
      catch (FileNotFoundException fnfe)
      {
         System.out.println("Dictionary file cannot be found or doesn't exist");
         System.exit(1);
      }

      // put the words in dictionary in an array
      String[] word_dict = new String[word_num];
      try
      {
         dict_scan_word = new Scanner(dictionary);
         for (int i = 0; i < word_num; i++) {word_dict[i] = dict_scan_word.nextLine();}
      }
      catch (FileNotFoundException fnfe)
      {
         System.out.println("Dictionary file cannot be found or doesn't exist");
         System.exit(1);
      }

      
      // check formable words
      char[] answer = new char[HIVE_COUNT];
      boolean still_run = true;
      while (still_run)
      {
         answer = random_characters();
         int formable_words = formable_words_count(word_dict, answer);
         if (formable_words >= MIN_FORMABLE && formable_words <= MAX_FORMABLE)
         {
            still_run = false;
         }
      }
      if (JavaBeeLauncher.DEBUG_USE_HARDCODED_HIVES) {return DEBUG_HARDCODED_HIVES;}
      else {return answer;}
      } //placeholder...
   
   
   
 
   
   
   //Complete your warmup task (Section 3.2.2 step 2) here by calling the requisite
   //functions out of GameGUI.
   //This function gets called ONCE after the graphics window has been
   //initialized and initializeGame has been called.
   public static void warmup(){

      /* 
      GameGUI.addToWordList("Raider", 100);
      GameGUI.addToWordList("Java", 2);
      GameGUI.setPlayerGuess("TOBY");
      GameGUI.setRank("CompSci");
      */

      //All of your 3.2.2 step 2 warmup code will go here!
      //Where will the code for step 3 go...?
      

   }     
   
   
   
   //This function gets called everytime the user types a valid key on the
   //keyboard (alphabetic character, enter, or backspace) or clicks one of the
   //hives/buttons in the game window.
   //
   //The key pressed is passed in as a char value.
   public static boolean not_english_word(String player_guess)
   {
      boolean notEnglishWord = true;
      Scanner dict_scan;
      File dictionary = new File(DICTIONARY_FILENAME);
      
      try
      {
         dict_scan = new Scanner(dictionary);
         while (dict_scan.hasNextLine()) 
         {
            if (dict_scan.nextLine().equalsIgnoreCase(player_guess)) 
            {
               notEnglishWord = false;
               break;
            }
         }
      }
      catch (FileNotFoundException fnfe)
      {
         System.out.println("Dictionary file cannot be found or doesn't exist");
         System.exit(1);
      }
      
      return notEnglishWord;
   }

   public static int word_point(String player_guess, char[] hive_array)
   {

      if (player_guess.length() == 4) {return 1;}
      int score = player_guess.length();

      int hive_in_word = 0;
      for (char hive : hive_array)
      {
         for (int i = 0; i < player_guess.length(); i++)
         {
            if (hive == player_guess.charAt(i)) 
            {
               hive_in_word++;
               break;
            }
         }
      }
      if (hive_in_word == HIVE_COUNT) {score += 7;}
      return score;
   }

   // 3.3.3 ranking helper functions
   public static boolean valid_word(String word, char center_hive, char[] hive_letters)
   {

      int valid_count = 0;

      // check if the word fit the length requirement
      if ((word.length() >= 4) && (word.length() <= 19))
      {
         valid_count++;
      }

      // check if the word has center hive
      for (int i = 0; i < word.length(); i++)
      {
         if (word.charAt(i) == center_hive)
         {
            valid_count++;
            break;
         }
      }
      // check if all letters in words are letters in hives
      boolean all_letter_valid = true;
         for (int i = 0; i < word.length(); i++)
         {
            boolean letter_valid = false;
            for (char one_of_hives : hive_letters)
            {
               if (word.charAt(i) == one_of_hives) {letter_valid = true; break;}
            }
            if (!letter_valid) {all_letter_valid = false; break;}
         }
         if (all_letter_valid) {valid_count++;}
      
      return (valid_count == 3);

   }

   public static int max_score(char center_hive, char[] hive_letters)
   {
      int max_score = 0;
      File dictionary = new File(DICTIONARY_FILENAME);
      Scanner dict;
      try{
         dict = new Scanner(dictionary);
         int i = 0;
         while (dict.hasNextLine())
         {
            String word = dict.nextLine();
            if (valid_word(word, center_hive, hive_letters))
            {
               validWords[i] = word;
               max_score += word_point(word, hive_letters);
               i++;
            }
         }
      }
      catch (FileNotFoundException fnfe)
      {
         System.out.println("Dictionary file cannot be found or doesn't exist");
         System.exit(1);
      }

      return max_score;
   }

   public static int player_score(String[] wordlist, char[] hive_letters)
   {
      int player_score = 0;
      for (String word : wordlist)
      {
         player_score += word_point(word.toUpperCase(), hive_letters);
      }
      return player_score;
   }

   public static String rank_name()
   {
      String[] wordlist = GameGUI.getWordList();
      char[] hive_letters = GameGUI.getAllHiveLetters();
      char center_hive = GameGUI.getCenterHiveLetter();

      maxPoints = max_score(center_hive, hive_letters);
      double player_score = player_score(wordlist, hive_letters);
      System.out.println(player_score);
      double max_score = maxPoints;
      double percent = player_score / max_score;


      int i = 0;
      for ( ; i < RANK_PERCENTS.length; i++)
      {
         if (percent < RANK_PERCENTS[i])
         {
            break;
         }
      }
      return RANK_TITLES[i-1];
      



   }
   



   public static void reactToKey(char key){
      /* 
      if (key == 'G')
      {
         GameGUI.displayErrorMessage("WARMUP!");
         GameGUI.wigglePlayerGuess();
      }
      */

      if ((key == BACKSPACE_KEY)&&(GameGUI.getPlayerGuessStr().length()>0))
      {
         GameGUI.setPlayerGuess(GameGUI.getPlayerGuessStr().substring(0, GameGUI.getPlayerGuessStr().length()-1));
      }
      else
      {
         if ((GameGUI.getPlayerGuessStr().length() < MAX_WORD_LENGTH)&&((key != ENTER_KEY)&&(key != BACKSPACE_KEY)))
         {
            GameGUI.setPlayerGuess(GameGUI.getPlayerGuessStr() + Character.toString(key));
         }
         else if ((GameGUI.getPlayerGuessStr().length() >= MAX_WORD_LENGTH))
         {
            GameGUI.displayErrorMessage(ERROR_TOO_LONG);
            GameGUI.wigglePlayerGuess();
         }
      }

         if (key == ENTER_KEY)
         {
            int num = 0;
            for (int i = 0; i < GameGUI.getPlayerGuessArr().length; i++)
            {
               for (int j = 0; j < GameGUI.getAllHiveLetters().length; j++)
               {
                  if (GameGUI.getPlayerGuessArr()[i] == GameGUI.getAllHiveLetters()[j]) 
                  {
                     num++;
                  }
               }
            }

            int num2 = 0;
            for (int i = 0; i < GameGUI.getPlayerGuessArr().length; i++)
            {
               if (GameGUI.getCenterHiveLetter() == GameGUI.getPlayerGuessArr()[i])
               {
                  num2++;
               }
            }

            int num3 = 0;
            for (int i = 0; i < GameGUI.getWordList().length; i++)
            {
               if (GameGUI.getPlayerGuessStr().equalsIgnoreCase(GameGUI.getWordList()[i]))
               {
                  num3++;
               }
            }

            // TOO SHORT
            if ((GameGUI.getPlayerGuessStr().length()<4))
            {
               GameGUI.displayErrorMessage(ERROR_TOO_SHORT);
               GameGUI.wigglePlayerGuess();
            }

            // NON_HIVE LETTERS
            else if ((num != GameGUI.getPlayerGuessArr().length)&&(!JavaBeeLauncher.DEBUG_ALL_LETTERS_VALID))
            {
               GameGUI.displayErrorMessage(ERROR_INVALID_LETTER);
               GameGUI.wigglePlayerGuess();
            }

            // MISSING CENTER
            else if (num2 < 1)
            {
               GameGUI.displayErrorMessage(ERROR_MISSING_CENTER);
               GameGUI.wigglePlayerGuess();         
            }

            // ALREADY FOUND
            else if (num3 != 0)
            {
               GameGUI.displayErrorMessage(ERROR_ALREADY_FOUND);
               GameGUI.wigglePlayerGuess();          
            }

            // NOT A WORD
            else if ((not_english_word(GameGUI.getPlayerGuessStr()))&&(!JavaBeeLauncher.DEBUG_NO_DICT_VERIFY))
            {
               GameGUI.displayErrorMessage(ERROR_NOT_WORD);
               GameGUI.wigglePlayerGuess();
            }

            // ADD INTO LIST
            else
            {
               GameGUI.addToWordList(GameGUI.getPlayerGuessStr(), word_point(GameGUI.getPlayerGuessStr(), GameGUI.getAllHiveLetters()));
               GameGUI.setPlayerGuess("");
               GameGUI.setRank(rank_name());
            }
         }



         System.out.println("reactToKey(...) called! key (int value) = '" + ((int)key) + "'");
         
         
      }


}


