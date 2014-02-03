package edu.arizona.biosemantics.micropie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.arizona.biosemantics.micropie.classify.ILabel;
import edu.arizona.biosemantics.micropie.classify.Label;
import edu.arizona.biosemantics.micropie.classify.MultiSVMClassifier;
import edu.arizona.biosemantics.micropie.classify.SVMClassifier;
import edu.arizona.biosemantics.micropie.io.CSVAbbreviationReader;
import edu.arizona.biosemantics.micropie.io.CSVClassifiedSentenceWriter;
import edu.arizona.biosemantics.micropie.io.CSVSentenceReader;
import edu.arizona.biosemantics.micropie.io.XMLTextReader;
import edu.arizona.biosemantics.micropie.model.ClassifiedSentence;
import edu.arizona.biosemantics.micropie.model.Sentence;
import edu.arizona.biosemantics.micropie.transform.ITextSentenceTransformer;
import edu.arizona.biosemantics.micropie.transform.ITextTransformer;
import edu.arizona.biosemantics.micropie.transform.MyTextSentenceTransformer;
import edu.arizona.biosemantics.micropie.transform.SeperatorTokenizer;
import edu.arizona.biosemantics.micropie.transform.TextNormalizer;
import edu.arizona.biosemantics.micropie.transform.feature.IFilterDecorator;
import edu.arizona.biosemantics.micropie.transform.feature.MyFilterDecorator;

public class Main {

	public static void main(String[] args) throws Exception {		
		//setup classifier
		//TODO add "feature scaling"
		IFilterDecorator filterDecorator = new MyFilterDecorator(1, 1, 1);
		MultiSVMClassifier classifier = new MultiSVMClassifier(Label.values(), filterDecorator);
		
		//train classifier
		CSVSentenceReader reader = new CSVSentenceReader(new SeperatorTokenizer(","));
		reader.setInputStream(new FileInputStream("131001-sampleCombinedSentencesList-csv-CB-manipulated-by-EW-131030-test-3.csv"));
		List<Sentence> trainingSentences = reader.read();
		classifier.train(trainingSentences);
		
		//read test sentences		
		List<Sentence> testSentences = new LinkedList<Sentence>();
		File inputFolder = new File("new-microbe-xml");
		CSVAbbreviationReader abbreviationReader = new CSVAbbreviationReader(new SeperatorTokenizer(","));
		abbreviationReader.setInputStream(new FileInputStream("abbrevlist.csv"));
		LinkedHashMap<String, String> abbreviations = abbreviationReader.read();
		ITextTransformer normalizer = new TextNormalizer(abbreviations);
		XMLTextReader textReader = new XMLTextReader();
		ITextSentenceTransformer transformer = new MyTextSentenceTransformer();
		//TODO parallelize here
		for(File inputFile : inputFolder.listFiles()) {
			textReader.setInputStream(new FileInputStream(inputFile));
			String text = textReader.read();
			text = normalizer.transform(text);
			testSentences.addAll(transformer.transform(textReader.read()));
		}
	
		//classify test sentences
		//TODO parallelize here
		List<ClassifiedSentence> predictionResult = new LinkedList<ClassifiedSentence>();
		for(Sentence testSentence : testSentences) {
			Set<ILabel> predictions = classifier.getClassification(testSentence);
			predictionResult.add(new ClassifiedSentence(testSentence, predictions));
		}
		
		//output resulting classified sentences
		CSVClassifiedSentenceWriter writer = new CSVClassifiedSentenceWriter(",");
		writer.setOutputStream(new FileOutputStream("output.csv"));
		writer.write(predictionResult);
	}

}
