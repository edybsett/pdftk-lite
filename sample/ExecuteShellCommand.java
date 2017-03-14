package sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by eric on 11/23/16.
 */
public class ExecuteShellCommand {
    public String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            BufferedReader inputreader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
            String outline = "";
            while ((outline = inputreader.readLine())!= null) {
                output.append(outline + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
