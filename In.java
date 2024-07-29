import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class In {
    private BufferedReader br;

    // Read from the file input
    public In(String filename) {
        try {
            FileReader fr = new FileReader(filename);
            br = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            System.err.println("The file is not found: " + filename);
            e.printStackTrace();
        }
    }

    // Return rest of line as string and return it, not including newline
    public String readLine() {
        String s = null;
        try { s = br.readLine(); }
        catch(IOException ioe) { ioe.printStackTrace(); }
        return s;
    }
}
