/**
 * 
 */
package gov.cdc.irdu.samwise.util;

import gov.cdc.irdu.math.Coordinate;
import gov.cdc.irdu.math.Line;
import gov.cdc.irdu.samwise.shared.ReadabilityScores;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.representqueens.lingua.en.Fathom;
import com.representqueens.lingua.en.Readability;

/**
 * @author joel
 *
 */
public class ReadabilityScoresCalculator {
	private static Logger LOG = Logger.getLogger(ReadabilityScoresCalculator.class);

	public static ReadabilityScores analyze(String text) {
		Fathom.Stats stats = Fathom.analyze(text);
		
		LOG.debug("Analysis stats: text lines " + stats.getNumTextLines() + 
				                ", sentences " + stats.getNumSentences() + 
				                ", words " + stats.getNumWords() +
				                ", syllables " + stats.getNumSyllables());

		ReadabilityScores scores = new ReadabilityScores();
		if (stats.getNumSyllables() > 0) {
			scores.setFleschReadingEaseScore(Readability.calcFlesch(stats));
			scores.setFogIndex(Readability.calcFog(stats));
			scores.setKincaidGradeLevelScore(Readability.calcKincaid(stats));
			scores.setAverageSyllablesPerWord(Readability.syllablesPerWords(stats));
			scores.setAverageWordsPerSentence(Readability.wordsPerSentence(stats));
			scores.setPercentComplexWords(Readability.percentComplexWords(stats));
		}
		
		setFryReadabilityValues(text, scores);
		
		return scores;
	}

	private static List<Line> FryLines = new ArrayList<Line>();
	
	static {
		FryLines.add(new Line(109, 10, 132, 23, false));		// 1st Grade
		FryLines.add(new Line(109, 8, 140, 21, false));			// 2nd Grade
		FryLines.add(new Line(109, 6.55, 142, 17.5, false));	// 3rd Grade
		FryLines.add(new Line(109.5, 5.8, 144, 14.3, false));	// 4th Grade
		FryLines.add(new Line(109.7, 5.15, 146, 12.5, false));	// 5th Grade
		FryLines.add(new Line(112.5, 4.2, 149, 12.2, false));	// 6th Grade
		FryLines.add(new Line(119, 3.55, 153, 9.1, false));		// 7th Grade
		FryLines.add(new Line(127, 3.2, 157, 8.8, false));		// 8th Grade
		FryLines.add(new Line(139, 3.15, 160, 8.4, false));		// 9th Grade
		FryLines.add(new Line(144, 2.5, 164, 7.7, false));		// 10th Grade
		FryLines.add(new Line(149, 2.5, 167.5, 7.1, false));	// 11th Grade
		FryLines.add(new Line(156, 2.5, 170, 7.1, false));		// 12th Grade
		FryLines.add(new Line(162, 2.5, 172, 7.1, false));		// College
		FryLines.add(new Line(168.5, 2.7, 174.5, 6.9, false));	// College
	}
	
	private static void setFryReadabilityValues(String text, ReadabilityScores scores) {
		Fathom.Stats stats = Fathom.analyze(text);
		float divisor = ((float) stats.getNumWords()) / 100;
		float sentencesPer100Words = ((float) stats.getNumSentences()) / divisor;
		float syllablesPer100Words = ((float) stats.getNumSyllables()) / divisor;
		scores.setAverageSentencesPer100Words(sentencesPer100Words);
		scores.setAverageSyllablesPer100Words(syllablesPer100Words);
		
		Coordinate graphPoint = new Coordinate(syllablesPer100Words, sentencesPer100Words);
		float boundaries = compareBoundaries(graphPoint);
		
		if (boundaries != IN_BOUNDS) {
			scores.setFryReadabilityScore(boundaries);
			return;
		}
		
		for (int i = 0; i < FryLines.size(); i++) {
			Line line = FryLines.get(i);
			double result = line.compare(graphPoint);
			if (result < 0) {
//			    float score = ((float) (i + 2)) + (float) result;
//			    scores.setFryReadabilityScore(score);
			    scores.setFryReadabilityScore(i + 1);
			    return;
			}
		}
	}
		
	private static final float IN_BOUNDS = 0;
	private static final float LONG_WORDS = Float.MAX_VALUE;
	private static final float LONG_SENTENCES = Float.MIN_VALUE;
	
	private static final Line LongSentencesBoundary = new Line(108, 4.2, 124, 2.0, false);
	private static final Line LongWordsBoundary = new Line(142, 25.0, 174, 7.8, false);
	
	private static float compareBoundaries(Coordinate point) {
		if (LongSentencesBoundary.compare(point) >= 0)
			return -1;
		
		if (LongWordsBoundary.compare(point) <= 0)
			return -1;
		
		return IN_BOUNDS;
	}
	
}
