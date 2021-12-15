import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class question_deal implements questionDao {
    public List<question> questionlist(){
        List<question> list = new ArrayList<question>();
        try{
            File file = new File("src/main/resources/questions.txt");
            File file2 = new File("src/main/resources/questions_more.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedReader br2 = new BufferedReader(new FileReader(file2));
            String str;
            String str2;
            while ((str = br.readLine()) != null && (str2 = br2.readLine()) != null) {
                str2 = remove_meanless(str2);
                String Category_syn = str2;

                //String Category = "\""+str+"\"~";
                String Category = str;

                str = br.readLine();
                //  String clue = "\""+str+"\"~";
                String clue = str;
               // System.out.println(clue);

                str2 = br2.readLine();
                str2 = remove_meanless(str2);
                String clue_syn = str2;

                str = br.readLine();
                String Ans = str;
                br.readLine();
                if(Category.contains("(")){
                    Category = Category.replace("(","");
                    Category = Category.replace(")","");
                    //System.out.println(clue);
                }
                if (clue.contains("\"") || (clue.contains("!")) || clue.contains(":")) {
                    //System.out.println(clue);
                    clue = clue.replace("\"", "");
                    clue = clue.replace("!", "");
                    clue = clue.replace(":", "");
                }
                if (Category.contains("\"") || (Category.contains("!")) || Category.contains("'")) {
                    //System.out.println(clue);
                    Category = Category.replace("\"", "");
                    Category = Category.replace("!", "");
                    Category = Category.replace("'", "");
                }
                question QQ = new question();
                QQ.setAnswer(Ans);
                QQ.setCategory(Category);
                QQ.setClue(clue);
                QQ.setCategory_syn(Category_syn);
                QQ.setClue_syn(clue_syn);
                list.add(QQ);
            }
            System.out.println(list.size());
       } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    private String remove_meanless(String str2) {
        str2 = str2.replaceAll("'","");
        str2 = str2.replaceAll(",","");
        str2 = str2.replaceAll("_", " ");
        str2 = str2.replaceAll("\\{","");
        str2 = str2.replaceAll("\\}","");
        str2 = str2.replaceAll("set\\(\\)", "the");
        return str2;

    }

}
