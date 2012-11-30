package gov.cdc.irdu.samwise.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author joel
 *
 */
public class ReadabilityScores implements Serializable {
	private static final long serialVersionUID = 6993751676452686108L;

	private float fleschReadingEaseScore = -1;
	private float fogIndex = -1;
	private float kincaidGradeLevelScore = -1;
	private float fryReadabilityScore = -1;
	private float percentComplexWords = -1;
	private float averageSyllablesPerWord = -1;
	private float averageWordsPerSentence = -1;
	private float averageSyllablesPer100Words = -1;
	private float averageSentencesPer100Words = -1;
	
	public ReadabilityScores() { }
	
	public float getGradeLevel() {
        float gradeLevel = fryReadabilityScore;
        if (gradeLevel > 20 || gradeLevel < 1)
            gradeLevel = (fogIndex + kincaidGradeLevelScore) / 2;
        return gradeLevel;
	}
	
	public float getFleschReadingEaseScore() {
		return fleschReadingEaseScore;
	}

	public void setFleschReadingEaseScore(float fleschReadingEaseScore) {
		this.fleschReadingEaseScore = fleschReadingEaseScore;
	}

	public float getFogIndex() {
		return fogIndex;
	}

	public void setFogIndex(float fogIndex) {
		this.fogIndex = fogIndex;
	}

	public float getKincaidGradeLevelScore() {
		return kincaidGradeLevelScore;
	}

	public void setKincaidGradeLevelScore(float kincaidGradeLevelScore) {
		this.kincaidGradeLevelScore = kincaidGradeLevelScore;
	}

	public float getPercentComplexWords() {
		return percentComplexWords;
	}

	public void setPercentComplexWords(float percentComplexWords) {
		this.percentComplexWords = percentComplexWords;
	}

	public float getAverageSyllablesPerWord() {
		return averageSyllablesPerWord;
	}

	public void setAverageSyllablesPerWord(float averageSyllablesPerWord) {
		this.averageSyllablesPerWord = averageSyllablesPerWord;
	}

	public float getAverageWordsPerSentence() {
		return averageWordsPerSentence;
	}

	public void setAverageWordsPerSentence(float averageWordsPerSentence) {
		this.averageWordsPerSentence = averageWordsPerSentence;
	}

	public float getFryReadabilityScore() {
		return fryReadabilityScore;
	}

	public void setFryReadabilityScore(float fryReadabilityScore) {
		this.fryReadabilityScore = fryReadabilityScore;
	}

    /**
     * @return the averageSyllablesPer100Words
     */
    public float getAverageSyllablesPer100Words()
    {
        return this.averageSyllablesPer100Words;
    }

    /**
     * @return the averageNumberOfSentencesPer100Words
     */
    public float getAverageSentencesPer100Words()
    {
        return this.averageSentencesPer100Words;
    }

    /**
     * @param averageSyllablesPer100Words the averageSyllablesPer100Words to set
     */
    public void setAverageSyllablesPer100Words(float averageSyllablesPer100Words)
    {
        this.averageSyllablesPer100Words = averageSyllablesPer100Words;
    }

    /**
     * @param averageNumberOfSentencesPer100Words the averageNumberOfSentencesPer100Words to set
     */
    public void setAverageSentencesPer100Words(
            float averageNumberOfSentencesPer100Words)
    {
        this.averageSentencesPer100Words = averageNumberOfSentencesPer100Words;
    }
	
	
}
