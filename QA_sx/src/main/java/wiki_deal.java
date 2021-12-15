import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class wiki_deal implements wikiDao {
    public List<wiki> queryWikiList(){
        List<wiki> list = new ArrayList<wiki>();
        File folder = new File("src/main/resources/wiki_dataset");
        //get file list
        File[] listf = folder.listFiles();
        for (int i = 0; i < listf.length; i++) {
            File file = listf[i];
            if (file.isFile() && file.getName().endsWith(".txt")&& (file.getName().length()>17)) {
                //using files' name as id
                String file_id = file.getName();
                file_id = "src/main/resources/wiki_dataset/" + file_id;
                try {
                    BufferedReader in = new BufferedReader(new FileReader(file_id));
                    String answer = "";
                    String content = "";
                    String category = "Unknown";
                    String str;
                    while((str = in.readLine()) != null)  {
                        //Condition1: Remove [ref].etc lines
                       // System.out.println(str);
                        String[] reg = {"==","[ref]","</ref>","}}","<li>","|","[/ref]","<div",".jpg","$GPGSV","HMAC","rom:","from:","text:","[tpl]","{{","PlotData"};
                        for(String x:reg){
                            if(str.contains(x)){
                                str = "";
                            }
                        }
                        if(str.equals("")) {
                            continue;
                        }
                        //Condition2: Remove long terms
                        if(str.contains(".")||str.contains("_")){
                            str = str.replace(".", " ");
                            str = str.replace("_"," ");
                        }
                        //Condition1 : answer
                        if(str.contains("[[") && str.contains("]]") ){
                            //store
                            if (!answer.equals("")){
                                //Substring very long text
//                                if(content.length()>20000){
//                                    // System.out.println(content.length());
//                                    //  content = " ";
//                                    content = content.substring(0,20000);
//                                }
                                wiki wi_ki = new wiki();
                                wi_ki.setAnswer(answer);
                                wi_ki.setCategory(category);
                                wi_ki.setContent(content);
                                list.add(wi_ki);
                                category = "Unknown";
                                content = "";
                            }
                            //get the length
                            int end_position = str.length();
                            answer = str.substring(2,end_position-2);
                            continue;
                        }
                        //Condition2 : CATEGORIES
                        if(str.contains("CATEGORIES")) {
                            category = str.substring(12, str.length());
                            continue;
                        }
                        //Condition4 : Redirect, using lucene update
                        if(str.contains("#REDIRECT") ||str.contains("#redirect") || str.contains("#Redirect") ){
                            answer = "";
                            continue;
                        }
                        if(str.equals("")){
                            continue;
                        }
                        if(content.equals("")){
                            content = str;
                            continue;
                        }
                        content = content+" "+str;
//
//                }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return list;

    }

}
