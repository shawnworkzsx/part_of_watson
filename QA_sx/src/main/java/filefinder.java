import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class filefinder {
    public static void main(String[] args) {
        File folder = new File("src/main/resources/wiki_dataset");
        File[] listf = folder.listFiles();
        for (int i = 0; i < listf.length; i++) {
            File file = listf[i];
            if (file.isFile() && file.getName().endsWith(".txt")&& (file.getName().length()>17)) {
                //using files' name as id
                String file_id = file.getName();
                file_id = "src/main/resources/wiki_dataset/" + file_id;
                try {
                    BufferedReader in = new BufferedReader(new FileReader(file_id));
                    String str;
                    while((str = in.readLine()) != null)  {
                        if(str.contains("[[Jackie Joyner-Kersee]]")){
                            System.out.println(file_id);
                        }

                    }
                } catch (IOException e) {
                }



            }
        }
    }
}
