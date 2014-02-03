package edu.arizona.biosemantics.micropie.io;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import edu.arizona.biosemantics.micropie.model.ClassifiedSentence;

public class CSVClassifiedSentenceWriter implements IClassifiedSentenceWriter {

	private OutputStream outputStream;
	private String seperator;

	public CSVClassifiedSentenceWriter(String seperator) {
		this.seperator = seperator;
	}
	
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public void write(List<ClassifiedSentence> classifiedSentences)
			throws Exception {
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
		
		for(ClassifiedSentence sentence : classifiedSentences) {
			bufferedWriter.write(sentence.getPrediction().toString() + seperator + sentence.getSentence().getText() + "\n"); 
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}

}
