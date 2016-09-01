/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datathon;


import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sujithg
 */
public class Datathon {


static int incomeDate=0;
static float income=0;
static float balance=0;
static float[] results = new float[10000];

public static void fileWrite(String name){
       try{
  // Create file 
  FileWriter fstream = new FileWriter(name);
           try (BufferedWriter out = new BufferedWriter(fstream)) {
               for (int i = 0; i<results.length; i++) {
                   out.write((i+1)+","+results[i]+"\n");
               }
               //Close the output stream
           }
  }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }
  }
    
    public static void loadBalance(int customer){
        FileInputStream inputStream = null;
    Scanner sc = null;
    try {
    inputStream = new FileInputStream("Balances.txt");
    sc = new Scanner(inputStream, "UTF-8");
    while (sc.hasNextLine()) {
        String line;
        
        line = sc.nextLine();
        String pattern = customer + ",";
        if(line.startsWith(pattern)){
            balance = Float.parseFloat(line.substring(line.indexOf(",")+1));
            }
        }
    } catch(FileNotFoundException | NumberFormatException ex){
        }
    
    finally {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Datathon.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        if (sc != null) {
            sc.close();
            }
        }
    }
    public static void loadIncome(int customer){
        FileInputStream inputStream = null;
    Scanner sc = null;
    try {
    inputStream = new FileInputStream("Income.txt");
    sc = new Scanner(inputStream, "UTF-8");
    while (sc.hasNextLine()) {
        String line;
        
        line = sc.nextLine();
        String pattern = customer + ",";
        if(line.startsWith(pattern)){
            income = Float.parseFloat(line.substring(pattern.length(),line.lastIndexOf(",")));
            incomeDate = Integer.parseInt(line.substring(line.lastIndexOf(",")+1));
            
        }
        }
    } catch(FileNotFoundException | NumberFormatException ex){
        }
    
    finally {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Datathon.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        if (sc != null) {
            sc.close();
            }
        }
    }
    public static void runThis(){
        try{
        SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
        int currentMonth = 11;
        Date currentDate = ft.parse("01/"+currentMonth+"/2015");
        for(int customer =1; customer <=100;customer++){
            System.out.println(customer);
        loadBalance(customer);
        loadIncome(customer);
        SimpleDateFormat dayDate = new SimpleDateFormat ("dd/MM/yyyy");
        Date payDate = dayDate.parse(incomeDate+"/"+currentMonth+"/2015");
        
        Rent rt = new Rent();
        double rent = rt.rentTotal(customer);
        int f=rt.getRentDate();
        Date rentDate = dayDate.parse(f+"/11/2015");

        tran tp = new tran();
        tp.loadTran(customer);
        tp.sendDates(payDate,currentDate);
        double costOfTrans = tp.getTransCost();
        
        double result = balance-costOfTrans;
        if(rentDate.before(payDate) && rentDate.after(currentDate)){
           result = result - rent;
            }
        else if(rentDate.equals(currentDate)){
           result = result - rent;
            }
        else{}
        
        results[customer-1]=(float)result;
        }
        fileWrite("results1_1.csv");
        }catch(Exception ex){
         //ex.getStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException, ParseException {
        
        runThis();
    }  
}
class tran {
    
    private static ArrayList<Date> tranDate;
    private static ArrayList<String> Category;
    private static ArrayList<String> subCategory;
    private static ArrayList<Float> tranCost;
    float id;
    private static double cost =0;
    private static Date payDate;
    static Date currentDate;
    
