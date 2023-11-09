/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogHistory {

    
    public static final int NONE = 0;

   
    public static final int ERROR = 1;

   
    public static final int INFO = 2;

  
    public static final int DEBUG = 3;

   
    public static final String[] PRIORITY_STRS = {"NONE", "ERROR", "INFO ", "DEBUG"};

    
    public static final int DEFAULT_CONSOLE_PRIORITY = INFO;

    
    public static final int DEFAULT_FILE_PRIORITY = DEBUG;

    
    public static final boolean DEFAULT_SHOW_PRIORITY = false;

 
    private static final String KEY_LOG_CONSOLE_PRIORITY = "log.priority.console";
    private static final String KEY_LOG_FILE_PRIORITY = "log.priority.file";
    private static final String KEY_LOG_SHOW_PRIORITY = "log.show.priority";

 
    private static final String LOG_DIRECTORY = "log/admin buff";
    private static final String LOG_DIRECTORY1 = "log/biến động lượng";
    private static final String LOG_DIRECTORY2 = "log/biến động xu";
    private static final String LOG_DIRECTORY3 = "log/giao dịch xu";
    private static final String LOG_DIRECTORY4 = "log/đổi xu - yên";
    private static final String LOG_DIRECTORY5 = "log/shinwa";
    private static final String LOG_DIRECTORY6 = "log/bán đồ";
    private static final String LOG_DIRECTORY7 = "log/vxmm";
    private static final String LOG_DIRECTORY8 = "log/all lịch sử";
    private static final String LOG_DIRECTORY9 = "log/mua đồ";
    private static final String LOG_DIRECTORY10 = "log/dấu hiệu bug";
    private static final String LOG_DIRECTORY11 = "log/gd item đơn";
    private static final String LOG_DIRECTORY12 = "log/gd item gộp";
//    private static final String LOG_DIRECTORY13 = "log/cất_lấy item hành trang";

    // dont log constant.
    private static final String DONT_LOG = "-1";

    /**
     * Name of this class.
     */
    protected String className;

    /**
     * Priority of console logging.
     */
    protected int consolePriority;

    /**
     * Priority of file logging.
     */
    protected int filePriority;

    /**
     * If this is true then show the priority of the log as a String.
     */
    protected boolean showPriority;

    // định dạng ngày giờ
    private static final String DATE_FORMAT_DIR = "yyyy_MM"; //  tên package
    private static final String DATE_FORMAT_FILE = "dd_MMM_yyyy"; // tên file txt
    private static final String DATE_FORMAT_LOG = "yyyy/MM/dd HH:mm:ss"; // time trong txt
    private SimpleDateFormat dateFormatDir = null; //  tên package
    private SimpleDateFormat dateFormatFile = null; // tên file txt
    private SimpleDateFormat dateFormatLog = null; // time trong txt

    /**
     * Constructor which takes the class of the logged Class.
     *
     * @param loggedClass
     */
    public LogHistory(Class loggedClass) {
        // Populate the class Name
        String fullClassName = loggedClass.getName();
        this.className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);

        // Initilise simple date formatters
        this.dateFormatDir = new SimpleDateFormat(DATE_FORMAT_DIR);
        this.dateFormatFile = new SimpleDateFormat(DATE_FORMAT_FILE);
        this.dateFormatLog = new SimpleDateFormat(DATE_FORMAT_LOG);

        // Set up booleans
        initilise();
    }

    public LogHistory() {
//        String fullClassName = "userItem";
//        this.className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);

        // Initilise simple date formatters
        this.dateFormatDir = new SimpleDateFormat(DATE_FORMAT_DIR);
        this.dateFormatFile = new SimpleDateFormat(DATE_FORMAT_FILE);
        this.dateFormatLog = new SimpleDateFormat(DATE_FORMAT_LOG);
//this.dateFormatLog = new SimpleDateFormat("hh:mm", Locale.UK);

        // Set up booleans
        initilise();
    }

    /**
     * Log an error message.
     *
     * @param method Method in a class.
     * @param message Message to log.
     */
    public void error(String method, String message) {
        log(ERROR, method, message);
    }

    /**
     * Log an information message.
     *
     * @param method Method in a class.
     * @param message Message to log.
     *
     */
    public void info(String method, String message) {
        log(INFO, method, message);
    }

    /**
     * Log a debug message.
     *
     * @param method Method in a class.
     * @param message Message to log.
     */
    public void debug(String method, String message) {
        log(DEBUG, method, message);
    }

    /**
     * Simple log which will go in at INFO level.
     *
     * @param message Message to log
     */
    public void log(String message) {
        log(INFO, DONT_LOG, message);
    }
    public void log1(String message) {
        log1(NONE, DONT_LOG, message);
    }
    public void log2(String message) {
        log2(NONE, DONT_LOG, message);
    }
    public void log3(String message) {
        log3(NONE, DONT_LOG, message);
    }
    public void log4(String message) {
        log4(NONE, DONT_LOG, message);
    }
    public void log5(String message) {
        log5(NONE, DONT_LOG, message);
    }
    public void log6(String message) {
        log6(NONE, DONT_LOG, message);
    }
    public void log7(String message) {
        log7(NONE, DONT_LOG, message);
    }
    public void log8(String message) {
        log8(NONE, DONT_LOG, message);
    }
    public void log9(String message) {
        log9(NONE, DONT_LOG, message);
    }
    public void log10(String message) {
        log10(NONE, DONT_LOG, message);
    }

    public void log11(String message) {
        log11(NONE, DONT_LOG, message);
    }

    public void log12(String message) {
        log12(NONE, DONT_LOG, message);
    }
