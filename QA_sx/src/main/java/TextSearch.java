import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TextSearch {
    public static void main(String[] args) throws Exception {
        TextSearch textSearch = new TextSearch();
        int a =20;
        int b =20;

        int result = textSearch.wikiSearch(a,b);
        System.out.println(result);
        }

    public int wikiSearch(int a, int b) throws Exception{
        //get questions
        int sum = 1;
        questionDao questiondao = new question_deal();
        List<question> questions_list = questiondao.questionlist();
        List<BooleanQuery.Builder> queryList = new ArrayList<>();
        List<String> ansList = new ArrayList<>();
        final List<String> stopWords = Arrays.asList("from","which","also","ha","on","he","it");
        final CharArraySet stopSet = new CharArraySet(stopWords, true);
        CharArraySet enStopSet = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
        stopSet.addAll(enStopSet);
        //EnglishAnalyzer
   //     Analyzer analyzer = new EnglishAnalyzer(stopSet);
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
        QueryParser queryParser1 = new QueryParser("Category", analyzer);
        QueryParser queryParser2 = new QueryParser("Content", analyzer);
//        QueryParser queryParser3 = new QueryParser("Category", analyzer);
//        QueryParser queryParser4 = new QueryParser("Content", analyzer);
        for (question quesTion: questions_list) {
            ////Boolean query
            //Query1
           // Query query1 = queryParser1.parse(quesTion.getCategory()+String.valueOf(a));
            Query query1 = queryParser1.parse(quesTion.getCategory());

            //Query2
          //  System.out.println(quesTion.getAnswer());
            ansList.add(quesTion.getAnswer());
           // Query query2 = queryParser2.parse(quesTion.getClue()+String.valueOf(b));
            Query query2 = queryParser2.parse(quesTion.getClue());

//            Query query3 = queryParser3.parse(quesTion.getCategory_syn());
//         //   System.out.println(quesTion.getCategory_syn());
//
//            Query query4 = queryParser4.parse(quesTion.getClue_syn());

            BooleanQuery.Builder query = new BooleanQuery.Builder();
            //query.add(query1,BooleanClause.Occur.SHOULD);
            query.add(query2,BooleanClause.Occur.SHOULD);
//            query.add(query3,BooleanClause.Occur.SHOULD);
//            query.add(query4,BooleanClause.Occur.SHOULD);
            queryList.add(query);
        }
        //4.from index directory
        Directory directory = FSDirectory.open(Paths.get("src/main/resources/wiki_tb"));
        //5. input
        IndexReader indexReader = DirectoryReader.open(directory);
        //6.Search
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//        ClassicSimilarity classicSimilarity = new ClassicSimilarity();
//        indexSearcher.setSimilarity(classicSimilarity);
        //Output
//        for(BooleanQuery.Builder Q:queryList){
//            TopDocs topDocs = indexSearcher.search(Q.build(),100000);
//            System.out.println("=================Count=================" + topDocs.totalHits);
//            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//            if(scoreDocs!=null){
//                for(ScoreDoc scoreDoc: scoreDocs){
//                    int id = scoreDoc.doc;
//                    Document doc = indexSearcher.doc(id);
//                    System.out.println("_________________________");
//                    System.out.println(doc.get("Answer"));
//                }
//            }
//        }
        Iterator<BooleanQuery.Builder> it1 = queryList.iterator();
        Iterator<String> it2 = ansList.iterator();


        while (it1.hasNext() && it2.hasNext()) {
                BooleanQuery.Builder Q = it1.next();
                String real_ans = it2.next();
                TopDocs topDocs = indexSearcher.search(Q.build(),10);

//                System.out.println("=================Count=================" + topDocs.totalHits);
                ScoreDoc[] scoreDocs = topDocs.scoreDocs;
                if(scoreDocs!=null){
                    for(int i =0 ; i<scoreDocs.length;i++){
                        int id = scoreDocs[i].doc;
                        Document doc = indexSearcher.doc(id);
                        if(doc.get("Answer").equals(real_ans)) {
//                            System.out.println("_________________________");
//                            System.out.println(i);
//                            System.out.println(doc.get("Answer"));
                            if(i<1){
                                System.out.println(doc.get("Answer"));
                               // System.out.println(i);
                                sum = sum +1;
                            }


                        }

                    }

                }
               // System.out.println(sum);
            }

        indexReader.close();


    return sum;

    }
}