    tran(){
        tranDate = new ArrayList();
        Category = new ArrayList();
        subCategory = new ArrayList();
        tranCost = new ArrayList();
    }
    //Setting time frame
    public static void sendDates(Date dt,Date dt2){
        payDate = dt;
        currentDate = dt2;
    }
    //parsing through transactions file for customer.
    public static void loadTran(int customer){
        FileInputStream inputStream = null;
        int count=0;
        Scanner sc = null;
        try {
        inputStream = new FileInputStream("Transactions.txt");
        sc = new Scanner(inputStream, "UTF-8");

        while (sc.hasNextLine()) {
            String line;
            line = sc.nextLine();
            String pattern = customer + ",";
            if(line.startsWith(pattern)){
                
                setTran(customer, count, pattern.length(), line);
               count++;
                }
            }
    
        } catch(FileNotFoundException | ParseException ex){
            
            //System.out.println("here:loadTran");
        }

        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }catch (IOException ex) {
                    Logger.getLogger(Datathon.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            if (sc != null) {
                sc.close();
                }
            }
        
    }
    //adding up cost of transactions within timeframe
    public static void tranDateSize (){
        //Working Here...
        Date maxDate = payDate;
        Date minDate = currentDate;
        //System.out.println("Tans_size:"+tranDate.size());
        try{
        for(int z=1;z<=3;z++){
            maxDate.setMonth(payDate.getMonth()-z);
            minDate.setMonth(currentDate.getMonth()-z);
        for(int i=0; i <=tranDate.size();i++){
            if(tranDate.get(i).after(minDate) 
                    && tranDate.get(i).before(maxDate)){
                 cost += tranCost.get(i);
             }
            else if (tranDate.get(i).equals(minDate)){
                cost += tranCost.get(i);
            }
        
            else {}
        
        }
        }
        }catch(Exception ex){
            ex.getStackTrace();
        }
     cost = cost/3;
    }
    
    public static double getTransCost(){
        tranDateSize();
        double costT=cost;
        return costT;
    }
    public static void setTran(int customer, int count, int pattern, String line) throws ParseException{
        try{
            //System.out.println("::"+ line);
            SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
            String dateFor = line.substring(pattern,pattern+10);
            String catLoc = line.substring(pattern+11,line.indexOf(",", pattern+11));
            //System.out.println("catLoc: "+catLoc);
            tranDate.add(ft.parse(dateFor));
            Category.add(catLoc);
            String subCatLoc = line.substring(pattern+11+Category.get(count).length(),line.indexOf(",", pattern+11+Category.get(count).length()+1));
            //System.out.println("subCatLoc: "+subCatLoc);
            subCategory.add(subCatLoc);
            String costLoc = line.substring(pattern+11+Category.get(count).length()+subCategory.get(count).length()+1, line.indexOf(",",pattern+11+Category.get(count).length()+subCategory.get(count).length()+1));
            //System.out.println("costLoc: "+costLoc);
            tranCost.add(Float.parseFloat(costLoc));
            
        }catch(ParseException | NumberFormatException ex){
            //System.out.println("Error!!! in setTran:: line print"+line);
        }
    }
}

class Rent{

static int rentMonths;
static int rentDate=0;
static double rentSum=0;
static Date payDate;
static Date currentDate;
static ArrayList<Date> rdate= new ArrayList();

    public static int getRentDate(){
        int date=0;
        try{
            Collections.sort(rdate);
        date += rdate.get(rdate.size()-1).getDate();
        date += rdate.get(rdate.size()-2).getDate();
        date += rdate.get(rdate.size()-3).getDate();
        date = date/3;
        
        }catch(Exception ex){
            
        }
        return date;
    }
 

    public static void loadRent(int customer){
        FileInputStream inputStream = null;
        int count=0;
        Scanner sc = null;
        try {
        inputStream = new FileInputStream("Rent.txt");
        sc = new Scanner(inputStream, "UTF-8");

        while (sc.hasNextLine()) {
            String line;
            line = sc.nextLine();
            String pattern = customer + ",";
            
            if(line.startsWith(pattern)){

                DateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
                Date rentD = ft.parse(line.substring(pattern.length(),line.lastIndexOf(",")));
                float rent = Float.parseFloat(line.substring(line.lastIndexOf(",")+1));
                rentSum += (double)rent;
                count++;
                //rentDate.
                //String renDate= ft.format(rentDate);
                rdate.add(rentD);
                
                

                }
            }
        } catch(FileNotFoundException | ParseException | NumberFormatException ex){
            }

        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }catch (IOException ex) {
                    Logger.getLogger(Datathon.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            if (sc != null) {
                sc.close();
                }
            }
        rentMonths=count;
        
    }
    public static double rentTotal(int customer){
        loadRent(customer);
        return rentSum/rentMonths;
    }
}