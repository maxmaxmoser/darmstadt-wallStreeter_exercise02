import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileLogger {
    private String pathToLogFile;
    private File textFile;
    private FileWriter fileWriter;

    public FileLogger(String pathToLogFile) {
        this.pathToLogFile = pathToLogFile;
    }

    public String getPathToLogFile() {
        return pathToLogFile;
    }

    public void setPathToLogFile(String pathToLogFile) {
        this.pathToLogFile = pathToLogFile;
    }

    //Writes the stockname and the stockprice to the logFile
    public void writeToFile(String stockName, String stockPrice){
        try{

            textFile = new File(pathToLogFile);
            fileWriter = new FileWriter(textFile, true);
            DateFormat df = new SimpleDateFormat("dd/MM/yy");//dd/MM/yy HH:mm:ss
            Date dateobj = new Date();
            String currentDate = df.format(dateobj).toString();
            String logToAdd = currentDate+";"+stockName+";"+stockPrice+"\n";
            // Write the logs to the logFile
            fileWriter.write(logToAdd);
            fileWriter.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Search the specified stock's data between dateMin and dateMax
    public void searchLogFile( String dMin, String dMax, String actionName){
        //Input file which needs to be parsed
        String fileToParse = pathToLogFile;
        BufferedReader fileReader = null;


        //Delimiter used in CSV file
        final String DELIMITER = ";";
        try
        {
            String line = "";
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(fileToParse));

            //Read the file line by line
            while ((line = fileReader.readLine()) != null)
            {
                //Get all tokens available in line
                String[] tokens = line.split(DELIMITER);

                DateFormat df = new SimpleDateFormat("dd/MM/yy");
                Date transactionDate = df.parse(tokens[0]);
                Date dateMin = df.parse(dMin);
                Date dateMax = df.parse(dMax);

                //print the token if the transaction date is between the min and max date
                if((transactionDate.after(dateMin))&&(transactionDate.before(dateMax)) && (actionName.equals(tokens[1]))){
                    for(String token : tokens)
                    {
                        //Print all tokens
                        System.out.println(token);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

   /* Usage exemple of fileLogger
   public static void main(String[] args) {
        FileLogger fileLogger = new FileLogger("text.csv");
        fileLogger.writeToFile("apple","100");
        fileLogger.writeToFile("apple","200");
        fileLogger.writeToFile("apple","300");
        fileLogger.writeToFile("apple","400");
        fileLogger.writeToFile("apple","500");
        fileLogger.searchLogFile("24/02/18","27/02/18","apple");
    }*/

}
