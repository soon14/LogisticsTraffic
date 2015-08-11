package com.bt.zhangzy.logisticstraffic.app;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ZhangZy on 2015/8/11.
 */
public class LocationXmlParserHandler extends DefaultHandler {

    final String TagProvince="province";
    final String TagCity = "city";
    final String TagDistrict = "district";
    HashMap<String,ArrayList<String>> provinceMap;
    ArrayList<String> cityArray;
    String currentProvince;

    public HashMap<String, ArrayList<String>> getProvinceMap() {
        return provinceMap;
    }

    @Override
    public void startDocument() throws SAXException {
//                super.startDocument();
        provinceMap = new HashMap<String,ArrayList<String>> ();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//                super.startElement(uri, localName, qName, attributes);
        if(qName.equals(TagProvince)){
            currentProvince = attributes.getValue(0);
            cityArray = new ArrayList<String>();
        }else if(qName.equals(TagCity)){
            if(cityArray!=null){
                cityArray.add(attributes.getValue(0));
            }
        }else if(qName.equals(TagDistrict)){

        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//                super.endElement(uri, localName, qName);
        if(qName.equals(TagProvince)){
            provinceMap.put(currentProvince,cityArray);
        }else if(qName.equals(TagCity)){

        }else if(qName.equals(TagDistrict)){

        }
    }

    @Override
    public void endDocument() throws SAXException {
//                super.endDocument();
    }
}
