/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ywrealty.subscriberupdate;

import HttpMailchimp.MailchimpRequestSender;
import HttpModel.ListInformation;
import HttpModel.Member;
import HttpModel.ResponseMember;
import com.ywrealty.subscriberupdate.GSUploader.SheetManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author albertliu
 */
public class Launcher {
 
    public static ArrayList<String> getEmailsFromSpreadsheets(String sprid, String sheetname)
    {
        ArrayList<String> emails = new ArrayList<String>();
        
        try {
            SheetManager sm = new SheetManager(new Properties(), "Mailchimp_Dev", sprid);
            List<List<Object>> table = sm.accessdata(sheetname);
            
            for(List<Object> ls : table)
            {
                for (Object obj : ls)
                {
                    String str = obj.toString().trim();
                    if (str.contains("@") && str.contains(".com"))
                    {
                        str = str.substring(0, str.indexOf(".com") + 4); 
                        emails.add(str.replaceAll("\"", ""));
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (GeneralSecurityException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
        return emails;
    }
    
    
    
    
    public static void main(String[] args)
    {
        MailchimpRequestSender mrs = new MailchimpRequestSender("098574958a2931e8cf7ea39a55d57573-us18");
        Hashtable<String, ListInformation> listdata = mrs.getLists();
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the Spreadsheet ID: ");
        String sprid = sc.nextLine();
        System.out.println("Enter the sheet name: ");
        String sheetname = sc.nextLine();
        System.out.println("Your lists. ");
        for (ListInformation li : listdata.values())
            System.out.println(li.getName());
        System.out.println("Enter the List name: ");
        String listname = sc.nextLine();
        String list_id = null;
        if (listname != null)
        {
            for(ListInformation list : listdata.values())
            {
                if (listname.equals(list.getName()))
                {
                    System.out.println("find");
                    list_id = list.getId();
                    break;
                }
            }
        }
        

        if (list_id != null)        
        {
            List<String> emails = getEmailsFromSpreadsheets(sprid, sheetname);
            int amountAdded = 0;
            int amountFailed = 0;
            for(String email : emails)
            {
                ResponseMember rm = mrs.addMember(new Member(email, "subscribed"), list_id);
                if (rm == null)
                {
                    amountFailed ++;
                    System.out.println("member with " + email + " already exist! ");
                }   
                else
                {
                    amountAdded ++;
                }
            }
            
            System.out.println("Amount successfully added: " + amountAdded);
            System.out.println("Amount already exist: " + amountFailed);
        }
        else
        {
            System.out.println(listname + " does not exist in your mailchimp account ");
        }
    }
}
