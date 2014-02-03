package edu.arizona.biosemantics.micropie.io;

import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class XMLTextReader implements ITextReader {

	private InputStream inputStream;

	/**
	 * @param inputStream to read from
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	public String read() throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document xmlDocument = (Document) builder.build(inputStream);
		Element rootNode = xmlDocument.getRootElement();
		String text = rootNode.getChildText("description");
		
		if(text != null) 
			return text;
		throw new Exception("Could not find a description");
	}
}