//    public void log13(String message) {
//        log13(NONE, DONT_LOG, message);
//    }



    /**
     * Logs a stacktrace.
     *
     * @param e Exception object.
     */
    public void stacktrace(Exception e) {
        error("Exception", e.getMessage());
    }

    /**
     * Intilise the priority level and booleans of where to output the logs.
     */
    private void initilise() {

        // Set defaults
        consolePriority = DEBUG;// DEFAULT_CONSOLE_PRIORITY;
        filePriority = DEBUG; // DEFAULT_FILE_PRIORITY; shutting this off for applets until better solution
        showPriority = DEFAULT_SHOW_PRIORITY;

    }

    /**
     * Method which performs the debug.
     *
     * @param logPriority Priority of the log (should be between 1 and 3)
     * @param method Used to show class.method () in log. If this is equal to
     * DONT_LOG then this isnt displayed.
     * @param message Message to log.
     */
    // hàm admin buff
      private void log(int logPriority, String method, String message) {

        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "Time : " + timeStamp + " "
                +
                priorityStr
                +
                classMethodStr
                +
                message;
        writeToFile(logMessage, date);
    }
    // xuất file log biến động lượng 
    private void log1(int logPriority, String method, String message) {

        // Check priority
        // Create log message from priority, time stamp, classs & message
        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        // Create log Strings
        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        // Create the priority string if required
        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "[" + timeStamp + "] "
                + // timestamp
                priorityStr
                + // error e.g. ERROR INFO DEBUG
                classMethodStr
                + // class and method
                message; // actual message
        writeToFile1(logMessage, date);
    }
    // xu
    private void log2(int logPriority, String method, String message) {

    
        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();


        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

   
        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "[" + timeStamp + "] "
                + // timestamp
                priorityStr
                + // error e.g. ERROR INFO DEBUG
                classMethodStr
                + // class and method
                message; // actual message
        writeToFile2(logMessage, date);
    }
    
    private void log3(int logPriority, String method, String message) {

        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "Time : " + timeStamp + " "
                + 
                priorityStr
                +
                classMethodStr
                + 
                message; 
        writeToFile3(logMessage, date);
    }
    
    private void log4(int logPriority, String method, String message) {

        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "Time : " + timeStamp + " "
                + 
                priorityStr
                +
                classMethodStr
                + 
                message; 
        writeToFile4(logMessage, date);
    }
    
    private void log5(int logPriority, String method, String message) {

        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "Time : " + timeStamp + " "
                + 
                priorityStr
                +
                classMethodStr
                + 
                message; 
        writeToFile5(logMessage, date);
    }
    
    private void log6(int logPriority, String method, String message) {

        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "Time : " + timeStamp + " "
                + 
                priorityStr
                +
                classMethodStr
                + 
                message; 
        writeToFile6(logMessage, date);
    }
    
    private void log7(int logPriority, String method, String message) {

        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "Time : " + timeStamp + " "
                + 
                priorityStr
                +
                classMethodStr
                + 
                message; 
        writeToFile7(logMessage, date);
    }
    
    private void log8(int logPriority, String method, String message) {

        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "Time : " + timeStamp + " "
                + 
                priorityStr
                +
                classMethodStr
                + 
                message; 
        writeToFile8(logMessage, date);
    }
    
    private void log9(int logPriority, String method, String message) {

        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "Time : " + timeStamp + " "
                + 
                priorityStr
                +
                classMethodStr
                + 
                message; 
        writeToFile9(logMessage, date);
    }
    
    private void log10(int logPriority, String method, String message) {

        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "Time : " + timeStamp + " "
                + 
                priorityStr
                +
                classMethodStr
                + 
                message; 
        writeToFile10(logMessage, date);
    }

    private void log11(int logPriority, String method, String message) {

        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "Time : " + timeStamp + " "
                +
                priorityStr
                +
                classMethodStr
                +
                message;
        writeToFile11(logMessage, date);
    }


    private void log12(int logPriority, String method, String message) {

        Calendar calender = Calendar.getInstance();
        Date date = calender.getTime();

        String timeStamp = dateFormatLog.format(date);
        String classMethodStr = "";
        String priorityStr = "";

        if (!method.equals(DONT_LOG)) {
            classMethodStr = className + "." + method + "(): ";
        }

        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
            priorityStr = PRIORITY_STRS[logPriority] + " ";
        }

        String logMessage = "Time : " + timeStamp + " "
                +
                priorityStr
                +
                classMethodStr
                +
                message;
        writeToFile12(logMessage, date);
    }

