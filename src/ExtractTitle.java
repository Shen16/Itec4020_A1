import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.*;
import java.io.*;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;




public class ExtractTitle {

    private static HttpURLConnection con;

    public static void main(String[] args) throws ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException, InterruptedException {
        //create the DocumentBuilder object.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Read the XML file to Document object.
        Document document = builder.parse(new File("src/pubmed.xml"));

        //Normalize the XML Structure; It's just too important !!
        document.getDocumentElement().normalize();

        //get the root element from XML document using below code
        Element root = document.getDocumentElement();
        System.out.println(root.getNodeName());

//        //examine the xml element attributes using below methods.
//        element.getAttribute("attributeName") ;    //returns specific attribute
//        element.getAttributes();                //returns a Map (table) of names/values


        //Get all PubmedArticle
        NodeList nList = document.getElementsByTagName("PubmedArticle");
        System.out.println("============================");

        ArrayList<String> titleArray = new ArrayList<String>(); // Create an ArrayList object

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            System.out.println("");    //Just a separator
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                //Print each employee's detail
                Element eElement = (Element) node;
//                System.out.println("Employee id : " + eElement.getAttribute("id"));
                String title = eElement.getElementsByTagName("ArticleTitle").item(0).getTextContent();
                System.out.println("Article Title " + temp + ": " + eElement.getElementsByTagName("ArticleTitle").item(0).getTextContent());
//                System.out.println("Last Name : " + eElement.getElementsByTagName("lastName").item(0).getTextContent());
//                System.out.println("Location : " + eElement.getElementsByTagName("location").item(0).getTextContent());

                //Store titles in an array
                titleArray.add(title);
            }
        }


        //Java Post Request to server


        // Create listOfLists in Java
        List<List<String>> listOfLists_Of_id = new ArrayList<>();

//        for (int i = 0; i < titleArray.size(); i++) {
        for (int i = 100; i < 200; i++) {
            String query_title = java.net.URLEncoder.encode(titleArray.get(i), "UTF-8");
//            System.out.println(java.net.URLEncoder.encode(titleArray.get(i), "UTF-8"));
//            String query_title = java.net.URLEncoder.encode("Increased reactive oxygen species production down-regulates peroxisome proliferator-activated alpha pathway in C2C12 skeletal muscle cells.", "UTF-8");


            var url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi";
            var urlParameters = "db=pubmed&term="+ query_title + "&field=title&api_key=260871635c0d212693f0775e75a10cfb0c08&usehistory=y";
            System.out.println(urlParameters);

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

            try {

                var myurl = new URL(url);
                con = (HttpURLConnection) myurl.openConnection();

                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Java client");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                try (var wr = new DataOutputStream(con.getOutputStream())) {

                    wr.write(postData);
                }

                StringBuilder content;

                try (var br = new BufferedReader(
                        new InputStreamReader(con.getInputStream()))) {

                    String line;
                    content = new StringBuilder();

                    while ((line = br.readLine()) != null) {
                        content.append(line);
                        content.append(System.lineSeparator());
                    }
//                    String xml = ""; //Populated XML String....
//
                    DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder2 = factory2.newDocumentBuilder();

                    Document document2 = builder2.parse(new InputSource(new StringReader(content.toString())));




//                    DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//                    InputSource src = new InputSource();
//                    src.setCharacterStream(new StringReader(content.toString()));
//
//                    Document document2 = b.parse(src);




                    //Normalize the XML Structure; It's just too important !!
                    document2.getDocumentElement().normalize();

                    Element root2 = document2.getDocumentElement();
                    System.out.println(root2.getNodeName());



                    // Creating innerList
                    List<String> pmid_Array = new ArrayList<>();
//                    ArrayList<String> pmid_Array = new ArrayList<String>(); // Create an ArrayList object //Created above as pmidArray


                    String pm_id="";

                    try{
                        //Get all ids
                        NodeList nList2 = document2.getElementsByTagName("IdList");
                        System.out.println("============================");

                        for (int j = 0; j < nList2.getLength(); j++) {
                            System.out.println(nList2);
                            System.out.println(nList2.getLength());

                            Node node = nList2.item(j);
                            System.out.println("");    //Just a separator

                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                //Print each employee's detail
                                Element eElement = (Element) node;
                                //                System.out.println("Employee id : " + eElement.getAttribute("id"));


                                pm_id = eElement.getElementsByTagName("Id").item(0).getTextContent();
                                System.out.println("PMID " + j + ": " + eElement.getElementsByTagName("Id").item(0).getTextContent());
                                //                System.out.println("Last Name : " + eElement.getElementsByTagName("lastName").item(0).getTextContent());
                                //                System.out.println("Location : " + eElement.getElementsByTagName("location").item(0).getTextContent());


                                //Store titles in an array
                                pmid_Array.add(pm_id);
                                System.out.println(pmid_Array);

                            }
                        }
                    }
                    catch(NullPointerException e){
                        System.out.print("NullPointerException caught");
                        pm_id="NoPMID";
                        pmid_Array.add(pm_id);
                        System.out.println(pmid_Array);
                    }
                    //Store all ids in an array
                    listOfLists_Of_id.add(pmid_Array);




                }

                System.out.println(content.toString());


            } finally {

                con.disconnect();
            }

            //Wait 1 sec after every 10 statements
            Object lock= new Object();
            synchronized(lock){
            // write your code here. You may use wait() or notify() as per your requirement.
                lock.wait(1);
                lock.notify();
            }


        } //for loop ends here

        System.out.println(listOfLists_Of_id);


    } //ends main string arg method


} //ends class


