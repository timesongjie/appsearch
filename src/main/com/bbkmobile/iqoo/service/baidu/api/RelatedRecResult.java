package com.bbkmobile.iqoo.service.baidu.api;


public class RelatedRecResult extends AndroidInterface {

    @Override
    public ResponseVO getResponseVO() {
        return getResponseVO(INDEX_RELATED_REC);
    }

    public String relatedRecApps(String package_name){
        
        StringBuilder searchURL = new StringBuilder();
        searchURL.append(getRequestURL());
        searchURL.append("&package=");
        if(package_name != null){
            searchURL.append(package_name);
        }
        return HttpURLConn.response(searchURL.toString()); 
    }
}