//    private void log13(int logPriority, String method, String message) {
//
//        Calendar calender = Calendar.getInstance();
//        Date date = calender.getTime();
//
//        String timeStamp = dateFormatLog.format(date);
//        String classMethodStr = "";
//        String priorityStr = "";
//
//        if (!method.equals(DONT_LOG)) {
//            classMethodStr = className + "." + method + "(): ";
//        }
//
//        if (showPriority && logPriority > 0 && logPriority < 4 && !method.equals(DONT_LOG)) {
//            priorityStr = PRIORITY_STRS[logPriority] + " ";
//        }
//
//        String logMessage = "Time : " + timeStamp + " "
//                +
//                priorityStr
//                +
//                classMethodStr
//                +
//                message;
//        writeToFile13(logMessage, date);
//    }


    private void writeToConsole(String logMessage) {
        System.out.println(logMessage);
    }

  
    
   // hàm admin buff
    private void writeToFile(String logMessage, Date date) {
        createDirectory(LOG_DIRECTORY);
        char PS = File.separatorChar;
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động adminbuff ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY + PS + logDirectory);

        try {
            BufferedWriter fileout = null;

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true));
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {

        }
    }

    //  biến động lượng
    private void writeToFile1(String logMessage, Date date) {

        createDirectory(LOG_DIRECTORY1);


        char PS = File.separatorChar; // path seperater
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động lượng ngày_" + dateFormatFile.format(date) + ".txt";

        // Create actual log file
        String fullFileName = LOG_DIRECTORY1 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);

        // Create the log directory if it doesn't exist
        createDirectory(LOG_DIRECTORY1 + PS + logDirectory);

        try {
            BufferedWriter fileout = null; // declare

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true)); // true = append
                fileout.newLine();
            } else // else create the file
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            // write text to file.
            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {
            // if error don't worry (do nothing)
        }
    }
    
    // biến động xu
    private void writeToFile2(String logMessage, Date date) {    
        createDirectory(LOG_DIRECTORY2);
        char PS = File.separatorChar; 
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động xu ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY2 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY2 + PS + logDirectory);

        try {
            BufferedWriter fileout = null; 

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true)); 
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {
            
        }
    }
    
     // biến động giao dịch // nếu số lượng 1 bỏ 
    private void writeToFile3(String logMessage, Date date) {    
        createDirectory(LOG_DIRECTORY3);
        char PS = File.separatorChar; 
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động GD xu ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY3 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY3 + PS + logDirectory);

        try {
            BufferedWriter fileout = null; 

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true)); 
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {
            
        }
    }
    
    // biến động đổi xu yên
    private void writeToFile4(String logMessage, Date date) {    
        createDirectory(LOG_DIRECTORY4);
        char PS = File.separatorChar; 
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động đổi xu_yên ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY4 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY4 + PS + logDirectory);

        try {
            BufferedWriter fileout = null; 

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true)); 
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {
            
        }
    }
    
    // biến động shinwa
    private void writeToFile5(String logMessage, Date date) {    
        createDirectory(LOG_DIRECTORY5);
        char PS = File.separatorChar; 
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động shinwa ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY5 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY5 + PS + logDirectory);

        try {
            BufferedWriter fileout = null; 

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true)); 
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {
            
        }
    }
    
   // biến động bán đồ
    private void writeToFile6(String logMessage, Date date) {    
        createDirectory(LOG_DIRECTORY6);
        char PS = File.separatorChar; 
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động bán đồ ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY6 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY6 + PS + logDirectory);

        try {
            BufferedWriter fileout = null; 

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true)); 
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {
            
        }
    }

     // biến động vxmm
    private void writeToFile7(String logMessage, Date date) {    
        createDirectory(LOG_DIRECTORY7);
        char PS = File.separatorChar; 
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động VXMM ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY7 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY7 + PS + logDirectory);

        try {
            BufferedWriter fileout = null; 

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true)); 
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {
            
        }
    }
    
     // biến động ALL
    private void writeToFile8(String logMessage, Date date) {    
        createDirectory(LOG_DIRECTORY8);
        char PS = File.separatorChar; 
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động ALL ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY8 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY8 + PS + logDirectory);

        try {
            BufferedWriter fileout = null; 

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true)); 
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {
            
        }
    }
     // biến động mua shop
    private void writeToFile9(String logMessage, Date date) {    
        createDirectory(LOG_DIRECTORY9);
        char PS = File.separatorChar; 
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động mua đồ ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY9 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY9 + PS + logDirectory);

        try {
            BufferedWriter fileout = null; 

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true)); 
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {
            
        }
    }
    
     // log dành cho bug
    private void writeToFile10(String logMessage, Date date) {    
        createDirectory(LOG_DIRECTORY10);
        char PS = File.separatorChar; 
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động bug ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY10 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY10 + PS + logDirectory);

        try {
            BufferedWriter fileout = null; 

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true)); 
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {
            
        }
    }

    // biến động item đơn
    private void writeToFile11(String logMessage, Date date) {
        createDirectory(LOG_DIRECTORY11);
        char PS = File.separatorChar;
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động GD item đơn ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY11 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY11 + PS + logDirectory);

        try {
            BufferedWriter fileout = null;

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true));
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {

        }
    }
    // biến động gd item  gộp
    private void writeToFile12(String logMessage, Date date) {
        createDirectory(LOG_DIRECTORY12);
        char PS = File.separatorChar;
        String logDirectory = dateFormatDir.format(date);
        String logFilename = "Biến động GD item gộp ngày_" + dateFormatFile.format(date) + ".txt";
        String fullFileName = LOG_DIRECTORY12 + PS + logDirectory + PS + logFilename;
        File logFile = new File(fullFileName);
        createDirectory(LOG_DIRECTORY12 + PS + logDirectory);

        try {
            BufferedWriter fileout = null;

            if (logFile.exists()) {
                fileout = new BufferedWriter(new FileWriter(fullFileName, true));
                fileout.newLine();
            } else
            {
                fileout = new BufferedWriter(new FileWriter(fullFileName));
            }

            fileout.write(logMessage);
            fileout.close();
        } catch (IOException ioe) {

        }
    }
  // biến động lấy cất item sll
//    private void writeToFile13(String logMessage, Date date) {
//        createDirectory(LOG_DIRECTORY13);
//        char PS = File.separatorChar;
//        String logDirectory = dateFormatDir.format(date);
//        String logFilename = "Biến động rương item sll ngày_" + dateFormatFile.format(date) + ".txt";
//        String fullFileName = LOG_DIRECTORY13 + PS + logDirectory + PS + logFilename;
//        File logFile = new File(fullFileName);
//        createDirectory(LOG_DIRECTORY13 + PS + logDirectory);
//
//        try {
//            BufferedWriter fileout = null;
//
//            if (logFile.exists()) {
//                fileout = new BufferedWriter(new FileWriter(fullFileName, true));
//                fileout.newLine();
//            } else
//            {
//                fileout = new BufferedWriter(new FileWriter(fullFileName));
//            }
//
//            fileout.write(logMessage);
//            fileout.close();
//        } catch (IOException ioe) {
//
//        }
//    }

    public static void createDirectory(String directory) {
        File logDirectory = new File(directory);
        if (!logDirectory.isDirectory()) // creates new directory if it doesn't exist
        {
            logDirectory.mkdir();
        }
    }
}