package lm;

import java.io.IOException;


import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

public interface LanguageModelResults {
	TopDocs searchLM(String queryLine, int topDocs) throws IOException;
	IndexSearcher getIndexSearcher();
}
