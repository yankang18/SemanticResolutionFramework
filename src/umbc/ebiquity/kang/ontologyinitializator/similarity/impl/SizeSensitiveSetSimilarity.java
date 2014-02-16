package umbc.ebiquity.kang.ontologyinitializator.similarity.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.ILabelSimilarity;
import umbc.ebiquity.kang.ontologyinitializator.similarity.interfaces.ISetSimilarity;

public class SizeSensitiveSetSimilarity implements ISetSimilarity {

	private ILabelSimilarity _labelSimilairty;

	public SizeSensitiveSetSimilarity(ILabelSimilarity labelSimilairty) {
		_labelSimilairty = labelSimilairty;
	}

	@Override
	public double computeSimilarity(Set<String> set1, Set<String> set2) {
		if (set1.size() == 0 || set2.size() == 0) {
			return 0.0;
		}
		Map<String, Double> valueMap = new HashMap<String, Double>();

		double matchedSize1 = 0.0;
		double totalScore1 = 0.0;
		for (String range1 : set1) {
			double maxScore = 0.0;
			for (String range2 : set2) {
				double score = this._labelSimilairty.computeLabelSimilarity(range1, range2);

				String key = range1 + "@" + range2;
				valueMap.put(key, score);
				if (score > 0.0) {
					System.out.println("1 <" + range1 + "  ?  " + range2 + ">  with score " + score);
				}
				if (score == 1.0) {
					maxScore = score;
					break;
				} else if (score > maxScore) {
					maxScore = score;
				}
			}
			if (maxScore > 0.0) {
				matchedSize1++;
				totalScore1 += maxScore;
			}
			// totalScore1 += maxScore;
		}

		double matchedSize2 = 0.0;
		double totalScore2 = 0.0;
		for (String range2 : set2) {
			double maxScore = 0.0;
			for (String range1 : set1) {
				double score = 0.0;
				String key = range1 + "@" + range2;
				if (valueMap.containsKey(key)) {
					score = valueMap.get(key);
					// System.out.println("from cache <" + range1 + "  ?  " +
					// range2 + ">  with score " + score);
				} else {
					// score = this.computeSimilarity(range1, range2);
					score = this._labelSimilairty.computeLabelSimilarity(range1, range2);
					// System.out.println("new computed <" + range1 + "  ?  " +
					// range2 + ">  with score " + score);
				}
				if (score > 0.0) {
					System.out.println("2 <" + range2 + "  ?  " + range1 + ">  with score " + score);
				}
				if (score == 1.0) {
					maxScore = score;
					break;
				} else if (score > maxScore) {
					maxScore = score;
				}
			}

			if (maxScore > 0.0) {
				matchedSize2++;
				totalScore2 += maxScore;
			}
			// totalScore2 += maxScore;
		}

		if (matchedSize1 == 0 || matchedSize2 == 0) {
			return 0.0;
		}

		double largerSize = matchedSize1 > matchedSize2 ? matchedSize1 : matchedSize2;
		double smallerSize = matchedSize1 < matchedSize2 ? matchedSize1 : matchedSize2;
		double sizeRatio = largerSize / smallerSize;
		double sizeRatio1 = (double) set1.size() / matchedSize1;
		double sizeRatio2 = (double) set2.size() / matchedSize2;
		System.out.println("Size Ratios: #" + sizeRatio + "; " + sizeRatio1 + "; " + sizeRatio2);
		double avePenaltyFactor = this.computePenaltyFactor(sizeRatio, sizeRatio1, sizeRatio2);
		double aveScore = ((double) totalScore1 + (double) totalScore2) / ((double) matchedSize1 + (double) matchedSize2);
		System.out.println("Ave Penalty: " + avePenaltyFactor);
		System.out.println("Ave Score: " + aveScore);
		return aveScore * avePenaltyFactor;
	}

	private double computePenaltyFactor(double multipleDiffOfTwoSide, double multipleDiffOfOneSide1, double multipleDiffOfOneSide2) {

		double penaltyFactor1 = 1 / (1 + Math.log(multipleDiffOfTwoSide));
		double penaltyFactor2 = 1 / (1 + Math.log(multipleDiffOfOneSide1));
		double penaltyFactor3 = 1 / (1 + Math.log(multipleDiffOfOneSide2));

		double avePenaltyFactor = (penaltyFactor1 + this.Fmeasure(penaltyFactor2, penaltyFactor3, 1)) / 2;
		System.out.println("Penalty: #" + penaltyFactor1 + "; " + penaltyFactor2 + "; " + penaltyFactor3);
		return avePenaltyFactor;
	}

	private double Fmeasure(double value1, double value2, int beta) {
		return (1 + beta * beta) * value1 * value2 / ((beta * beta * value1) + value2);
	}

}
