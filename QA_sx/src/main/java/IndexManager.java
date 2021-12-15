import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndexManager {
    /**Create index dataset
     * */
    public static void main(String[] args) throws Exception {
        IndexManager indexManager = new IndexManager();
        indexManager.indexMader();

    }
    public void indexMader() throws Exception{
        //1. get data
         wikiDao wikidao = new wiki_deal();
         List<wiki> wiki_index_list = wikidao.queryWikiList();
         //Document set
         List<Document> docList = new ArrayList<>();



         FieldType fieldType = new FieldType();
         fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
         fieldType.setStored(true);


         for (wiki wi_ki: wiki_index_list){
             //2.Create Document
             Document document = new Document();
             document.add(new StringField("Answer",wi_ki.getAnswer(), Field.Store.YES));
             document.add(new TextField("Category",wi_ki.getCategory(), Field.Store.YES));
             document.add(new TextField("Content",wi_ki.getContent(), Field.Store.YES));
//             document.add(new Field("Category",wi_ki.getCategory(), fieldType));
//             document.add(new Field("Content",wi_ki.getContent(), fieldType));
             docList.add(document);
         }
         //3.Split terms
        //CharArraySet stopSet = CharArraySet.copy(StandardAnalyzer.STOP_WORDS_SET);
//        stopSet.add("how");
//        stopSet.add("when");
//        stopSet.add("from");
//        stopSet.add("you");
//        stopSet.add("can");
//        stopSet.add("get");
        final List<String> stopWords = Arrays.asList("from","which","also","ha","on","he","it");
        final CharArraySet stopSet = new CharArraySet(stopWords, true);


        CharArraySet enStopSet = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
        stopSet.addAll(enStopSet);
        //Analyzer analyzer = new EnglishAnalyzer(stopSet);
        //Analyzer analyzer = new SimpleAnalyzer();
        //Analyzer analyzer = new StandardAnalyzer()
        /**
         * Customer analyzer
         * */
        Analyzer analyzer = new Analyzer() {
            @Override
            protected Reader initReader(String fieldName, Reader reader) {
                return new HTMLStripCharFilter(reader);
            }

            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer source = new StandardTokenizer();

                //TokenStream stream = new EnglishPossessiveFilter(source);
                //Order matters!  If LowerCaseFilter and StopFilter were swapped here, StopFilter's
                //matching would be case sensitive, so "the" would be eliminated, but not "The"


                TokenStream stream = new LowerCaseFilter(source);
                //lemmatization
                stream = new EnglishPossessiveFilter(stream);
                //Stemming
                //stream = new PorterStemFilter(stream);

               // stream = new StopFilter(stream, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
                return new TokenStreamComponents(source, stream);
            }
        };



         //4.Create directoy
        Directory directory = FSDirectory.open(Paths.get("src/main/resources/wiki_tb"));
            //5. Indexwriterconfig
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        //6.indexwrite
        IndexWriter indexWriter = new IndexWriter(directory,config);
        //7.add index into indexset
        for(Document doc: docList){
            indexWriter.addDocument(doc);
        }
        //8.release
        indexWriter.close();




    }
}
