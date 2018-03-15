package Test;

import org.junit.Test;

import common.java.httpClient.request;
import common.java.httpServer.booter;
import common.java.nlogger.nlogger;
import unit.ExcelUtils;
import unit.Print_nlogger;

public class TestContent {
    public static void main(String[] args) {
        booter booter = new booter();
        try {
       
            System.out.println("CrawlerContent");
            System.setProperty("AppName", "CrawlerContent");
            booter.start(1006);
        } catch (Exception e) {
            nlogger.logout(e);
        } 
    }
    
    
	
}
