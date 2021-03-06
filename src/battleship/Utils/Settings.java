package battleship.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Scanner;


public class Settings {
    private static final Settings settings = new Settings();
    
    //public static String IP;
    public static String Port = "25564";
    
    private Settings() {}
    
    public static Settings getInstance()
    {
        ReadFile();
        Settings.WriteFile();

        return settings;
    }
    
    private static void ReadFile()
    {
        try
        {
            File file = new File("settings.cfg");
            Scanner reader = new Scanner(file);
            Field[] fields = Settings.class.getFields();
            while (reader.hasNextLine()) 
            {
                String data = reader.nextLine();
                for (int i = 0; i < fields.length; ++i)
                {
                    if (data.contains(fields[i].getName()))
                    {
                        try
                        {
                            String value = data.substring(data.indexOf(" ")+1);
                            fields[i].set(Settings.class.getClass(), value);
                        }
                        catch(IllegalAccessException ex)
                        {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            }
        }
        catch(FileNotFoundException | IllegalArgumentException | SecurityException  ex)
        {
            System.out.println(ex.getMessage());
            Port = "25564";           
            WriteFile();
        }
    }
    
    public static void WriteFile()
    {
        try
        {
            File file = new File("settings.cfg");
            if(!file.createNewFile())
            {
                file.delete();
                file.createNewFile();
            }
            
            try (FileWriter fw = new FileWriter("settings.cfg")) 
            {
                Field[] fields = Settings.class.getFields();
                for (Field field : fields) 
                {
                    fw.write(field.getName() + " " + field.get(Settings.class.getClass()) + System.lineSeparator());
                }
            }
        }
        catch(IOException | IllegalAccessException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    
    public static String getIP() 
    {
        return "0.0.0.0";
    }

    public static int getPort() 
    {
        return Integer.parseInt(Port);
    }
    
    public static void setPort(int port)
    {
        Port = String.valueOf(port);
    }
}